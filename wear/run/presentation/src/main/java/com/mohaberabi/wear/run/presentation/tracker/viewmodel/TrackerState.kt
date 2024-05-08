package com.mohaberabi.wear.run.presentation.tracker.viewmodel

import kotlin.time.Duration

data class TrackerState(
    val elapsedDuration: Duration = Duration.ZERO,
    val distanceMeters: Int = 0,
    val heartRate: Int = 0,
    val isTrackable: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val isConnectedPhoneIsNearby: Boolean = false,
    val isRunActive: Boolean = false,
    val canTrackHearRate: Boolean = false,
)
