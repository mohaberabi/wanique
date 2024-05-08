package com.mohaberabi.core.database.di

import androidx.room.Room
import com.mohaberabi.core.database.db.RunDatabase
import com.mohaberabi.core.database.source.RoomRunLocalDataSource
import com.mohaberabi.core.domain.run.source.RunLocalDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val databaseModule = module {

    single {

        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        ).build()
    }
    singleOf(::RoomRunLocalDataSource).bind<RunLocalDataSource>()
    single {
        get<RunDatabase>().runDao
    }
    single {
        get<RunDatabase>().runPendingSyncDao
    }
    single {
        get<RunDatabase>().deletedRunDao
    }
 
}