package com.mohaberabi.core.domain.run.source

import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import javax.xml.crypto.Data


typealias RunId = String

interface RunLocalDataSource {

    fun getRuns(): Flow<List<RunModel>>
    suspend fun upsertRun(run: RunModel): AppResult<RunId, DataError.Local>
    suspend fun upsertRuns(runs: List<RunModel>): AppResult<List<RunId>, DataError.Local>
    suspend fun deleteRun(id: String)
    suspend fun deleteAllRuns()
}