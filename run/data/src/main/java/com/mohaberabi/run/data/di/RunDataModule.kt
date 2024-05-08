package com.mohaberabi.run.data.di

import com.mohaberabi.core.domain.sync.RunSyncer
import com.mohaberabi.run.data.connector.PhoneToWatchConnector
import com.mohaberabi.run.data.sync.SyncRunWorkerSchaduler
import com.mohaberabi.run.data.workers.CreateRunWorker
import com.mohaberabi.run.data.workers.DeleteRunWorker
import com.mohaberabi.run.data.workers.FetchRunWorkers
import com.mohaberabi.run.domain.WatchConnector
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val runDataModule = module {


    workerOf(::FetchRunWorkers)
    workerOf(::CreateRunWorker)

    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerSchaduler).bind<RunSyncer>()
    singleOf(::PhoneToWatchConnector).bind<WatchConnector>()

}