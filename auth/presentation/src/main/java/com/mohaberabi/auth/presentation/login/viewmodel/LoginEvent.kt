package com.mohaberabi.auth.presentation.login.viewmodel

import com.mohaberabi.core.presentation.ui.util.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText) : LoginEvent
    data object Done : LoginEvent
}