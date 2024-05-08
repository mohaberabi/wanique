package com.mohaberabi.core.presentation.designsystem.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mohaberabi.core.presentation.designsystem.theme.RunIcon
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme


@Composable
fun RuniqueFab(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDesc: String? = null,
    iconSize: Dp = 25.dp
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            .clickable {
                onClick()
            },
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .padding(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)

        ) {

            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(iconSize)
            )
        }
    }

}

@Preview
@Composable
fun PreviewFab() {
    RuniqueTheme {
        RuniqueFab(
            icon = RunIcon,
            onClick = {},
        )
    }
}