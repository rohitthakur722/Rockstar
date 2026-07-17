package com.example.rockstar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.rockstar.data.preferences.ThemeMode

private val RockstarDarkColorScheme = darkColorScheme(
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

private val RockstarLightColorScheme = lightColorScheme(
    primary = RockstarAccent,
    onPrimary = RockstarOnAccent,
    secondary = RockstarAccent,
    onSecondary = RockstarOnAccent,
    background = RockstarLightBackground,
    onBackground = RockstarLightTextPrimary,
    surface = RockstarLightSurface,
    onSurface = RockstarLightTextPrimary,
    surfaceVariant = RockstarLightSurfaceElevated,
    onSurfaceVariant = RockstarLightTextSecondary,
    outline = RockstarLightOutline,
    error = RockstarError
)

@Composable
fun RockstarTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
    }
    MaterialTheme(
        colorScheme = if (darkTheme) RockstarDarkColorScheme else RockstarLightColorScheme,
        typography = Typography,
        shapes = RockstarShapes,
        content = content
    )
}
