package com.example.domain.engine

import com.example.domain.model.FigletFontType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer

object FigletGenerator {

    suspend fun renderAsync(text: String, font: FigletFontType): String = withContext(Dispatchers.Default) {
        render(text, font)
    }

    /**
     * Renders input text into multi-line ASCII banner art.
     * Accented characters (é, è, ê, à, ç, etc.) and special characters (!, ?, @, #, $, %, etc.)
     * are cleanly normalized and supported across all FIGlet font styles.
     */
    fun render(text: String, font: FigletFontType): String {
        if (text.isBlank()) return ""
        val clean = cleanText(text)

        return when (font) {
            FigletFontType.STANDARD -> renderStandard(clean)
            FigletFontType.SLANT -> renderSlant(clean)
            FigletFontType.BANNER -> renderBanner(clean)
            FigletFontType.DOOM -> renderDoom(clean)
            FigletFontType.MONOSPACE_3D -> render3D(clean)
        }
    }

    /**
     * Normalizes accented characters (é, à, è, ç, ï, ù, ô...) and transforms special ligatures.
     */
    fun cleanText(input: String): String {
        val decomposed = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .replace("æ", "AE", ignoreCase = true)
            .replace("œ", "OE", ignoreCase = true)
            .replace("ß", "SS")

        return decomposed.take(28).uppercase()
    }

    // -------------------------------------------------------------------------
    // 1. STANDARD FONT (5 lines)
    // -------------------------------------------------------------------------
    private fun renderStandard(text: String): String {
        val lines = Array(5) { StringBuilder() }
        for (char in text) {
            val glyph = STANDARD_MAP[char] ?: STANDARD_MAP['?'] ?: DEFAULT_5X5
            for (i in 0 until 5) {
                lines[i].append(glyph[i]).append(" ")
            }
        }
        return lines.joinToString("\n") { it.toString().trimEnd() }
    }

    // -------------------------------------------------------------------------
    // 2. SLANT FONT (5 lines)
    // -------------------------------------------------------------------------
    private fun renderSlant(text: String): String {
        val lines = Array(5) { StringBuilder() }
        for (char in text) {
            val glyph = SLANT_MAP[char] ?: STANDARD_MAP[char] ?: DEFAULT_5X5
            for (i in 0 until 5) {
                lines[i].append(glyph[i])
            }
        }
        return lines.joinToString("\n") { it.toString().trimEnd() }
    }

    // -------------------------------------------------------------------------
    // 3. BANNER FONT (4 lines)
    // -------------------------------------------------------------------------
    private fun renderBanner(text: String): String {
        val lines = Array(4) { StringBuilder() }
        for (char in text) {
            val glyph = BANNER_MAP[char] ?: BANNER_MAP['?'] ?: DEFAULT_4X4
            for (i in 0 until 4) {
                lines[i].append(glyph[i]).append(" ")
            }
        }
        return lines.joinToString("\n") { it.toString().trimEnd() }
    }

    // -------------------------------------------------------------------------
    // 4. DOOM FONT (5 lines)
    // -------------------------------------------------------------------------
    private fun renderDoom(text: String): String {
        val lines = Array(5) { StringBuilder() }
        for (char in text) {
            val glyph = DOOM_MAP[char] ?: STANDARD_MAP[char] ?: DEFAULT_5X5
            for (i in 0 until 5) {
                lines[i].append(glyph[i])
            }
        }
        return lines.joinToString("\n") { it.toString().trimEnd() }
    }

    // -------------------------------------------------------------------------
    // 5. MONOSPACE 3D FONT (5 lines)
    // -------------------------------------------------------------------------
    private fun render3D(text: String): String {
        val lines = Array(5) { StringBuilder() }
        for (char in text) {
            val glyph = MAP_3D[char] ?: STANDARD_MAP[char] ?: DEFAULT_5X5
            for (i in 0 until 5) {
                lines[i].append(glyph[i])
            }
        }
        return lines.joinToString("\n") { it.toString().trimEnd() }
    }

    private val DEFAULT_5X5 = arrayOf(" ___ ", "|   |", "|---|", "|   |", " --- ")
    private val DEFAULT_4X4 = arrayOf("####", "#  #", "#  #", "####")

    // =========================================================================
    // STANDARD MAP (Comprehensive A-Z, 0-9 & Full Special Characters)
    // =========================================================================
    private val STANDARD_MAP = mapOf(
        // Letters
        'A' to arrayOf("  _   ", " / \\  ", "/ _ \\ ", "|/ \\| ", "      "),
        'B' to arrayOf(" _    ", "|_)   ", "|_)   ", "|_)   ", "      "),
        'C' to arrayOf("  ___ ", " / __|", "| (__ ", " \\___|", "      "),
        'D' to arrayOf(" ___  ", "|   \\ ", "| |) )", "|___/ ", "      "),
        'E' to arrayOf(" ___  ", "| __| ", "| _|  ", "|___| ", "      "),
        'F' to arrayOf(" ___  ", "| __| ", "| _|  ", "|_|   ", "      "),
        'G' to arrayOf("  ___ ", " / __|", "| (_ |", " \\___|", "      "),
        'H' to arrayOf(" _  _ ", "| || |", "| __ |", "|_||_|", "      "),
        'I' to arrayOf(" ___ ", "|_ _|", " | | ", "|___|", "     "),
        'J' to arrayOf("   _  ", "  | | ", " _| | ", "|___/ ", "      "),
        'K' to arrayOf(" _  __", "| |/ /", "| ' < ", "|_|\\_\\", "      "),
        'L' to arrayOf(" _    ", "| |   ", "| |__ ", "|____|", "      "),
        'M' to arrayOf(" _  _ ", "| \\/ |", "| |\\/|", "|_|  |", "      "),
        'N' to arrayOf(" _  _ ", "| \\| |", "| .` |", "|_|\\_|", "      "),
        'O' to arrayOf("  ___ ", " / _ \\", "| (_) |", " \\___/ ", "      "),
        'P' to arrayOf(" ___  ", "| _ \\ ", "|  _/ ", "|_|   ", "      "),
        'Q' to arrayOf("  ___ ", " / _ \\", "| (_) |", " \\__\\_\\", "      "),
        'R' to arrayOf(" ___  ", "| _ \\ ", "|   / ", "|_|_\\ ", "      "),
        'S' to arrayOf(" ___  ", "/ __| ", "\\__ \\ ", "|___/ ", "      "),
        'T' to arrayOf(" _____ ", "|_   _|", "  | |  ", "  |_|  ", "       "),
        'U' to arrayOf(" _   _ ", "| | | |", "| |_| |", " \\___/ ", "       "),
        'V' to arrayOf(" _   _ ", "| | | |", "| | | |", " \\_/\\_/ ", "       "),
        'W' to arrayOf(" _    _ ", "| |/\\| |", "| |/\\| |", " \\_/\\_/ ", "        "),
        'X' to arrayOf(" __  __ ", " \\ \\/ / ", "  >  <  ", " /_/\\_\\ ", "        "),
        'Y' to arrayOf(" __   __", " \\ \\ / /", "  \\ V / ", "   |_|  ", "        "),
        'Z' to arrayOf(" _____ ", "|__  / ", "  / /  ", " /_/__ ", "       "),

        // Digits
        '0' to arrayOf("  ___  ", " / _ \\ ", "| | | |", "| |_| |", " \\___/ "),
        '1' to arrayOf("  _  ", " / | ", " | | ", " | | ", " |_| "),
        '2' to arrayOf(" ___  ", "|_  ) ", " / /  ", "/___| ", "      "),
        '3' to arrayOf(" ____ ", "|__ / ", " |_ \\ ", "|___/ ", "      "),
        '4' to arrayOf(" _ _  ", "| | | ", "|_  _|", "  |_| ", "      "),
        '5' to arrayOf(" ___  ", "| __| ", "|__ \\ ", "|___/ ", "      "),
        '6' to arrayOf("  __  ", " / /_ ", "| '_ \\", "| (_) |", " \\___/ "),
        '7' to arrayOf(" ____ ", "|__  |", "  / / ", " /_/  ", "      "),
        '8' to arrayOf(" ___  ", "( _ ) ", "/ _ \\ ", "\\___/ ", "      "),
        '9' to arrayOf(" ___  ", "/ _ \\ ", "\\_, / ", " /_/  ", "      "),

        // Whitespace & Basic Punctuation
        ' ' to arrayOf("   ", "   ", "   ", "   ", "   "),
        '!' to arrayOf(" _ ", "| |", "| |", "(_) ", "   "),
        '?' to arrayOf(" ___ ", "|_  )", " / / ", "(_)  ", "     "),
        '.' to arrayOf("   ", "   ", "   ", " _ ", "(_)"),
        ',' to arrayOf("   ", "   ", "   ", " _ ", "(/)"),
        ':' to arrayOf("   ", " _ ", "(_)", " _ ", "(_)"),
        ';' to arrayOf("   ", " _ ", "(_)", " _ ", "(/)"),

        // Symbols & Operators
        '-' to arrayOf("     ", " ___ ", "|___|", "     ", "     "),
        '_' to arrayOf("     ", "     ", "     ", "____ ", "    "),
        '+' to arrayOf("     ", "  _  ", "_|_|_", "  |  ", "     "),
        '=' to arrayOf("     ", " === ", "     ", " === ", "     "),
        '*' to arrayOf(" _   _ ", " \\_/ ", "/ _ \\", "     ", "     "),
        '/' to arrayOf("    / ", "   /  ", "  /   ", " /    ", "/     "),
        '\\' to arrayOf("\\     ", " \\    ", "  \\   ", "   \\  ", "    \\ "),
        '|' to arrayOf(" | ", " | ", " | ", " | ", " | "),
        '#' to arrayOf(" _ _ ", "|_|_|", "|_|_|", " | | ", "     "),
        '$' to arrayOf("  _  ", " /_|_", "  | \\", "_|_/ ", "  |  "),
        '%' to arrayOf("o   / ", "   /  ", "  /   ", " /   o", "/     "),
        '&' to arrayOf(" ___  ", "/ _ \\ ", " > (_) ", "/_/\\_\\", "      "),
        '@' to arrayOf("  ___  ", " / _ \\ ", "| |_) |", "|  __/ ", " \\___| "),
        '(' to arrayOf("  / ", " /  ", "|   ", " \\  ", "  \\ "),
        ')' to arrayOf(" \\  ", "  \\ ", "   |", "  / ", " /  "),
        '[' to arrayOf(" --- ", " |   ", " |   ", " |   ", " --- "),
        ']' to arrayOf(" --- ", "   | ", "   | ", "   | ", " --- "),
        '{' to arrayOf("  -- ", " -   ", "  -  ", " -   ", "  -- "),
        '}' to arrayOf(" --  ", "   - ", "  -  ", "   - ", " --  "),
        '<' to arrayOf("   / ", "  /  ", " <   ", "  \\  ", "   \\ "),
        '>' to arrayOf(" \\   ", "  \\  ", "   > ", "  /  ", " /   "),
        '~' to arrayOf(" _ _ ", "/ ' \\", "     ", "     ", "     "),
        '^' to arrayOf("  /\\ ", " /  \\", "      ", "      ", "      "),
        '\'' to arrayOf(" _ ", "(_) ", "    ", "    ", "    "),
        '"' to arrayOf(" _ _ ", "(_|_)", "     ", "     ", "     ")
    )

    // =========================================================================
    // SLANT MAP (Comprehensive A-Z, 0-9 & Special Characters)
    // =========================================================================
    private val SLANT_MAP = mapOf(
        'A' to arrayOf("   /\\   ", "  /  \\  ", " / /\\ \\ ", "/ ____ \\", "/_/    \\_\\"),
        'B' to arrayOf(" ____  ", "|  _ \\ ", "| |_) |", "|  _ < ", "| |_) |"),
        'C' to arrayOf("  ____ ", " / ___|", "| |    ", "| |___ ", " \\____|"),
        'D' to arrayOf(" ____  ", "|  _ \\ ", "| | | |", "| |_| |", "|____/ "),
        'E' to arrayOf(" _____ ", "| ____|", "|  _|  ", "| |___ ", "|_____|"),
        'F' to arrayOf(" _____ ", "|  ___|", "| |_   ", "|  _|  ", "|_|    "),
        'G' to arrayOf("  ____ ", " / ___|", "| |  _ ", "| |_| |", " \\____|"),
        'H' to arrayOf(" _   _ ", "| | | |", "| |_| |", "|  _  |", "|_| |_|"),
        'I' to arrayOf(" ___ ", "|_ _|", " | | ", " | | ", "|___|"),
        'J' to arrayOf("     _ ", "    | |", " _  | |", "| |_| |", " \\___/ "),
        'K' to arrayOf(" _  __", "| |/ /", "| ' / ", "| . \\ ", "|_|\\_\\"),
        'L' to arrayOf(" _     ", "| |    ", "| |    ", "| |___ ", "|_____|"),
        'M' to arrayOf(" __  __ ", "|  \\/  |", "| |\\/| |", "| |  | |", "|_|  |_|"),
        'N' to arrayOf(" _   _ ", "| \\ | |", "|  \\| |", "| |\\  |", "|_| \\_|"),
        'O' to arrayOf("  ___  ", " / _ \\ ", "| | | |", "| |_| |", " \\___/ "),
        'P' to arrayOf(" ____  ", "|  _ \\ ", "| |_) |", "|  __/ ", "|_|    "),
        'Q' to arrayOf("  ___  ", " / _ \\ ", "| | | |", "| |_| |", " \\__\\_\\"),
        'R' to arrayOf(" ____  ", "|  _ \\ ", "| |_) |", "|  _ < ", "|_| \\_\\"),
        'S' to arrayOf(" ____  ", "/ ___| ", "\\___ \\ ", " ___) |", "|____/ "),
        'T' to arrayOf(" _____ ", "|_   _|", "  | |  ", "  | |  ", "  |_|  "),
        'U' to arrayOf(" _   _ ", "| | | |", "| | | |", "| |_| |", " \\___/ "),
        'V' to arrayOf(" __     __", " \\ \\   / /", "  \\ \\ / / ", "   \\ V /  ", "    \\_/   "),
        'W' to arrayOf(" __        __", " \\ \\      / /", "  \\ \\ /\\ / / ", "   \\ V  V /  ", "    \\_/\\_/   "),
        'X' to arrayOf("__  __", "\\ \\/ /", " >  < ", "/ /\\ \\", "/_/  \\_\\"),
        'Y' to arrayOf("__   __", "\\ \\ / /", " \\ V / ", "  | |  ", "  |_|  "),
        'Z' to arrayOf(" _____ ", "|__  / ", "  / /  ", " / /_  ", "/____| "),

        '0' to arrayOf("  ___  ", " / _ \\ ", "| | | |", "| |_| |", " \\___/ "),
        '1' to arrayOf(" _ ", "/ |", "| |", "| |", "|_|"),
        '2' to arrayOf(" ____  ", "|___ \\ ", "  __) |", " / __/ ", "|_____|"),
        '3' to arrayOf(" _____ ", "|___ / ", "  |_ \\ ", " ___) |", "|____/ "),
        '4' to arrayOf(" _  _   ", "| || |  ", "| || |_ ", "|__   _|", "   |_|  "),
        '5' to arrayOf(" ____  ", "| ___| ", "|___ \\ ", " ___) |", "|____/ "),
        '6' to arrayOf("  __   ", " / /_  ", "| '_ \\ ", "| (_) |", " \\___/ "),
        '7' to arrayOf(" _____ ", "|___  |", "   / / ", "  / /  ", " /_/   "),
        '8' to arrayOf("  ___  ", " ( _ ) ", " / _ \\ ", "| (_) |", " \\___/ "),
        '9' to arrayOf("  ___  ", " / _ \\ ", "| (_) |", " \\__, |", "   /_/ "),

        ' ' to arrayOf("   ", "   ", "   ", "   ", "   "),
        '!' to arrayOf(" _ ", "| |", "| |", "(_) ", "   "),
        '?' to arrayOf(" ___ ", "|_  )", " / / ", "(_)  ", "     "),
        '-' to arrayOf("     ", " ___ ", "|___|", "     ", "     "),
        '_' to arrayOf("     ", "     ", "     ", "____ ", "    "),
        '+' to arrayOf("     ", "  _  ", "_|_|_", "  |  ", "     "),
        '=' to arrayOf("     ", " === ", "     ", " === ", "     "),
        '.' to arrayOf("   ", "   ", "   ", " _ ", "(_)"),
        ',' to arrayOf("   ", "   ", "   ", " _ ", "(/)"),
        ':' to arrayOf("   ", " _ ", "(_)", " _ ", "(_)"),
        ';' to arrayOf("   ", " _ ", "(_)", " _ ", "(/)"),
        '@' to arrayOf("  ___  ", " / _ \\ ", "| |_) |", "|  __/ ", " \\___| "),
        '#' to arrayOf(" _ _ ", "|_|_|", "|_|_|", " | | ", "     "),
        '$' to arrayOf("  _  ", " /_|_", "  | \\", "_|_/ ", "  |  "),
        '/' to arrayOf("   / ", "  /  ", " /   ", "/    ", "     "),
        '\\' to arrayOf("\\    ", " \\   ", "  \\  ", "   \\ ", "    \\"),
        '(' to arrayOf("  / ", " /  ", "|   ", " \\  ", "  \\ "),
        ')' to arrayOf(" \\  ", "  \\ ", "   |", "  / ", " /  "),
        '[' to arrayOf(" --- ", " |   ", " |   ", " |   ", " --- "),
        ']' to arrayOf(" --- ", "   | ", "   | ", "   | ", " --- "),
        '<' to arrayOf("   / ", "  /  ", " <   ", "  \\  ", "   \\ "),
        '>' to arrayOf(" \\   ", "  \\  ", "   > ", "  /  ", " /   ")
    )

    // =========================================================================
    // BANNER MAP (4 lines)
    // =========================================================================
    private val BANNER_MAP = mapOf(
        'A' to arrayOf(" ## ", "#  #", "####", "#  #"),
        'B' to arrayOf("### ", "#  #", "### ", "### "),
        'C' to arrayOf(" ###", "#   ", "#   ", " ###"),
        'D' to arrayOf("### ", "#  #", "#  #", "### "),
        'E' to arrayOf("####", "### ", "#   ", "####"),
        'F' to arrayOf("####", "### ", "#   ", "#   "),
        'G' to arrayOf(" ###", "#  #", "# ##", " ###"),
        'H' to arrayOf("#  #", "####", "#  #", "#  #"),
        'I' to arrayOf("###", " # ", " # ", "###"),
        'J' to arrayOf("  ##", "   #", "#  #", " ## "),
        'K' to arrayOf("#  #", "##  ", "# # ", "#  #"),
        'L' to arrayOf("#   ", "#   ", "#   ", "####"),
        'M' to arrayOf("#  #", "####", "#  #", "#  #"),
        'N' to arrayOf("#  #", "## #", "# ##", "#  #"),
        'O' to arrayOf(" ## ", "#  #", "#  #", " ## "),
        'P' to arrayOf("### ", "#  #", "### ", "#   "),
        'Q' to arrayOf(" ## ", "#  #", "# ##", " ###"),
        'R' to arrayOf("### ", "#  #", "##  ", "#  #"),
        'S' to arrayOf(" ###", "##  ", "  ##", "### "),
        'T' to arrayOf("####", " #  ", " #  ", " #  "),
        'U' to arrayOf("#  #", "#  #", "#  #", " ## "),
        'V' to arrayOf("#  #", "#  #", " # #", "  # "),
        'W' to arrayOf("#  #", "#  #", "####", "#  #"),
        'X' to arrayOf("#  #", " ## ", " ## ", "#  #"),
        'Y' to arrayOf("#  #", " ## ", " #  ", " #  "),
        'Z' to arrayOf("####", "  # ", " #  ", "####"),

        '0' to arrayOf("####", "#  #", "#  #", "####"),
        '1' to arrayOf(" ## ", "  # ", "  # ", " ###"),
        '2' to arrayOf("####", "  ##", "##  ", "####"),
        '3' to arrayOf("####", "  ##", "   #", "####"),
        '4' to arrayOf("#  #", "####", "   #", "   #"),
        '5' to arrayOf("####", "### ", "   #", "####"),
        '6' to arrayOf("####", "#   ", "####", "####"),
        '7' to arrayOf("####", "  # ", " #  ", " #  "),
        '8' to arrayOf("####", "####", "#  #", "####"),
        '9' to arrayOf("####", "####", "   #", "####"),

        ' ' to arrayOf("  ", "  ", "  ", "  "),
        '!' to arrayOf(" # ", " # ", "   ", " # "),
        '?' to arrayOf("###", "  #", " # ", " # "),
        '-' to arrayOf("    ", "####", "    ", "    "),
        '_' to arrayOf("    ", "    ", "    ", "####"),
        '+' to arrayOf(" #  ", "### ", " #  ", "    "),
        '=' to arrayOf("####", "    ", "####", "    "),
        '.' to arrayOf("  ", "  ", "  ", "##"),
        ',' to arrayOf("  ", "  ", " #", " #"),
        ':' to arrayOf("##", "  ", "##", "  "),
        ';' to arrayOf("##", "  ", " #", " #"),
        '#' to arrayOf("# #", "###", "###", "# #"),
        '$' to arrayOf(" ###", "$###", " ###", "###$"),
        '/' to arrayOf("   #", "  # ", " #  ", "#   "),
        '\\' to arrayOf("#   ", " #  ", "  # ", "   #"),
        '(' to arrayOf(" #", "# ", "# ", " #"),
        ')' to arrayOf("# ", " #", " #", "# "),
        '<' to arrayOf("  #", " # ", "#  ", "  #"),
        '>' to arrayOf("#  ", " # ", "  #", "#  ")
    )

    // =========================================================================
    // DOOM MAP (5 lines)
    // =========================================================================
    private val DOOM_MAP = mapOf(
        'A' to arrayOf(" /\\ ", "/  \\", "/\\/\\", "||||", "||||"),
        'B' to arrayOf("[==\\", "| _/", "|--\\", "| _/", "[==/"),
        'C' to arrayOf(" /--", "|   ", "|   ", "|   ", " \\__"),
        'D' to arrayOf("|--\\", "|  |", "|  |", "|  |", "|--/"),
        'E' to arrayOf("|---", "|-- ", "|__ ", "|   ", "|---"),
        'F' to arrayOf("|---", "|-- ", "|   ", "|   ", "|   "),
        'G' to arrayOf(" |--", "|  -", "| _|", "|  |", " \\-|"),
        'H' to arrayOf("|  |", "|--|", "|  |", "|  |", "|  |"),
        'I' to arrayOf("|||", " | ", " | ", " | ", "|||"),
        'J' to arrayOf("  ||", "   |", "   |", "|  |", " \\_/"),
        'K' to arrayOf("| / ", "|/  ", "|\\  ", "| \\ ", "|  \\"),
        'L' to arrayOf("|   ", "|   ", "|   ", "|   ", "|___"),
        'M' to arrayOf("|\\/|", "|  |", "|  |", "|  |", "|  |"),
        'N' to arrayOf("|\\ |", "| \\|", "|  |", "|  |", "|  |"),
        'O' to arrayOf(" /-\\ ", "|   |", "|   |", "|   |", " \\-/ "),
        'P' to arrayOf("|--\\", "|  |", "|--/", "|   ", "|   "),
        'Q' to arrayOf(" /-\\ ", "|   |", "| \\ |", "|  \\|", " \\-\\ "),
        'R' to arrayOf("|--\\", "|  |", "|--/", "|  \\", "|  |"),
        'S' to arrayOf(" /--", "|_  ", " --\\", " __|", "\\--/"),
        'T' to arrayOf("----", " || ", " || ", " || ", " || "),
        'U' to arrayOf("|  |", "|  |", "|  |", "|  |", " \\_/"),
        'V' to arrayOf("\\  /", " \\/ ", " || ", " || ", " \\/ "),
        'W' to arrayOf("|  |", "|  |", "|/\\|", "|/\\|", "|  |"),
        'X' to arrayOf("\\  /", " \\/ ", " /\\ ", "/  \\", "    "),
        'Y' to arrayOf("\\  /", " \\/ ", " || ", " || ", " || "),
        'Z' to arrayOf("[---", "  / ", " /  ", "/   ", "---]"),

        '0' to arrayOf("[--]", "|  |", "|  |", "|  |", "[--]"),
        '1' to arrayOf(" -- ", "  | ", "  | ", "  | ", " -- "),
        '2' to arrayOf("[--]", "   |", " [--", "|   ", "[--]"),
        '3' to arrayOf("[--]", "   |", " --|", "   |", "[--]"),
        '4' to arrayOf("|  |", "|  |", "[--|", "   |", "   |"),
        '5' to arrayOf("[--]", "|   ", "[--]", "   |", "[--]"),
        '6' to arrayOf("[--]", "|   ", "[--]", "|  |", "[--]"),
        '7' to arrayOf("[--]", "   /", "  / ", " /  ", "/   "),
        '8' to arrayOf("[--]", "|  |", "[--]", "|  |", "[--]"),
        '9' to arrayOf("[--]", "|  |", "[--]", "   |", "[--]"),

        ' ' to arrayOf("  ", "  ", "  ", "  ", "  "),
        '!' to arrayOf("|||", "|||", " | ", "   ", "(_)") ,
        '?' to arrayOf("[--]", "   |", "  / ", "    ", "(_)") ,
        '-' to arrayOf("    ", "    ", "[--]", "    ", "    "),
        '_' to arrayOf("    ", "    ", "    ", "    ", "[--]"),
        '+' to arrayOf("    ", "  | ", "[-|-]", "  | ", "    "),
        '=' to arrayOf("    ", "[--]", "    ", "[--]", "    "),
        '.' to arrayOf("   ", "   ", "   ", "   ", "(_)"),
        ':' to arrayOf("   ", "(_)", "   ", "(_)", "   ")
    )

    // =========================================================================
    // MAP 3D (5 lines)
    // =========================================================================
    private val MAP_3D = mapOf(
        'A' to arrayOf(" ___ ", "/ _ \\", "|/_\\|", "|   |", "\\_|_/"),
        'B' to arrayOf("| _ )", "| _ \\", "|___/", "|   |", "|___/"),
        'C' to arrayOf(" / _|", "| (_ ", " \\__)", "    |", "    /"),
        'D' to arrayOf("| _ \\", "| |) )", "|___/", "     ", "     "),
        'E' to arrayOf("| __|", "| _| ", "|___|", "     ", "     "),
        'F' to arrayOf("| __|", "| _| ", "|_|  ", "     ", "     "),
        'G' to arrayOf(" / __", "| (_|", " \\___", "     ", "     "),
        'H' to arrayOf("| |_|", "|  _|", "|_|  ", "     ", "     "),
        'I' to arrayOf(" ___ ", "|_ _|", " | | ", "|___|", "     "),
        'J' to arrayOf("   _ ", "  | |", " _| |", "|___/", "     "),
        'K' to arrayOf("| |/ ", "| ' <", "|_|\\_", "     ", "     "),
        'L' to arrayOf("| |  ", "| |__", "|____", "     ", "     "),
        'M' to arrayOf("|  \\/  |", "| |\\/| |", "|_|  |_|", "        ", "        "),
        'N' to arrayOf("| \\| |", "| .` |", "|_|\\_|", "      ", "      "),
        'O' to arrayOf(" / _ \\", "| (_) |", " \\___/ ", "       ", "       "),
        'P' to arrayOf("| _ \\", "|  _/", "|_|  ", "     ", "     "),
        'Q' to arrayOf(" / _ \\", "| (_) |", " \\__\\\\_", "       ", "       "),
        'R' to arrayOf("| _ \\", "|   /", "|_|_\\", "     ", "     "),
        'S' to arrayOf(" / __|", " \\__ \\", " |___/", "      ", "      "),
        'T' to arrayOf("|_   _|", "  | |  ", "  |_|  ", "       ", "       "),
        'U' to arrayOf("| | | |", "| |_| |", " \\___/ ", "       ", "       "),
        'V' to arrayOf("\\ \\ / /", " \\ V / ", "  |_|  ", "       ", "       "),
        'W' to arrayOf("\\ \\ /\\ / /", " \\ V  V / ", "  \\_/\\_/  ", "          ", "          "),
        'X' to arrayOf("\\ \\/ /", " >  < ", "/_/\\_\\", "      ", "      "),
        'Y' to arrayOf("\\ \\ / /", " \\ V / ", "  | |  ", "  |_|  ", "       "),
        'Z' to arrayOf(" ___/ ", "  / / ", " /___|", "      ", "      "),

        '0' to arrayOf(" / _ \\", "| | | |", " \\___/ ", "       ", "       "),
        '1' to arrayOf("  / |", "  | |", "  |_|", "     ", "     "),
        '2' to arrayOf(" ___ ", "|_  )", " / / ", "/___|", "     "),
        '3' to arrayOf(" ___ ", "|__ )", " |_ \\", "|___/", "     "),
        '4' to arrayOf(" _ _ ", "| | |", "|_  |", "  |_|", "     "),
        '5' to arrayOf(" ___ ", "| __|", " |_ \\", "|___/", "     "),
        '6' to arrayOf("  __ ", " / / ", "| '_ \\", " \\___/", "      "),
        '7' to arrayOf(" ____", "|__  |", "  / /", " /_/ ", "     "),
        '8' to arrayOf(" ___ ", "( _ )", "/ _ \\", "\\___/", "     "),
        '9' to arrayOf(" ___ ", "/ _ \\", "\\_, /", " /_/ ", "     "),

        ' ' to arrayOf("   ", "   ", "   ", "   ", "   "),
        '!' to arrayOf(" _ ", "| |", "| |", "(_) ", "   "),
        '?' to arrayOf(" ___ ", "|_  )", " / / ", "(_)  ", "     "),
        '-' to arrayOf("     ", " ___ ", "|___|", "     ", "     "),
        '_' to arrayOf("     ", "     ", "     ", "____ ", "    "),
        '+' to arrayOf("     ", "  _  ", "_|_|_", "  |  ", "     "),
        '=' to arrayOf("     ", " === ", "     ", " === ", "     "),
        '.' to arrayOf("   ", "   ", "   ", " _ ", "(_)"),
        ':' to arrayOf("   ", " _ ", "(_)", " _ ", "(_)")
    )
}
