package com.mohaberabi.anaylitcs.presentation.di

import com.mohaberabi.anaylitcs.presentation.viewmodel.AnalyticsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsViewModel)
}