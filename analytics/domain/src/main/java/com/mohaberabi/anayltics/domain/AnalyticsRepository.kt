package com.mohaberabi.anayltics.domain

interface AnalyticsRepository {


    suspend fun getAnalytics(): AnalyticsHolder
}