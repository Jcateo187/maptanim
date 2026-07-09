package com.maptanim.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = LeafGreen80,
    tertiary = SoilBrown80,

    background = BackgroundDark,
    surface = SurfaceDark,

    onPrimary = OnPrimary,
    onBackground = Color.White,
    onSurface = Color.White,

    error = ErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = LeafGreen40,
    tertiary = SoilBrown40,

    background = BackgroundLight,
    surface = SurfaceLight,

    onPrimary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnSurface,

    error = ErrorLight
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