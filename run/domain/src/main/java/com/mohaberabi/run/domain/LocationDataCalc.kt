package com.mohaberabi.run.domain

import com.mohaberabi.core.domain.model.LocationTimestamp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalc {
    fun getTotalDistanceInMeteres(
        locations: List<List<LocationTimestamp>>
    ): Int {
        return locations.sumOf { timeStampPerLine ->
            timeStampPerLine.zipWithNext { loc1, loc2 ->
                loc1.location.latLng.distanceTo(loc2.location.latLng)
            }.sum().roundToInt()
        }
    }

    fun getTotalEleveationsMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { locs ->
            locs.zipWithNext { loc1, loc2 ->
                val altitude1 = loc1.location.altitude
                val altitude2 = loc2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }

    fun getMaxSpeedKm(locations: List<List<LocationTimestamp>>): Double {
        return if (locations.isEmpty()) 0.0 else locations.maxOf { locs ->
            locs.zipWithNext { loc1, loc2 ->
                val distance = loc1.location.latLng.distanceTo(loc2.location.latLng)
                val hoursDiff = (loc2.durationTimestamp - loc1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)
                if (hoursDiff == 0.0) {
                    0.0
                } else {
                    (distance / 1000.0) / hoursDiff
                }
            }.maxOrNull() ?: 0.0
        }
    }
}