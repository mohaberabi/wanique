package com.mohaberabi.run.presentation.maps.compose

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.awaitSnapshot
import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.domain.model.LocationTimestamp
import com.mohaberabi.core.presentation.designsystem.theme.RunIcon
import com.mohaberabi.run.presentation.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackerMap(
    modifier: Modifier = Modifier,
    /** [isRunFinished] this will always be false as long as the user is running  , it's extremly usefull this variable
     * please continue reading the bottom comments to understand [MOHAB LOSER MOHAB LOSER]*/
    isRunFinished: Boolean = false,
    currentLocation: AppLatLng? = null,
    locations: List<List<LocationTimestamp>>,
    onSnapShot: (Bitmap) -> Unit,
) {

    val context = LocalContext.current

    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )
    val markerPositionLng by animateFloatAsState(
        targetValue = currentLocation?.lng?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    val markerPosition = remember {
        LatLng(markerPositionLat.toDouble(), markerPositionLng.toDouble())
    }


    /**
     * this corutine scope will be canceled and restarted again if [markerPositionLng] changed
     * or [isRunFinished] changed so when the coroutine is launched it will see if the [isRunFinished] is false
     * means that use is still running
     * if so, it will update the marker position with the current users position which is attached to [GoogleMap]
     */
    LaunchedEffect(
        markerPositionLng,
        isRunFinished
    ) {

        if (!isRunFinished) {
            markerState.position = markerPosition
        }

    }

    /**
     * this corutine scope will be canceled and restarted again if [currentLocation] changed
     * or [isRunFinished] changed so when the coroutine is launched it will see if the [isRunFinished] is false
     * means that use is still running and the [currentLocation]is not null , user is allowing the location
     * if so, it will update the camera position of the google maps to the current user pov location
     */
    LaunchedEffect(
        currentLocation,
        isRunFinished
    ) {

        if (currentLocation != null && !isRunFinished) {
            val latlng = LatLng(currentLocation.lat, currentLocation.lng)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    latlng,
                    17f
                )
            )
        }

    }


    /**
     * this [triggerCapture] boolean is created to state that we need to capture the screenshot of t
     * he map now when it is = [true]
     */
    var triggerCapture by remember {

        mutableStateOf(false)
    }


    /**
     * the coroutine job that will carry the map screenshot capture result
     */
    var createSnapshotJob: Job? = remember { null }


    GoogleMap(


        /**
         * if [isRunFinished] it means we need to take a screenshot of the map now in order to save it with
         * run model and take it's bitmap to store in the database attached with the run model
         * so we don't want to take the screenshot from the whole device size  we need to take it with a [16/9] ratio
         * so if the [isRunFinished] make it not visible and decrease it size to [16/9]
         * then make the [triggerCapture] ready to take the screenshot with  [16/9] ratio
         */
        modifier = if (isRunFinished) {
            modifier
                .width(300.dp)
                .aspectRatio(16 / 9f)
                .alpha(0f)
                .onSizeChanged {
                    if (it.width >= 300) {
                        triggerCapture = true
                    }
                }
        } else modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle


        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true
        )

    ) {


        /**
         * a courtine scope allowing to have an instacne to google map object
         * same as [LaunchedEffect] , this will be triggers if
         * [locations] [isRunFinished] [triggerCapture] [createSnapshotJob] changed
         * what will it do then ?
         * it will take the actual screenshot we want if the [isRunFinished] is true  [triggerCapture] = true
         * and [createSnapshotJob] = null
         * then stop [triggerCapture]  and flatten the list to get the locations instances not the iterable of <location>
         * then include in the google map screnshot and store the result of the [Job] in the created job of ours
         *
         */
        MapEffect(
            locations,
            isRunFinished,
            triggerCapture,
            createSnapshotJob,
        ) { map ->
            if (isRunFinished && triggerCapture && createSnapshotJob == null) {
                triggerCapture = false
                val boundsBuilder = LatLngBounds.Builder()
                locations.flatten().forEach { loc ->
                    boundsBuilder.include(
                        LatLng(
                            loc.location.latLng.lat,
                            loc.location.latLng.lng
                        )
                    )
                }

                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        100,
                    )
                )
                map.setOnCameraIdleListener {
                    createSnapshotJob?.cancel()
                    createSnapshotJob = GlobalScope.launch {
                        // make sure the map is sharp and focused before taking  the screenshot
                        delay(500L)
                        map.awaitSnapshot()?.let(onSnapShot)
                    }
                }

            }
        }

        RuniquePolyLine(locations = locations)

        if (!isRunFinished && currentLocation != null) {


            MarkerComposable(
                currentLocation, state = markerState,
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),

                    ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}