package com.mohaberabi.core.di

import android.content.SharedPreferences
import com.mohaberabi.core.data.auth.EncryptedSessionStorage
import com.mohaberabi.core.data.network.HttpClientFactory
import com.mohaberabi.core.data.run.OfflineFirstRunRepository
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.session.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val coreDataModule = module {


    single {

        HttpClientFactory(get()).build()
    }


    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}