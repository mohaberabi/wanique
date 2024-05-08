package com.mohaberabi.core.domain.model

import kotlin.time.Duration

data class RunData(
    val distanceMetres: Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationTimestamp>> = emptyList(),
    val hearRates: List<Int> = emptyList()
)
