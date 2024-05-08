package com.mohaberabi.runique

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.mohaberabi.auth.presentation.di.authViewModelModule
import com.mohaberabi.core.connectivity.di.coreConnectivityDataModule
import com.mohaberabi.core.database.di.databaseModule
import com.mohaberabi.core.di.coreDataModule
import com.mohaberabi.data.di.authDataModule
import com.mohaberabi.run.data.di.runDataModule
import com.mohaberabi.run.location.di.locationModule
import com.mohaberabi.run.network.di.runNetworkModule
import com.mohaberabi.run.presentation.di.runPresentationModule
import com.mohaberabi.runique.core.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp : Application() {

    /**
     * [SupervisorJob] is used because if we used this [applicationScope] in many places
     * and it was failed in such a place that means that it will cancel or will damage or other
     * coroutines launched under this scope even if they were working without no problesm
     * so this will make the coroutines launched as if they are Independent
     */
    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            workManagerFactory()
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule,
                runNetworkModule,
                runDataModule,
                coreConnectivityDataModule,

                )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}