package com.landezhaole.landing

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Water-blue theme, matching the website's yetai water-blue direction:
// light = pale blue background + #0088FF accent, dark = deep navy + #0091FF accent.
// This is a plain Material3 theme on purpose: no true-refraction library in v1,
// so the app builds on the proven AGP 9.1 / compileSdk 36 surface.
private val LightScheme = lightColorScheme(
    primary = Color(0xFF0088FF),
    onPrimary = Color.White,
    background = Color(0xFFEEF4FF),
    surface = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFDCE9FF),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFCBD8F0),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF0091FF),
    onPrimary = Color.White,
    background = Color(0xFF0E1420),
    surface = Color(0xFF1C2233),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF273044),
    onSurfaceVariant = Color(0xFFC6CCDD),
    outline = Color(0xFF2A3049),
)

@Composable
fun LandingTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        content = content,
    )
}

/** Stable avatar colours for site cards without a logo, picked by title hash. */
val AvatarColors = listOf(
    Color(0xFF0088FF), Color(0xFF00A896), Color(0xFF7C5CFF),
    Color(0xFFFF6B35), Color(0xFFE84D8A), Color(0xFF0091FF),
    Color(0xFF22A06B), Color(0xFFD97706),
)

fun avatarColorFor(title: String): Color {
    val h = title.hashCode().let { if (it < 0) -it else it }
    return AvatarColors[h % AvatarColors.size]
}
