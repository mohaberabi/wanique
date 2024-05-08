package com.mohaberabi.core.presentation.designsystem.compose

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme


@Composable
fun GradientBg(
    modifier: Modifier = Modifier,
    hasToolBar: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.roundToPx()

    }


    val smallDimension = minOf(
        configuration.screenWidthDp.dp,
        configuration.screenHeightDp.dp
    )

    val smallDimensionPx = with(density) {
        smallDimension.roundToPx()
    }
    val primaryColor = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    val isAtLeast12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isAtLeast12) {
                        Modifier.blur(smallDimension / 3f)
                    } else Modifier
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isAtLeast12) primaryColor else primaryColor.copy(alpha = 0.3f),
                            bgColor
                        ),
                        center = Offset(x = (screenWidthPx / 2).toFloat(), y = -100f),
                        radius = smallDimensionPx / 2f
                    )
                )
        ) {

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (hasToolBar) {
                        Modifier
                    } else Modifier.systemBarsPadding()
                )
        ) {

            content()
        }
    }
}


@Preview
@Composable
fun PreviewGradientBg() {
    RuniqueTheme {

        GradientBg {

        }
    }
}