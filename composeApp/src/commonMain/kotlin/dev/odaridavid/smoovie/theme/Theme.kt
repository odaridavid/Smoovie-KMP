package dev.odaridavid.smoovie.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF3D6363),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFBFE9E8),
        onPrimaryContainer = Color(0xFF002020),
        secondary = Color(0xFF4A6363),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFCCE8E7),
        onSecondaryContainer = Color(0xFF051F1F),
        tertiary = Color(0xFF4B607C),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD3E4FF),
        onTertiaryContainer = Color(0xFF041C35),
        background = Color(0xFFF5FAFA),
        onBackground = Color(0xFF161D1D),
        surface = Color(0xFFF5FAFA),
        onSurface = Color(0xFF161D1D),
        surfaceVariant = Color(0xFFDAE5E4),
        onSurfaceVariant = Color(0xFF3F4948),
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFFA4CDCC),
        onPrimary = Color(0xFF013736),
        primaryContainer = Color(0xFF234D4D),
        onPrimaryContainer = Color(0xFFBFE9E8),
        secondary = Color(0xFFB1CCCB),
        onSecondary = Color(0xFF1C3534),
        secondaryContainer = Color(0xFF334B4B),
        onSecondaryContainer = Color(0xFFCCE8E7),
        tertiary = Color(0xFFB3C8E8),
        onTertiary = Color(0xFF1C314B),
        tertiaryContainer = Color(0xFF334863),
        onTertiaryContainer = Color(0xFFD3E4FF),
        background = Color(0xFF0E1414),
        onBackground = Color(0xFFDEE4E3),
        surface = Color(0xFF0E1414),
        onSurface = Color(0xFFDEE4E3),
        surfaceVariant = Color(0xFF3F4948),
        onSurfaceVariant = Color(0xFFBEC9C8),
    )

@Composable
fun SmoovieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
