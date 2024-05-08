package com.mohaberabi.run.presentation.maps.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.mohaberabi.core.domain.model.AppLatLng
import com.mohaberabi.core.presentation.designsystem.theme.CalendarIcon
import com.mohaberabi.core.presentation.designsystem.theme.RunOutlinedIcon
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.run.presentation.R
import com.mohaberabi.run.presentation.overview.mapper.toRunModelUi
import com.mohaberabi.run.presentation.overview.util.RunModelUi
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunListItem(
    runUi: RunModelUi,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    var showDropDown by remember {

        mutableStateOf(false)
    }

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        showDropDown = true
                    }
                )
        ) {

            MapImage(runUi.mapPictureUrl)



            RunningTimeSection(
                duration = runUi.duration,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            RunningDateSection(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp), dateTime = runUi.dateTime
            )
            DataGrid(
                run = runUi,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = {
                showDropDown = false
            },
        ) {


            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.delete)) },
                onClick = {
                    onDeleteClick()
                    showDropDown = false
                },
            )

        }
    }


}


@Composable
private fun RunningDateSection(
    modifier: Modifier,
    dateTime: String
) {


    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = dateTime, color = MaterialTheme.colorScheme.onSurface)
    }

}

@Composable
fun RunningTimeSection(
    modifier: Modifier = Modifier,
    duration: String,
) {

    Row(
        modifier = modifier.padding(12.dp)
    ) {


        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )


        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun MapImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(15.dp)),
        model = url,
        contentDescription = stringResource(R.string.running_location_image),


        loading = {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center,
            ) {

                Text(text = stringResource(R.string.error_loading_image))
            }
        }
    )


}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DataGrid(
    run: RunModelUi,
    modifier: Modifier = Modifier,
) {
    val runDataUiList = listOf(
        stringResource(R.string.distance) to run.distance,
        stringResource(R.string.pace) to run.pace,
        stringResource(R.string.avg_speed) to run.avgSpeed,
        stringResource(R.string.max_speed) to run.maxSpeed,
        stringResource(R.string.elevation) to run.totalElevation,
        stringResource(R.string.avg_heart_rate) to run.avgHeartRate,
        stringResource(R.string.max_heart_rate) to run.maxHeartRate,
    )

    var maxWidth by remember {

        mutableIntStateOf(0)
    }

    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        runDataUiList.forEach { pair ->
            DataGridCell(
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .padding(12.dp)
                    .onSizeChanged {
                        maxWidth = Math.max(maxWidth, it.width)
                    },
                title = pair.first,
                subtitle = pair.second
            )
        }
    }
}


@Composable
private fun DataGridCell(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
fun PreviewRunListItem() {


    RuniqueTheme {

        RunListItem(runUi = RunModel(
            id = "1",
            duration = 10.minutes + 30.seconds,
            dateTimeUtc = ZonedDateTime.now(),
            location = AppLatLng(30.00, 31.00),
            maxSpeedKmh = 15.6,
            totalElevationMeters = 123,
            mapPictureUrl = null,
            distanceMeters = 200,
            avgHeartRate = 0,
            maxHeartRate = 0,
        ).toRunModelUi(), onDeleteClick = { /*TODO*/ })
    }
}
