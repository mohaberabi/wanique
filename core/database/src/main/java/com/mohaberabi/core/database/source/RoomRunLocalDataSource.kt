package com.mohaberabi.core.database.source

import android.database.sqlite.SQLiteFullException
import com.mohaberabi.core.database.dao.RunDao
import com.mohaberabi.core.database.mapper.toRun
import com.mohaberabi.core.database.mapper.toRunEntity
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.source.RunId
import com.mohaberabi.core.domain.run.source.RunLocalDataSource
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomRunLocalDataSource(
    private val runDao: RunDao
) : RunLocalDataSource {
    override fun getRuns(): Flow<List<RunModel>> =
        runDao.getRuns().map { runsLocal -> runsLocal.map { it.toRun() } }

    override suspend fun upsertRun(run: RunModel): AppResult<RunId, DataError.Local> {

        return try {
            val entity = run.toRunEntity()
            runDao.upsertRun(entity)
            AppResult.Done(entity.id)
        } catch (e: SQLiteFullException) {
            AppResult.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun upsertRuns(runs: List<RunModel>): AppResult<List<RunId>, DataError.Local> {
        return try {
            val entities = runs.map { it.toRunEntity() }
            runDao.upsertRuns(entities)
            AppResult.Done(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            AppResult.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteRun(id: String) = runDao.deleteRun(id)

    override suspend fun deleteAllRuns() = runDao.deleteAllRuns()
}