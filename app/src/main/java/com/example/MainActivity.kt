package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavShell
import com.example.ui.components.TopAppHeader
import com.example.ui.screens.DevKitScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.AsciiCraftTheme
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceLowest
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.AsciiCraftViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsciiCraftTheme {
                AsciiCraftApp()
            }
        }
    }
}

@Composable
fun AsciiCraftApp(
    viewModel: AsciiCraftViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Direct Export View Mode inside Studio tab or dedicated screen
    var isShowingDirectExport by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SurfaceBase,
        topBar = {
            TopAppHeader(
                onAvatarClick = { viewModel.setTab(3) }
            )
        },
        bottomBar = {
            BottomNavShell(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    isShowingDirectExport = false
                    viewModel.setTab(index)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                Snackbar(
                    containerColor = SurfaceContainerHigh,
                    contentColor = TextPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ) {
                    androidx.compose.material3.Text(
                        text = data.visuals.message,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TerminalGreen
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceBase)
        ) {
            AnimatedContent(
                targetState = if (selectedTab == 0 && isShowingDirectExport) 100 else selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> StudioScreen(
                        viewModel = viewModel,
                        onNavigateToExport = { isShowingDirectExport = true }
                    )
                    100 -> ExportScreen(
                        viewModel = viewModel,
                        onNewProject = { isShowingDirectExport = false }
                    )
                    1 -> GalleryScreen(
                        viewModel = viewModel,
                        onNavigateToStudio = {
                            isShowingDirectExport = false
                            viewModel.setTab(0)
                        }
                    )
                    2 -> DevKitScreen(
                        viewModel = viewModel
                    )
                    3 -> SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
