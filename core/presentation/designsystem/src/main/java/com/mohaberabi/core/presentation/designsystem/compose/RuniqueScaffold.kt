package com.mohaberabi.core.presentation.designsystem.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun RuniqueScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    withGradient: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {


    Scaffold(
        modifier = modifier,
        topBar = topAppBar,
        floatingActionButton = fab,
        floatingActionButtonPosition = FabPosition.Center,
    ) {

            padding ->
        if (withGradient) {

            GradientBg {
                content(padding)
            }
        } else {
            content(padding)

        }

    }
}


@Preview
@Composable
fun PreviewRuniqueScaffolld() {

}