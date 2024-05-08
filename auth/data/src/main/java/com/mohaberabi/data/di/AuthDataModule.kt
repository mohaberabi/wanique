package com.mohaberabi.data.di

import com.mohaberabi.auth.domain.repository.AuthRepository
import com.mohaberabi.auth.domain.validators.PatternValidator
import com.mohaberabi.auth.domain.validators.UserDataValidator
import com.mohaberabi.data.repository.AuthRepositoryImpl
import com.mohaberabi.data.validators.EmailPatternValidator
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {


    single<PatternValidator> {

        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()


}