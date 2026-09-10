package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Surfaces & Backgrounds (Terminal Cyber-Craft)
val SurfaceLowest = Color(0xFF0A0E14)      // OLED pure black for max contrast
val SurfaceBase = Color(0xFF10141A)        // App background & navigation shell
val SurfaceContainerLow = Color(0xFF161B22) // Headers, toolbars, containers
val SurfaceContainer = Color(0xFF181C22)    // Cards, inputs, dialogs
val SurfaceContainerHigh = Color(0xFF21262D)// Elevated cards, code blocks
val SurfaceBright = Color(0xFF353940)       // Hover & discrete active states

// Primary Colors (Terminal Green / Phosphor Emerald)
val TerminalGreen = Color(0xFF3FB950)       // Iconic console green for primary CTA
val TerminalGreenDark = Color(0xFF2EA043)   // Pressed state & active accents
val TerminalGreenBorder = Color(0xFF238636) // Interactive border highlight
val TerminalGreenGaze = Color(0x333FB950)   // Soft green glow

// Secondary & Technical Accents
val CyanCyber = Color(0xFF58A6FF)          // Cyber cyan / console blue
val AmberCRT = Color(0xFFD29922)           // Vintage CRT VT220 amber
val PurpleDev = Color(0xFFBC8CFF)          // DevKit purple for functions & types
val DevKitPurple = PurpleDev               // Alias for devkit styling
val SystemRed = Color(0xFFF85149)          // System red for destructive actions & macOS close
val SystemYellow = Color(0xFFE3B341)       // macOS minimize dot
val SystemGreen = Color(0xFF3FB950)        // macOS maximize dot
val MagentaNeon = Color(0xFFFF007F)        // Cyberpunk magenta

// Typography & Borders
val TextPrimary = Color(0xFFF0F6FC)        // High-contrast white/light gray
val TextSecondary = Color(0xFF8B949E)      // Muted technical gray
val TextTertiary = Color(0xFF6E7681)       // Darker muted text
val BorderDefault = Color(0xFF30363D)      // Crisp 1px card/divider borders
val BorderSubtle = Color(0xFF21262D)       // Subtle divider border
