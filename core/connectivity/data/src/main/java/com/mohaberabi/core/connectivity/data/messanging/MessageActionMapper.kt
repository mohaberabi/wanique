package com.mohaberabi.core.connectivity.data.messanging

import com.mohaberabi.core.connectivity.domain.messanging.MessageAction


fun MessageAction.toMessageActionDto(): MessageActionDto {
    return when (this) {
        MessageAction.ConnectionRequest -> MessageActionDto.ConnectionRequest
        is MessageAction.DistanceUpdate -> MessageActionDto.DistanceUpdate(distanceMeters)
        MessageAction.Finish -> MessageActionDto.Finish
        is MessageAction.HearRateUpdate -> MessageActionDto.HearRateUpdate(heartRate)
        MessageAction.Pause -> MessageActionDto.Pause
        MessageAction.StartOrResume -> MessageActionDto.StartOrResume
        is MessageAction.TimeUpdate -> MessageActionDto.TimeUpdate(elapsedDuration)
        MessageAction.Trackable -> MessageActionDto.Trackable
        MessageAction.UnTrackable -> MessageActionDto.UnTrackable
    }
}

fun MessageActionDto.toMessageAction(): MessageAction {
    return when (this) {
        MessageActionDto.ConnectionRequest -> MessageAction.ConnectionRequest
        is MessageActionDto.DistanceUpdate -> MessageAction.DistanceUpdate(distanceMeters)
        MessageActionDto.Finish -> MessageAction.Finish
        is MessageActionDto.HearRateUpdate -> MessageAction.HearRateUpdate(heartRate)
        MessageActionDto.Pause -> MessageAction.Pause
        MessageActionDto.StartOrResume -> MessageAction.StartOrResume
        is MessageActionDto.TimeUpdate -> MessageAction.TimeUpdate(elapsedDuration)
        MessageActionDto.Trackable -> MessageAction.Trackable
        MessageActionDto.UnTrackable -> MessageAction.UnTrackable
    }
}