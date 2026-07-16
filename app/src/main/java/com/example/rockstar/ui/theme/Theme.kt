package com.example.rockstar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RockstarColorScheme = darkColorScheme(
    primary = RockstarAccent,
    onPrimary = RockstarOnAccent,
    secondary = RockstarAccent,
    onSecondary = RockstarOnAccent,
    background = RockstarBackground,
    onBackground = RockstarTextPrimary,
    surface = RockstarSurface,
    onSurface = RockstarTextPrimary,
    surfaceVariant = RockstarSurfaceElevated,
    onSurfaceVariant = RockstarTextSecondary,
    outline = RockstarOutline,
    error = RockstarError
)

/**
 * The Rockstar brand identity is a fixed near-black theme; it intentionally
 * does not follow the system light/dark setting or dynamic color.
 */
@Composable
fun RockstarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RockstarColorScheme,
        typography = Typography,
        shapes = RockstarShapes,
        content = content
    )
}
