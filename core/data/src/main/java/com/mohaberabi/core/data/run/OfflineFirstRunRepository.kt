package com.mohaberabi.core.data.run

import com.mohaberabi.core.data.network.get
import com.mohaberabi.core.database.dao.DeletedRunDao
import com.mohaberabi.core.database.dao.RunPendingSyncDao
import com.mohaberabi.core.database.mapper.toRun
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.run.source.RunId
import com.mohaberabi.core.domain.run.source.RunLocalDataSource
import com.mohaberabi.core.domain.run.source.RunRemoteDataSource
import com.mohaberabi.core.domain.session.SessionStorage
import com.mohaberabi.core.domain.sync.RunSyncer
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.asEmptyResult
import com.mohaberabi.core.domain.utils.error.DataError
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val runPendingSyncDao: RunPendingSyncDao,
    private val localDataSource: RunLocalDataSource,
    private val remoteDataSource: RunRemoteDataSource,
    private val sessionStorage: SessionStorage,
    private val runSyncer: RunSyncer,
    private val client: HttpClient,
    private val deletedRunDao: DeletedRunDao,
    /**
     *Using [applicationScope] ensures that the coroutine's lifecycle is tied to the application's lifecycle,
     *  which means it will continue executing even if the current UI component (like an Activity or Fragment)
     *  is destroyed or recreated. This is important for background tasks like database operations,
     *  network requests, etc., which shouldn't be tied to the lifecycle of a specific UI component.
     * Overall, using applicationScope here ensures proper coroutine management and
     * helps in maintaining a responsive and efficient application architecture.
     * because what  might happen again  you call this method from the [viewmodelScope] so the scope will call the
     * [suspend] function and in first you are executing the [localDataSource] operations
     * if the scope you are executing the [suspend]  function from it was canceled then the [remoteDataSource]
     * will not execute the operation as the scope is canceled
     * but when you tight the remote operation to a scope that will always live as long as the application is alive
     * the desired task will be executed in the background regardless of cancellation of caller scope
     */
    private val applicationScope: CoroutineScope
) : RunRepository {
    /**
     * SSOF - Single Source Of Truth
     */

    override fun getRuns(): Flow<List<RunModel>> = localDataSource.getRuns()

    override suspend fun fetchRuns(): EmptyDataResult<DataError> {
        return when (val result = remoteDataSource.getRuns()) {

            is AppResult.Error -> result.asEmptyResult()
            is AppResult.Done -> {
                applicationScope.async {
                    localDataSource.upsertRuns(result.data).asEmptyResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(
        run: RunModel,
        mapPicture: ByteArray
    ): EmptyDataResult<DataError> {

        val localResult = localDataSource.upsertRun(run)
        if (localResult !is AppResult.Done) {
            return localResult.asEmptyResult()
        }
        val runWithId = run.copy(id = localResult.data)

        return when (val remoteResult = remoteDataSource.postRun(runWithId, mapPicture)) {
            is AppResult.Error -> {

                applicationScope.launch {
                    runSyncer.scheduleSync(RunSyncer.SyncType.CreateRun(run, mapPicture))
                }.join()
                AppResult.Done(Unit)
            }

            is AppResult.Done -> {
                applicationScope.async {
                    localDataSource.upsertRun(remoteResult.data).asEmptyResult()
                }.await()

            }
        }
    }


    override suspend fun deleteRun(id: RunId) {

        localDataSource.deleteRun(id)

        /**
         * added in offline mode then was synced waiting to be added to remote
         * also still in offline mode then deletes it , so no need to sync with remote ever
         */
        val isPendingSyncExist = runPendingSyncDao.getRunPendingEntity(id) != null

        if (isPendingSyncExist) {
            runPendingSyncDao.deletePendingSyncEntity(id)
            return
        }
        val remoteResult = applicationScope.async { remoteDataSource.deleteRun(id) }.await()

        if (remoteResult is AppResult.Error) {

            applicationScope.launch {
                runSyncer.scheduleSync(RunSyncer.SyncType.DeleteRun(id))
            }.join()
        }

    }

    override suspend fun syncPendingRuns() {

        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            /**
             * making use of [async] and [await] to execute both of this functions in parallel
             * to make function execution faster and optimal as they will each one of [createdRuns] as well as [deletedRuns]
             * will be launched in two different coroutines parallel in conquer manner
             */
            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingEntities(userId)
            }

            val deletedRuns = async {
                runPendingSyncDao.getAllRunPendingEntities(userId)
            }
            val createdJob = createdRuns
                .await()
                .map { pending ->
                    launch {
                        val run = pending.run.toRun()
                        when (remoteDataSource.postRun(run, pending.mapPictureBytes)) {
                            is AppResult.Error -> Unit
                            is AppResult.Done -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deletePendingSyncEntity(pending.runId)
                                }.join()
                            }
                        }
                    }

                }

            val deletedJobs = deletedRuns
                .await().map { pending ->
                    launch {
                        when (remoteDataSource.deleteRun(pending.runId)) {
                            is AppResult.Error -> Unit
                            is AppResult.Done -> applicationScope.launch {
                                deletedRunDao.deleteDeletedRunSyncEntity(pending.runId)
                            }.join()
                        }
                    }

                }


            /**
             * createdJob.forEach { it.join() }
             *     deletedJobs.forEach { it.join() }
             *     wait and suspend  and all of the coroutines is all done and finished complete
             */

            createdJob.forEach { it.join() }
            deletedJobs.forEach { it.join() }

        }

    }

    override suspend fun logout(): EmptyDataResult<DataError.Network> {

        val result = client.get<Unit>(
            route = "/logout"
        ).asEmptyResult()

        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()
        return result
    }

    override suspend fun deleteAllRuns() = localDataSource.deleteAllRuns()

}