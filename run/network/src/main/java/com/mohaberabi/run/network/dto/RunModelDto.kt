package com.mohaberabi.run.network.dto

import kotlinx.serialization.Serializable


@Serializable
data class RunModelDto(
    val id: String,
    val dateTimeUtc: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?
)
