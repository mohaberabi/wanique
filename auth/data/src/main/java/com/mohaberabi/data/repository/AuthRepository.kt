package com.mohaberabi.data.repository

import com.mohaberabi.auth.domain.repository.AuthRepository
import com.mohaberabi.core.data.network.post
import com.mohaberabi.core.domain.session.AuthInfo
import com.mohaberabi.core.domain.session.SessionStorage
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.asEmptyResult
import com.mohaberabi.core.domain.utils.const.EndPoints
import com.mohaberabi.core.domain.utils.error.DataError
import com.mohaberabi.data.model.LoginRequest
import com.mohaberabi.data.model.LoginResponse
import com.mohaberabi.data.model.RegisterRequest
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> {

        val request = RegisterRequest(email = email, password = password)

        return httpClient.post<RegisterRequest, Unit>(
            route = EndPoints.REGISTER,
            body = request
        )


    }

    override suspend fun login(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = EndPoints.LOGIN,
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if (result is AppResult.Done) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId,
                )
            )
        }
        return result.asEmptyResult()
    }
}