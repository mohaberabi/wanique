package com.mohaberabi.wear.run.presentation.tracker.di

import com.mohaberabi.wear.run.presentation.tracker.viewmodel.TrackerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val wearRunPresentationModule = module {


    viewModelOf(::TrackerViewModel)
}