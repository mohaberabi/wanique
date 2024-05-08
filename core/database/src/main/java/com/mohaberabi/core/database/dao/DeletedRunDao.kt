package com.mohaberabi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mohaberabi.core.database.entity.DeletedRunEntity

@Dao
interface DeletedRunDao {

    @Query("SELECT * FROM DeletedRunEntity WHERE userId=:userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunEntity>


    @Upsert
    suspend fun upsertDeletedRunSyncEntity(run: DeletedRunEntity)


    @Query("DELETE FROM DeletedRunEntity WHERE runId=:runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}