package com.mohaberabi.run.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.mohaberabi.core.database.dao.DeletedRunDao
import com.mohaberabi.core.database.dao.RunPendingSyncDao
import com.mohaberabi.core.database.entity.DeletedRunEntity
import com.mohaberabi.core.database.entity.RunPendingEntity
import com.mohaberabi.core.database.mapper.toRunEntity
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.source.RunId
import com.mohaberabi.core.domain.session.SessionStorage
import com.mohaberabi.run.data.workers.CreateRunWorker
import com.mohaberabi.run.data.workers.DeleteRunWorker
import com.mohaberabi.run.data.workers.FetchRunWorkers
import com.mohaberabi.core.domain.sync.RunSyncer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration


class SyncRunWorkerSchaduler(

    private val context: Context,
    private val sessionStorage: SessionStorage,
    private val pendingSyncDao: RunPendingSyncDao,
    private val deletedRunDao: DeletedRunDao,

    private val applicationScope: CoroutineScope,
) : RunSyncer {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun scheduleSync(type: RunSyncer.SyncType) {

        when (type) {
            is RunSyncer.SyncType.FetchRuns -> scheduleFetchWorker(type.interval)
            is RunSyncer.SyncType.DeleteRun -> schaduleDeleteRunWorker(type.runId)
            is RunSyncer.SyncType.CreateRun -> schadualeCreateRunWorker(
                type.run,
                type.mapPictureBytes
            )

        }
    }

    override suspend fun cancelAllSyncs() {

        workManager.cancelAllWork().await()
    }


    private suspend fun scheduleFetchWorker(interval: Duration) {
        val alreadySyncing = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag(SYNC_WORK)
                .get()
                .isNotEmpty()
        }

        if (alreadySyncing) {
            return
        }

        val syncWorkRequest = PeriodicWorkRequestBuilder<FetchRunWorkers>(
            repeatInterval = interval.toJavaDuration()
        )
            .setConstraints(baseConstraints)
            .setBackoffCriteria(
                backoffPolicy = RETRY_POLICY,
                backoffDelay = BACK_OFF_DELAY,
                timeUnit = BACK_OFF_TIME_UNIT
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            ).addTag(SYNC_WORK)
            .build()

        workManager.enqueue(syncWorkRequest).await()
    }


    private suspend fun schadualeCreateRunWorker(
        run: RunModel,
        mapPicture: ByteArray,
    ) {
        val userId = sessionStorage.get()?.userId ?: return

        val pendingRun = RunPendingEntity(
            run = run.toRunEntity(),
            userId = userId,
            mapPictureBytes = mapPicture
        )

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag(CREATE_WORK)
            .setConstraints(baseConstraints)
            .setBackoffCriteria(
                backoffPolicy = RETRY_POLICY,
                backoffDelay = BACK_OFF_DELAY,
                timeUnit = BACK_OFF_TIME_UNIT
            ).setInputData(
                Data.Builder().putString(CreateRunWorker.RUN_ID, pendingRun.runId).build()
            ).build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun schaduleDeleteRunWorker(runid: RunId) {

        val userId = sessionStorage.get()?.userId ?: return

        val entity = DeletedRunEntity(
            runId = runid,
            userId = userId
        )
        deletedRunDao.upsertDeletedRunSyncEntity(entity)
        val deleteRunWorker = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag(DELETE_WORK)
            .setConstraints(baseConstraints)
            .setBackoffCriteria(
                backoffPolicy = RETRY_POLICY,
                backoffDelay = BACK_OFF_DELAY,
                timeUnit = BACK_OFF_TIME_UNIT
            ).setInputData(
                Data.Builder().putString(DeleteRunWorker.RUN_ID, entity.runId).build()
            ).build()

        applicationScope.launch {
            workManager.enqueue(deleteRunWorker).await()
        }.join()
    }

    companion object {
        const val SYNC_WORK = "SYNC_WORK"
        const val CREATE_WORK = "CREATE_WORK"
        const val DELETE_WORK = "DELETE_WORK"
        val RETRY_POLICY = BackoffPolicy.EXPONENTIAL
        const val BACK_OFF_DELAY = 2000L
        val BACK_OFF_TIME_UNIT = TimeUnit.MILLISECONDS
    }

    private val baseConstraints: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()


}