package com.mohaberabi.wear.run.presentation.tracker.viewmodel

sealed interface TrackerAction {
    data object OnToggleRunClick : TrackerAction
    data object OnFinishRunClick : TrackerAction
    data class OnBodySensorPermissionResult(
        val allowed: Boolean
    ) : TrackerAction

}