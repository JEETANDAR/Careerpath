package com.aistudio.carrerpath.counseling.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = EduBluePrimary,
    onPrimary = EduBlueOnPrimary,
    primaryContainer = EduBlueContainer,
    onPrimaryContainer = EduBlueOnContainer,
    secondary = EduPurpleSecondary,
    onSecondary = EduPurpleOnSecondary,
    secondaryContainer = EduPurpleContainer,
    onSecondaryContainer = EduPurpleOnContainer,
    tertiary = EduCyanTertiary,
    background = EduBackgroundLight,
    surface = EduSurfaceLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

private val DarkColorScheme = darkColorScheme(
    primary = EduBluePrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = EduBlueContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = EduPurpleSecondaryDark,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = EduPurpleContainerDark,
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = EduCyanTertiary,
    background = EduBackgroundDark,
    surface = EduSurfaceDark,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = EduSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8)
)

@Composable
fun EduVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded blue/purple colors distinct
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    EduVerseTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

