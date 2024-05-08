package com.mohaberabi.run.presentation.activerun.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.presentation.ui.util.asUiText
import com.mohaberabi.run.domain.RunningTracker
import com.mohaberabi.run.domain.WatchConnector
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.run.domain.LocationDataCalc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope,
) : ViewModel() {


    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            hasStarted = ActiveRunService.isServiceActive.value
        )
    )
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val event = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)
    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(currentLocation = it?.latLng)
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .onEach {
                state = state.copy(runData = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }
            .launchIn(viewModelScope)

        listenToWatchAction()
    }

    fun onAction(
        action: ActiveRunAction,
        triggeredOnWatch: Boolean = false
    ) {
        if (!triggeredOnWatch) {

            val messagingAction: MessageAction? = when (action) {
                ActiveRunAction.OnFinishRunClick -> MessageAction.Finish
                ActiveRunAction.OnResumeRunClick -> MessageAction.StartOrResume
                ActiveRunAction.OnToggleRunClick -> {
                    if (state.hasStarted) MessageAction.Pause else MessageAction.StartOrResume
                }

                else -> null

            }
            messagingAction?.let {
                viewModelScope.launch {
                    watchConnector.sendActionToWatch(messagingAction)
                }
            }
        }
        when (action) {

            is ActiveRunAction.OnRunDone -> {
                finishRun(action.mapPictureByteArray)
            }

            is ActiveRunAction.OnFinishRunClick -> {

                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }

            is ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(shouldTrack = true)
            }

            is ActiveRunAction.OnBackClick -> {
                state = state.copy(shouldTrack = false)
            }

            is ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStarted = true,
                    shouldTrack = !state.shouldTrack
                )
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocation
                state = state.copy(
                    showLocationRational = action.showRational
                )
            }

            is ActiveRunAction.SubmitNotificationInfo -> {
                state = state.copy(
                    showNotificationRational = action.showRational
                )
            }

            is ActiveRunAction.DismissRationalDialog -> {
                state = state.copy(
                    showNotificationRational = false,
                    showLocationRational = false
                )
            }

        }
    }

    private fun finishRun(mapPictureByteArray: ByteArray) {

        val locations = state.runData.locations
        if (locations.isEmpty() || locations.first().size <= 1) {
            state = state.copy(isSavingRun = false)
            return
        }
        runningTracker.finishRun()
        viewModelScope.launch {

            when (val result = runRepository.upsertRun(
                RunModel(
                    id = null,
                    duration = state.elapsedTime,
                    dateTimeUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")),
                    distanceMeters = state.runData.distanceMetres,
                    location = state.currentLocation ?: AppLatLng(0.0, 0.0),
                    maxSpeedKmh = LocationDataCalc.getMaxSpeedKm(state.runData.locations),
                    totalElevationMeters = LocationDataCalc.getTotalEleveationsMeters(state.runData.locations),
                    mapPictureUrl = null,
                    avgHeartRate = if (state.runData.hearRates.isEmpty()) null else state.runData.hearRates.average()
                        .roundToInt(),
                    maxHeartRate = if (state.runData.hearRates.isEmpty()) null else state.runData.hearRates.max()
                ), mapPictureByteArray
            )) {
                is AppResult.Error -> eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                is AppResult.Done -> eventChannel.send(ActiveRunEvent.RunSaved)
            }

            state = state.copy(
                isSavingRun = false
            )
        }
    }


    private fun listenToWatchAction() {
        watchConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessageAction.ConnectionRequest -> {
                        if (isTracking.value) {
                            watchConnector.sendActionToWatch(MessageAction.StartOrResume)
                        }

                    }

                    MessageAction.Finish -> onAction(
                        ActiveRunAction.OnFinishRunClick,
                        true
                    )

                    MessageAction.Pause -> {
                        if (isTracking.value) {
                            onAction(ActiveRunAction.OnToggleRunClick, true)
                        }
                    }

                    MessageAction.StartOrResume -> {
                        if (!isTracking.value) {
                            if (state.hasStarted) {
                                onAction(ActiveRunAction.OnResumeRunClick, true)
                            }
                        } else {
                            onAction(ActiveRunAction.OnToggleRunClick, true)

                        }
                    }

                    else -> Unit
                }
            }.launchIn(viewModelScope)
    }


    override fun onCleared() {
        super.onCleared()
        if (ActiveRunService.isServiceActive.value) {
            applicationScope.launch {
                watchConnector.sendActionToWatch(MessageAction.UnTrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }
}