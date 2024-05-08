package com.mohaberabi.wear.run.presentation.tracker.viewmodel

import com.mohaberabi.core.presentation.ui.util.UiText


sealed interface TrackerEvent {
    data object RunDone : TrackerEvent
    data class Error(val error: UiText) : TrackerEvent

}