package com.maptanim.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.core.audio.AmbientSound
import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.core.audio.TrackAmbientEffect
import com.maptanim.app.core.audio.TrackBgmEffect
import com.maptanim.app.domain.model.CanvasMode
import com.maptanim.app.navigation.Routes
import com.maptanim.app.renderer.canvas.FarmCanvas
import com.maptanim.app.ui.components.layout.BottomToolbar
import com.maptanim.app.ui.components.layout.LeftToolbar
import com.maptanim.app.ui.components.layout.RightToolbar
import com.maptanim.app.ui.components.layout.TopBar
import com.maptanim.app.ui.screens.edit.EditViewModel
import com.maptanim.app.ui.screens.settings.AudioSettingsDialog
import kotlinx.coroutines.launch

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
    var isNavigatingToEdit by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val homePlots = uiState.plots
    val zonesForHome = remember(homePlots) {
        homePlots.map { plot ->
            val zone = com.maptanim.app.renderer.model.CropZoneRenderData(
                id = "zone-${plot.id}",
                plotId = plot.id,
                cropName = plot.cropName,
                offsetX = 0.0f,
                offsetY = 0.0f,
                widthM = plot.widthM,
                heightM = plot.heightM,
                spacingM = 1.0f,
                growthStage = plot.growthStage
            )
            zone.copy(
                plantInstances = com.maptanim.app.renderer.PlantInstanceGenerator.generate(zone, plot.posX, plot.posY)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 2D Isometric Map Canvas Background (Same scenery engine as EditScreen)

        FarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = editUiState.copy(plots = homePlots, cropZones = zonesForHome),
            editViewModel = editViewModel,
            canvasMode = CanvasMode.VIEW,
            onOpenMonitoring = {
                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                showMonitoringOverlay = true
            }
        )

        val totalPlotsCount = homePlots.size
        val totalCropsCount = homePlots.count { !it.cropName.isNullOrEmpty() }

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
                navController.navigate(Routes.profileRoute(2))
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
            isLoading = isNavigatingToEdit,
            onEditClick = {
                if (!isNavigatingToEdit) {
                    isNavigatingToEdit = true
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    if (tutorialUiState.isTutorialActive) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT)
                    }
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(450)
                        navController.navigate(Routes.EDIT)
                        kotlinx.coroutines.delay(300)
                        isNavigatingToEdit = false
                    }
                }
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
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
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
private fun SummaryCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF5F5F5), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
