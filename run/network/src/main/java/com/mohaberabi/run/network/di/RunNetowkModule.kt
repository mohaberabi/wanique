package com.mohaberabi.run.network.di

import com.mohaberabi.core.domain.run.source.RunRemoteDataSource
import com.mohaberabi.run.network.source.KtorRunRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val runNetworkModule = module {
    singleOf(::KtorRunRemoteDataSource).bind<RunRemoteDataSource>()
}