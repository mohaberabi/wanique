package com.mohaberabi.core.presentation.ui.util

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration


fun Duration.formatted(): String {

    val totalSeconds = inWholeSeconds

    /**
     * put the second parameter [totalSecond/3600] on the first with a leading 0 if it's from 0--9 else will add it lik [10]
     * d is whole number [Digit]
     */
    val hours = String.format("%02d", totalSeconds / (60 * 60))

    val minutes = String.format("%02d", (totalSeconds % 3600) / 60)
    val seconds = String.format("%02d", (totalSeconds % 60))

    return "$hours:$minutes:$seconds"

}


fun Double.toFormattedKm(): String {

    return "${this.roundToDecimals(1)} km"
}


fun Double.toFormattedKmh(): String {
    return "${roundToDecimals(1)}km/h"
}

fun Int.toFormattedMetres(): String {
    return "${this} m"
}


fun Duration.toFormattedPace(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "_"
    }
    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPacePerMinutes = secondsPerKm / 60
    val avgPaceSeconds = String.format("%02d", secondsPerKm % 60)

    return "$avgPacePerMinutes:$avgPaceSeconds / km"
}


/**
 * if you have[5.678] and the count = 1 you need it to be 5.6
 * so factor = 10^1 = 10
 *round(5.678*factor) so it will return 5.7
 *
 */
private fun Double.roundToDecimals(count: Int): Double {
    val factor = 10f.pow(count)
    return round(this * factor) / factor
}

fun Int?.toFormattedHeartRate(): String {
    return if (this != null) "$this bpm" else "-"
}