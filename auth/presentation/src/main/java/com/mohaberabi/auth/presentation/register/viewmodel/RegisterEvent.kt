package com.mohaberabi.auth.presentation.register.viewmodel

import com.mohaberabi.core.presentation.ui.util.UiText

sealed class RegisterEvent {


    data object Done : RegisterEvent()
    data class Error(val error: UiText) : RegisterEvent()

}