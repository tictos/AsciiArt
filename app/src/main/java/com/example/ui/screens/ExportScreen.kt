package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.engine.ImageExporter
import com.example.domain.model.ExportBackgroundAmbiance
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanCyber
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

@Composable
fun ExportScreen(
    viewModel: AsciiCraftViewModel,
    onNewProject: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val asciiResult by viewModel.asciiResult.collectAsStateWithLifecycle()
    val exportAmbiance by viewModel.exportAmbiance.collectAsStateWithLifecycle()
    val exportWatermark by viewModel.exportWatermark.collectAsStateWithLifecycle()
    val exportFormat by viewModel.exportFormat.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .testTag("export_screen_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TerminalGreen))
                    Text(
                        text = "PRÊT POUR DIFFUSION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGreen
                    )
                }
                Text(
                    text = "Exporter ma Création",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Enregistrez votre art en image HD ou texte partageable.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // 2. Artwork Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("export_preview_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Preview Header
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
                                    .background(CyanCyber.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = asciiResult.sourceName.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanCyber
                                )
                            }
                            Text(
                                text = "${asciiResult.cols}x${asciiResult.rows} ch",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(TerminalGreen))
                            Text(
                                text = "HDR Buffer",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TerminalGreen
                            )
                        }
                    }

                    // Render Preview Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                when (exportAmbiance) {
                                    ExportBackgroundAmbiance.OLED -> SurfaceLowest
                                    ExportBackgroundAmbiance.CRT_GREEN -> Color(0xFF08120C)
                                    ExportBackgroundAmbiance.ALPHA -> Color(0xFF161B22)
                                    ExportBackgroundAmbiance.CYBER -> SurfaceLowest
                                }
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState()),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Text(
                                text = asciiResult.fullText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                lineHeight = 9.sp,
                                letterSpacing = 0.sp,
                                color = when (exportAmbiance) {
                                    ExportBackgroundAmbiance.CRT_GREEN -> TerminalGreen
                                    ExportBackgroundAmbiance.CYBER -> CyanCyber
                                    else -> TerminalGreen
                                }
                            )
                        }

                        // Watermark tag
                        if (exportWatermark) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "asciicraft.art",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Ambiance Selector Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AMBIANCE DE FOND", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                            Text(exportAmbiance.subtitle, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyanCyber)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ExportBackgroundAmbiance.values().forEach { ambiance ->
                                val isSelected = exportAmbiance == ambiance
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SurfaceBright else SurfaceContainerHigh)
                                        .border(
                                            1.dp,
                                            if (isSelected) TerminalGreen else BorderDefault,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setExportAmbiance(ambiance) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (ambiance) {
                                                        ExportBackgroundAmbiance.OLED -> Color.Black
                                                        ExportBackgroundAmbiance.CRT_GREEN -> TerminalGreen
                                                        ExportBackgroundAmbiance.ALPHA -> TextSecondary
                                                        ExportBackgroundAmbiance.CYBER -> CyanCyber
                                                    }
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ambiance.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TextPrimary else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Export Image & Réseaux
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("export_image_card"),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = TerminalGreen, modifier = Modifier.size(18.dp))
                            Text("Export Image & Réseaux", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TerminalGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Sans compression", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                        }
                    }

                    Text("FORMAT & RÉSOLUTION", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "PNG 4K" to "Wallpaper",
                            "SVG" to "Vectoriel",
                            "GIF CRT" to "Scanlines"
                        ).forEach { (fmt, sub) ->
                            val isSelected = exportFormat == fmt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TerminalGreen else SurfaceContainerHigh)
                                    .border(1.dp, if (isSelected) TerminalGreenBorder else BorderDefault, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setExportFormat(fmt) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = fmt,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SurfaceLowest else TextPrimary
                                    )
                                    Text(
                                        text = sub,
                                        fontSize = 9.sp,
                                        color = if (isSelected) SurfaceLowest.copy(alpha = 0.8f) else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.saveArtworkToPhotos() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_photos_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerminalGreen,
                            contentColor = SurfaceLowest
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("ENREGISTRER DANS PHOTOS", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Export Texte & Presse-papier 1-TAP COPY
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("export_text_card"),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(18.dp))
                            Text("Export Texte & Presse-papier", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text("1-TAP COPY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanCyber)
                    }

                    Text(
                        text = "Prêt à coller sur vos plateformes favorites avec le formattage monospacé adapté.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Discord Markdown Block
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                                .clickable {
                                    val discordFormat = "```\n${asciiResult.fullText}\n```"
                                    clipboardManager.setText(AnnotatedString(discordFormat))
                                    viewModel.showToast("Format Discord copié !")
                                }
                                .padding(10.dp)
                                .testTag("copy_discord_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Forum, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Discord", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Code block", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        // Copier Brut UTF-8
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(asciiResult.fullText))
                                    viewModel.showToast("Texte brut copié !")
                                }
                                .padding(10.dp)
                                .testTag("copy_raw_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TerminalGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Copier Brut", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("UTF-8", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        // Fichier .txt Share
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                                .clickable {
                                    ImageExporter.shareText(context, asciiResult.fullText, "Art ASCII AsciiCraft")
                                }
                                .padding(10.dp)
                                .testTag("share_txt_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = AmberCRT, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fichier .txt", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Télécharger", fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                    }

                    // Optimised channels footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Canaux optimisés :", fontSize = 10.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("WhatsApp", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextPrimary)
                            Text("Insta Bio", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextPrimary)
                            Text("X / Twitter", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 5. Signature & Tag Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Signature & Tag AsciiCraft", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Ajoute un filigrane discret pour créditer l'outil", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = exportWatermark,
                            onCheckedChange = { viewModel.setExportWatermark(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TerminalGreen,
                                checkedTrackColor = TerminalGreenDark.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceContainerHigh
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Taille de matrice calculée", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            "${asciiResult.cols} ch x ${asciiResult.rows} lg (${asciiResult.sourceResolution} px)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanCyber
                        )
                    }
                }
            }
        }

        // 6. Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.saveCurrentToGallery() },
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_gallery_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerHigh,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = TerminalGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAUVEGARDER DANS LA GALERIE LOCALE", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.setTab(0)
                        onNewProject()
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("new_project_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("NOUVEAU PROJET", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
