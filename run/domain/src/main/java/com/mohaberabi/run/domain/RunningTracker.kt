package com.mohaberabi.run.domain

import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.domain.model.LocationTimestamp
import com.mohaberabi.core.domain.model.RunData
import com.mohaberabi.core.domain.utils.AppTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.nio.file.WatchEvent
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)

class RunningTracker(

    private val watchConnector: WatchConnector,
    private val locationObserver: LocationObserver,
    /**
     * [RunningTracker] it's a singleton class so imagine if the Android Os
     * starves for memory  and your app is on the recently used apps  so the Os may and it's strongly
     * for sure will happen it will kill the app so , when the user re opens the app
     * the backstack will be restored so user will return the last screen they were on it
     * but all the state will not be restored so all of this flows will be reset from the initial state
     * so we will use it in combination of the Foreground services running so if we used [viewModelScope] when
     * the process is killed the viewmodel will also be killed  but we have the process in the foreground so
     * that's why we are really using [applicationScope]
     */
    private val applicationScope: CoroutineScope
) {


    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()


    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()


    private val isObservingLocation = MutableStateFlow(false)

    private val heartRates =
        _isTracking
            .flatMapLatest { tracking ->
                if (tracking) {
                    watchConnector.messagingActions
                } else flowOf()
            }
            .filterIsInstance<MessageAction.HearRateUpdate>()
            .map { it.heartRate }
            .runningFold(initial = emptyList<Int>()) { curr, new ->
                curr + new
            }
            .stateIn(
                applicationScope,
                SharingStarted.Lazily,
                emptyList()
            )

    val currentLocation = isObservingLocation
        /**
         * trigggered whenver the [isObservingLocation]
         * the [flatMapLatest] job is easy if the condition is satsfied [isObserving]
         * so it mapps the latest value to another different flow : in this case [locationObserver] observeLocation
         * so if not it switches to an empty flow
         */
        .flatMapLatest { isObserving ->
            if (isObserving) {
                locationObserver.observeLocation(1000L)

            } else {
                flowOf()
            }

        }.stateIn(
            /**
             * [Dispatchers.Main] we don't have any control of so that's why it was commented  to use a custom dispatcher that we
             * can really make use of it so thats why we did replace it with our injected [applicationScope]
             * why ? so that you can control it and make any use of it
             */
            applicationScope,
//            CoroutineScope(Dispatchers.Main),
            // it will not be launched until it's being really called
            SharingStarted.Lazily,
            null
        )


    init {
        _isTracking.onEach { tracking ->

            if (!tracking) {
                val newList = buildList {
                    addAll(runData.value.locations)
                    add(emptyList<LocationTimestamp>())
                }.toList()
                _runData.update {
                    it.copy(
                        locations = newList
                    )
                }
            }

        }.flatMapLatest {

                isTracking ->
            if (isTracking) {
                AppTimer.timeAndEmit()
            } else {
                flowOf()
            }

        }.onEach {
            _elapsedTime.value += it
        }.launchIn(applicationScope)



        currentLocation.filterNotNull()
            /**
             * [combineTransform] allows to make use of the emit function of the flow collector
             */
            .combineTransform(_isTracking) {


                    location, isTracking ->

                if (isTracking) {
                    emit(location)
                }

            }

            /**
             * for example if u tried to listen on this flow [onEach] or collecting it with any method
             * you will get a new values if and only if the [currentLocation] is not null and your are really [_isTracking]
             */


            /**
             *
             * [zip] this function works same as the combing but with a little small difference
             * in the combine you recieve an emitition
             * in case of any of the flows combined changed
             * bu the zip needs all flows to change in order to emit a new values
             *
             */
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }.combine(heartRates) { location, heartRatesList ->
                _runData.update {
                    val currentLocations = _runData.value.locations
                    val lastLocationsList = if (currentLocations.isNotEmpty()) {
                        currentLocations.last() + location
                    } else {
                        listOf(location)
                    }

                    val newLocations = currentLocations.replaceLast(lastLocationsList)

                    val totalDistancesMeteres = LocationDataCalc.getTotalDistanceInMeteres(
                        newLocations
                    )

                    val distanceKm = totalDistancesMeteres / 1000.0
                    val currentDuration = location.durationTimestamp
                    val avgSecondsPerKm =
                        if (distanceKm == 0.0) 0 else {
                            (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                        }
                    RunData(
                        distanceMetres = totalDistancesMeteres,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocations,
                        hearRates = heartRatesList,
                    )
                }
            }.launchIn(applicationScope)



        elapsedTime
            .onEach { duration ->
                watchConnector.sendActionToWatch(MessageAction.TimeUpdate(duration))
            }.launchIn(applicationScope)

        _runData
            .map { it.distanceMetres }
            .distinctUntilChanged()
            .onEach { dist -> watchConnector.sendActionToWatch(MessageAction.DistanceUpdate(dist)) }
            .launchIn(applicationScope)
    }


    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
        watchConnector.setIsTrackable(true)
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
        watchConnector.setIsTrackable(false)
    }

    fun finishRun() {

        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        _runData.value = RunData()
    }

}


private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {

    return if (this.isEmpty()) {
        listOf(replacement)
    } else {
        this.dropLast(1) + listOf(replacement)
    }

}