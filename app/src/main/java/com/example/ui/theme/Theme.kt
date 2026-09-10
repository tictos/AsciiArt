package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TerminalGreen,
    onPrimary = SurfaceLowest,
    primaryContainer = TerminalGreenDark,
    onPrimaryContainer = TextPrimary,
    secondary = CyanCyber,
    onSecondary = SurfaceLowest,
    secondaryContainer = SurfaceContainerHigh,
    onSecondaryContainer = CyanCyber,
    tertiary = AmberCRT,
    onTertiary = SurfaceLowest,
    background = SurfaceBase,
    onBackground = TextPrimary,
    surface = SurfaceContainer,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    outlineVariant = BorderSubtle,
    error = SystemRed,
    onError = SurfaceLowest
)

@Composable
fun AsciiCraftTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SurfaceBase.toArgb()
                window.navigationBarColor = SurfaceBase.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
