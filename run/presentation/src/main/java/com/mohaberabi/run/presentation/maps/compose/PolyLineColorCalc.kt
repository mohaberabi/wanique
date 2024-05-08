package com.mohaberabi.run.presentation.maps.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.mohaberabi.core.domain.model.LocationTimestamp
import kotlin.math.abs


object PolyLineColorCalc {


    fun locationsToColor(
        location1: LocationTimestamp,
        location2: LocationTimestamp
    ): Color {
        val distanceInMetres = location1.location.latLng.distanceTo(location2.location.latLng)
        val timeDiff =
            abs((location2.durationTimestamp - location1.durationTimestamp).inWholeSeconds)
        val speedKmh = (distanceInMetres / timeDiff) * 3.6
        return interpolateColor(
            speedKmh,
            minSpeed = 0.5,
            maxSpeed = 20.0,
            colorStart = Color.Green,
            colorMid = Color.Yellow,
            colorEnd = Color.Red
        )
    }

    private fun interpolateColor(
        speedKmh: Double,
        minSpeed: Double,
        maxSpeed: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color,
    ): Color {

        // slow = green , insane = red , in between = yellow
        /**
         *what does this function do ?
         * imagine that [minSpeed] =5.0 km/H and [maxSpeed] =20.0 km/H
         *that means that if the running speed = 5.0 or slower this means  that the drawn line will be completely green
         * and else if the running speed = 20.0 km/h so the line is completely red
         * if the speed is between the [minSpeed] and  the [maxSpeed] so we need to get the ratio between them
         */

        val ratio = ((speedKmh - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0..1.0)

        val colorInt = if (ratio <= 0.5) {
            val midRatio = ratio / 0.5
            ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
        } else {
            val midToEndRatio = (ratio - 0.5) / 0.5
            ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())

        }
        return Color(colorInt)

    }
}