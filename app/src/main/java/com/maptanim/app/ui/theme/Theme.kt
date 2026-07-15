package com.maptanim.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(

    primary = ForestGreen,
    secondary = LeafGreen,
    tertiary = Sunlight,

    background = NightBlue,
    surface = CardDark,

    onPrimary = White,
    onSecondary = White,
    onTertiary = TextDark,

    onBackground = TextPrimary,
    onSurface = TextPrimary,

    error = Danger
)

private val LightColorScheme = lightColorScheme(

    primary = ForestGreen,
    secondary = LeafGreen,
    tertiary = Sunlight,

    background = Color(0xFFF7FAF7),
    surface = CardLight,

    onPrimary = White,
    onSecondary = White,
    onTertiary = TextDark,

    onBackground = TextDark,
    onSurface = TextDark,

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