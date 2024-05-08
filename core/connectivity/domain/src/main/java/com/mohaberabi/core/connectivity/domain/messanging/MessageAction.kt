package com.mohaberabi.core.connectivity.domain.messanging

import kotlin.time.Duration

sealed interface MessageAction {


    data object StartOrResume : MessageAction
    data object Pause : MessageAction
    data object Finish : MessageAction
    data object Trackable : MessageAction
    data object UnTrackable : MessageAction
    data object ConnectionRequest : MessageAction
    data class HearRateUpdate(val heartRate: Int) : MessageAction
    data class DistanceUpdate(val distanceMeters: Int) : MessageAction
    data class TimeUpdate(val elapsedDuration: Duration) : MessageAction

}