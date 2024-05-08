package com.mohaberabi.core.database.mapper

import com.mohaberabi.core.database.entity.RunEntity
import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.domain.model.RunModel
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds


fun RunEntity.toRun(): RunModel {
    return RunModel(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMetres,
        location = AppLatLng(lat, lng),
        maxSpeedKmh = maxSpeedKmh,
        mapPictureUrl = mapPictureUrl,
        totalElevationMeters = totalElevationsMetres,
        maxHeartRate = maxHeartRate,
        avgHeartRate = avgHeartRate,
    )

}

fun RunModel.toRunEntity(): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(),
        durationMillis = duration.inWholeMilliseconds,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        lat = location.lat,
        lng = location.lng,
        avgSpeedKmh = avgSpeedKmh,
        totalElevationsMetres = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        distanceMetres = distanceMeters,
        maxSpeedKmh = maxSpeedKmh,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
}

