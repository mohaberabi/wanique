package com.mohaberabi.auth.domain.validators

class UserDataValidator(
    private val patternValidator: PatternValidator
) {

    fun isValidEmail(email: String): Boolean = patternValidator.matches(email.trim().lowercase())

    fun validatePassword(password: String): PasswordValidateState {
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        return PasswordValidateState(
            hasMinLength = hasMinLength,
            hasLower = hasLower,
            hasUpper = hasUpper,
            hasNumber = hasDigit,
        )
    }

    companion object {

        const val MIN_PASSWORD_LENGTH = 9
    }
}