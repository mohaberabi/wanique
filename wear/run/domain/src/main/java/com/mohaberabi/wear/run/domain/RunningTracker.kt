package com.mohaberabi.wear.run.domain

import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.wear.run.domain.repository.ExerciseTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(


    private val watchToPhoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope,
) {


    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()
    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()
    val distanceMeters = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessageAction.DistanceUpdate>()
        .map { it.distanceMeters }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            0
        )
    val elapsedTime = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessageAction.TimeUpdate>()
        .map { it.elapsedDuration }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            Duration.ZERO
        )

    init {
        watchToPhoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    is MessageAction.Trackable -> _isTrackable.update { true }
                    is MessageAction.UnTrackable -> _isTrackable.update { false }
                    else -> Unit
                }
            }.launchIn(applicationScope)


        watchToPhoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { node ->
                exerciseTracker.prepareExercise()

            }.launchIn(applicationScope)


        _isTracking
            .flatMapLatest { tracking ->
                if (tracking) {
                    exerciseTracker.hearRate
                } else {
                    flowOf()
                }
            }.onEach { bpm ->
                watchToPhoneConnector.sendActionToPhone(MessageAction.HearRateUpdate(bpm))
                _heartRate.update { bpm }
            }
            .launchIn(applicationScope)
    }


    fun setIsTracking(isTrackingNow: Boolean) = _isTracking.update { isTrackingNow }
}
