package com.mohaberabi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mohaberabi.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {


    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)


    @Query("SELECT * from runs ORDER BY dateTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>


    @Query("DELETE FROM runs WHERE id=:id")
    suspend fun deleteRun(id: String)

    @Query("DELETE FROM runs")
    suspend fun deleteAllRuns()
}