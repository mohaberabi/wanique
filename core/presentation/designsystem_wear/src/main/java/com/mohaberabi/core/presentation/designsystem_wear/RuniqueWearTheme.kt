package com.mohaberabi.core.presentation.designsystem_wear

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography
import com.mohaberabi.core.presentation.designsystem.theme.DarkColorScheme
import com.mohaberabi.core.presentation.designsystem.theme.Poppins


private fun createWearColors(): ColorScheme {
    val phoneTheme = DarkColorScheme

    return ColorScheme(
        primary = phoneTheme.primary,
        primaryContainer = phoneTheme.primaryContainer,
        onPrimary = phoneTheme.onPrimary,
        onPrimaryContainer = phoneTheme.onPrimaryContainer,
        secondary = phoneTheme.secondary,
        onSecondary = phoneTheme.onSecondary,
        secondaryContainer = phoneTheme.secondaryContainer,
        onSecondaryContainer = phoneTheme.onSecondaryContainer,
        tertiary = phoneTheme.tertiary,
        onTertiary = phoneTheme.onTertiary,
        tertiaryContainer = phoneTheme.tertiaryContainer,
        onTertiaryContainer = phoneTheme.onTertiaryContainer,
        surface = phoneTheme.surface,
        onSurface = phoneTheme.onSurface,
        surfaceDim = phoneTheme.surfaceVariant,
        onSurfaceVariant = phoneTheme.onSurfaceVariant,
        background = phoneTheme.background,
        error = phoneTheme.error,
        onError = phoneTheme.onError,
        onBackground = phoneTheme.onBackground,

        )
}

private fun createTypography(): Typography {
    return androidx.wear.compose.material3.Typography(
        defaultFontFamily = Poppins,
    )
}

private val WearColors = createWearColors()
private val WearTypography = createTypography()

@Composable
fun RuniqueWearTheme(
    content: @Composable () -> Unit,
) {

    MaterialTheme(
        colorScheme = WearColors,
        typography = WearTypography
    ) {

        content()
    }
}