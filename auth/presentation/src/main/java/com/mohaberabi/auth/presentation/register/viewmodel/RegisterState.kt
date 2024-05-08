package com.mohaberabi.auth.presentation.register.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.mohaberabi.auth.domain.validators.PasswordValidateState

@OptIn(ExperimentalFoundationApi::class)
data class RegisterState constructor(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val passwordValidState: PasswordValidateState = PasswordValidateState(),
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
)