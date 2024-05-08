package com.mohaberabi.wear.run.presentation.tracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.wear.run.domain.PhoneConnector
import com.mohaberabi.wear.run.domain.RunningTracker
import com.mohaberabi.wear.run.domain.repository.ExerciseTracker
import com.mohaberabi.wear.run.presentation.tracker.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration

class TrackerViewModel(

    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker,
) : ViewModel() {


    private val _event = Channel<TrackerEvent>()
    val event = _event.receiveAsFlow()


    var state by mutableStateOf(
        TrackerState(
            hasStartedRunning = ActiveRunService.isServiceActive.value,
            isRunActive = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            isTrackable = ActiveRunService.isServiceActive.value
        )
    )
        private set
    private val hasBodyPermissionAllowed = MutableStateFlow(false)

    private val isTracking = snapshotFlow {
        state.isRunActive &&
                state.isTrackable &&
                state.isConnectedPhoneIsNearby
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    init {


        phoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { node ->
                println("node ${node.displayName}")
                state = state.copy(isConnectedPhoneIsNearby = node.isNearby)
            }
            .combine(isTracking) { _, tracking ->
                if (!tracking) {
                    phoneConnector.sendActionToPhone(MessageAction.ConnectionRequest)
                }
            }
            .launchIn(viewModelScope)



        runningTracker.isTrackable
            .onEach { trackable ->
                state = state.copy(isTrackable = trackable)
            }.launchIn(viewModelScope)


        isTracking
            .onEach { tracking ->
                val result = when {
                    tracking && !state.hasStartedRunning -> exerciseTracker.startExercise()
                    tracking && state.hasStartedRunning -> exerciseTracker.resumeExercise()
                    !tracking && state.hasStartedRunning -> exerciseTracker.pauseExercise()
                    else -> AppResult.Done(Unit)
                }

                if (result is AppResult.Error) {

                    result.error.toUiText()?.let {
                        _event.send(TrackerEvent.Error(it))
                    }
                }
                if (tracking) {
                    state = state.copy(hasStartedRunning = true)
                }

                runningTracker.setIsTracking(tracking)
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            val isHeartRateSupported = exerciseTracker.isHearRateTrackingSupported()
            state = state.copy(canTrackHearRate = isHeartRateSupported)
        }

        runningTracker.heartRate.onEach { bpm ->
            state = state.copy(heartRate = bpm)
        }.launchIn(viewModelScope)
        runningTracker.distanceMeters.onEach { dist ->
            state = state.copy(distanceMeters = dist)
        }.launchIn(viewModelScope)
        runningTracker.elapsedTime.onEach { time ->
            state = state.copy(elapsedDuration = time)
        }.launchIn(viewModelScope)
        listenToIncomingPhoneActions()
    }

    fun onAction(
        action: TrackerAction,
        triggeredOnPhone: Boolean = false
    ) {
        if (!triggeredOnPhone) {
            sendActionToPhone(action)
        }
        when (action) {
            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodyPermissionAllowed.update { action.allowed }
                if (action.allowed) {
                    viewModelScope.launch {
                        val isExerciseTrackerSupported =
                            exerciseTracker.isHearRateTrackingSupported()
                        state = state.copy(
                            canTrackHearRate = isExerciseTrackerSupported
                        )
                    }


                }
            }

            TrackerAction.OnFinishRunClick -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    _event.send(TrackerEvent.RunDone)
                    state = state.copy(
                        elapsedDuration = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }

            TrackerAction.OnToggleRunClick -> {
                if (state.isTrackable) {
                    state = state.copy(
                        isRunActive = !state.isRunActive
                    )
                }
            }
        }
    }

    private fun sendActionToPhone(action: TrackerAction) {
        viewModelScope.launch {
            val toPhoneMessageAction: MessageAction? = when (action) {

                TrackerAction.OnFinishRunClick -> MessageAction.Finish
                TrackerAction.OnToggleRunClick ->
                    if (state.isRunActive)
                        MessageAction.Pause
                    else MessageAction.StartOrResume

                else -> null
            }

            toPhoneMessageAction?.let {
                val result = phoneConnector.sendActionToPhone(toPhoneMessageAction)
                if (result is AppResult.Error) {
                    println("Tracker error :${result.error}")
                }
            }
        }

    }


    private fun listenToIncomingPhoneActions() {
        phoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessageAction.Finish -> onAction(
                        TrackerAction.OnFinishRunClick,
                        true
                    )

                    MessageAction.Pause -> {
                        if (state.isTrackable) {
                            state = state.copy(isRunActive = false)
                        }
                    }

                    MessageAction.StartOrResume -> {
                        if (state.isTrackable) {
                            state = state.copy(isRunActive = true)
                        }
                    }

                    MessageAction.Trackable -> {
                        state = state.copy(isTrackable = true)
                    }

                    MessageAction.UnTrackable -> {
                        state = state.copy(isTrackable = false)

                    }

                    else -> Unit

                }
            }.launchIn(viewModelScope)
    }
}