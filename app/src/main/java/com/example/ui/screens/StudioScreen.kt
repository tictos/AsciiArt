package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AsciiColorMode
import com.example.domain.model.CharacterRamp
import com.example.domain.model.DitherAlgorithm
import com.example.domain.model.FigletFontType
import com.example.ui.components.CustomRampDialog
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanCyber
import com.example.ui.theme.MagentaNeon
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceLowest
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalGreenBorder
import com.example.ui.theme.TerminalGreenDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AsciiCraftViewModel
import com.example.ui.viewmodel.CanvasViewMode
import com.example.ui.viewmodel.MainInputMode

@Composable
fun StudioScreen(
    viewModel: AsciiCraftViewModel,
    onNavigateToExport: () -> Unit
) {
    val inputMode by viewModel.inputMode.collectAsStateWithLifecycle()
    val sourceFileName by viewModel.sourceFileName.collectAsStateWithLifecycle()
    val sourceResolution by viewModel.sourceResolution.collectAsStateWithLifecycle()
    val figletText by viewModel.figletText.collectAsStateWithLifecycle()
    val selectedFigletFont by viewModel.selectedFigletFont.collectAsStateWithLifecycle()
    val targetColumns by viewModel.targetColumns.collectAsStateWithLifecycle()
    val contrast by viewModel.contrast.collectAsStateWithLifecycle()
    val gamma by viewModel.gamma.collectAsStateWithLifecycle()
    val invertLuminance by viewModel.invertLuminance.collectAsStateWithLifecycle()
    val characterRamp by viewModel.characterRamp.collectAsStateWithLifecycle()
    val colorMode by viewModel.colorMode.collectAsStateWithLifecycle()
    val ditherAlgorithm by viewModel.ditherAlgorithm.collectAsStateWithLifecycle()
    val canvasViewMode by viewModel.canvasViewMode.collectAsStateWithLifecycle()
    val zoomLevel by viewModel.zoomLevel.collectAsStateWithLifecycle()
    val isCrtScanlines by viewModel.isCrtScanlineEnabled.collectAsStateWithLifecycle()
    val asciiResult by viewModel.asciiResult.collectAsStateWithLifecycle()
    val customRampOpen by viewModel.customRampDialogOpen.collectAsStateWithLifecycle()
    val customRampText by viewModel.customRampText.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    var isExpandedCanvas by remember { mutableStateOf(false) }
    var isFullscreenDialogOpen by remember { mutableStateOf(false) }
    var fullscreenZoom by remember { mutableStateOf(0.7f) }

    // Media & Camera Launchers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.setSourceImageUri(uri)
        }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            if (bitmap != null) viewModel.setSourceBitmap(bitmap)
        }
    )

    if (customRampOpen) {
        CustomRampDialog(
            initialText = customRampText,
            onDismiss = { viewModel.setCustomRampDialogOpen(false) },
            onApply = { newRamp -> viewModel.applyCustomRamp(newRamp) }
        )
    }

    if (isFullscreenDialogOpen) {
        Dialog(
            onDismissRequest = { isFullscreenDialogOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLowest)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Dialog Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { isFullscreenDialogOpen = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextPrimary)
                            }
                            Column {
                                Text(
                                    text = "Rendu ASCII Intégral",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${asciiResult.cols} cols × ${asciiResult.rows} lignes • ${asciiResult.glyphCount} glyphes",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = CyanCyber
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(asciiResult.fullText))
                                    viewModel.showToast("Texte ASCII copié !")
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TerminalGreen, modifier = Modifier.size(18.dp))
                            }

                            listOf(0.4f to "40%", 0.7f to "70%", 1.0f to "100%").forEach { (z, label) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (fullscreenZoom == z) TerminalGreen else SurfaceContainerHigh)
                                        .clickable { fullscreenZoom = z }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (fullscreenZoom == z) SurfaceLowest else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Main Fullscreen Scrollable Area (both Vertical and Horizontal)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(SurfaceLowest)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = asciiResult.fullText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = (8 * fullscreenZoom).sp,
                            lineHeight = (9 * fullscreenZoom).sp,
                            letterSpacing = 0.sp,
                            color = when (colorMode) {
                                AsciiColorMode.GREEN_PHOSPHOR -> TerminalGreen
                                AsciiColorMode.AMBER_VINTAGE -> AmberCRT
                                AsciiColorMode.ANSI_TRUECOLOR -> CyanCyber
                                AsciiColorMode.CYBERPUNK_NEON -> MagentaNeon
                            }
                        )

                        if (isCrtScanlines) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val step = 4.dp.toPx()
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(
                                        color = Color(0x103FB950),
                                        start = androidx.compose.ui.geometry.Offset(0f, y),
                                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    y += step
                                }
                            }
                        }
                    }

                    // Bottom info bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Défilement 2D (horizontal & vertical) actif",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { fullscreenZoom = (fullscreenZoom - 0.2f).coerceAtLeast(0.3f) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom -", tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "${(fullscreenZoom * 100).toInt()}%",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerminalGreen
                            )
                            IconButton(
                                onClick = { fullscreenZoom = (fullscreenZoom + 0.2f).coerceAtMost(2.5f) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom +", tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceBase)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Dual Mode Selector Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, BorderDefault, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isImageMode = inputMode == MainInputMode.IMAGE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isImageMode) TerminalGreen else Color.Transparent)
                            .clickable { viewModel.setInputMode(MainInputMode.IMAGE) }
                            .padding(vertical = 10.dp)
                            .testTag("mode_image_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = if (isImageMode) SurfaceLowest else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Image / Photo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isImageMode) SurfaceLowest else TextSecondary
                            )
                        }
                    }

                    val isTextMode = inputMode == MainInputMode.TEXT
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isTextMode) TerminalGreen else Color.Transparent)
                            .clickable { viewModel.setInputMode(MainInputMode.TEXT) }
                            .padding(vertical = 10.dp)
                            .testTag("mode_text_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = if (isTextMode) SurfaceLowest else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Texte / Bannière",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTextMode) SurfaceLowest else TextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Photo Source Input Card (When in Image Mode)
            if (inputMode == MainInputMode.IMAGE) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("photo_source_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Source Image Mini-Preview
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceLowest)
                                        .border(1.dp, CyanCyber.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = CyanCyber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(TerminalGreen)
                                        )
                                        Text(
                                            text = "PHOTO SOURCE • ACTIVE",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TerminalGreen
                                        )
                                    }
                                    Text(
                                        text = sourceFileName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "$sourceResolution • RAW Dither",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Import action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f).testTag("pick_gallery_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = TerminalGreen)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galerie Locale", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { takePictureLauncher.launch(null) },
                                    modifier = Modifier.weight(1f).testTag("take_photo_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanCyber)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Prendre Photo", fontSize = 12.sp)
                                }
                            }

                            Text(
                                text = "Importer vos photos JPG, PNG, WebP • Détection automatique du contraste",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            // 3. FIGlet Typo Engine Card (When in Text Mode)
            if (inputMode == MainInputMode.TEXT) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("figlet_generator_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.TextFields, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(18.dp))
                                    Text("Générateur FIGlet ASCII", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyanCyber.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("TYPO ENGINE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanCyber)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Texte à convertir en ASCII", fontSize = 12.sp, color = TextSecondary)
                                Text("Max 24 car.", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = figletText,
                                    onValueChange = { if (it.length <= 24) viewModel.setFigletText(it) },
                                    modifier = Modifier.weight(1f).testTag("figlet_input"),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TerminalGreen
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TerminalGreen,
                                        unfocusedBorderColor = BorderDefault,
                                        focusedContainerColor = SurfaceContainerHigh,
                                        unfocusedContainerColor = SurfaceContainerHigh
                                    )
                                )

                                Button(
                                    onClick = { viewModel.convertAndSaveToHistory(showToast = true) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TerminalGreen,
                                        contentColor = SurfaceLowest
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("figlet_render_btn")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Convertir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text("POLICE FIGLET :", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)

                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FigletFontType.values().forEach { font ->
                                    val isSelected = selectedFigletFont == font
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) CyanCyber else SurfaceContainerHigh)
                                            .clickable { viewModel.setFigletFont(font) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = font.label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) SurfaceLowest else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. ASCII Canvas Rendering Zone
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("ascii_canvas_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Canvas Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceContainerHigh)
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${asciiResult.cols}x${asciiResult.rows} CHARS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanCyber
                                    )
                                }
                                Text("•", color = TextSecondary, fontSize = 10.sp)
                                Text(
                                    text = "${String.format("%,d", asciiResult.glyphCount)} GLYPHS",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { isFullscreenDialogOpen = true },
                                    modifier = Modifier.size(28.dp).testTag("fullscreen_ascii_btn")
                                ) {
                                    Icon(Icons.Default.Fullscreen, contentDescription = "Plein écran", tint = TerminalGreen, modifier = Modifier.size(20.dp))
                                }

                                IconButton(
                                    onClick = { isExpandedCanvas = !isExpandedCanvas },
                                    modifier = Modifier.size(28.dp).testTag("toggle_expand_canvas_btn")
                                ) {
                                    Icon(
                                        if (isExpandedCanvas) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                                        contentDescription = if (isExpandedCanvas) "Réduire" else "Étendre intégrale",
                                        tint = CyanCyber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Terminal Box with Matrix Glyphs & CRT Scanlines
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (isExpandedCanvas) {
                                        Modifier.heightIn(min = 280.dp, max = 650.dp)
                                    } else {
                                        Modifier.height(250.dp)
                                    }
                                )
                                .background(SurfaceLowest)
                                .padding(8.dp)
                        ) {
                            // Top left coordinate
                            Text(
                                text = if (isExpandedCanvas) "[0,0] • VUE INTÉGRALE" else "[0,0]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = if (isExpandedCanvas) CyanCyber else BorderDefault,
                                modifier = Modifier.align(Alignment.TopStart)
                            )

                            // Top right HUD: status & lines
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceContainerHigh.copy(alpha = 0.85f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(5.dp).clip(CircleShape).background(TerminalGreen)
                                )
                                Text(
                                    text = if (isExpandedCanvas) "${asciiResult.rows} LIGNES (TOUT VISIBLE)" else "DÉFILABLE 2D",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                            }

                            // Render Monospace ASCII Matrix Text in scrollable area (both Vertical and Horizontal)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 18.dp, bottom = 28.dp)
                                    .verticalScroll(rememberScrollState())
                                    .horizontalScroll(rememberScrollState()),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text(
                                    text = asciiResult.fullText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = (9 * zoomLevel).sp,
                                    lineHeight = (10 * zoomLevel).sp,
                                    letterSpacing = 0.sp,
                                    color = when (colorMode) {
                                        AsciiColorMode.GREEN_PHOSPHOR -> TerminalGreen
                                        AsciiColorMode.AMBER_VINTAGE -> AmberCRT
                                        AsciiColorMode.ANSI_TRUECOLOR -> CyanCyber
                                        AsciiColorMode.CYBERPUNK_NEON -> MagentaNeon
                                    },
                                    modifier = Modifier.testTag("ascii_render_matrix")
                                )
                            }

                            // Subtle Scanline Overlay Effect
                            if (isCrtScanlines) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val step = 4.dp.toPx()
                                    var y = 0f
                                    while (y < size.height) {
                                        drawLine(
                                            color = Color(0x103FB950),
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(size.width, y),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                        y += step
                                    }
                                }
                            }

                            // Bottom inspection toolbar
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { viewModel.setZoomLevel((zoomLevel - 0.2f).coerceAtLeast(0.5f)) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom -", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${(zoomLevel * 100).toInt()}%",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                    IconButton(
                                        onClick = { viewModel.setZoomLevel((zoomLevel + 0.2f).coerceAtMost(2.5f)) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom +", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.setZoomLevel(1f) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.FitScreen, contentDescription = "Fit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainerHigh)
                                            .clickable { isExpandedCanvas = !isExpandedCanvas }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isExpandedCanvas) "RÉDUIRE" else "DÉROULER TOUT",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanCyber
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(TerminalGreen.copy(alpha = 0.2f))
                                            .clickable { isFullscreenDialogOpen = true }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "PLEIN ÉCRAN",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TerminalGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Jeu de Caractères (Character Ramps) - Uniquement pour Image-to-ASCII
            if (inputMode == MainInputMode.IMAGE) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("character_ramp_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TerminalGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("A", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                                }
                                Text("Jeu de Caractères", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Row(
                                modifier = Modifier.clickable { viewModel.setCustomRampDialogOpen(true) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(14.dp))
                                Text("Éditer Ramp", fontSize = 12.sp, color = CyanCyber, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Presets Row
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CharacterRamp.PRESETS.forEach { preset ->
                                val isSelected = characterRamp.id == preset.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) TerminalGreen else SurfaceContainerHigh)
                                        .border(1.dp, if (isSelected) TerminalGreenBorder else BorderDefault, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setCharacterRamp(preset) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = preset.name,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SurfaceLowest else TextPrimary
                                        )
                                        Text(
                                            text = preset.rampChars.take(8),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = if (isSelected) SurfaceLowest.copy(alpha = 0.8f) else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Active Ramp Display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Ramp active :", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = characterRamp.rampChars.chunked(1).joinToString(" "),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                            }
                            IconButton(
                                onClick = { viewModel.reverseActiveRamp() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Inverser rampe", tint = CyanCyber, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

            // 6. Calibrage Dynamique - Uniquement pour Image-to-ASCII
            if (inputMode == MainInputMode.IMAGE) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("calibrage_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calibrage Dynamique", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TerminalGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("RASTER ENGINE V2", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                            }
                        }

                        // Résolution / Densité Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Résolution / Densité", fontSize = 12.sp, color = TextPrimary)
                                Text(
                                    text = "${((targetColumns - 20) * 100 / 120)}% ($targetColumns cols)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                            }
                            Slider(
                                value = targetColumns.toFloat(),
                                onValueChange = { viewModel.setTargetColumns(it.toInt()) },
                                valueRange = 30f..140f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = TerminalGreen,
                                    activeTrackColor = TerminalGreen,
                                    inactiveTrackColor = SurfaceContainerHigh
                                )
                            )
                        }

                        // Contraste / Gamma Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Contraste / Gamma", fontSize = 12.sp, color = TextPrimary)
                                Text(
                                    text = "${if (contrast >= 1f) "+" else ""}${((contrast - 1f) * 100).toInt()}% (${String.format("%.2f", contrast)}γ)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanCyber
                                )
                            }
                            Slider(
                                value = contrast,
                                onValueChange = { viewModel.setContrast(it) },
                                valueRange = 0.5f..2.0f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanCyber,
                                    activeTrackColor = CyanCyber,
                                    inactiveTrackColor = SurfaceContainerHigh
                                )
                            )
                        }

                        // Inversion Négatif Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Inversion Négatif", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Inverse la luminance des glyphes", fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = invertLuminance,
                                onCheckedChange = { viewModel.setInvertLuminance(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TerminalGreen,
                                    checkedTrackColor = TerminalGreenDark.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = SurfaceContainerHigh
                                )
                            )
                        }
                    }
                }
            }
        }

            // 7. Palette & Matrice de Couleur
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("palette_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Palette & Matrice de Couleur", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AmberCRT.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("RGB 24-BIT", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberCRT)
                            }
                        }

                        // 2x2 Grid of Color Mode Cards
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorModeItem(
                                    mode = AsciiColorMode.GREEN_PHOSPHOR,
                                    isSelected = colorMode == AsciiColorMode.GREEN_PHOSPHOR,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setColorMode(AsciiColorMode.GREEN_PHOSPHOR) }
                                )
                                ColorModeItem(
                                    mode = AsciiColorMode.AMBER_VINTAGE,
                                    isSelected = colorMode == AsciiColorMode.AMBER_VINTAGE,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setColorMode(AsciiColorMode.AMBER_VINTAGE) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorModeItem(
                                    mode = AsciiColorMode.ANSI_TRUECOLOR,
                                    isSelected = colorMode == AsciiColorMode.ANSI_TRUECOLOR,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setColorMode(AsciiColorMode.ANSI_TRUECOLOR) }
                                )
                                ColorModeItem(
                                    mode = AsciiColorMode.CYBERPUNK_NEON,
                                    isSelected = colorMode == AsciiColorMode.CYBERPUNK_NEON,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setColorMode(AsciiColorMode.CYBERPUNK_NEON) }
                                )
                            }
                        }
                    }
                }
            }

            // 8. Algorithme de Tramage (Dithering) - Uniquement pour Image-to-ASCII
            if (inputMode == MainInputMode.IMAGE) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("dithering_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Algorithme de Tramage", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Dithering", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DitherAlgorithm.values().forEach { dither ->
                                    val isSelected = ditherAlgorithm == dither
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) TerminalGreen.copy(alpha = 0.15f) else SurfaceContainerHigh)
                                            .border(
                                                1.dp,
                                                if (isSelected) TerminalGreen else BorderDefault,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.setDitherAlgorithm(dither) }
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = dither.label,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) TerminalGreen else TextPrimary
                                            )
                                            Text(
                                                text = dither.subtitle,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fixed Sticky Bottom Action Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .border(1.dp, BorderDefault.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = { isFullscreenDialogOpen = true },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                    .size(44.dp)
                    .testTag("fullscreen_dialog_trigger_btn")
            ) {
                Icon(Icons.Default.Fullscreen, contentDescription = "Plein écran", tint = TerminalGreen)
            }

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(asciiResult.fullText))
                    viewModel.showToast("Texte ASCII copié dans le presse-papier !")
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                    .size(44.dp)
                    .testTag("quick_copy_raw_btn")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TextPrimary)
            }

            IconButton(
                onClick = { viewModel.saveCurrentToGallery() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                    .size(44.dp)
                    .testTag("quick_save_gallery_btn")
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Sauvegarder dans la galerie", tint = AmberCRT)
            }

            Button(
                onClick = onNavigateToExport,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("finalize_export_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerminalGreen,
                    contentColor = SurfaceLowest
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FINALISER & EXPORTER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ColorModeItem(
    mode: AsciiColorMode,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SurfaceContainerHigh else SurfaceContainerLow)
            .border(
                1.dp,
                if (isSelected) mode.dotColor else BorderDefault,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(mode.dotColor)
            )
            Column {
                Text(
                    text = mode.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = mode.subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
