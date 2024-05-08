package com.mohaberabi.run.data.workers

import androidx.work.ListenableWorker
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError

fun DataError.toWorkerResult(): ListenableWorker.Result {

    return when (this) {
        DataError.Local.DISK_FULL -> ListenableWorker.Result.failure()
        DataError.Local.UNKNOWN -> ListenableWorker.Result.failure()
        DataError.Network.REQUEST_TIMEOUT -> ListenableWorker.Result.retry()
        DataError.Network.UNAUTHORIZED -> ListenableWorker.Result.failure()
        DataError.Network.CONFLICT -> ListenableWorker.Result.failure()
        DataError.Network.TOO_MANY_REQUEST -> ListenableWorker.Result.retry()
        DataError.Network.NO_NETWORK -> ListenableWorker.Result.retry()
        DataError.Network.PAYLOAD_TOO_LARGE -> ListenableWorker.Result.failure()
        DataError.Network.SERVER_ERROR -> ListenableWorker.Result.retry()
        DataError.Network.SERIALIZATION_ERROR -> ListenableWorker.Result.failure()
        DataError.Network.UNKNOWN_ERROR -> ListenableWorker.Result.failure()
    }


}