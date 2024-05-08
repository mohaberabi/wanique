package com.mohaberabi.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

@Entity(tableName = "runs")
data class RunEntity(
    val durationMillis: Long,
    val distanceMetres: Int,
    val dateTimeUtc: String,
    val lat: Double,
    val lng: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationsMetres: Int,
    val mapPictureUrl: String?,
    val maxHeartRate: Int?,
    val avgHeartRate: Int?,
    @PrimaryKey(autoGenerate = false)
    val id: String = ObjectId().toHexString(),
)
