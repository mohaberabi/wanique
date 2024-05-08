package com.mohaberabi.core.domain.model

import kotlin.time.Duration


data class LocationTimestamp(
    val location: AppAltitude,
    val durationTimestamp: Duration,
)