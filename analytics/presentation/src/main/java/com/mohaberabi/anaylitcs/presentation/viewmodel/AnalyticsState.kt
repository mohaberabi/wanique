package com.mohaberabi.anaylitcs.presentation.viewmodel

import com.mohaberabi.anayltics.domain.AnalyticsHolder
import com.mohaberabi.core.presentation.ui.util.formatted
import com.mohaberabi.core.presentation.ui.util.toFormattedKm
import com.mohaberabi.core.presentation.ui.util.toFormattedKmh
import com.mohaberabi.core.presentation.ui.util.toFormattedMetres
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

data class AnalyticsState(
    val totalDistanceRun: String = "0",
    val totalTimeRun: String = "",
    val fastestEverRun: String = "",
    val avgDistance: String = "",
    val avgPace: String = "",
)


fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

fun AnalyticsHolder.toAnalyticsState(): AnalyticsState {

    return AnalyticsState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()

    )
}