package com.mohaberabi.run.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError

class FetchRunWorkers(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository,
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {


        if (runAttemptCount >= 5) {
            return Result.failure()
        }
        return when (
            val result = runRepository.fetchRuns()) {
            is AppResult.Error -> result.error.toWorkerResult()
            is AppResult.Done -> Result.success()
        }
    }

}