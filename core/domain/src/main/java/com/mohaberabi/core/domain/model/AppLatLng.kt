package com.mohaberabi.core.domain.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AppLatLng(
    val lat: Double,
    val lng: Double,
) {


    fun distanceTo(
        other: AppLatLng
    ): Float {

        val latDistance = Math.toRadians(other.lat - lat)
        val longDistance = Math.toRadians(other.lng - lng)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(other.lat)) *
                sin(longDistance / 2) * sin(longDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METRES * c.toFloat()
    }

    companion object {
        private const val EARTH_RADIUS_METRES = 6_371_000
    }
}
