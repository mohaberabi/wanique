package com.mohaberabi.run.location.di

import com.mohaberabi.run.domain.LocationObserver
import com.mohaberabi.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val locationModule = module {

    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}