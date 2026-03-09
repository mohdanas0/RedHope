package com.example.redhope.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 🌞 Light Theme Colors
private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    secondary = RedSecondary,
    onSecondary = White,
    background = White,
    surface = White,
    onBackground = TextDark,
    onSurface = TextDark,
)

// 🌙 Dark Theme Colors (optional but nice for demo)
private val DarkColors = darkColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    secondary = RedSecondary,
    onSecondary = White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = White,
    onSurface = White,
)

@Composable
fun RedHopeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}