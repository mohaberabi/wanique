package com.mohaberabi.run.presentation.activerun.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohaberabi.core.domain.model.RunData
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.presentation.ui.util.formatted
import com.mohaberabi.core.presentation.ui.util.toFormattedHeartRate
import com.mohaberabi.core.presentation.ui.util.toFormattedKm
import com.mohaberabi.core.presentation.ui.util.toFormattedPace
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


@Composable
fun RunDataCard(
    runData: RunData,
    elapsedTime: Duration,
    modifier: Modifier = Modifier,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {


        RunDataItem(
            title = "Duration",
            value = elapsedTime.formatted(),
            valueFontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RunDataItem(
                modifier = Modifier.defaultMinSize(minWidth = 75.dp),
                title = "Distance",
                value = (runData.distanceMetres / 100.0).toFormattedKm(),
            )
            RunDataItem(
                modifier = Modifier.defaultMinSize(minWidth = 75.dp),
                title = "Heart Rate",
                value = runData.hearRates.lastOrNull().toFormattedHeartRate()
            )
            RunDataItem(
                modifier = Modifier.defaultMinSize(minWidth = 75.dp),
                title = "Pace",
                value = elapsedTime.toFormattedPace(distanceKm = (runData.distanceMetres / 100.0)),
            )


        }
    }

}

@Preview
@Composable
fun RunDataCardPreview() {

    RuniqueTheme {
        RunDataCard(
            runData = RunData(
                pace = 3.minutes,
                distanceMetres = 3425
            ),
            elapsedTime = 10.minutes
        )
    }
}


@Composable
private fun RunDataItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 16.sp

) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = valueFontSize,
        )
    }
}