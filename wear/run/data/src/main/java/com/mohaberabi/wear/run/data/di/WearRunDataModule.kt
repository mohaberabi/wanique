package com.mohaberabi.wear.run.data.di

import com.mohaberabi.wear.run.data.repository.AndroidExerciseTracker
import com.mohaberabi.wear.run.data.repository.WatchToPhoneConnector
import com.mohaberabi.wear.run.domain.PhoneConnector
import com.mohaberabi.wear.run.domain.RunningTracker
import com.mohaberabi.wear.run.domain.repository.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val wearRunDataModule = module {

    singleOf(::AndroidExerciseTracker).bind<ExerciseTracker>()

    singleOf(::WatchToPhoneConnector).bind<PhoneConnector>()

    singleOf(::RunningTracker)

    single {
        get<RunningTracker>().elapsedTime
    }
}