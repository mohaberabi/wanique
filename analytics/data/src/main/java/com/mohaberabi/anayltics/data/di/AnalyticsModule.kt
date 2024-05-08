package com.mohaberabi.anayltics.data.di

import com.mohaberabi.anayltics.data.RoomAnalyticsRepository
import com.mohaberabi.anayltics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val analyticsModule = module {

    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()

}