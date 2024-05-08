package com.mohaberabi.anayltics.data

import com.mohaberabi.anayltics.domain.AnalyticsHolder
import com.mohaberabi.anayltics.domain.AnalyticsRepository
import com.mohaberabi.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val anaylitcsDao: AnalyticsDao,
) : AnalyticsRepository {


    override suspend fun getAnalytics(): AnalyticsHolder {


        return withContext(Dispatchers.IO) {

            val totalDistance = async { anaylitcsDao.getTotalDistance() }.await()
            val totalTimeMillis = async { anaylitcsDao.getTotalTimeRun() }.await()
            val maxRunSpeed = async { anaylitcsDao.getMaxSpeed() }.await()
            val avgDistancePerRun = async { anaylitcsDao.getAvgDistancePerRun() }.await()
            val avgPacePerRun = async { anaylitcsDao.getAvgPacePerRun() }.await()

            AnalyticsHolder(
                totalDistanceRun = totalDistance,
                totalTimeRun = totalTimeMillis.milliseconds,
                fastestEverRun = maxRunSpeed,
                avgDistancePerRun = avgDistancePerRun,
                avgPacePerRun = avgPacePerRun

            )
        }
    }
}