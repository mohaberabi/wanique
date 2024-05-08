package com.mohaberabi.wear.run.domain.repository

import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.error.AppError
import kotlinx.coroutines.flow.Flow

interface ExerciseTracker {


    /**
     * current heart rate to be observed in realtime as long as hardware emits a new value of heart beat
     *
     */
    val hearRate: Flow<Int>

    /**
     * a [suspend] function which indicates if the device has heart rate tracking supported by device
     * as well as user allowed the permission to track the hear rate
     *
     */
    suspend fun isHearRateTrackingSupported(): Boolean

    /**
     * tell ths Operating-System System we are about to start a exercise to make the sensors ready
     * but this can fail.. why ?
     * because the os might be now allowing other app to track the hear rate
     * which is not allowed for multiple apps to track the heart rates of the user at same time
     * or also the sensors are not yet ready so the Operating-System needs to be notified
     * that we are attempting to track an exercise now
     */

    suspend fun prepareExercise(): EmptyDataResult<ExerciseError>


    suspend fun startExercise(): EmptyDataResult<ExerciseError>
    suspend fun resumeExercise(): EmptyDataResult<ExerciseError>
    suspend fun pauseExercise(): EmptyDataResult<ExerciseError>
    suspend fun stopExercise(): EmptyDataResult<ExerciseError>

}


enum class ExerciseError : AppError {
    /**
     * this will be returned if the device has no  support for exercise tracking
     */
    TRACKING_NOT_SUPPORTED,

    /**
     * this will be returned if owr own app is already has an ongoing tracking of the user exercise
     */
    ONGOING_OWN_EXERCISE,

    /**
     * this will be returned if there is another app is already tracking the exercise now
     */
    ONGOING_OTHER_EXERCISE,

    /**
     * this will be returned if the exercise was already ended
     */
    EXERCISE_ALREADY_ENDED,

    /**
     * this will be returned if the exercise was already ended
     */
    UNKNOWN
}