package com.mohaberabi.auth.domain.validators

data class PasswordValidateState(
    val hasMinLength: Boolean = false,
    val hasNumber: Boolean = false,

    val hasLower: Boolean = false,
    val hasUpper: Boolean = false,

    ) {
    val isValid: Boolean
        get() = hasMinLength && hasNumber && hasLower && hasUpper
}