package com.example.domain.model

enum class TemplateType(val label: String, val badgeText: String) {
    IMAGE_TO_ASCII("Image-to-ASCII", "🖼️ IMAGE"),
    TEXT_TO_ASCII("Text-to-ASCII", "🔤 TYPO / FIGLET")
}

data class AsciiTemplate(
    val id: String,
    val title: String,
    val type: TemplateType,
    val category: String,
    val description: String,
    val tags: List<String>,
    val asciiArtwork: String,
    val colorHex: String = "#3FB950",
    val colorMode: AsciiColorMode = AsciiColorMode.GREEN_PHOSPHOR,
    val characterRamp: CharacterRamp = CharacterRamp.STANDARD,
    val ditherAlgorithm: DitherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
    val contrast: Float = 1.2f,
    val targetColumns: Int = 64,
    val textSource: String? = null,
    val figletFont: FigletFontType? = null,
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false
) {
    val lines: List<String> get() = asciiArtwork.lines()
    val rows: Int get() = lines.size
    val cols: Int get() = lines.maxOfOrNull { it.length } ?: 1
    val glyphCount: Int get() = lines.sumOf { it.length }
}

object AsciiTemplateCatalog {

    val templates: List<AsciiTemplate> = listOf(
        // --- 1. FEATURED / CYBERPUNK ---
        AsciiTemplate(
            id = "tpl_cyber_cat",
            title = "Cyber Katana v2.0",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Cyberpunk & Néon",
            description = "Portrait d'un félin cybernétique avec visor holographique et matrice de caractères dense.",
            tags = listOf("Cyberpunk", "Monochrome", "High-Contrast", "Animal"),
            asciiArtwork = """
               /\_/\   [CYBER-CAT // MK-IV]   /\_/\
              ( o.o )   ===================  ( o.o )
               > ^ <    :: MATRIX ACCEL ::    > ^ <
            .----------------------------------------.
            |  ░▒▓██████▓▒░            ░▒▓██████▓▒░  |
            | ░██▓▒░░▒▓██░              ░██▓▒░░▒▓██░ |
            | ██▒  (o)(o)  ██▒          ██▒  (o)(o)  ██▒ |
            | ██░    ▲     ░██          ██░    ▲     ░██ |
            | ░██▓▒  ═  ▒▓██░            ░██▓▒  ═  ▒▓██░ |
            |  ░▒▓██████▓▒░   [0x7F]      ░▒▓██████▓▒░  |
            '----------------------------------------'
                  \________/            \________/   
                   ||    ||              ||    ||    
                   ++====++              ++====++    
            """.trimIndent(),
            colorHex = "#3FB950",
            colorMode = AsciiColorMode.GREEN_PHOSPHOR,
            characterRamp = CharacterRamp.BLOCKS,
            ditherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
            contrast = 1.35f,
            targetColumns = 60,
            isPopular = true,
            isFeatured = true
        ),

        // --- 2. FIGLET BANNER / TEXT ---
        AsciiTemplate(
            id = "tpl_figlet_asciicraft",
            title = "ASCIICRAFT Pro Banner",
            type = TemplateType.TEXT_TO_ASCII,
            category = "Typographie & Bannières",
            description = "Bannière FIGlet géante au format Slant avec ombrages et typographie rétro.",
            tags = listOf("Bannière", "Slant", "Header CLI", "ANSI 24b"),
            asciiArtwork = """
       ___           _ _  ____            __ _   
      / _ \ ___  ___(_|_) / ___|_ __ __ _ / _| |_ 
     / /_\ / __|/ __| | | | |   | '__/ _` | |_| __|
    / /_\\ \__ \ (__| | | | |___| | | (_| |  _| |_ 
    \____/ |___/\___|_|_|  \____|_|  \__,_|_|  \__|
    ===============================================
    >> GENERATEUR ARTISTIQUE ASCII HAUTE DENSITE <<
            """.trimIndent(),
            colorHex = "#58A6FF",
            colorMode = AsciiColorMode.ANSI_TRUECOLOR,
            characterRamp = CharacterRamp.STANDARD,
            textSource = "AsciiCraft",
            figletFont = FigletFontType.SLANT,
            isPopular = true
        ),

        // --- 3. RETROWAVE SUNSET ---
        AsciiTemplate(
            id = "tpl_synthwave_sunset",
            title = "Outrun Sunset 1984",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Rétro & 8-Bit",
            description = "Soleil couchant dégradé Synthwave avec grille de perspective rétro-futuriste.",
            tags = listOf("Synthwave", "Ambre", "Landscape", "80s"),
            asciiArtwork = """
                   .-----------------------.
                 /    .---''''''''---.      \
                /   .'       __       '.     \
               |   /    .---'  '---.    \     |
               |  |    /     ||     \    |    |
               |  |===|======||======|===|    |
               |  |    \     ||     /    |    |
               |   \    '---.__.---'    /     |
                \   '.                 .'    /
                 \    '---.........---'     /
             '-----------------------------------'
             /////////////////////////////////////
            ///////////////////////////////////////
           /////////////////////////////////////////
          ===========================================
            """.trimIndent(),
            colorHex = "#D29922",
            colorMode = AsciiColorMode.AMBER_VINTAGE,
            characterRamp = CharacterRamp.STANDARD,
            ditherAlgorithm = DitherAlgorithm.ORDERED_4X4,
            contrast = 1.25f,
            targetColumns = 55,
            isPopular = true
        ),

        // --- 4. FIGLET / HACKER TERMINAL ---
        AsciiTemplate(
            id = "tpl_figlet_cyberpunk",
            title = "CYBERPUNK Doom Font",
            type = TemplateType.TEXT_TO_ASCII,
            category = "Typographie & Bannières",
            description = "Typographie lourde style DOOM pour titres d'interfaces et d'applications CLI.",
            tags = listOf("Doom", "Heavy", "Gaming", "Cyber"),
            asciiArtwork = """
     ______  ______  ______  ______  ______  
    /\  ___\/\  __ \/\  == \/\  ___\/\  == \ 
    \ \ \___\ \ \/\ \ \  __<\ \  __\\ \  __< 
     \ \_____\ \_____\ \_____\ \_____\ \_\ \_\
      \/_____/\/_____/\/_____/\/_____/\/_/ /_/
    -------------------------------------------
    [!] SYSTEM ACCESS GRANTED // OVERRIDE 0x99
            """.trimIndent(),
            colorHex = "#BC8CFF",
            colorMode = AsciiColorMode.CYBERPUNK_NEON,
            characterRamp = CharacterRamp.STANDARD,
            textSource = "CYBER",
            figletFont = FigletFontType.DOOM,
            isPopular = true
        ),

        // --- 5. NEON SKULL GLITCH ---
        AsciiTemplate(
            id = "tpl_neon_skull",
            title = "Glitch Skull Matrix",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Cyberpunk & Néon",
            description = "Crâne stylisé en blocs ASCII denses avec micro-tramage d'erreurs logiques.",
            tags = listOf("Skull", "Dark", "Blocks", "Floyd-Steinberg"),
            asciiArtwork = """
                   .---''''''''---.
                 .'  ░▒▓██████▓▒░  '.
                /  ░▓████████████▓░  \
               |  ░██▓▒░    ░▒▓██░    |
               |  ██▒ (O)  (O)  ▒██   |
               |  ██░    /\    ░██    |
                \ ░██▓▒ ====== ▒▓██░ /
                 '. ░▒▓██████▓▒░   .'
                   '--| | | | |--'
                      | | | | |
                      '-'-'-'-'
            """.trimIndent(),
            colorHex = "#58A6FF",
            colorMode = AsciiColorMode.ANSI_TRUECOLOR,
            characterRamp = CharacterRamp.BLOCKS,
            ditherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
            contrast = 1.4f,
            targetColumns = 50
        ),

        // --- 6. MONA LISA / CLASSICAL ART ---
        AsciiTemplate(
            id = "tpl_mona_shade",
            title = "Mona Lisa Grayscale",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Rétro & 8-Bit",
            description = "Conversion subtile utilisant la rampe de caractères fins pour un dégradé continu.",
            tags = listOf("Classique", "Nuances", "Minimal Ramp", "Fine"),
            asciiArtwork = """
                    .:░▒▓██████▓▒░:.
                 .:▒▓██████████████▓▒:.
                :▓████████████████████▓:
               :████████████████████████:
              :██████▓▒░░░░░░░░▒▓████████:
              ▓█████▒  (.)  (.)  ▒███████▓
              ▓█████░     👃     ░███████▓
              ▒█████▓    ===     ▓███████▒
              :██████▓▒░░   ░░▒▓█████████:
               :████████████████████████:
                :▓████████████████████▓:
                 ':▒▓██████████████▓▒:'
                    ':░▒▓██████▓▒░:'
            """.trimIndent(),
            colorHex = "#F0F6FC",
            colorMode = AsciiColorMode.GREEN_PHOSPHOR,
            characterRamp = CharacterRamp.STANDARD,
            ditherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
            contrast = 1.15f,
            targetColumns = 60
        ),

        // --- 7. FIGLET / MATRIX CODE ---
        AsciiTemplate(
            id = "tpl_figlet_matrix",
            title = "MATRIX 3D Font",
            type = TemplateType.TEXT_TO_ASCII,
            category = "Code & Développeur",
            description = "Lettrage isométrique 3D idéal pour les commentaires d'en-tête de code source.",
            tags = listOf("3D", "Monospace", "Header Code", "Python/Rust"),
            asciiArtwork = """
     _   _ _____ _____ _   _ _____ _   _ _____ 
    | | | |_   _/  ___| | | |_   _| \ | |  __ \
    | |_| | | | \ `--.| |_| | | | |  \| | |  \/
    |  _  | | |  `--. \  _  | | | | . ` | | __ 
    | | | |_| |_/\__/ / | | |_| |_| |\  | |_\ \
    \_| |_/\___/\____/\_| |_/\___/\_| \_/\____/
    -------------------------------------------
    const MATRIX_CORE: &[u8; 1024] = b"0xCAFEBABE";
            """.trimIndent(),
            colorHex = "#3FB950",
            colorMode = AsciiColorMode.GREEN_PHOSPHOR,
            characterRamp = CharacterRamp.STANDARD,
            textSource = "HACKING",
            figletFont = FigletFontType.MONOSPACE_3D
        ),

        // --- 8. MECHA ROBOT ---
        AsciiTemplate(
            id = "tpl_mecha_robot",
            title = "Mecha Unit 01",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Cyberpunk & Néon",
            description = "Robot de combat avec armure angulaire et capteurs optiques.",
            tags = listOf("Mecha", "Robot", "Futuriste", "High-Tech"),
            asciiArtwork = """
                 /|               |\
                / |  .=========.  | \
               |  | /  [ O O ]  \ |  |
               |  \/    \ = /    \/  |
               \   \____/ - \____/   /
                '---[  0xDEADBEEF ]---'
                   /  |  ===  |  \
                  /   |       |   \
                 /____|_______|____\
                 |  [=========]  |
                 |  |  |     |  |  |
                 (__)  (__) (__)  (__)
            """.trimIndent(),
            colorHex = "#58A6FF",
            colorMode = AsciiColorMode.ANSI_TRUECOLOR,
            characterRamp = CharacterRamp.STANDARD,
            ditherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
            contrast = 1.3f,
            targetColumns = 54
        ),

        // --- 9. FIGLET / STANDARD TERMINAL ---
        AsciiTemplate(
            id = "tpl_figlet_terminal",
            title = "TERMINAL Standard Font",
            type = TemplateType.TEXT_TO_ASCII,
            category = "Code & Développeur",
            description = "Texte FIGlet standard classique et épuré compatible avec tout terminal VT100.",
            tags = listOf("Standard", "Clean", "CLI", "Shell"),
            asciiArtwork = """
      _____ _____ ____  __  __ ___ _   _    _    _     
     |_   _| ____|  _ \|  \/  |_ _| \ | |  / \  | |    
       | | |  _| | |_) | |\/| || ||  \| | / _ \ | |    
       | | | |___|  _ <| |  | || || |\  |/ ___ \| |___ 
       |_| |_____|_| \_\_|  |_|___|_| \_/_/   \_\_____|
    ---------------------------------------------------
    $ ./asciicraft --verbose --output stdout
            """.trimIndent(),
            colorHex = "#3FB950",
            colorMode = AsciiColorMode.GREEN_PHOSPHOR,
            characterRamp = CharacterRamp.STANDARD,
            textSource = "TERMINAL",
            figletFont = FigletFontType.STANDARD
        ),

        // --- 10. BINARY MATRIX RAIN ---
        AsciiTemplate(
            id = "tpl_binary_cascade",
            title = "Digital Rain Matrix",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Code & Développeur",
            description = "Pluie de code binaire pur (0 et 1) simulant le flux matriciel.",
            tags = listOf("Binaire", "Matrix", "Code", "01"),
            asciiArtwork = """
    1010110010101011001010101100101010110010101011
    0101001101010100110101010011010101001101010100
    1100101011001010101100101010110010101011001010
    0011010100110101010011010101001101010100110101
    1010110010101011001010101100101010110010101011
    0101001101010100110101010011010101001101010100
    1100101011001010101100101010110010101011001010
            """.trimIndent(),
            colorHex = "#3FB950",
            colorMode = AsciiColorMode.GREEN_PHOSPHOR,
            characterRamp = CharacterRamp.BINARY,
            ditherAlgorithm = DitherAlgorithm.NONE,
            contrast = 1.0f,
            targetColumns = 48
        ),

        // --- 11. RETRO GAMING DRAGON ---
        AsciiTemplate(
            id = "tpl_pixel_dragon",
            title = "8-Bit Dragon Boss",
            type = TemplateType.IMAGE_TO_ASCII,
            category = "Rétro & 8-Bit",
            description = "Boss légendaire de jeu de rôle 8-bit en art ASCII soigné.",
            tags = listOf("Dragon", "8-Bit", "Boss", "RPG"),
            asciiArtwork = """
                     /\_/\   
                    / o o \  
                   (   "   ) 
                    \~(*)~/  
              /\_/\ / / \ \ /\_/\
             /     \ V   V /     \
            | (o o) | === | (o o) |
             \  ^  /       \  ^  /
              |||||         |||||
             =====================
             [!] DRAGON BOSS LVL 99
            """.trimIndent(),
            colorHex = "#D29922",
            colorMode = AsciiColorMode.AMBER_VINTAGE,
            characterRamp = CharacterRamp.STANDARD,
            ditherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
            contrast = 1.3f,
            targetColumns = 50
        ),

        // --- 12. FIGLET / BANNER GRAFFITI ---
        AsciiTemplate(
            id = "tpl_figlet_banner",
            title = "RETRO FIGLET Banner",
            type = TemplateType.TEXT_TO_ASCII,
            category = "Typographie & Bannières",
            description = "Bannière majuscule percutante pour générer des headers de documentation et README.",
            tags = listOf("Banner", "Headers", "Readme", "ASCII Art"),
            asciiArtwork = """
     # # # # # # # # # # # # # # # # # # # # # 
     #   ____  _____ _____ ____   ___         # 
     #  |  _ \| ____|_   _|  _ \ / _ \        # 
     #  | |_) |  _|   | | | |_) | | | |       # 
     #  |  _ <| |___  | | |  _ <| |_| |       # 
     #  |_| \_\_____| |_| |_| \_\\___/        # 
     #                                        # 
     # # # # # # # # # # # # # # # # # # # # # 
            """.trimIndent(),
            colorHex = "#BC8CFF",
            colorMode = AsciiColorMode.CYBERPUNK_NEON,
            characterRamp = CharacterRamp.STANDARD,
            textSource = "RETRO",
            figletFont = FigletFontType.BANNER
        )
    )
}
