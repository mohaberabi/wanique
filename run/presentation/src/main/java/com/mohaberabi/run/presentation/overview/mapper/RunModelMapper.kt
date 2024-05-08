package com.mohaberabi.run.presentation.overview.mapper

import com.mohaberabi.core.presentation.ui.util.formatted
import com.mohaberabi.core.presentation.ui.util.toFormattedKm
import com.mohaberabi.core.presentation.ui.util.toFormattedKmh
import com.mohaberabi.core.presentation.ui.util.toFormattedMetres
import com.mohaberabi.core.presentation.ui.util.toFormattedPace
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.presentation.ui.util.toFormattedHeartRate
import com.mohaberabi.run.presentation.overview.util.RunModelUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun RunModel.toRunModelUi(): RunModelUi {

    val dateTimeLocal = dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
    val formatedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy -hh:mma").format(dateTimeLocal)
    val distanceKm = distanceMeters / 1000.0
    return RunModelUi(
        dateTime = formatedDateTime,
        duration = duration.formatted(),
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMetres(),
        mapPictureUrl = mapPictureUrl,
        id = id!!,
        avgHeartRate = avgHeartRate.toFormattedHeartRate(),
        maxHeartRate = maxHeartRate.toFormattedHeartRate(),
    )
}