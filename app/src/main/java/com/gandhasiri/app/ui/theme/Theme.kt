package com.gandhasiri.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Sandalwood / Wood Color Palette
val SandalwoodBrown = Color(0xFF8B4513)
val SandalwoodDark = Color(0xFF5C2E00)
val SandalwoodMedium = Color(0xFFA0522D)
val SandalwoodLight = Color(0xFFD2691E)
val SandalwoodPale = Color(0xFFDEB887)
val SandalwoodCream = Color(0xFFF5DEB3)
val SandalwoodIvory = Color(0xFFFFF8DC)

val ForestGreen = Color(0xFF2D5A27)
val ForestGreenLight = Color(0xFF4CAF50)
val LeafGreen = Color(0xFF81C784)

val HeartWoodRed = Color(0xFF8B0000)
val GoldenSap = Color(0xFFDAA520)
val BarkGray = Color(0xFF696969)
val SkyBlue = Color(0xFF87CEEB)

val AlertRed = Color(0xFFD32F2F)
val SafeGreen = Color(0xFF388E3C)
val WarningAmber = Color(0xFFF57C00)

private val LightColorScheme = lightColorScheme(
    primary = SandalwoodBrown,
    onPrimary = SandalwoodIvory,
    primaryContainer = SandalwoodPale,
    onPrimaryContainer = SandalwoodDark,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = LeafGreen,
    onSecondaryContainer = ForestGreen,
    tertiary = GoldenSap,
    onTertiary = SandalwoodDark,
    background = SandalwoodIvory,
    onBackground = SandalwoodDark,
    surface = SandalwoodCream,
    onSurface = SandalwoodDark,
    surfaceVariant = Color(0xFFEEDCB8),
    onSurfaceVariant = SandalwoodBrown,
    error = AlertRed,
    onError = Color.White,
    outline = SandalwoodMedium,
)

private val DarkColorScheme = darkColorScheme(
    primary = SandalwoodPale,
    onPrimary = SandalwoodDark,
    primaryContainer = SandalwoodBrown,
    onPrimaryContainer = SandalwoodIvory,
    secondary = LeafGreen,
    onSecondary = ForestGreen,
    secondaryContainer = ForestGreen,
    onSecondaryContainer = LeafGreen,
    tertiary = GoldenSap,
    onTertiary = SandalwoodDark,
    background = Color(0xFF1A0E00),
    onBackground = SandalwoodCream,
    surface = Color(0xFF2C1A00),
    onSurface = SandalwoodCream,
    surfaceVariant = Color(0xFF3D2800),
    onSurfaceVariant = SandalwoodPale,
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF690000),
    outline = SandalwoodMedium,
)

@Composable
fun GandhaSiriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
