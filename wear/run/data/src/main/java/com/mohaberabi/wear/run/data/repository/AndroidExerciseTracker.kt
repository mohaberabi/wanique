package com.mohaberabi.wear.run.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesException
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.wear.run.domain.repository.ExerciseError
import com.mohaberabi.wear.run.domain.repository.ExerciseTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class AndroidExerciseTracker(
    private val context: Context,
) : ExerciseTracker {


    private val client = HealthServices.getClient(context).exerciseClient


    override val hearRate: Flow<Int>
        get() = callbackFlow {
            val callback = object : ExerciseUpdateCallback {
                /**
                 * we already checking this from the app so just return [Unit]
                 */
                override fun onAvailabilityChanged(
                    dataType: DataType<*, *>,
                    availability: Availability
                ) = Unit

                override fun onExerciseUpdateReceived(
                    update:
                    ExerciseUpdate
                ) {
                    val heartRates = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                    val currentHearRate = heartRates.firstOrNull()?.value

                    currentHearRate?.let {
                        trySend(currentHearRate.roundToInt())
                    }
                }

                /**
                 * if it is a race with laps and we count the laps
                 * we do not need it
                 */
                override fun onLapSummaryReceived(
                    lapSummary:
                    ExerciseLapSummary
                ) = Unit

                override fun onRegistered() = Unit

                override fun onRegistrationFailed(
                    throwable:
                    Throwable
                ) = throwable.printStackTrace()

            }
            client.setUpdateCallback(callback)
            awaitClose {
                runBlocking {
                    client.clearUpdateCallback(callback)
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun isHearRateTrackingSupported(): Boolean {


        return hasBodySensorsPermissions() && runCatching {
            /**
             * get the [capabilities] from the [client ]
             */
            val capabilities = client.getCapabilities()

            /**
             * get the [supportedCapabilities] from the [capabilities ]
             * then lookup [ExerciseType.RUNNING] in the set
             * if it is [null] we assign an empty set to it
             */
            val supportedCapabilities = capabilities
                .typeToCapabilities[ExerciseType.RUNNING]
                ?.supportedDataTypes ?: setOf()


            /**
             * if the [HEART_RATE_BPM] is inside the [supportedCapabilities]
             * then the [runCatching]block returns[true] if notthing goes wrong
             */
            DataType.HEART_RATE_BPM in supportedCapabilities
            /**
            getOrDefault gets the result or returns false in case if any errors
             */
        }.getOrDefault(false)
    }


    /**
     * as previously said that the Operating System need to warm up the hardware sensors first before
     * committing any new operations on it
     * so we use this method to warm up the sensors
     *
     */
    override suspend fun prepareExercise(): EmptyDataResult<ExerciseError> {

        /**
         * check if the device already has a [HearTracking] Supported
         * if not allowed then immediate returns an error with [TRACKING_NOT_SUPPORTED] from our [ExerciseError]
         */

        if (!isHearRateTrackingSupported()) {
            return AppResult.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        /**
         * check the current status of the sensors from our method [getActiveExerciseInfo]
         * if it is an [AppResult.Error] so also return an errror
         *
         */

        val result = getActiveExerciseInfo()
        if (result is AppResult.Error) {
            return result
        }


        /**
         * now we can really safely use the sensors
         * but we need to pass the [WarmUpConfig] to Operating System to warm up Sensors
         */
        val config = WarmUpConfig(
            exerciseType = ExerciseType.RUNNING,
            dataTypes = setOf(DataType.HEART_RATE_BPM)

        )

        client.prepareExercise(config)
        return AppResult.Done(Unit)
    }

    override suspend fun startExercise(): EmptyDataResult<ExerciseError> {

        if (!hasBodySensorsPermissions()) {
            return AppResult.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        val currentExerciseStatus = getActiveExerciseInfo()
        if (currentExerciseStatus is AppResult.Error) {
            return currentExerciseStatus
        }


        val config = ExerciseConfig.builder(ExerciseType.RUNNING)
            .setDataTypes(setOf(DataType.HEART_RATE_BPM))
            /**
             * handled by owr app not auto
             */
            .setIsAutoPauseAndResumeEnabled(false)
            .build()

        client.startExercise(config)
        return AppResult.Done(Unit)
    }

    override suspend fun resumeExercise(): EmptyDataResult<ExerciseError> {
        if (!hasBodySensorsPermissions()) {
            return AppResult.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val currentExerciseInfo = getActiveExerciseInfo()

        /**
         * as the name says [resumeExercise] so the exercise needs and must be paused
         * not only paused it needs to be paused from our own app
         *  so we need to check if the error returned was [ONGOING_OTHER_EXERCISE] so we can not
         *  ever resume it as it is running on another app not of owr own
         *  NOTICE: Hardware resources is shared among all android apps and components , think of
         *  as if it is a SINGLETON
         */
        if (currentExerciseInfo is AppResult.Error
            && currentExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE
        ) {
            return currentExerciseInfo
        }

        return try {
            client.resumeExercise()
            AppResult.Done(Unit)
        } catch (e: HealthServicesException) {
            AppResult.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }


    }

    override suspend fun pauseExercise(): EmptyDataResult<ExerciseError> {
        if (!hasBodySensorsPermissions()) {
            return AppResult.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val currentExerciseInfo = getActiveExerciseInfo()

        /**
         * as the name says [pauseExercise] so the exercise needs and must be ongoing and from our app
         * it is the same impl here as [resumeExercise]

         */


        if (currentExerciseInfo is AppResult.Error
            && currentExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE
        ) {
            return currentExerciseInfo
        }

        return try {
            client.pauseExercise()
            AppResult.Done(Unit)
        } catch (e: HealthServicesException) {
            AppResult.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }

    }

    override suspend fun stopExercise(): EmptyDataResult<ExerciseError> {
        if (!hasBodySensorsPermissions()) {
            return AppResult.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val currentExerciseInfo = getActiveExerciseInfo()

        if (currentExerciseInfo is AppResult.Error
            && currentExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE
        ) {
            return currentExerciseInfo
        }

        return try {
            client.endExercise()
            AppResult.Done(Unit)
        } catch (e: HealthServicesException) {
            AppResult.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }


    private fun hasBodySensorsPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS,
        ) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("RestrictedApi")
    private suspend fun getActiveExerciseInfo(): EmptyDataResult<ExerciseError> {
        val info = client.getCurrentExerciseInfo()
        return when (info.exerciseTrackedStatus) {


            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> AppResult.Done(Unit)
            ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> AppResult.Error(ExerciseError.ONGOING_OWN_EXERCISE)
            ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> AppResult.Error(ExerciseError.ONGOING_OTHER_EXERCISE)
            else -> AppResult.Error(ExerciseError.UNKNOWN)

        }
    }

}