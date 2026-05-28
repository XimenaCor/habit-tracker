package com.habittracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple600,
    onPrimary = White,
    primaryContainer = Purple50,
    onPrimaryContainer = Purple900,
    background = White,
    onBackground = Purple900,
    surface = White,
    onSurface = Purple900,
    surfaceVariant = Purple50,
    onSurfaceVariant = Gray400,
    outline = Purple100
)

@Composable
fun HabitTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}