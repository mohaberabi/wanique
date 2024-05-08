package com.mohaberabi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mohaberabi.core.database.entity.DeletedRunEntity
import com.mohaberabi.core.database.entity.RunPendingEntity

@Dao
interface RunPendingSyncDao {

    @Query("SELECT * FROM RunPendingEntity WHERE userId=:userId")
    suspend fun getAllRunPendingEntities(userId: String): List<RunPendingEntity>


    @Query("SELECT * FROM RunPendingEntity WHERE runId=:runId")
    suspend fun getRunPendingEntity(runId: String): RunPendingEntity?


    @Upsert(RunPendingEntity::class)
    suspend fun upsertRunPendingEntity(run: RunPendingEntity)

    @Query("DELETE FROM RunPendingEntity WHERE runId=:runId")
    suspend fun deletePendingSyncEntity(runId: String)

}