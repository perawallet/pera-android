package com.algorand.android.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val lightScheme = lightColorScheme(
    primary = PeraColor.primaryLight,
    onPrimary = PeraColor.onPrimaryLight,
    primaryContainer = PeraColor.primaryContainerLight,
    onPrimaryContainer = PeraColor.onPrimaryContainerLight,
    secondary = PeraColor.secondaryLight,
    onSecondary = PeraColor.onSecondaryLight,
    secondaryContainer = PeraColor.secondaryContainerLight,
    onSecondaryContainer = PeraColor.onSecondaryContainerLight,
    tertiary = PeraColor.tertiaryLight,
    onTertiary = PeraColor.onTertiaryLight,
    tertiaryContainer = PeraColor.tertiaryContainerLight,
    onTertiaryContainer = PeraColor.onTertiaryContainerLight,
    error = PeraColor.errorLight,
    onError = PeraColor.onErrorLight,
    errorContainer = PeraColor.errorContainerLight,
    onErrorContainer = PeraColor.onErrorContainerLight,
    background = PeraColor.backgroundLight,
    onBackground = PeraColor.onBackgroundLight,
    surface = PeraColor.surfaceLight,
    onSurface = PeraColor.onSurfaceLight,
    surfaceVariant = PeraColor.surfaceVariantLight,
    onSurfaceVariant = PeraColor.onSurfaceVariantLight,
    outline = PeraColor.outlineLight,
    outlineVariant = PeraColor.outlineVariantLight,
    scrim = PeraColor.scrimLight,
    inverseSurface = PeraColor.inverseSurfaceLight,
    inverseOnSurface = PeraColor.inverseOnSurfaceLight,
    inversePrimary = PeraColor.inversePrimaryLight,
    surfaceDim = PeraColor.surfaceDimLight,
    surfaceBright = PeraColor.surfaceBrightLight,
    surfaceContainerLowest = PeraColor.surfaceContainerLowestLight,
    surfaceContainerLow = PeraColor.surfaceContainerLowLight,
    surfaceContainer = PeraColor.surfaceContainerLight,
    surfaceContainerHigh = PeraColor.surfaceContainerHighLight,
    surfaceContainerHighest = PeraColor.surfaceContainerHighestLight
)

private val darkScheme = darkColorScheme(
    primary = PeraColor.primaryDark,
    onPrimary = PeraColor.onPrimaryDark,
    primaryContainer = PeraColor.primaryContainerDark,
    onPrimaryContainer = PeraColor.onPrimaryContainerDark,
    secondary = PeraColor.secondaryDark,
    onSecondary = PeraColor.onSecondaryDark,
    secondaryContainer = PeraColor.secondaryContainerDark,
    onSecondaryContainer = PeraColor.onSecondaryContainerDark,
    tertiary = PeraColor.tertiaryDark,
    onTertiary = PeraColor.onTertiaryDark,
    tertiaryContainer = PeraColor.tertiaryContainerDark,
    onTertiaryContainer = PeraColor.onTertiaryContainerDark,
    error = PeraColor.errorDark,
    onError = PeraColor.onErrorDark,
    errorContainer = PeraColor.errorContainerDark,
    onErrorContainer = PeraColor.onErrorContainerDark,
    background = PeraColor.backgroundDark,
    onBackground = PeraColor.onBackgroundDark,
    surface = PeraColor.surfaceDark,
    onSurface = PeraColor.onSurfaceDark,
    surfaceVariant = PeraColor.surfaceVariantDark,
    onSurfaceVariant = PeraColor.onSurfaceVariantDark,
    outline = PeraColor.outlineDark,
    outlineVariant = PeraColor.outlineVariantDark,
    scrim = PeraColor.scrimDark,
    inverseSurface = PeraColor.inverseSurfaceDark,
    inverseOnSurface = PeraColor.inverseOnSurfaceDark,
    inversePrimary = PeraColor.inversePrimaryDark,
    surfaceDim = PeraColor.surfaceDimDark,
    surfaceBright = PeraColor.surfaceBrightDark,
    surfaceContainerLowest = PeraColor.surfaceContainerLowestDark,
    surfaceContainerLow = PeraColor.surfaceContainerLowDark,
    surfaceContainer = PeraColor.surfaceContainerDark,
    surfaceContainerHigh = PeraColor.surfaceContainerHighDark,
    surfaceContainerHighest = PeraColor.surfaceContainerHighestDark
)

@Composable
fun PeraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
