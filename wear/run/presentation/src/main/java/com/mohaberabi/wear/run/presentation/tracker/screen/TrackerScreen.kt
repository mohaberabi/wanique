package com.mohaberabi.wear.run.presentation.tracker.screen

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mohaberabi.core.presentation.designsystem_wear.RuniqueWearTheme
import com.mohaberabi.wear.run.presentation.tracker.viewmodel.TrackerAction
import com.mohaberabi.wear.run.presentation.tracker.viewmodel.TrackerState
import com.mohaberabi.wear.run.presentation.tracker.viewmodel.TrackerViewModel
import org.koin.androidx.compose.koinViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedIconButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.core.presentation.designsystem.theme.ExclamationMarkIcon
import com.mohaberabi.core.presentation.designsystem.theme.FinishIcon
import com.mohaberabi.core.presentation.designsystem.theme.PauseIcon
import com.mohaberabi.core.presentation.designsystem.theme.StartIcon
import com.mohaberabi.core.presentation.ui.compose.ObserveAsEvent
import com.mohaberabi.core.presentation.ui.util.formatted
import com.mohaberabi.core.presentation.ui.util.toFormattedHeartRate
import com.mohaberabi.core.presentation.ui.util.toFormattedKm
import com.mohaberabi.wear.run.presentation.R
import com.mohaberabi.wear.run.presentation.tracker.compose.RunDataCard
import com.mohaberabi.wear.run.presentation.tracker.util.hasBodyPermissions
import com.mohaberabi.wear.run.presentation.tracker.util.hasNotificationsPermissions
import com.mohaberabi.wear.run.presentation.tracker.util.requiresNotificaitonPermisison
import com.mohaberabi.wear.run.presentation.tracker.viewmodel.TrackerEvent

@Composable
fun TrackerScreenRoot(
    trackerViewModel: TrackerViewModel = koinViewModel(),
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
) {

    val context = LocalContext.current
    val state = trackerViewModel.state
    val isServicesActive = ActiveRunService.isServiceActive.collectAsStateWithLifecycle().value
    LaunchedEffect(
        state.isRunActive,
        state.hasStartedRunning,
        isServicesActive
    ) {

        if (state.isRunActive && !isServicesActive) {
            onServiceToggle(true)
        }

    }
    ObserveAsEvent(flow = trackerViewModel.event) { event ->
        when (event) {
            is TrackerEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

            is TrackerEvent.RunDone -> onServiceToggle(false)
        }
    }
    TrackerScreen(
        state = trackerViewModel.state,
        onAction = trackerViewModel::onAction,
    )
}

@Composable
fun TrackerScreen(
    state: TrackerState,
    onAction: (TrackerAction) -> Unit,
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val allowedBodyPermission = perms[Manifest.permission.BODY_SENSORS] == true
            onAction(TrackerAction.OnBodySensorPermissionResult(allowedBodyPermission))
        }
    )
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {

        val hasBodyPermissions = context.hasBodyPermissions()

        val hasNotificationsPermissions = context.hasNotificationsPermissions()
        onAction(TrackerAction.OnBodySensorPermissionResult(hasBodyPermissions))

        val permissionsRequests = mutableListOf<String>()
        if (!hasBodyPermissions) {
            permissionsRequests.add(Manifest.permission.BODY_SENSORS)
        }
        if (!hasNotificationsPermissions && requiresNotificaitonPermisison()) {
            permissionsRequests.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionLauncher.launch(permissionsRequests.toTypedArray())
    }

    if (state.isConnectedPhoneIsNearby) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                RunDataCard(
                    title = stringResource(id = R.string.heart_rate),
                    value = if (state.canTrackHearRate) {
                        state.heartRate.toFormattedHeartRate()
                    } else {
                        stringResource(id = R.string.unsupported)
                    },
                    valueTextColor = if (state.canTrackHearRate) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RunDataCard(
                    title = stringResource(id = R.string.distance),
                    value = (state.distanceMeters / 1000.0).toFormattedKm(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.elapsedDuration.formatted(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isTrackable) {
                    ToggleRunButton(
                        isRunActive = state.isRunActive,
                        onClick = {
                            onAction(TrackerAction.OnToggleRunClick)
                        }
                    )
                    if (!state.isRunActive && state.hasStartedRunning) {
                        FilledTonalIconButton(
                            onClick = {
                                onAction(TrackerAction.OnFinishRunClick)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Icon(
                                imageVector = FinishIcon,
                                contentDescription = stringResource(id = R.string.finish_run)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.open_active_run_screen),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ExclamationMarkIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.connect_your_phone),
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun ToggleRunButton(
    isRunActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        if (isRunActive) {
            Icon(
                imageVector = PauseIcon,
                contentDescription = stringResource(id = R.string.pause_run),
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Icon(
                imageVector = StartIcon,
                contentDescription = stringResource(id = R.string.start_run),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun PreviewTrackerScreen() {

    RuniqueWearTheme {

        TrackerScreen(
            state = TrackerState(
                isConnectedPhoneIsNearby = true,
                isRunActive = false,
                isTrackable = true,
                hasStartedRunning = true,
                canTrackHearRate = true,
                heartRate = 150
            ),
            onAction = {},
        )
    }
}