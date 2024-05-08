package com.mohaberabi.core.domain.model

import com.mohaberabi.core.domain.model.AppLatLng
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class RunModel(
    /** null if a new run  as we still waiting for id to be generated from the DB */
    val id: String?, // null if new run
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: AppLatLng,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?
) {
    val avgSpeedKmh: Double
        get() = if (duration == Duration.ZERO) 0.0 else (distanceMeters / 1000.0) / duration.toDouble(
            DurationUnit.HOURS
        )
}
