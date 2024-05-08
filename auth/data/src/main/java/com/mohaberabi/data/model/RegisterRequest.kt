package com.mohaberabi.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email: String, val password: String)
