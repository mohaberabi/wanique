package com.mohaberabi.core.database.dao

import androidx.room.Dao
import androidx.room.Query


@Dao
interface AnalyticsDao {


    @Query("SELECT SUM(distanceMetres)FROM runs")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(durationMillis)FROM runs")
    suspend fun getTotalTimeRun(): Long

    @Query("SELECT MAX(maxSpeedKmh)FROM runs")
    suspend fun getMaxSpeed(): Double

    @Query("SELECT AVG(distanceMetres)FROM runs")
    suspend fun getAvgDistancePerRun(): Double

    @Query("SELECT Sum((durationMillis/60000.0)/(distanceMetres/1000.0))FROM runs")
    suspend fun getAvgPacePerRun(): Double
}