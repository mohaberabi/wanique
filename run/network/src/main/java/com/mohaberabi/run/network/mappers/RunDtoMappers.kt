package com.mohaberabi.run.network.mappers

import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.run.network.dto.CreateRunRequest
import com.mohaberabi.run.network.dto.RunModelDto
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


fun RunModelDto.toRun(): RunModel {
    return RunModel(
        id = id,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = AppLatLng(lat, long),
        maxSpeedKmh = maxSpeedKmh,
        mapPictureUrl = mapPictureUrl,
        totalElevationMeters = totalElevationMeters,
        duration = durationMillis.milliseconds,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )

}

fun RunModel.toRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        id = id!!,
        durationMillis = duration.inWholeMilliseconds,
        lat = location.lat,
        long = location.lng,
        avgSpeedKmh = avgSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        distanceMeters = distanceMeters,
        maxSpeedKmh = maxSpeedKmh,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000L,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
}

