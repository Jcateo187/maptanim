package com.maptanim.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.isometric.layout.IsometricLayout
import com.maptanim.app.ui.components.layout.BottomToolbar
import com.maptanim.app.ui.components.layout.LeftToolbar
import com.maptanim.app.ui.components.layout.RightToolbar
import com.maptanim.app.ui.components.layout.TopBar

import com.maptanim.app.domain.model.CanvasMode
import com.maptanim.app.renderer.canvas.FarmCanvas
import com.maptanim.app.ui.screens.edit.EditViewModel

import com.maptanim.app.core.audio.AmbientSound
import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.core.audio.TrackAmbientEffect
import com.maptanim.app.core.audio.TrackBgmEffect
import com.maptanim.app.ui.screens.settings.AudioSettingsDialog

/**
 * HomeScreen — Clash of Clans inspired HUD design with shared 2D Isometric Scenery.
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    editViewModel: EditViewModel = viewModel(),
    tutorialViewModel: com.maptanim.app.viewmodel.TutorialViewModel = viewModel()
) {
    TrackBgmEffect(BackgroundTrack.PEACEFUL_FARM)
    TrackAmbientEffect(AmbientSound.DAY_BIRDS)

    val soundManager = LocalSoundManager.current
    val uiState by homeViewModel.uiState.collectAsState()
    val editUiState by editViewModel.uiState.collectAsState()
    val tutorialUiState by tutorialViewModel.uiState.collectAsState()

    var showMonitoringOverlay by remember { mutableStateOf(false) }
    var showTasksOverlay by remember { mutableStateOf(false) }
    var showSummaryOverlay by remember { mutableStateOf(false) }
    var showAudioSettingsDialog by remember { mutableStateOf(false) }

    val mergedPlots = remember(editUiState.plots, uiState.plots) {
        editUiState.plots.map { editPlot ->
            val homeMatch = uiState.plots.firstOrNull { it.id == editPlot.id }
            editPlot.copy(
                activeTasks = homeMatch?.activeTasks ?: editPlot.activeTasks,
                isMonitoringStarted = homeMatch?.isMonitoringStarted ?: editPlot.isMonitoringStarted
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 2D Isometric Map Canvas Background (Same scenery engine as EditScreen)
        HomeBackground()
        FarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = editUiState.copy(plots = mergedPlots),
            editViewModel = editViewModel,
            canvasMode = CanvasMode.VIEW,
            onOpenMonitoring = {
                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                showMonitoringOverlay = true
            }
        )

        val currentPlots = editUiState.plots.ifEmpty { uiState.plots }
        val totalPlotsCount = currentPlots.size
        val totalCropsCount = currentPlots.count { !it.cropName.isNullOrEmpty() }.let { if (it > 0) it else totalPlotsCount }

        // Top Bar HUD — all real data from Supabase (cloud) or Room (local)
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            totalCrops = totalCropsCount,
            readyToHarvest = uiState.farmSummary.readyToHarvest,
            notificationCount = uiState.notificationCount,
            nickname = uiState.nickname,
            avatarAssetPath = uiState.avatarAssetPath,
            farmName = uiState.activeFarm?.farmName ?: "",
            onProfileClick = {
                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                navController.navigate(Routes.profileRoute(0))
            },
            onNotificationClick = {
                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                navController.navigate(Routes.profileRoute(1))
            },
            onSettingsClick = {
                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                navController.navigate(com.maptanim.app.navigation.Routes.profileRoute(2))
            }
        )

        // Left Side HUD Buttons (Monitoring, Today's Tasks)
        LeftToolbar(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            uiState = uiState,
            onMonitoringClick = { showMonitoringOverlay = true },
            onTasksClick = { showTasksOverlay = true }
        )

        // Right Side HUD Buttons (Library, Community)
        RightToolbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            onLibraryClick = { navController.navigate(Routes.LIBRARY) },
            onCommunityClick = { navController.navigate(Routes.COMMUNITY) }
        )

        // Bottom Floating Single Edit Button
        BottomToolbar(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onEditClick = {
                if (tutorialUiState.isTutorialActive) {
                    tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT)
                }
                navController.navigate(Routes.EDIT)
            }
        )

        // ── 1. Fullscreen Monitoring Overlay ──────────────────────────────
        if (showMonitoringOverlay) {
            com.maptanim.app.ui.components.monitoring.MonitoringDashboardOverlay(
                onDismiss = { showMonitoringOverlay = false }
            )
        }

        // ── 2. Today's Tasks Overlay Sheet ───────────────────────────────
        if (showTasksOverlay) {
            com.maptanim.app.ui.components.tasks.TodaysTasksOverlay(
                onDismiss = { showTasksOverlay = false }
            )
        }

        // ── 3. Audio & Sound Settings Modal ──────────────────────────────
        val coroutineScope = rememberCoroutineScope()
        if (showAudioSettingsDialog) {
            AudioSettingsDialog(
                onDismissRequest = { showAudioSettingsDialog = false },
                onLogoutClick = {
                    coroutineScope.launch {
                        com.maptanim.app.data.repository.RepositoryProvider.userRepository.logout()
                        navController.navigate(com.maptanim.app.navigation.Routes.WELCOME) {
                            popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ── 3. Farm Summary Overlay Sheet ─────────────────────────────────
        if (showSummaryOverlay) {
            Dialog(onDismissRequest = { showSummaryOverlay = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Farm Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                            IconButton(onClick = { showSummaryOverlay = false }) {
                                Icon(Icons.Default.Close, "Close", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard("$totalPlotsCount", "Total Plots", Color(0xFF43A047), Modifier.weight(1f))
                            SummaryCard("$totalCropsCount", "Total Crops", Color(0xFF43A047), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard("${uiState.farmSummary.readyToHarvest}", "Ready Harvest", Color(0xFFFFA000), Modifier.weight(1f))
                            SummaryCard("${uiState.farmSummary.activeAlerts}", "Active Alerts", Color(0xFFE53935), Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // ── 4. Old Man Farmer Guided Tutorial Overlay (CoC Style) ─────────
        if (tutorialUiState.isTutorialActive) {
            when (tutorialUiState.currentStep) {
                com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON -> {
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Napakaganda, ${tutorialUiState.userNickname.ifEmpty { "Magsasaka" }}! Click the EDIT button below to start!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        nextButtonText = "Let's Go!",
                        dialogAlignment = Alignment.TopCenter,
                        compactMode = true,
                        scrimAlpha = 0.40f,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        onNext = {
                            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT)
                            navController.navigate(Routes.EDIT)
                        },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 24.dp, bottom = 24.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.DOWN,
                                        label = "CLICK EDIT TO SET UP FARM",
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    com.maptanim.app.ui.components.guide.SpotlightPulseRing(size = 80.dp)
                                }
                            }
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun MonitoringCard(title: String, value: String, status: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.15f), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(status, fontSize = 10.sp, color = Color.White)
        }
    }
}

@Composable
private fun TaskItemRow(title: String, subtext: String, type: TaskType) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.CheckCircleOutline, null, tint = Color.Gray)
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(subtext, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF5F5F5), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}