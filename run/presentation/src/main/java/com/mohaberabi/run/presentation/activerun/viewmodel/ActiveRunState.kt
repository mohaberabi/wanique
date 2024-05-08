package com.mohaberabi.run.presentation.activerun.viewmodel

import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.domain.model.RunData
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.run.domain.LocationDataCalc
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration

data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO,
    val shouldTrack: Boolean = false,
    val hasStarted: Boolean = false,
    val currentLocation: AppLatLng? = null,
    val isRunFinished: Boolean = false,
    val isSavingRun: Boolean = false,
    val runData: RunData = RunData(),
    val showLocationRational: Boolean = false,
    val showNotificationRational: Boolean = false,
)
