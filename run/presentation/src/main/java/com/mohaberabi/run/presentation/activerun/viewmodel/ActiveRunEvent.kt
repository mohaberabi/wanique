package com.mohaberabi.run.presentation.activerun.viewmodel

import com.mohaberabi.core.presentation.ui.util.UiText


sealed interface ActiveRunEvent {

    data class Error(val error: UiText) : ActiveRunEvent

    data object RunSaved : ActiveRunEvent
}