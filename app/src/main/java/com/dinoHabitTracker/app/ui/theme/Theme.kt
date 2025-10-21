package com.dinoHabitTracker.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme // ← HELYES
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DinoGreen,
    onPrimary = Color.White,
    secondary = DinoSky,
    onSecondary = Color.Black,
    background = DinoEgg,
    surface = Bone,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = LavaOrange
)

private val DarkColors = darkColorScheme(
    primary = DinoLeaf,
    onPrimary = Color.White,
    secondary = DinoSky,
    onSecondary = Color.Black,
    background = Cave,
    surface = Slate,
    onBackground = Color.White,
    onSurface = Color.White,
    error = LavaOrange
)

@Composable
fun DinoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // ← pontos név!
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}