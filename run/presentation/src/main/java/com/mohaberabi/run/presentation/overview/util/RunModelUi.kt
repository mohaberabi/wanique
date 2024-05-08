package com.mohaberabi.run.presentation.overview.util

import com.mohaberabi.core.domain.model.AppLatLng
import java.time.ZonedDateTime
import kotlin.time.Duration

data class RunModelUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?,
    val avgHeartRate: String,
    val maxHeartRate: String,
)
