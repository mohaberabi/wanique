package com.mohaberabi.wear.app.presentation.di

import com.mohaberabi.wear.app.presentation.RuniqueWear
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val runiqueWearAppModule = module {

    single { (androidApplication() as RuniqueWear).applicationScope }
}