package com.mohaberabi.core.domain.session

interface SessionStorage {


    suspend fun get(): AuthInfo?
    suspend fun set(info: AuthInfo?)

}




