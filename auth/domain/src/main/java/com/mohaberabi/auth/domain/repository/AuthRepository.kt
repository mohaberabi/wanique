package com.mohaberabi.auth.domain.repository

import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.error.DataError

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network>


    suspend fun login(
        email: String,
        password: String
    ):
            EmptyDataResult<DataError.Network>
}