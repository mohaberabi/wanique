package com.mohaberabi.data.model

import kotlinx.serialization.Serializable
import java.sql.Timestamp


@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTimestamp: Long,
    val userId: String,
)
