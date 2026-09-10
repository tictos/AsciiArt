package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AsciiProjectEntity
import com.example.domain.engine.ImageExporter
import com.example.domain.model.AsciiTemplate
import com.example.domain.model.AsciiTemplateCatalog
import com.example.domain.model.TemplateType
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.CyanCyber
import com.example.ui.theme.MagentaNeon
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceLowest
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalGreenBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AsciiCraftViewModel
import com.example.ui.viewmodel.GallerySection

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GalleryScreen(
    viewModel: AsciiCraftViewModel,
    onNavigateToStudio: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val gallerySection by viewModel.gallerySection.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedGalleryCategory.collectAsStateWithLifecycle()
    val selectedTemplateCategory by viewModel.selectedTemplateCategory.collectAsStateWithLifecycle()

    var projectToDelete by remember { mutableStateOf<AsciiProjectEntity?>(null) }
    var fullscreenPreviewText by remember { mutableStateOf<Pair<String, String>?>(null) }
    var fullscreenPreviewColor by remember { mutableStateOf("#3FB950") }

    // Dialog for Fullscreen ASCII Inspection
    if (fullscreenPreviewText != null) {
        val (title, content) = fullscreenPreviewText!!
        var previewFontSize by remember { mutableStateOf(8) }

        Dialog(
            onDismissRequest = { fullscreenPreviewText = null },
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
                    // Responsive Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { fullscreenPreviewText = null },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextPrimary)
                            }
                            Column {
                                Text(
                                    text = title,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Aperçu Plein Écran • ${content.lines().size} lignes",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = CyanCyber
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(content))
                                    viewModel.showToast("ASCII copié dans le presse-papier !")
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TerminalGreen, modifier = Modifier.size(16.dp))
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { previewFontSize = (previewFontSize - 1).coerceAtLeast(3) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom -", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = "${previewFontSize}pt",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = { previewFontSize = (previewFontSize + 1).coerceAtMost(18) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom +", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    // 2D Scrollable Full Canvas
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(SurfaceLowest)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState()),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = content,
                            fontFamily = FontFamily.Monospace,
                            fontSize = previewFontSize.sp,
                            lineHeight = (previewFontSize + 2).sp,
                            color = try {
                                Color(android.graphics.Color.parseColor(fullscreenPreviewColor))
                            } catch (e: Exception) {
                                TerminalGreen
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Delete
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = {
                Text("Supprimer la création ?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    "Voulez-vous vraiment supprimer définitivement '${projectToDelete?.title}' de vos créations locales ?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagentaNeon)
                ) {
                    Text("Supprimer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Annuler", color = TextSecondary, fontSize = 12.sp)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    // Filtering User Projects
    val filteredProjects = projects.filter { project ->
        val matchesSearch = project.title.contains(searchQuery, ignoreCase = true) ||
                project.tagSubtitle.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "Tous" -> true
            "Favoris ⭐" -> project.isFavorite
            else -> project.category.equals(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    // Filtering Inspiration Templates
    val allTemplates = AsciiTemplateCatalog.templates
    val filteredTemplates = allTemplates.filter { tpl ->
        val matchesSearch = tpl.title.contains(searchQuery, ignoreCase = true) ||
                tpl.description.contains(searchQuery, ignoreCase = true) ||
                tpl.tags.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesCategory = when (selectedTemplateCategory) {
            "Tous les modèles" -> true
            "🖼️ Image-to-ASCII" -> tpl.type == TemplateType.IMAGE_TO_ASCII
            "🔤 Text-to-ASCII" -> tpl.type == TemplateType.TEXT_TO_ASCII
            else -> tpl.category.equals(selectedTemplateCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    val featuredTemplate = allTemplates.find { it.isFeatured } ?: allTemplates.first()

    // Adaptive Container based on available screen width
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        val screenWidth = maxWidth
        // Calculate adaptive layout metrics
        val isWideScreen = screenWidth >= 600.dp
        val isCompactScreen = screenWidth < 360.dp
        val projectColumns = when {
            screenWidth >= 700.dp -> 3
            screenWidth >= 350.dp -> 2
            else -> 1
        }
        val templateColumns = if (screenWidth >= 650.dp) 2 else 1
        val horizontalPadding: Dp = if (isCompactScreen) 10.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("gallery_screen_list"),
            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Dual Section Tab Bar (Mes Créations vs Modèles & Inspiration)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, BorderDefault, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Section 1: Mes Créations
                    val isCreations = gallerySection == GallerySection.MY_CREATIONS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCreations) TerminalGreen else Color.Transparent)
                            .clickable { viewModel.setGallerySection(GallerySection.MY_CREATIONS) }
                            .padding(vertical = if (isCompactScreen) 8.dp else 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = if (isCreations) SurfaceLowest else TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isCompactScreen) "Créations" else "Mes Créations",
                                fontFamily = FontFamily.Monospace,
                                fontSize = if (isCompactScreen) 11.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCreations) SurfaceLowest else TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCreations) SurfaceLowest.copy(alpha = 0.2f) else SurfaceContainerHigh)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${projects.size}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCreations) SurfaceLowest else TerminalGreen
                                )
                            }
                        }
                    }

                    // Section 2: Modèles & Inspiration
                    val isInspiration = gallerySection == GallerySection.INSPIRATION_LIBRARY
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isInspiration) CyanCyber else Color.Transparent)
                            .clickable { viewModel.setGallerySection(GallerySection.INSPIRATION_LIBRARY) }
                            .padding(vertical = if (isCompactScreen) 8.dp else 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = if (isInspiration) SurfaceLowest else AmberCRT,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isCompactScreen) "Inspiration" else "Modèles & Inspiration",
                                fontFamily = FontFamily.Monospace,
                                fontSize = if (isCompactScreen) 11.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInspiration) SurfaceLowest else TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isInspiration) SurfaceLowest.copy(alpha = 0.2f) else SurfaceContainerHigh)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${allTemplates.size}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInspiration) SurfaceLowest else CyanCyber
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECTION A: MES CRÉATIONS (ROOM DATABASE)
            // ==========================================
            if (gallerySection == GallerySection.MY_CREATIONS) {
                // Storage Gauge Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .testTag("storage_gauge_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("SQLITE LOCAL", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary)
                                    Text("• ${projects.size} projet(s)", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyanCyber)
                                }
                                Text("Room 100% Hors-ligne", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalGreen)
                            }
                            LinearProgressIndicator(
                                progress = { (projects.size / 50f).coerceIn(0.05f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = TerminalGreen,
                                trackColor = SurfaceContainerHigh,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Search Bar for Creations
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                "> filtrer vos créations...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .testTag("gallery_search_input"),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerminalGreen,
                            unfocusedBorderColor = BorderDefault,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        )
                    )
                }

                // Category Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Tous",
                            "Monochrome",
                            "Couleurs ANSI",
                            "Code Snippets",
                            "Favoris ⭐"
                        ).forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TerminalGreen else SurfaceContainerHigh)
                                    .border(
                                        1.dp,
                                        if (isSelected) TerminalGreenBorder else BorderDefault,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setSelectedGalleryCategory(cat) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SurfaceLowest else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Empty State
                if (filteredProjects.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "Aucun résultat trouvé" else "Aucune création sauvegardée",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "Essayez un autre mot clé ou réinitialisez le filtre." else "Sauvegardez vos rendus depuis le Studio ou explorez l'onglet Inspiration pour débuter !",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        viewModel.setGallerySection(GallerySection.INSPIRATION_LIBRARY)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanCyber, contentColor = SurfaceLowest),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("EXPLORER LES MODÈLES", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Adaptive Grid for User Creations (1, 2 or 3 columns based on screen width)
                val chunkedProjects = filteredProjects.chunked(projectColumns)
                items(chunkedProjects) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { project ->
                            ProjectCardItem(
                                project = project,
                                modifier = Modifier.weight(1f),
                                isCompact = isCompactScreen,
                                onOpen = {
                                    viewModel.loadProjectIntoStudio(project)
                                    onNavigateToStudio()
                                },
                                onFullscreen = {
                                    fullscreenPreviewText = Pair(project.title, project.asciiContent)
                                    fullscreenPreviewColor = project.colorHex
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(project) },
                                onDelete = { projectToDelete = project }
                            )
                        }
                        // Fill empty slots in grid row
                        repeat(projectColumns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Bottom CTA: Importer & Convertir
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            viewModel.setTab(0)
                            onNavigateToStudio()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .height(44.dp)
                            .testTag("gallery_create_new_btn"),
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
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "NOUVELLE CRÉATION STUDIO",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ==========================================
            // SECTION B: BIBLIOTHÈQUE DE MODÈLES & INSPIRATION
            // ==========================================
            if (gallerySection == GallerySection.INSPIRATION_LIBRARY) {
                // Search Bar for Inspiration Catalog
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                "> chercher dans la bibliothèque...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .testTag("inspiration_search_input"),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanCyber,
                            unfocusedBorderColor = BorderDefault,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        )
                    )
                }

                // Categories Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Tous les modèles",
                            "🖼️ Image-to-ASCII",
                            "🔤 Text-to-ASCII",
                            "Cyberpunk & Néon",
                            "Rétro & 8-Bit",
                            "Typographie & Bannières",
                            "Code & Développeur"
                        ).forEach { cat ->
                            val isSelected = selectedTemplateCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanCyber else SurfaceContainerHigh)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanCyber else BorderDefault,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setSelectedTemplateCategory(cat) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SurfaceLowest else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Hero Featured Model Showcase (when no search query)
                if (searchQuery.isEmpty() && selectedTemplateCategory == "Tous les modèles") {
                    item {
                        FeaturedInspirationCard(
                            template = featuredTemplate,
                            isCompact = isCompactScreen,
                            onUseTemplate = {
                                viewModel.loadTemplateIntoStudio(featuredTemplate)
                                onNavigateToStudio()
                            },
                            onFullscreen = {
                                fullscreenPreviewText = Pair(featuredTemplate.title, featuredTemplate.asciiArtwork)
                                fullscreenPreviewColor = featuredTemplate.colorHex
                            },
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(featuredTemplate.asciiArtwork))
                                viewModel.showToast("ASCII de '${featuredTemplate.title}' copié !")
                            },
                            onShare = {
                                ImageExporter.shareText(context, featuredTemplate.asciiArtwork, featuredTemplate.title)
                            }
                        )
                    }
                }

                // Empty State if no template matches search
                if (filteredTemplates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                                Text("Aucun modèle correspondant", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Réinitialisez la recherche pour afficher toute la bibliothèque.", fontSize = 12.sp, color = TextSecondary)
                                Button(
                                    onClick = {
                                        viewModel.setSearchQuery("")
                                        viewModel.setSelectedTemplateCategory("Tous les modèles")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen, contentColor = SurfaceLowest),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("RÉINITIALISER LES FILTRES", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // List of Templates for Inspiration (Adaptive grid on tablets/wide screens)
                val chunkedTemplates = filteredTemplates.chunked(templateColumns)
                items(chunkedTemplates) { rowTemplates ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowTemplates.forEach { template ->
                            InspirationTemplateCard(
                                template = template,
                                modifier = Modifier.weight(1f),
                                isCompact = isCompactScreen,
                                onUseTemplate = {
                                    viewModel.loadTemplateIntoStudio(template)
                                    onNavigateToStudio()
                                },
                                onFullscreen = {
                                    fullscreenPreviewText = Pair(template.title, template.asciiArtwork)
                                    fullscreenPreviewColor = template.colorHex
                                },
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(template.asciiArtwork))
                                    viewModel.showToast("ASCII de '${template.title}' copié !")
                                },
                                onShare = {
                                    ImageExporter.shareText(context, template.asciiArtwork, template.title)
                                }
                            )
                        }
                        repeat(templateColumns - rowTemplates.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPOSABLES: CARDS & WIDGETS WITH RESPONSIVE SCREEN ADAPTATION
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeaturedInspirationCard(
    template: AsciiTemplate,
    isCompact: Boolean = false,
    onUseTemplate: () -> Unit,
    onFullscreen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 700.dp)
            .testTag("featured_inspiration_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        border = BorderStroke(1.dp, TerminalGreenBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberCRT.copy(alpha = 0.2f))
                            .border(1.dp, AmberCRT, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⭐ VEDETTE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberCRT
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (template.type == TemplateType.IMAGE_TO_ASCII) CyanCyber.copy(alpha = 0.2f) else TerminalGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = template.type.badgeText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (template.type == TemplateType.IMAGE_TO_ASCII) CyanCyber else TerminalGreen
                        )
                    }
                }

                IconButton(
                    onClick = onFullscreen,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Plein écran", tint = TerminalGreen, modifier = Modifier.size(18.dp))
                }
            }

            // Title & Description
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = template.title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = template.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Monospace ASCII Canvas Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 100.dp else 120.dp)
                    .background(SurfaceLowest)
                    .padding(6.dp)
                    .clickable { onFullscreen() }
                    .horizontalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = template.lines.take(8).joinToString("\n"),
                    fontFamily = FontFamily.Monospace,
                    fontSize = if (isCompact) 7.sp else 8.sp,
                    lineHeight = if (isCompact) 8.sp else 9.sp,
                    color = Color(android.graphics.Color.parseColor(template.colorHex))
                )
            }

            // Meta & Actions Footer - Responsive FlowRow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "${template.cols}x${template.rows} • ${template.glyphCount} chars",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanCyber
                    )
                    Text(
                        text = template.tags.take(3).joinToString(" • "),
                        fontSize = 9.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Partager", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    Button(
                        onClick = onUseTemplate,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen, contentColor = SurfaceLowest),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("UTILISER", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InspirationTemplateCard(
    template: AsciiTemplate,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onUseTemplate: () -> Unit,
    onFullscreen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (template.type == TemplateType.IMAGE_TO_ASCII) CyanCyber.copy(alpha = 0.2f) else TerminalGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = template.type.badgeText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (template.type == TemplateType.IMAGE_TO_ASCII) CyanCyber else TerminalGreen
                        )
                    }

                    Text(
                        text = template.title,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = template.category,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Description
            Text(
                text = template.description,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )

            // ASCII Artwork Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 85.dp else 95.dp)
                    .background(SurfaceLowest)
                    .padding(4.dp)
                    .clickable { onFullscreen() }
                    .horizontalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = template.lines.take(7).joinToString("\n"),
                    fontFamily = FontFamily.Monospace,
                    fontSize = if (isCompact) 6.sp else 7.sp,
                    lineHeight = if (isCompact) 7.sp else 8.sp,
                    color = Color(android.graphics.Color.parseColor(template.colorHex))
                )
            }

            // Footer info & actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "${template.cols}x${template.rows}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanCyber
                    )
                    Text(
                        text = if (template.type == TemplateType.TEXT_TO_ASCII) "Font: ${template.figletFont?.label?.take(10) ?: "Std"}" else "Ramp: ${template.characterRamp.name.take(8)}",
                        fontSize = 8.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(onClick = onFullscreen, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Agrandir", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Partager", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (template.type == TemplateType.IMAGE_TO_ASCII) CyanCyber else TerminalGreen)
                            .clickable { onUseTemplate() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CHARGER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceLowest
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: AsciiProjectEntity,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onOpen: () -> Unit,
    onFullscreen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("project_item_${project.id}")
            .clickable { onOpen() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = project.title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = project.dateLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    color = TextSecondary
                )
            }

            // Thumbnail ASCII Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 75.dp else 85.dp)
                    .background(SurfaceLowest)
                    .clickable { onFullscreen() }
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.asciiContent.lines().take(6).joinToString("\n"),
                    fontFamily = FontFamily.Monospace,
                    fontSize = if (isCompact) 6.sp else 7.sp,
                    lineHeight = if (isCompact) 7.sp else 8.sp,
                    color = try {
                        Color(android.graphics.Color.parseColor(project.colorHex))
                    } catch (e: Exception) {
                        TerminalGreen
                    }
                )
            }

            // Footer Info & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "${project.cols}x${project.rows}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanCyber
                    )
                    Text(
                        text = project.tagSubtitle,
                        fontSize = 8.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (project.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favori",
                            tint = if (project.isFavorite) AmberCRT else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
