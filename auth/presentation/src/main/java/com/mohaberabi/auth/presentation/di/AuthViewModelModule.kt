package com.mohaberabi.auth.presentation.di

import com.mohaberabi.auth.presentation.login.viewmodel.LoginViewModel
import com.mohaberabi.auth.presentation.register.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val authViewModelModule = module {

    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)

}