package com.mohaberabi.core.domain.run.source

import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.error.DataError

interface RunRemoteDataSource {

    suspend fun getRuns(): AppResult<List<RunModel>, DataError.Network>


    suspend fun postRun(
        run: RunModel,
        mapPicture: ByteArray
    ): AppResult<RunModel, DataError.Network>


    suspend fun deleteRun(id: String): EmptyDataResult<DataError.Network>
}