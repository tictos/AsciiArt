package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AsciiProjectEntity
import com.example.data.repository.AsciiProjectRepository
import com.example.domain.engine.AsciiConverter
import com.example.domain.engine.CodeExporter
import com.example.domain.engine.FigletGenerator
import com.example.domain.engine.ImageExporter
import com.example.domain.model.AsciiColorMode
import com.example.domain.model.AsciiMatrixResult
import com.example.domain.model.AsciiTemplate
import com.example.domain.model.CharacterRamp
import com.example.domain.model.DitherAlgorithm
import com.example.domain.model.ExportBackgroundAmbiance
import com.example.domain.model.ExportLanguage
import com.example.domain.model.ExportStructure
import com.example.domain.model.FigletFontType
import com.example.domain.model.TemplateType
import com.example.util.SampleImageGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MainInputMode {
    IMAGE, TEXT
}

enum class CanvasViewMode {
    FULL_ASCII, SPLIT, BEFORE_AFTER
}

enum class GallerySection {
    MY_CREATIONS, INSPIRATION_LIBRARY
}

class AsciiCraftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AsciiProjectRepository

    // 1. Navigation Shell State
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // 2. Studio Input Mode (Image vs FIGlet Text)
    private val _inputMode = MutableStateFlow(MainInputMode.IMAGE)
    val inputMode: StateFlow<MainInputMode> = _inputMode.asStateFlow()

    // 3. Image Mode Properties
    private val _sourceBitmap = MutableStateFlow<Bitmap?>(null)
    val sourceBitmap: StateFlow<Bitmap?> = _sourceBitmap.asStateFlow()

    private val _sourceFileName = MutableStateFlow("cyber_portrait.jpg")
    val sourceFileName: StateFlow<String> = _sourceFileName.asStateFlow()

    private val _sourceResolution = MutableStateFlow("1080x1080")
    val sourceResolution: StateFlow<String> = _sourceResolution.asStateFlow()

    // 4. FIGlet Text Properties
    private val _figletText = MutableStateFlow("CYBER")
    val figletText: StateFlow<String> = _figletText.asStateFlow()

    private val _selectedFigletFont = MutableStateFlow(FigletFontType.STANDARD)
    val selectedFigletFont: StateFlow<FigletFontType> = _selectedFigletFont.asStateFlow()

    // 5. Calibration Parameters
    private val _targetColumns = MutableStateFlow(80)
    val targetColumns: StateFlow<Int> = _targetColumns.asStateFlow()

    private val _contrast = MutableStateFlow(1.18f)
    val contrast: StateFlow<Float> = _contrast.asStateFlow()

    private val _gamma = MutableStateFlow(1.0f)
    val gamma: StateFlow<Float> = _gamma.asStateFlow()

    private val _invertLuminance = MutableStateFlow(false)
    val invertLuminance: StateFlow<Boolean> = _invertLuminance.asStateFlow()

    private val _characterRamp = MutableStateFlow(CharacterRamp.STANDARD)
    val characterRamp: StateFlow<CharacterRamp> = _characterRamp.asStateFlow()

    private val _colorMode = MutableStateFlow(AsciiColorMode.GREEN_PHOSPHOR)
    val colorMode: StateFlow<AsciiColorMode> = _colorMode.asStateFlow()

    private val _ditherAlgorithm = MutableStateFlow(DitherAlgorithm.FLOYD_STEINBERG)
    val ditherAlgorithm: StateFlow<DitherAlgorithm> = _ditherAlgorithm.asStateFlow()

    // 6. Viewport & Canvas Mode
    private val _canvasViewMode = MutableStateFlow(CanvasViewMode.FULL_ASCII)
    val canvasViewMode: StateFlow<CanvasViewMode> = _canvasViewMode.asStateFlow()

    private val _zoomLevel = MutableStateFlow(1.0f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()

    private val _isCrtScanlineEnabled = MutableStateFlow(true)
    val isCrtScanlineEnabled: StateFlow<Boolean> = _isCrtScanlineEnabled.asStateFlow()

    // 7. Computed Live ASCII Result
    private val _asciiResult = MutableStateFlow<AsciiMatrixResult>(AsciiConverter.generateSampleCyberCat())
    val asciiResult: StateFlow<AsciiMatrixResult> = _asciiResult.asStateFlow()

    // 8. DevKit Export State
    private val _devLanguage = MutableStateFlow(ExportLanguage.PYTHON)
    val devLanguage: StateFlow<ExportLanguage> = _devLanguage.asStateFlow()

    private val _devStructure = MutableStateFlow(ExportStructure.MATRIX_2D)
    val devStructure: StateFlow<ExportStructure> = _devStructure.asStateFlow()

    private val _includeAnsi = MutableStateFlow(true)
    val includeAnsi: StateFlow<Boolean> = _includeAnsi.asStateFlow()

    private val _includeCli = MutableStateFlow(true)
    val includeCli: StateFlow<Boolean> = _includeCli.asStateFlow()

    private val _generatedDevCode = MutableStateFlow("")
    val generatedDevCode: StateFlow<String> = _generatedDevCode.asStateFlow()

    // 9. Standard Export Sheet State
    private val _isExportSheetOpen = MutableStateFlow(false)
    val isExportSheetOpen: StateFlow<Boolean> = _isExportSheetOpen.asStateFlow()

    private val _exportAmbiance = MutableStateFlow(ExportBackgroundAmbiance.OLED)
    val exportAmbiance: StateFlow<ExportBackgroundAmbiance> = _exportAmbiance.asStateFlow()

    private val _exportWatermark = MutableStateFlow(true)
    val exportWatermark: StateFlow<Boolean> = _exportWatermark.asStateFlow()

    private val _exportFormat = MutableStateFlow("PNG 4K")
    val exportFormat: StateFlow<String> = _exportFormat.asStateFlow()

    // 10. Gallery Search & Filter
    private val _gallerySection = MutableStateFlow(GallerySection.MY_CREATIONS)
    val gallerySection: StateFlow<GallerySection> = _gallerySection.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGalleryCategory = MutableStateFlow("Tous")
    val selectedGalleryCategory: StateFlow<String> = _selectedGalleryCategory.asStateFlow()

    private val _selectedTemplateCategory = MutableStateFlow("Tous les modèles")
    val selectedTemplateCategory: StateFlow<String> = _selectedTemplateCategory.asStateFlow()

    // 11. Custom Ramp Dialog
    private val _customRampDialogOpen = MutableStateFlow(false)
    val customRampDialogOpen: StateFlow<Boolean> = _customRampDialogOpen.asStateFlow()

    private val _customRampText = MutableStateFlow("@ $ # * ! = ; : ~ - , . ")
    val customRampText: StateFlow<String> = _customRampText.asStateFlow()

    // 12. Toast / Notification Messages
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val projects: StateFlow<List<AsciiProjectEntity>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = AsciiProjectRepository(database.asciiProjectDao())
        projects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize with default high-tech cyber portrait
        viewModelScope.launch {
            val defaultBmp = SampleImageGenerator.generateCyberPortrait()
            _sourceBitmap.value = defaultBmp
            recomputeAscii()
        }
    }

    // --- Action Handlers ---

    fun setTab(index: Int) {
        _selectedTab.value = index
        if (index == 2) {
            updateDevCode()
        }
    }

    private var textDebounceJob: kotlinx.coroutines.Job? = null

    fun setInputMode(mode: MainInputMode) {
        _inputMode.value = mode
        if (mode == MainInputMode.TEXT) {
            convertAndSaveToHistory(showToast = false)
        } else {
            recomputeAscii()
        }
    }

    private fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.FRANCE)
        return sdf.format(java.util.Date())
    }

    fun setSourceImageUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        _sourceBitmap.value = bmp
                        _sourceFileName.value = "photo_${System.currentTimeMillis() % 10000}.jpg"
                        _sourceResolution.value = "${bmp.width}x${bmp.height}"
                        convertAndSaveToHistory(showToast = false)
                        withContext(Dispatchers.Main) {
                            _toastMessage.value = "Image chargée et enregistrée dans la Galerie !"
                        }
                    }
                }
            } catch (e: Exception) {
                _toastMessage.value = "Erreur de chargement: ${e.message}"
            }
        }
    }

    fun setSourceBitmap(bitmap: Bitmap) {
        _sourceBitmap.value = bitmap
        _sourceFileName.value = "photo_capture_${System.currentTimeMillis() % 10000}.jpg"
        _sourceResolution.value = "${bitmap.width}x${bitmap.height}"
        convertAndSaveToHistory(showToast = false)
        _toastMessage.value = "Photo convertie et enregistrée dans la Galerie !"
    }

    fun setFigletText(text: String) {
        _figletText.value = text
        if (_inputMode.value == MainInputMode.TEXT) {
            recomputeAscii()
            // Automatically save to history after user stops typing for 700ms
            textDebounceJob?.cancel()
            textDebounceJob = viewModelScope.launch(Dispatchers.Default) {
                kotlinx.coroutines.delay(700)
                if (_figletText.value.isNotBlank()) {
                    convertAndSaveToHistory(showToast = false)
                }
            }
        }
    }

    fun setFigletFont(font: FigletFontType) {
        _selectedFigletFont.value = font
        if (_inputMode.value == MainInputMode.TEXT) {
            convertAndSaveToHistory(showToast = false)
        }
    }

    fun setTargetColumns(cols: Int) {
        _targetColumns.value = cols
        recomputeAscii()
    }

    fun setContrast(contrastVal: Float) {
        _contrast.value = contrastVal
        recomputeAscii()
    }

    fun setGamma(gammaVal: Float) {
        _gamma.value = gammaVal
        recomputeAscii()
    }

    fun setInvertLuminance(invert: Boolean) {
        _invertLuminance.value = invert
        recomputeAscii()
    }

    fun setCharacterRamp(ramp: CharacterRamp) {
        _characterRamp.value = ramp
        recomputeAscii()
    }

    fun reverseActiveRamp() {
        val current = _characterRamp.value
        val reversed = CharacterRamp(
            id = "reversed_${current.id}",
            name = current.name,
            rampChars = current.rampChars.reversed(),
            isCustom = true
        )
        _characterRamp.value = reversed
        recomputeAscii()
    }

    fun applyCustomRamp(customChars: String) {
        if (customChars.isNotBlank()) {
            val ramp = CharacterRamp("custom_${System.currentTimeMillis()}", "PERSONNALISÉE", customChars, true)
            _characterRamp.value = ramp
            _customRampText.value = customChars
            _customRampDialogOpen.value = false
            recomputeAscii()
        }
    }

    fun setCustomRampDialogOpen(open: Boolean) {
        _customRampDialogOpen.value = open
    }

    fun setColorMode(mode: AsciiColorMode) {
        _colorMode.value = mode
        recomputeAscii()
    }

    fun setDitherAlgorithm(algo: DitherAlgorithm) {
        _ditherAlgorithm.value = algo
        recomputeAscii()
    }

    fun setCanvasViewMode(mode: CanvasViewMode) {
        _canvasViewMode.value = mode
    }

    fun setZoomLevel(zoom: Float) {
        _zoomLevel.value = zoom.coerceIn(0.5f, 3.0f)
    }

    fun toggleCrtScanlines() {
        _isCrtScanlineEnabled.value = !_isCrtScanlineEnabled.value
    }

    fun openExportSheet() {
        _isExportSheetOpen.value = true
    }

    fun closeExportSheet() {
        _isExportSheetOpen.value = false
    }

    fun setExportAmbiance(ambiance: ExportBackgroundAmbiance) {
        _exportAmbiance.value = ambiance
    }

    fun setExportWatermark(watermark: Boolean) {
        _exportWatermark.value = watermark
    }

    fun setExportFormat(format: String) {
        _exportFormat.value = format
    }

    fun setDevLanguage(lang: ExportLanguage) {
        _devLanguage.value = lang
        updateDevCode()
    }

    fun setDevStructure(struct: ExportStructure) {
        _devStructure.value = struct
        updateDevCode()
    }

    fun setIncludeAnsi(include: Boolean) {
        _includeAnsi.value = include
        updateDevCode()
    }

    fun setIncludeCli(include: Boolean) {
        _includeCli.value = include
        updateDevCode()
    }

    fun setGallerySection(section: GallerySection) {
        _gallerySection.value = section
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedGalleryCategory(cat: String) {
        _selectedGalleryCategory.value = cat
    }

    fun setSelectedTemplateCategory(cat: String) {
        _selectedTemplateCategory.value = cat
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    // --- Core Computation ---

    fun recomputeAscii() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = if (_inputMode.value == MainInputMode.IMAGE) {
                val bmp = _sourceBitmap.value ?: SampleImageGenerator.generateCyberPortrait()
                AsciiConverter.convertBitmap(
                    bitmap = bmp,
                    targetColumns = _targetColumns.value,
                    ramp = _characterRamp.value,
                    colorMode = _colorMode.value,
                    dither = _ditherAlgorithm.value,
                    contrast = _contrast.value,
                    gamma = _gamma.value,
                    invert = _invertLuminance.value,
                    sourceTitle = _sourceFileName.value
                )
            } else {
                // FIGlet rendering
                val bannerText = FigletGenerator.render(_figletText.value, _selectedFigletFont.value)
                val lines = bannerText.lines()
                val rows = lines.size
                val cols = lines.maxOfOrNull { it.length } ?: 1
                val colors = Array(rows) { y ->
                    IntArray(cols) { x ->
                        when (_colorMode.value) {
                            AsciiColorMode.GREEN_PHOSPHOR -> android.graphics.Color.parseColor("#3FB950")
                            AsciiColorMode.AMBER_VINTAGE -> android.graphics.Color.parseColor("#D29922")
                            AsciiColorMode.ANSI_TRUECOLOR -> android.graphics.Color.parseColor("#58A6FF")
                            AsciiColorMode.CYBERPUNK_NEON -> if (x % 2 == 0) android.graphics.Color.parseColor("#58A6FF") else android.graphics.Color.parseColor("#FF007F")
                        }
                    }
                }
                AsciiMatrixResult(
                    lines = lines,
                    colors = colors,
                    cols = cols,
                    rows = rows,
                    glyphCount = lines.sumOf { it.length },
                    renderTimeMs = 4,
                    sourceName = "${_figletText.value}.figlet",
                    sourceResolution = "${cols}x${rows}"
                )
            }

            _asciiResult.value = result
            updateDevCode()
        }
    }

    private fun updateDevCode() {
        val matrix = _asciiResult.value
        _generatedDevCode.value = CodeExporter.generateCode(
            matrix = matrix,
            language = _devLanguage.value,
            structure = _devStructure.value,
            includeAnsi = _includeAnsi.value,
            includeCliFunction = _includeCli.value
        )
    }

    // --- Core Computation & History Recording ---

    fun convertAndSaveToHistory(showToast: Boolean = true) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = if (_inputMode.value == MainInputMode.IMAGE) {
                val bmp = _sourceBitmap.value ?: SampleImageGenerator.generateCyberPortrait()
                AsciiConverter.convertBitmap(
                    bitmap = bmp,
                    targetColumns = _targetColumns.value,
                    ramp = _characterRamp.value,
                    colorMode = _colorMode.value,
                    dither = _ditherAlgorithm.value,
                    contrast = _contrast.value,
                    gamma = _gamma.value,
                    invert = _invertLuminance.value,
                    sourceTitle = _sourceFileName.value
                )
            } else {
                val bannerText = FigletGenerator.render(_figletText.value, _selectedFigletFont.value)
                val lines = bannerText.lines()
                val rows = lines.size
                val cols = lines.maxOfOrNull { it.length } ?: 1
                val colors = Array(rows) { y ->
                    IntArray(cols) { x ->
                        when (_colorMode.value) {
                            AsciiColorMode.GREEN_PHOSPHOR -> android.graphics.Color.parseColor("#3FB950")
                            AsciiColorMode.AMBER_VINTAGE -> android.graphics.Color.parseColor("#D29922")
                            AsciiColorMode.ANSI_TRUECOLOR -> android.graphics.Color.parseColor("#58A6FF")
                            AsciiColorMode.CYBERPUNK_NEON -> if (x % 2 == 0) android.graphics.Color.parseColor("#58A6FF") else android.graphics.Color.parseColor("#FF007F")
                        }
                    }
                }
                AsciiMatrixResult(
                    lines = lines,
                    colors = colors,
                    cols = cols,
                    rows = rows,
                    glyphCount = lines.sumOf { it.length },
                    renderTimeMs = 4,
                    sourceName = "${_figletText.value}.figlet",
                    sourceResolution = "${cols}x${rows}"
                )
            }

            _asciiResult.value = result
            updateDevCode()

            // Automatically record conversion to Room database
            val computedTitle = if (_inputMode.value == MainInputMode.TEXT) {
                "FIGlet_${_figletText.value.trim().ifBlank { "Banner" }.replace(" ", "_")}"
            } else {
                result.sourceName.ifBlank { "Image_Art" }.replace(" ", "_")
            }

            val project = AsciiProjectEntity(
                title = computedTitle,
                asciiContent = result.fullText,
                colorHex = when (_colorMode.value) {
                    AsciiColorMode.GREEN_PHOSPHOR -> "#3FB950"
                    AsciiColorMode.AMBER_VINTAGE -> "#D29922"
                    AsciiColorMode.ANSI_TRUECOLOR -> "#58A6FF"
                    AsciiColorMode.CYBERPUNK_NEON -> "#BC8CFF"
                },
                cols = result.cols,
                rows = result.rows,
                glyphCount = result.glyphCount,
                category = if (_inputMode.value == MainInputMode.TEXT) "Bannière FIGlet" else if (_colorMode.value == AsciiColorMode.GREEN_PHOSPHOR || _colorMode.value == AsciiColorMode.AMBER_VINTAGE) "Monochrome" else "Couleurs ANSI",
                isFavorite = false,
                isFeatured = false,
                dateLabel = getFormattedDate(),
                tagSubtitle = if (_inputMode.value == MainInputMode.TEXT) "${_selectedFigletFont.value.label} • FIGlet Typo" else "${_characterRamp.value.name} • ${_ditherAlgorithm.value.label}",
                fileSizeFormatted = "${(result.fullText.length / 1024f).let { String.format("%.1f KB", it) }}"
            )
            repository.saveProject(project)

            if (showToast) {
                withContext(Dispatchers.Main) {
                    _toastMessage.value = "Conversion terminée & enregistrée dans la Galerie !"
                }
            }
        }
    }

    // --- Save to Gallery (Room) ---

    fun saveCurrentToGallery() {
        viewModelScope.launch(Dispatchers.IO) {
            val matrix = _asciiResult.value
            val computedTitle = if (_inputMode.value == MainInputMode.TEXT) {
                "FIGlet_${_figletText.value.trim().ifBlank { "Banner" }.replace(" ", "_")}"
            } else {
                matrix.sourceName.ifBlank { "Image_Art" }.replace(" ", "_")
            }

            val project = AsciiProjectEntity(
                title = computedTitle,
                asciiContent = matrix.fullText,
                colorHex = when (_colorMode.value) {
                    AsciiColorMode.GREEN_PHOSPHOR -> "#3FB950"
                    AsciiColorMode.AMBER_VINTAGE -> "#D29922"
                    AsciiColorMode.ANSI_TRUECOLOR -> "#58A6FF"
                    AsciiColorMode.CYBERPUNK_NEON -> "#BC8CFF"
                },
                cols = matrix.cols,
                rows = matrix.rows,
                glyphCount = matrix.glyphCount,
                category = if (_inputMode.value == MainInputMode.TEXT) "Bannière FIGlet" else if (_colorMode.value == AsciiColorMode.GREEN_PHOSPHOR || _colorMode.value == AsciiColorMode.AMBER_VINTAGE) "Monochrome" else "Couleurs ANSI",
                isFavorite = false,
                isFeatured = false,
                dateLabel = getFormattedDate(),
                tagSubtitle = if (_inputMode.value == MainInputMode.TEXT) "${_selectedFigletFont.value.label} • FIGlet Typo" else "${_characterRamp.value.name} • ${_ditherAlgorithm.value.label}",
                fileSizeFormatted = "${(matrix.fullText.length / 1024f).let { String.format("%.1f KB", it) }}"
            )
            repository.saveProject(project)
            withContext(Dispatchers.Main) {
                _toastMessage.value = "Projet sauvegardé dans Mes Créations !"
            }
        }
    }

    fun toggleFavorite(project: AsciiProjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProject(project.copy(isFavorite = !project.isFavorite))
        }
    }

    fun deleteProject(project: AsciiProjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProject(project)
            withContext(Dispatchers.Main) {
                _toastMessage.value = "Projet '${project.title}' supprimé"
            }
        }
    }

    fun loadProjectIntoStudio(project: AsciiProjectEntity) {
        val lines = project.asciiContent.lines()
        val rows = lines.size
        val cols = lines.maxOfOrNull { it.length } ?: 1
        val colorInt = android.graphics.Color.parseColor(project.colorHex)
        val colors = Array(rows) { IntArray(cols) { colorInt } }

        _asciiResult.value = AsciiMatrixResult(
            lines = lines,
            colors = colors,
            cols = project.cols,
            rows = project.rows,
            glyphCount = project.glyphCount,
            renderTimeMs = 5,
            sourceName = project.title,
            sourceResolution = "${project.cols}x${project.rows}"
        )
        _selectedTab.value = 0
        updateDevCode()
        _toastMessage.value = "Projet '${project.title}' chargé dans le Studio"
    }

    fun loadTemplateIntoStudio(template: AsciiTemplate) {
        val lines = template.lines
        val rows = template.rows
        val cols = template.cols
        val colorInt = android.graphics.Color.parseColor(template.colorHex)
        val colors = Array(rows) { IntArray(cols) { colorInt } }

        _colorMode.value = template.colorMode
        _characterRamp.value = template.characterRamp
        _contrast.value = template.contrast
        _ditherAlgorithm.value = template.ditherAlgorithm
        _targetColumns.value = template.targetColumns

        if (template.type == TemplateType.TEXT_TO_ASCII) {
            _inputMode.value = MainInputMode.TEXT
            if (template.textSource != null) {
                _figletText.value = template.textSource
            }
            if (template.figletFont != null) {
                _selectedFigletFont.value = template.figletFont
            }
        } else {
            _inputMode.value = MainInputMode.IMAGE
        }

        _asciiResult.value = AsciiMatrixResult(
            lines = lines,
            colors = colors,
            cols = cols,
            rows = rows,
            glyphCount = template.glyphCount,
            renderTimeMs = 4,
            sourceName = template.title.replace(" ", "_"),
            sourceResolution = "${cols}x${rows}"
        )

        _selectedTab.value = 0
        updateDevCode()
        _toastMessage.value = "Modèle '${template.title}' chargé dans le Studio !"
    }

    fun saveArtworkToPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val matrix = _asciiResult.value
                val bmp = ImageExporter.createArtworkBitmap(
                    matrix = matrix,
                    ambiance = _exportAmbiance.value,
                    includeWatermark = _exportWatermark.value,
                    scaleMultiplier = 3 // High resolution HD
                )
                val uri = ImageExporter.saveBitmapToGallery(getApplication(), bmp, matrix.sourceName)
                withContext(Dispatchers.Main) {
                    if (uri != null) {
                        _toastMessage.value = "Image HD enregistrée dans Photos !"
                    } else {
                        _toastMessage.value = "Erreur lors de l'enregistrement."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _toastMessage.value = "Erreur: ${e.message}"
                }
            }
        }
    }
}
