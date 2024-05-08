package com.mohaberabi.run.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mohaberabi.core.database.dao.DeletedRunDao
import com.mohaberabi.core.database.dao.RunPendingSyncDao
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.run.source.RunRemoteDataSource
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError

class DeleteRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RunRemoteDataSource,
    private val deletedRunDao: DeletedRunDao,
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {


        if (runAttemptCount >= 5) {
            return Result.failure()
        }


        val rundId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        return when (val result = remoteRunDataSource.deleteRun(rundId)) {
            is AppResult.Error -> result.error.toWorkerResult()
            is AppResult.Done -> {
                deletedRunDao.deleteDeletedRunSyncEntity(rundId)
                Result.success()
            }
        }

    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}