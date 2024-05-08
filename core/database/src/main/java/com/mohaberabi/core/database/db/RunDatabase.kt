package com.mohaberabi.core.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mohaberabi.core.database.dao.AnalyticsDao
import com.mohaberabi.core.database.dao.DeletedRunDao
import com.mohaberabi.core.database.dao.RunDao
import com.mohaberabi.core.database.dao.RunPendingSyncDao
import com.mohaberabi.core.database.entity.DeletedRunEntity
import com.mohaberabi.core.database.entity.RunEntity
import com.mohaberabi.core.database.entity.RunPendingEntity


@Database(
    entities = [
        RunPendingEntity::class,
        RunEntity::class,
        DeletedRunEntity::class,

    ],
    version = 1,
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao

    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val deletedRunDao: DeletedRunDao
    abstract val analyticsDao: AnalyticsDao

}