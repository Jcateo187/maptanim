package com.maptanim.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenLight,
    tertiary = Sunlight,

    background = Color(0xFF111813),
    surface = Color(0xFF1F2937),

    onPrimary = White,
    onSecondary = White,
    onTertiary = BlackPrimary,

    onBackground = White,
    onSurface = White,

    error = Danger
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenLight,
    tertiary = Sunlight,

    background = White,
    surface = CardBackground,

    onPrimary = White,
    onSecondary = White,
    onTertiary = BlackPrimary,

    onBackground = BlackPrimary,
    onSurface = BlackPrimary,

    error = Danger
)

@Composable
fun MapTanimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}