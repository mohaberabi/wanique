package com.mohaberabi.run.network.dto

import kotlinx.serialization.Serializable


@Serializable
data class CreateRunRequest(
    val durationMillis: Long,
    val distanceMeters: Int,
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val id: String,
    val epochMillis: Long,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?,
)
