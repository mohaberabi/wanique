package com.mohaberabi.run.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mohaberabi.core.database.dao.RunPendingSyncDao
import com.mohaberabi.core.database.mapper.toRun
import com.mohaberabi.core.domain.run.source.RunRemoteDataSource
import com.mohaberabi.core.domain.utils.AppResult

class CreateRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RunRemoteDataSource,
    private val pendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {


        if (runAttemptCount >= 5) {
            return Result.failure()
        }


        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        val pendingRun = pendingSyncDao.getRunPendingEntity(pendingRunId) ?: return Result.failure()


        val run = pendingRun.run.toRun()
        return when (val result = remoteRunDataSource.postRun(run, pendingRun.mapPictureBytes)) {
            is AppResult.Error -> result.error.toWorkerResult()
            is AppResult.Done -> {
                pendingSyncDao.deletePendingSyncEntity(pendingRunId)
                Result.success()
            }
        }


    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}