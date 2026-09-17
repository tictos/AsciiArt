package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AmberCRT
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.CyanCyber
import com.example.ui.theme.DevKitPurple
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalGreenDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AsciiCraftViewModel

@Composable
fun SettingsScreen(
    viewModel: AsciiCraftViewModel
) {
    val isCrtScanlines by viewModel.isCrtScanlineEnabled.collectAsStateWithLifecycle()
    val exportWatermark by viewModel.exportWatermark.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .testTag("settings_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Paramètres & Config",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Personnalisez l'environnement de rendu et les exports.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // 2. Rendu Visuel CRT & Interface
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings_visual_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tv, contentDescription = null, tint = TerminalGreen, modifier = Modifier.size(18.dp))
                        Text("Rendu Visuel & Moniteur", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Balayage CRT Scanlines", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Simule les lignes entrelacées d'un écran cathodique", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = isCrtScanlines,
                            onCheckedChange = { viewModel.toggleCrtScanlines() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TerminalGreen,
                                checkedTrackColor = TerminalGreenDark.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceContainerHigh
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Accélération GPU 60 FPS", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Rendu matériel temps réel des matrices ASCII", fontSize = 11.sp, color = TextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TerminalGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("ACTIF", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                        }
                    }
                }
            }
        }

        // 3. Export & Signatures
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings_export_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = CyanCyber, modifier = Modifier.size(18.dp))
                        Text("Export & Diffusion", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Filigrane discret AsciiCraft", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Ajoute la signature technique aux images sauvegardées", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = exportWatermark,
                            onCheckedChange = { viewModel.setExportWatermark(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyanCyber,
                                checkedTrackColor = CyanCyber.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceContainerHigh
                            )
                        )
                    }
                }
            }
        }

        // 4. Moteur & Local Persistence
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings_storage_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = AmberCRT, modifier = Modifier.size(18.dp))
                        Text("Stockage & Confidentialité", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base locale Room SQLite", fontSize = 12.sp, color = TextSecondary)
                        Text("${projects.size} créations enregistrées", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Politique de données", fontSize = 12.sp, color = TextSecondary)
                        Text("100% Local • Zéro Télémétrie", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalGreen)
                    }
                }
            }
        }

        // 5. À Propos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DevKitPurple, modifier = Modifier.size(18.dp))
                        Text("À propos d'AsciiArt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Text(
                        text = "AsciiArt est un studio créatif et un moteur de conversion d'images et typographies en ASCII Art haute fidélité. Conçu pour le grand public et les développeurs avec exports multi-langages (Python, Rust, TypeScript, Go, C++).",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version de l'application", fontSize = 11.sp, color = TextSecondary)
                        val context = LocalContext.current
                        val appVersion = remember(context) {
                            try {
                                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                                pInfo.versionName ?: com.example.BuildConfig.VERSION_NAME
                            } catch (e: Exception) {
                                com.example.BuildConfig.VERSION_NAME
                            }
                        }
                        Text("v$appVersion", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberCRT)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Moteur de calcul", fontSize = 11.sp, color = TextSecondary)
                        Text("Coroutines Asynchrones", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyanCyber)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Licence open-source", fontSize = 11.sp, color = TextSecondary)
                        Text("Apache 2.0", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                    }
                }
            }
        }
    }
}
