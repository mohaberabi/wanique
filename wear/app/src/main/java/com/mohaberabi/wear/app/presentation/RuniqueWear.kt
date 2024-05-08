package com.mohaberabi.wear.app.presentation

import android.app.Application
import com.mohaberabi.core.connectivity.di.coreConnectivityDataModule
import com.mohaberabi.wear.app.presentation.di.runiqueWearAppModule
import com.mohaberabi.wear.run.data.di.wearRunDataModule
import com.mohaberabi.wear.run.presentation.tracker.di.wearRunPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class RuniqueWear : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RuniqueWear)
            modules(
                runiqueWearAppModule,
                wearRunPresentationModule,
                wearRunDataModule,
                coreConnectivityDataModule,
            )
        }
    }
}