package com.mohaberabi.core.connectivity.data.messanging

import kotlinx.serialization.Serializable
import kotlin.time.Duration


@Serializable
sealed interface MessageActionDto {

    @Serializable
    data object StartOrResume : MessageActionDto

    @Serializable

    data object Pause : MessageActionDto

    @Serializable

    data object Finish : MessageActionDto

    @Serializable

    data object Trackable : MessageActionDto

    @Serializable

    data object UnTrackable : MessageActionDto

    @Serializable

    data object ConnectionRequest : MessageActionDto

    @Serializable

    data class HearRateUpdate(val heartRate: Int) : MessageActionDto

    @Serializable

    data class DistanceUpdate(val distanceMeters: Int) : MessageActionDto

    @Serializable

    data class TimeUpdate(val elapsedDuration: Duration) : MessageActionDto
}