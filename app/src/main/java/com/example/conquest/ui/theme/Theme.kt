package com.example.conquest.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OffWhite,
    secondary = DarkGreen,
    tertiary = LightGray,
    background = DarkGray,
    surface = LightGreen,
    onBackground = OffWhite,  // Text color on background
    onSurface = OffWhite,     // Text color on surface
    onPrimary = OffWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGray,
    secondary = LighterDarkGreen,
    tertiary = LighterGray,
    background = OffWhite,
    surface = LighterGreen,
    onBackground = DarkGray,
    onSurface = DarkGray,
    onPrimary = DarkGray

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val HighContrastScheme = darkColorScheme(
    primary = OffWhite,
    secondary = HighContrastDarkGreen,
    tertiary = HighContrastLightGray,
    background = HighContrastDarkGray,
    surface = HighContrastLightGreen,
    onBackground = OffWhite,
    onSurface = OffWhite,
    onPrimary = OffWhite
)

@Composable
fun ConQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val typographyScheme = if (darkTheme) TypographyDark else TypographyWhite

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typographyScheme,
        content = content
    )
}