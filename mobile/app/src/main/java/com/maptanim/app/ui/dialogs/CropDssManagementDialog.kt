package com.maptanim.app.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.ui.dialogs.components.*
import com.maptanim.app.ui.screens.farm.MonitoredPlant
import com.maptanim.app.ui.theme.White

/**
 * Phase 2 & 3: Refactored Crop Management Dialog.
 * Uses CropDssManagementViewModel, background DSS evaluation, Room task persistence,
 * dynamic daysToHarvest, and modular UI components.
 */
@Composable
fun CropDssManagementDialog(
    plant: MonitoredPlant? = null,
    plot: CropPlot? = null,
    plotRender: PlotRenderData? = null,
    initialPlantingMethod: String = "Transplanting",
    initialGrowingApproach: String = "Organic",
    onDismiss: () -> Unit,
    onReadInLibrary: ((cropName: String) -> Unit)? = null,
    onRecordObservation: ((CropPlot) -> Unit)? = null,
    onStartPlanting: ((String) -> Unit)? = null,
    onHarvest: ((String) -> Unit)? = null,
    dssViewModel: CropDssManagementViewModel = viewModel()
) {
    LaunchedEffect(plant, plot, plotRender) {
        dssViewModel.initialize(
            plant = plant,
            plot = plot,
            plotRender = plotRender,
            initialPlantingMethod = initialPlantingMethod,
            initialGrowingApproach = initialGrowingApproach
        )
    }

    val uiState by dssViewModel.uiState.collectAsState()

    // Observe dynamic logs from Room database
    val observedLogs by remember(uiState.plotId) {
        try {
            RepositoryProvider.cropLogRepository.observeLogsForPlanting(uiState.plotId)
        } catch (_: Exception) {
            kotlinx.coroutines.flow.flowOf(emptyList<CropLog>())
        }
    }.collectAsState(initial = emptyList())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF10160F)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                // ── 1. HEADER & TOP TABS (Overview | Recommendation) ───────────
                CropManagementHeader(
                    currentStage = uiState.currentStage,
                    selectedTopTab = uiState.selectedTopTab,
                    onBack = onDismiss,
                    onSelectTopTab = { dssViewModel.selectTopTab(it) }
                )

                // ── 2. STAGE NOTIFICATION BANNER ──────────────────────────────
                AnimatedVisibility(
                    visible = uiState.stageNotificationText != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = Color(0xFF1B3B1B),
                        border = BorderStroke(1.dp, Color(0xFF388E3C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.stageNotificationText ?: "",
                                color = Color(0xFFA5D6A7),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                color = Color(0xFF81C784),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { dssViewModel.dismissNotification() }
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                // ── 3. BODY CONTENT (Scrollable) ──────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (uiState.selectedTopTab) {
                        TopTab.OVERVIEW -> {
                            // Crop Info Card with Settings Gear
                            CropInfoCard(
                                plotLabel = uiState.plotLabel,
                                cropName = uiState.cropName,
                                cropVariety = uiState.cropVariety,
                                plantedDate = uiState.plantedDateStr,
                                plantingMethod = uiState.plantingMethod,
                                growingApproach = uiState.growingApproach,
                                currentStage = uiState.currentStage,
                                isPlanted = uiState.isPlanted,
                                onOpenSettings = { dssViewModel.openSettings() }
                            )

                            // 6-Stage Linear Timeline with Action Buttons & Prev/Next Navigation
                            CropTimelineCard(
                                currentStage = uiState.currentStage,
                                isExpanded = uiState.isTimelineExpanded,
                                canGoPrevious = uiState.currentStage.stageNumber > 1,
                                canGoNext = uiState.currentStage.stageNumber < ManagementStage.entries.size,
                                onPreviousStage = { dssViewModel.requestPreviousStage() },
                                onNextStage = { dssViewModel.requestNextStage() },
                                onToggleExpand = { dssViewModel.toggleTimeline() },
                                onRecordObservation = {
                                    dssViewModel.openAddLog()
                                },
                                onHarvest = {
                                    dssViewModel.openHarvest()
                                }
                            )

                            // Sub-tabs: TODAY'S TASKS and LOGS with auto-remove
                            DssOutputTabsSection(
                                currentStage = uiState.currentStage,
                                selectedDssTab = uiState.selectedDssTab,
                                dynamicTasks = uiState.dynamicTasks,
                                observedLogs = observedLogs,
                                onSelectDssTab = { dssViewModel.selectDssTab(it) },
                                onCheckDynamicTask = { dssViewModel.checkDynamicTask(it) },
                                onSwitchToRecommendations = { dssViewModel.selectTopTab(TopTab.RECOMMENDATION) }
                            )

                            // Dynamic Agronomic Guides & Recommendations generated at the bottom
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "AGRONOMIC GUIDES & RECOMMENDATIONS",
                                            color = Color(0xFFA5D6A7),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        if (uiState.dynamicRecommendations.isNotEmpty()) {
                                            Surface(
                                                color = Color(0xFF2E7D32),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(
                                                    text = "${uiState.dynamicRecommendations.size} Active",
                                                    color = White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (uiState.dynamicRecommendations.isEmpty()) {
                                        Text(
                                            text = "Record an observation above to generate tailored agronomic guides and crop-specific management recommendations for this stage.",
                                            color = Color(0xFF8B9E8B),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    } else {
                                        uiState.dynamicRecommendations.forEachIndexed { idx, rec ->
                                            Surface(
                                                color = Color(0xFF1B2418),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFF2A3A25)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "💡 ${rec.title}",
                                                            color = Color(0xFF81C784),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = rec.content,
                                                        color = Color(0xFFC0CDC0),
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TopTab.RECOMMENDATION -> {
                            // Dedicated Recommendation Tab
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "AGRONOMIC RECOMMENDATIONS",
                                        color = White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    if (uiState.dynamicRecommendations.isEmpty()) {
                                        Text(
                                            text = "1. Maintain 3–5 cm organic mulch around the root zone to conserve soil moisture and suppress weeds.\n\n" +
                                                    "2. Practice early morning drip or base irrigation to minimize foliage moisture and reduce fungal spore germination.\n\n" +
                                                    "3. Intercrop with beneficial companion plants (Basil, Marigold, or Green Onion) to deter pests naturally.\n\n" +
                                                    "4. Inspect lower leaf surfaces twice weekly for early signs of mites or thrips.",
                                            color = Color(0xFFC0CDC0),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    } else {
                                        uiState.dynamicRecommendations.forEachIndexed { idx, rec ->
                                            Text(
                                                text = "${idx + 1}. ${rec.title}: ${rec.content}",
                                                color = Color(0xFFC0CDC0),
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Footer Link to Agronomic Library
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onReadInLibrary?.invoke(uiState.cropName) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Read Agronomic Guide in Library →",
                            color = Color(0xFF81C784),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // ── MODALS ─────────────────────────────────────────────────────────────

    // Phase 3.4: Confirmation dialog before stage transition (manual or DSS automated)
    if (uiState.pendingStageTransition != null) {
        StageTransitionConfirmDialog(
            currentStage = uiState.currentStage,
            targetStage = uiState.pendingStageTransition!!,
            isManual = uiState.isStageTransitionManual,
            onConfirm = { dssViewModel.confirmStageTransition() },
            onDismiss = { dssViewModel.dismissStageTransition() }
        )
    }

    // Crop Settings Dialog (Variety, Date, Method, Approach)
    if (uiState.isSettingsOpen) {
        CropSettingsDialog(
            initialVariety = uiState.cropVariety,
            initialDate = uiState.plantedDateStr,
            initialMethod = uiState.plantingMethod,
            initialApproach = uiState.growingApproach,
            onDismiss = { dssViewModel.closeSettings() },
            onSave = { v, d, m, a -> dssViewModel.updateCropSettings(v, d, m, a) }
        )
    }

    // Add Log Dialog
    if (uiState.isAddLogOpen) {
        AddLogDialog(
            cropPlantingId = uiState.plotId,
            bedId = uiState.plotLabel,
            cropId = uiState.cropId,
            cropName = uiState.cropName,
            varietyName = uiState.cropVariety,
            currentStage = uiState.currentStage,
            plantingMethod = uiState.plantingMethod,
            onDismiss = { dssViewModel.closeAddLog() },
            onSubmitLog = { newLog ->
                dssViewModel.evaluateAndSubmitLog(newLog)
                val targetPlot = plot ?: CropPlot(
                    id = uiState.plotId,
                    farmId = uiState.farmId,
                    plotLabel = uiState.plotLabel,
                    cropName = uiState.cropName,
                    cropId = uiState.cropId,
                    cropVariety = uiState.cropVariety,
                    soilType = uiState.soilType,
                    posX = 0f,
                    posY = 0f,
                    widthM = 1f,
                    heightM = 1f,
                    plantedDate = uiState.plantedDateStr
                )
                onRecordObservation?.invoke(targetPlot)
            }
        )
    }

    // Harvest Dialog
    if (uiState.isHarvestOpen) {
        HarvestDialog(
            plotId = uiState.plotId,
            farmId = uiState.farmId,
            plotLabel = uiState.plotLabel,
            cropName = uiState.cropName,
            cropVariety = uiState.cropVariety,
            cropPlantingId = uiState.plotId,
            plantedDate = uiState.plantedDateStr,
            onDismiss = { dssViewModel.closeHarvest() },
            onSubmitHarvest = { harvestRecord ->
                dssViewModel.submitHarvest(harvestRecord)
                onHarvest?.invoke(uiState.plotId)
            }
        )
    }
}
