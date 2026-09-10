package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.CyanCyber
import com.example.ui.theme.MagentaNeon
import com.example.ui.theme.TerminalGreen

enum class AsciiColorMode(val label: String, val subtitle: String, val dotColor: Color) {
    GREEN_PHOSPHOR("Vert Phosphore", "VT220 Mono", TerminalGreen),
    AMBER_VINTAGE("Ambre Vintage", "IBM 3270", AmberCRT),
    ANSI_TRUECOLOR("ANSI Vrai 24b", "Full Gamut", CyanCyber),
    CYBERPUNK_NEON("Cyberpunk Néon", "Cyan & Magenta", MagentaNeon)
}

enum class DitherAlgorithm(val label: String, val subtitle: String) {
    NONE("AUCUN", "Seuil Brut"),
    FLOYD_STEINBERG("FLOYD-STEIN", "Diffusé 2D"),
    ORDERED_4X4("ORDERED 4x4", "Bayer Rétro")
}

data class CharacterRamp(
    val id: String,
    val name: String,
    val rampChars: String,
    val isCustom: Boolean = false
) {
    companion object {
        val STANDARD = CharacterRamp("standard", "STANDARD", "@%#*+=-:. ")
        val BLOCKS = CharacterRamp("blocks", "BLOCS RÉTRO", "█▓▒░ ")
        val BINARY = CharacterRamp("binary", "BINAIRE", "10 ")
        val MINIMAL = CharacterRamp("minimal", "MINIMAL", "%#*+=-:. ")

        val PRESETS = listOf(STANDARD, BLOCKS, BINARY, MINIMAL)
    }
}

enum class FigletFontType(val label: String) {
    STANDARD("Standard"),
    SLANT("Slant"),
    BANNER("Banner"),
    DOOM("Doom"),
    MONOSPACE_3D("Monospace 3D")
}

enum class ExportBackgroundAmbiance(val label: String, val subtitle: String) {
    OLED("OLED", "Noir Pur"),
    CRT_GREEN("CRT Vert", "Scanlines"),
    ALPHA("Alpha", "PNG Transparent"),
    CYBER("Cyber", "Halo / Glow")
}

enum class ExportLanguage(val label: String, val displayName: String, val extension: String, val versionTag: String) {
    PYTHON("Python (.py)", "Python", ".py", "3.11+"),
    RUST("Rust (.rs)", "Rust", ".rs", "1.75+"),
    TYPESCRIPT("TypeScript (.ts)", "TypeScript", ".ts", "5.0+"),
    GO("Go (.go)", "Go", ".go", "1.21+"),
    CPP("C++ (.cpp)", "C++", ".cpp", "C++20")
}

enum class ExportStructure(val label: String, val subtitle: String) {
    MATRIX_2D("Matrice 2D", "Array"),
    MULTILINE_RAW("Multiline Raw", "String"),
    BYTES_GZIP("Bytes gzip", "Buffer"),
    CLI_STANDALONE("Script CLI", "Standalone")
}

data class AsciiMatrixResult(
    val lines: List<String>,
    val colors: Array<IntArray>, // [row][col] ARGB color
    val cols: Int,
    val rows: Int,
    val glyphCount: Int,
    val renderTimeMs: Long,
    val sourceName: String = "cyber_portrait.jpg",
    val sourceResolution: String = "1080x1080"
) {
    val fullText: String by lazy { lines.joinToString("\n") }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AsciiMatrixResult) return false
        return lines == other.lines && cols == other.cols && rows == other.rows
    }

    override fun hashCode(): Int {
        var result = lines.hashCode()
        result = 31 * result + cols
        result = 31 * result + rows
        return result
    }
}
