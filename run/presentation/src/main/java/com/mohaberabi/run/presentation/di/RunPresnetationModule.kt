package com.mohaberabi.run.presentation.di

import com.mohaberabi.run.domain.RunningTracker
import com.mohaberabi.run.presentation.activerun.viewmodel.ActiveRunViewModel
import com.mohaberabi.run.presentation.overview.viewmodel.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runPresentationModule = module {
    singleOf(::RunningTracker)
    single {
        get<RunningTracker>().elapsedTime
    }
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)

}