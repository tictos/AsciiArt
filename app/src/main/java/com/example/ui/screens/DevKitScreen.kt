package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.engine.ImageExporter
import com.example.domain.model.ExportLanguage
import com.example.domain.model.ExportStructure
import com.example.ui.components.MacOsControlDots
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.CyanCyber
import com.example.ui.theme.DevKitPurple
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceLowest
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalGreenBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AsciiCraftViewModel

@Composable
fun DevKitScreen(
    viewModel: AsciiCraftViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val asciiResult by viewModel.asciiResult.collectAsStateWithLifecycle()
    val devLanguage by viewModel.devLanguage.collectAsStateWithLifecycle()
    val devStructure by viewModel.devStructure.collectAsStateWithLifecycle()
    val includeAnsi by viewModel.includeAnsi.collectAsStateWithLifecycle()
    val includeCli by viewModel.includeCli.collectAsStateWithLifecycle()
    val generatedCode by viewModel.generatedDevCode.collectAsStateWithLifecycle()

    var isExpandedCode by remember { mutableStateOf(false) }
    var isFullscreenCodeOpen by remember { mutableStateOf(false) }
    var codeFontSize by remember { mutableStateOf(10) }

    if (isFullscreenCodeOpen) {
        Dialog(
            onDismissRequest = { isFullscreenCodeOpen = false },
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = { isFullscreenCodeOpen = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextPrimary)
                            }
                            Column {
                                Text(
                                    text = "export_${asciiResult.sourceName.substringBefore(".")}${devLanguage.extension}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${generatedCode.lines().size} lignes • ${(generatedCode.length / 1024f).let { String.format("%.1f KB", it) }} • UTF-8",
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
                                    clipboardManager.setText(AnnotatedString(generatedCode))
                                    viewModel.showToast("Code ${devLanguage.displayName} copié !")
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TerminalGreen, modifier = Modifier.size(18.dp))
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { codeFontSize = (codeFontSize - 1).coerceAtLeast(8) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom -", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${codeFontSize}pt",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = { codeFontSize = (codeFontSize + 1).coerceAtMost(16) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom +", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Fullscreen Code Area with Line Numbers & 2D Scrolling
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(SurfaceLowest)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        val codeLines = generatedCode.lines()
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Line numbers column
                            Column(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                            ) {
                                codeLines.forEachIndexed { idx, _ ->
                                    Text(
                                        text = String.format("%03d", idx + 1),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = codeFontSize.sp,
                                        lineHeight = (codeFontSize + 4).sp,
                                        color = TextSecondary.copy(alpha = 0.45f)
                                    )
                                }
                            }

                            // Code Syntax Highlighted Column
                            Column {
                                codeLines.forEach { line ->
                                    Text(
                                        text = formatSyntaxHighlight(line),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = codeFontSize.sp,
                                        lineHeight = (codeFontSize + 4).sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Défilement vertical et horizontal 2D actif",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TerminalGreen)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(generatedCode))
                                    viewModel.showToast("Code ${devLanguage.displayName} copié !")
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "TOUT COPIER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceLowest
                            )
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .testTag("devkit_screen_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export Code Source",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DevKitPurple.copy(alpha = 0.2f))
                            .border(1.dp, DevKitPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DEVKIT V1.4",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DevKitPurple
                        )
                    }
                }
                Text(
                    text = "${asciiResult.sourceName} • ${asciiResult.cols}x${asciiResult.rows} chars • ANSI UTF-8",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // 2. Language Selector Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportLanguage.values().forEach { lang ->
                    val isSelected = devLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TerminalGreen else SurfaceContainerHigh)
                            .border(
                                1.dp,
                                if (isSelected) TerminalGreenBorder else BorderDefault,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setDevLanguage(lang) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("lang_tab_${lang.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = "${lang.displayName} (${lang.extension})",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurfaceLowest else TextPrimary
                            )
                            Text(
                                text = lang.versionTag,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = if (isSelected) SurfaceLowest.copy(alpha = 0.8f) else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Export Structure Options
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("devkit_structure_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "STRUCTURE D'EXPORTATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExportStructure.values().forEach { struct ->
                            val isSelected = devStructure == struct
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanCyber.copy(alpha = 0.2f) else SurfaceContainerHigh)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanCyber else BorderDefault,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setDevStructure(struct) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column {
                                    Text(
                                        text = struct.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyanCyber else TextPrimary
                                    )
                                    Text(
                                        text = struct.subtitle,
                                        fontSize = 9.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Toggle Options
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIncludeAnsi(!includeAnsi) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = includeAnsi,
                                onCheckedChange = { viewModel.setIncludeAnsi(it) },
                                colors = CheckboxDefaults.colors(checkedColor = TerminalGreen)
                            )
                            Column {
                                Text("Couleurs ANSI (\\033[...m)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Intègre la palette 24-bit truecolor dans les séquences d'échappement", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIncludeCli(!includeCli) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = includeCli,
                                onCheckedChange = { viewModel.setIncludeCli(it) },
                                colors = CheckboxDefaults.colors(checkedColor = CyanCyber)
                            )
                            Column {
                                Text("Fonction CLI print_canvas()", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Génère l'exécution prête à l'emploi avec animation de balayage", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 4. Terminal Code Window
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("devkit_code_window"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Window Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacOsControlDots()

                        Text(
                            text = "export_${asciiResult.sourceName.substringBefore(".")}${devLanguage.extension}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { isFullscreenCodeOpen = true },
                                modifier = Modifier.size(28.dp).testTag("fullscreen_code_btn")
                            ) {
                                Icon(Icons.Default.Fullscreen, contentDescription = "Plein écran", tint = TerminalGreen, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { isExpandedCode = !isExpandedCode },
                                modifier = Modifier.size(28.dp).testTag("toggle_expand_code_btn")
                            ) {
                                Icon(
                                    if (isExpandedCode) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                                    contentDescription = if (isExpandedCode) "Réduire" else "Étendre tout",
                                    tint = CyanCyber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedCode))
                                    viewModel.showToast("Code source copié !")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier code", tint = TextPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Code Body with Line Numbers & 2D Scrolling
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isExpandedCode) {
                                    Modifier.heightIn(min = 320.dp, max = 680.dp)
                                } else {
                                    Modifier.height(280.dp)
                                }
                            )
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        val codeLines = generatedCode.lines()
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Line numbers column
                            Column(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .border(width = 0.dp, color = Color.Transparent)
                            ) {
                                codeLines.forEachIndexed { idx, _ ->
                                    Text(
                                        text = String.format("%02d", idx + 1),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        color = TextSecondary.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Code Syntax Highlighted Column
                            Column {
                                codeLines.forEach { line ->
                                    Text(
                                        text = formatSyntaxHighlight(line),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom info bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(generatedCode.length / 1024f).let { String.format("%.1f KB", it) }} • ${generatedCode.lines().size} lignes • UTF-8",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceContainerHigh)
                                    .clickable { isExpandedCode = !isExpandedCode }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isExpandedCode) "RÉDUIRE" else "DÉROULER TOUT",
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
                                    .clickable { isFullscreenCodeOpen = true }
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

        // 5. Developer Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(generatedCode))
                        viewModel.showToast("Code ${devLanguage.displayName} copié avec succès !")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("copy_dev_code_btn"),
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
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = "COPIER LE CODE ${devLanguage.displayName.uppercase()}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            ImageExporter.shareText(
                                context,
                                generatedCode,
                                "AsciiCraft export_${devLanguage.displayName}${devLanguage.extension}"
                            )
                        },
                        modifier = Modifier.weight(1f).testTag("share_script_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Partager fichier", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val gistContent = "```${devLanguage.name.lowercase()}\n$generatedCode\n```"
                            clipboardManager.setText(AnnotatedString(gistContent))
                            viewModel.showToast("Format Gist copié dans le presse-papier !")
                        },
                        modifier = Modifier.weight(1f).testTag("copy_gist_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = AmberCRT, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Créer Gist", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun formatSyntaxHighlight(line: String): AnnotatedString {
    return buildAnnotatedString {
        when {
            line.trimStart().startsWith("#") || line.trimStart().startsWith("//") || line.trimStart().startsWith("/*") || line.trimStart().startsWith("*") -> {
                pushStyle(SpanStyle(color = Color(0xFF8B949E)))
                append(line)
                pop()
            }
            line.contains("import ") || line.contains("from ") || line.contains("def ") || line.contains("pub fn") || line.contains("const ") || line.contains("export ") || line.contains("package ") -> {
                pushStyle(SpanStyle(color = Color(0xFFFF7B72), fontWeight = FontWeight.Bold))
                append(line)
                pop()
            }
            line.contains("\"") || line.contains("`") -> {
                pushStyle(SpanStyle(color = Color(0xFF7EE787)))
                append(line)
                pop()
            }
            else -> {
                pushStyle(SpanStyle(color = Color(0xFFF0F6FC)))
                append(line)
                pop()
            }
        }
    }
}
