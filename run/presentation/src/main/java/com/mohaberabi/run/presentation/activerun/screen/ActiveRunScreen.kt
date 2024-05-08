package com.mohaberabi.run.presentation.activerun.screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueButton
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueDialog
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueFab
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueOutlinedButton
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueScaffold
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueToolbar
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.presentation.designsystem.theme.StartIcon
import com.mohaberabi.core.presentation.designsystem.theme.StopIcon
import com.mohaberabi.core.presentation.ui.compose.ObserveAsEvent
import com.mohaberabi.run.presentation.R
import com.mohaberabi.run.presentation.activerun.compose.RunDataCard
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.run.presentation.activerun.viewmodel.ActiveRunAction
import com.mohaberabi.run.presentation.activerun.viewmodel.ActiveRunEvent
import com.mohaberabi.run.presentation.activerun.viewmodel.ActiveRunState
import com.mohaberabi.run.presentation.activerun.viewmodel.ActiveRunViewModel
import com.mohaberabi.run.presentation.maps.compose.TrackerMap
import com.mohaberabi.run.presentation.util.hasLocationPermission
import com.mohaberabi.run.presentation.util.hasNotificationPermission
import com.mohaberabi.run.presentation.util.requiresNotificationPermission
import com.mohaberabi.run.presentation.util.shouldShowLocationPermissionRationale
import com.mohaberabi.run.presentation.util.shouldShowNotificationRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream


@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel(),
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onRunDone: () -> Unit,
    onBack: () -> Unit,

    ) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.event) { event ->
        when (event) {
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ActiveRunEvent.RunSaved -> {
                onRunDone()
            }
        }
    }

    val state = viewModel.state
    ActiveRunScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ActiveRunAction.OnBackClick -> {
                    if (!viewModel.state.hasStarted) {
                        onBack()
                    }
                }

                else -> Unit
            }

            viewModel.onAction(action)
        },
        onServiceToggle = onServiceToggle,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRunScreen(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit,
) {

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val hasAcceptedCourse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            val hasAcceptedFine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val hasNotificationAllowed = if (requiresNotificationPermission()) {
                perms[Manifest.permission.POST_NOTIFICATIONS] == true
            } else true

            val activity = context as ComponentActivity
            val showLocationRational = activity.shouldShowLocationPermissionRationale()
            val showNotificationRational = activity.shouldShowNotificationRationale()
            onAction(
                ActiveRunAction.SubmitLocationPermissionInfo(
                    acceptedLocation = hasAcceptedCourse && hasAcceptedFine,
                    showRational = showLocationRational,
                )
            )

            onAction(
                ActiveRunAction.SubmitNotificationInfo(
                    acceptedNotification = hasNotificationAllowed,
                    showRational = showNotificationRational,
                )
            )
        }
    )


    LaunchedEffect(key1 = true) {

        val activity = context as ComponentActivity
        val showLocationRational = activity.shouldShowLocationPermissionRationale()
        val showNotifiactionRational = activity.shouldShowNotificationRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocation = context.hasLocationPermission(),
                showRational = showLocationRational,
            )
        )

        onAction(
            ActiveRunAction.SubmitNotificationInfo(
                acceptedNotification = context.hasNotificationPermission(),
                showRational = showNotifiactionRational,
            )
        )

        if (!showLocationRational && !showNotifiactionRational) {
            permissionLauncher.requestRuniquePermission(context)
        }
    }



    LaunchedEffect(key1 = state.isRunFinished) {

        if (state.isRunFinished) {
            onServiceToggle(false)
        }
    }

    val isServiceActive = ActiveRunService.isServiceActive.collectAsStateWithLifecycle().value
    LaunchedEffect(key1 = state.shouldTrack, isServiceActive) {
        if (context.hasLocationPermission()
            && state.shouldTrack &&
            !isServiceActive
        ) {
            onServiceToggle(true)
        }
    }
    RuniqueScaffold(
        fab = {
            RuniqueFab(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                iconSize = 20.dp,
                contentDesc = if (state.shouldTrack) stringResource(R.string.pause)
                else stringResource(
                    R.string.start
                ),
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
            )
        },
        topAppBar = {
            RuniqueToolbar(
                showBackButton = true,
                title = stringResource(R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                }
            ) {

            }
        },
        withGradient = false,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TrackerMap(
                locations = state.runData.locations,
                onSnapShot = { bitmap ->

                    /**
                     * use is used to close the stream when it's done of executing
                     */
                    val stream = ByteArrayOutputStream()
                    stream.use { byteArray ->
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            byteArray
                        )
                    }


                    onAction(ActiveRunAction.OnRunDone(stream.toByteArray()))
                },
                modifier = Modifier.fillMaxSize(),
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
            )
            RunDataCard(
                runData = state.runData,
                elapsedTime = state.elapsedTime,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }

    }

    if (!state.shouldTrack && state.hasStarted) {

        RuniqueDialog(

            title = stringResource(R.string.running_is_paused),
            description = stringResource(R.string.do_you_want_to_resume_or_finish),
            positive = {


                RuniqueButton(

                    label = stringResource(R.string.resume),
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            negative = {
                RuniqueOutlinedButton(
                    isLoading = state.isSavingRun,
                    label = stringResource(R.string.stop),
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

        )
    }
    if (state.showLocationRational || state.showNotificationRational) {

        RuniqueDialog(
            title = stringResource(R.string.permission_required),
            description = when {
                state.showLocationRational && state.showNotificationRational -> stringResource(R.string.noti_loc_permisison)
                state.showLocationRational -> stringResource(R.string.location_permission)
                else -> stringResource(R.string.notification_permission)
            },
            positive = {
                RuniqueButton(
                    onClick = {
                        onAction(ActiveRunAction.DismissRationalDialog)
                        permissionLauncher.requestRuniquePermission(context)
                    },
                    label = stringResource(R.string.okay)
                )
            },
            negative = {
                RuniqueOutlinedButton(
                    onClick = {},
                    label = ""
                )
            },
        )
    }
}


private fun ActivityResultLauncher<Array<String>>.requestRuniquePermission(
    context: Context
) {
    val hasLocation = context.hasLocationPermission()
    val hasNotification = context.hasNotificationPermission()
    val locationPermision = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    val notificationPermisison = if (requiresNotificationPermission()) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocation && !hasNotification -> launch(locationPermision + notificationPermisison)
        !hasNotification -> launch(notificationPermisison)
        !hasLocation -> launch(locationPermision)
    }
}


@Preview
@Composable
fun ActiveRunScreenPreview() {

    RuniqueTheme {

        ActiveRunScreen(
            onServiceToggle = {},
            onAction = {},
            state = ActiveRunState()
        )
    }
}