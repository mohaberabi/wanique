package com.mohaberabi.core.domain.run.repository

import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.source.RunId
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.error.DataError
import kotlinx.coroutines.flow.Flow

interface RunRepository {

    fun getRuns(): Flow<List<RunModel>>


    suspend fun deleteAllRuns()
    suspend fun fetchRuns(): EmptyDataResult<DataError>

    suspend fun upsertRun(run: RunModel, mapPicture: ByteArray): EmptyDataResult<DataError>

    suspend fun deleteRun(id: RunId)
    suspend fun syncPendingRuns()

    suspend fun logout(): EmptyDataResult<DataError.Network>
}