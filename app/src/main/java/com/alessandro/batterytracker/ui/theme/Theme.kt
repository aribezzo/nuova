package com.alessandro.batterytracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = CyanAccent,
    secondary = PurpleAccent,
    tertiary = GreenAccent,
    background = BgDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDarkElevated,
    error = RedAccent,
    onPrimary = BgDark,
    onSecondary = BgDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun BatteryTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
