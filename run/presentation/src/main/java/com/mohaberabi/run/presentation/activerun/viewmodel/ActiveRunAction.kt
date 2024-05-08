package com.mohaberabi.run.presentation.activerun.viewmodel


sealed interface ActiveRunAction {
    data object OnToggleRunClick : ActiveRunAction
    data object OnBackClick : ActiveRunAction
    data object OnResumeRunClick : ActiveRunAction
    class OnRunDone(val mapPictureByteArray: ByteArray) : ActiveRunAction

    data object OnFinishRunClick : ActiveRunAction
    data object DismissRationalDialog : ActiveRunAction
    data class SubmitLocationPermissionInfo(
        val acceptedLocation: Boolean,
        val showRational: Boolean
    ) : ActiveRunAction

    data class SubmitNotificationInfo(
        val acceptedNotification: Boolean,
        val showRational: Boolean
    ) : ActiveRunAction
}