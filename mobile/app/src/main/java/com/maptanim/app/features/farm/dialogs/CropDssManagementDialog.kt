package com.maptanim.app.features.farm.dialogs

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WarningAmber
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
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.components.*
import com.maptanim.app.features.farm.dialogs.*
import com.maptanim.app.features.farm.tabs.guide.*
import com.maptanim.app.features.farm.tabs.checkup.CropTimelineCard
import com.maptanim.app.features.farm.components.MonitoredPlant
import com.maptanim.app.features.farm.viewmodel.*
import com.maptanim.app.ui.theme.White
import java.time.LocalDate
import java.util.Locale

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

    // Auto-dismiss notification banner after 3 seconds so it never clutters the UI
    LaunchedEffect(uiState.stageNotificationText) {
        if (uiState.stageNotificationText != null) {
            kotlinx.coroutines.delay(3000)
            dssViewModel.dismissNotification()
        }
    }

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
                        TopTab.PLAN -> {
                            // ── TAB 1: PLAN & BED SETUP ───────────────────────
                            // Bed & Planting Summary Card with Settings Gear
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

                            // Soil & Agronomic Fit Card
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "SOIL & COMPANION SUITABILITY",
                                        color = Color(0xFFA5D6A7),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Bed Soil Type:", color = Color(0xFFA0B09A), fontSize = 12.sp)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF22331E),
                                            border = BorderStroke(1.dp, Color(0xFF385532))
                                        ) {
                                            Text(
                                                text = "${uiState.soilType.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase() }} Soil",
                                                color = Color(0xFF81C784),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    // Philippine Season Fit
                                    val currentMonth = LocalDate.now().monthValue
                                    val isWetSeason = currentMonth in 5..10
                                    val seasonText = if (isWetSeason) "Wet Season (May to Oct)" else "Dry Season (Nov to Apr)"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Philippine Season:", color = Color(0xFFA0B09A), fontSize = 12.sp)
                                        Text(text = seasonText, color = White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp)

                                    // Companion plant advice
                                    val companions = when (uiState.cropName.lowercase(Locale.ROOT)) {
                                        "tomato" -> "Basil, Marigold, Green Onion, Pechay"
                                        "eggplant" -> "Beans (Sitaw), Marigold, Basil"
                                        "pechay" -> "Tomato, Cucumber, Corn"
                                        "cucumber" -> "Beans, Corn, Radish"
                                        "chili", "pepper" -> "Basil, Onion, Tomato"
                                        else -> "Basil, Marigold, Legumes"
                                    }
                                    val avoidCrops = when (uiState.cropName.lowercase(Locale.ROOT)) {
                                        "tomato" -> "Corn (attracts fruit borer), Fennel, Potato"
                                        "eggplant" -> "Fennel, Potato"
                                        "pechay" -> "Strawberries"
                                        else -> "Fennel"
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Spa, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                                            Text(text = "Beneficial Companions:", color = Color(0xFF81C784), fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(text = companions, color = Color(0xFFE0ECE0), fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp))
                                            Text(text = "Antagonists to Avoid:", color = Color(0xFFFFB74D), fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(text = avoidCrops, color = Color(0xFFE0ECE0), fontSize = 13.sp)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { dssViewModel.openSettings() },
                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20351C)),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFF385532))
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFA5D6A7), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Edit Bed & Planting Settings", color = Color(0xFFA5D6A7), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        TopTab.GUIDE -> {
                            // ── TAB 2: STAGE GUIDE & TODAY'S TASKS ─────────────
                            // 6-Stage Linear Timeline with Action Buttons & Prev/Next Navigation
                            CropTimelineCard(
                                currentStage = uiState.currentStage,
                                isExpanded = uiState.isTimelineExpanded,
                                canGoPrevious = uiState.currentStage.stageNumber > 1,
                                canGoNext = uiState.currentStage.stageNumber < ManagementStage.entries.size,
                                onPreviousStage = { dssViewModel.requestPreviousStage() },
                                onNextStage = { dssViewModel.requestNextStage() },
                                onToggleExpand = { dssViewModel.toggleTimeline() },
                                onRecordObservation = { dssViewModel.openAddLog() },
                                onHarvest = { dssViewModel.openHarvest() }
                            )

                            // Sub-tabs: TODAY'S TASKS and LOGS with auto-remove & 5/3 preview
                            DssOutputTabsSection(
                                currentStage = uiState.currentStage,
                                selectedDssTab = uiState.selectedDssTab,
                                dynamicTasks = uiState.dynamicTasks,
                                observedLogs = observedLogs,
                                onSelectDssTab = { dssViewModel.selectDssTab(it) },
                                onCheckDynamicTask = { dssViewModel.checkDynamicTask(it) },
                                onSwitchToRecommendations = { dssViewModel.selectTopTab(TopTab.CHECKUP) }
                            )

                            // Stage-by-Stage Guidance Accordions
                            StageGuidanceSection(
                                currentStage = uiState.currentStage,
                                observedLogs = observedLogs,
                                expandedSections = uiState.expandedStageAccordions,
                                onToggleSection = { dssViewModel.toggleAccordion(it) }
                            )
                        }

                        TopTab.CHECKUP -> {
                            // ── TAB 3: CHECK-UP & OBSERVATION ──────────────────
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
                                            text = "🩺 FIELD SCOUTING & DIAGNOSIS",
                                            color = Color(0xFFA5D6A7),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Stage ${uiState.currentStage.stageNumber}",
                                            color = Color(0xFFA0B09A),
                                            fontSize = 10.sp
                                        )
                                    }

                                    Text(
                                        text = "Regularly inspect your crop for pests, nutrient hunger, or disease signs. Recording an observation automatically runs the DSS engine to generate corrective tasks.",
                                        color = Color(0xFFC0CDC0),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )

                                    Button(
                                        onClick = { dssViewModel.openAddLog() },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("📝 Record Observation / Scouting", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Dynamic Agronomic Guides & Recommendations generated from logs
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
                                            text = "AGRONOMIC RECOMMENDATIONS",
                                            color = White,
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
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "Standard IPM (Integrated Pest Management) Guidelines:",
                                                color = Color(0xFF81C784),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "1. Maintain 3–5 cm organic mulch around the root zone to conserve soil moisture and suppress weeds.\n\n" +
                                                        "2. Practice early morning drip or base irrigation to minimize foliage moisture and reduce fungal spore germination.\n\n" +
                                                        "3. Intercrop with beneficial companion plants (Basil, Marigold, or Green Onion) to deter pests naturally.\n\n" +
                                                        "4. Inspect lower leaf surfaces twice weekly for early signs of mites, aphids, or thrips.",
                                                color = Color(0xFFC0CDC0),
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    } else {
                                        uiState.dynamicRecommendations.forEachIndexed { idx, rec ->
                                            Surface(
                                                color = Color(0xFF1B2418),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFF2A3A25)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = androidx.compose.material.icons.Icons.Default.Eco,
                                                            contentDescription = null,
                                                            tint = Color(0xFF81C784),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = rec.title,
                                                            color = Color(0xFF81C784),
                                                            fontSize = 13.sp,
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

                        TopTab.HARVEST -> {
                            // ── TAB 4: HARVEST & CROP ROTATION LOOP ────────────
                            val dth = uiState.daysToHarvest.coerceAtLeast(1)
                            val dp = uiState.daysPlanted
                            val progress = (dp.toFloat() / dth.toFloat()).coerceIn(0f, 1f)
                            val daysRemaining = (dth - dp).coerceAtLeast(0)
                            val isReady = progress >= 0.90f

                            // Readiness Gauge Card
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "HARVEST READINESS EVALUATOR",
                                            color = Color(0xFFA5D6A7),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isReady) Color(0xFF1B5E20) else Color(0xFF332A15),
                                            border = BorderStroke(1.dp, if (isReady) Color(0xFF4CAF50) else Color(0xFF5C4A1C))
                                        ) {
                                            Text(
                                                text = if (isReady) "🌾 READY FOR PICKING" else "⏳ $daysRemaining DAYS LEFT",
                                                color = if (isReady) Color(0xFFA5D6A7) else Color(0xFFFFD54F),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = if (isReady) Color(0xFF4CAF50) else Color(0xFFFFD54F),
                                        trackColor = Color(0xFF1C2819)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Elapsed: $dp days", color = Color(0xFFA0B09A), fontSize = 11.sp)
                                        Text(text = "${(progress * 100).toInt()}% Mature", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Target: $dth days", color = Color(0xFFA0B09A), fontSize = 11.sp)
                                    }
                                }
                            }

                            // Harvest Summary & Record Action Card
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
                                    Text(
                                        text = "YIELD & PICKINGS SUMMARY",
                                        color = Color(0xFFA5D6A7),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1A2617),
                                            border = BorderStroke(1.dp, Color(0xFF2C3E27)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(text = "Pickings Count", color = Color(0xFFA0B09A), fontSize = 10.sp)
                                                Text(text = "${uiState.harvestCount} harvests", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1A2617),
                                            border = BorderStroke(1.dp, Color(0xFF2C3E27)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(text = "Total Yield Logged", color = Color(0xFFA0B09A), fontSize = 10.sp)
                                                Text(text = "%.1f kg".format(Locale.ROOT, uiState.totalYieldKg), color = Color(0xFF81C784), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { dssViewModel.openHarvest() },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("🌾 Record Harvest (Ongoing or Final)", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Closed-Loop "What Next?" Crop Rotation Advisor Card
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "🔄 CLOSED-LOOP CROP ROTATION ADVISOR",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    val cropFamily = when (uiState.cropName.lowercase(Locale.ROOT)) {
                                        "tomato", "eggplant", "chili", "pepper" -> "Solanaceae (Nightshade)"
                                        "cucumber", "patola", "squash", "kalabasa" -> "Cucurbitaceae (Gourd/Melon)"
                                        "pechay", "mustard", "cabbage", "radish" -> "Brassicaceae (Crucifer)"
                                        "sitaw", "baguio beans", "mungbean" -> "Fabaceae (Legume)"
                                        else -> "Vegetable"
                                    }

                                    val nextRotationRecommendation = when (uiState.cropName.lowercase(Locale.ROOT)) {
                                        "tomato", "eggplant", "chili", "pepper" ->
                                            "Do not follow with another Solanaceae. Rotate with 🫘 Legumes (Sitaw or Baguio Beans) to fix atmospheric nitrogen and starve bacterial wilt bacteria."
                                        "cucumber", "squash" ->
                                            "Follow with 🥬 Leafy Greens (Pechay or Lettuce) or Legumes to restore organic matter."
                                        "sitaw", "beans" ->
                                            "Your garden soil is now nitrogen-enriched! Follow with heavy feeders like 🍅 Tomato, 🍆 Eggplant, or 🌽 Sweet Corn."
                                        else ->
                                            "Rotate with nitrogen-fixing legumes or rest soil with a 2-week vermicompost cover."
                                    }

                                    Text(
                                        text = "Current Crop Family: $cropFamily",
                                        color = White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = nextRotationRecommendation,
                                        color = Color(0xFFC0CDC0),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )

                                    Button(
                                        onClick = { dssViewModel.selectTopTab(TopTab.PLAN) },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22311C)),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF384F31))
                                    ) {
                                        Text("🌱 Plan Next Crop in Tab 1 →", color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                if (harvestRecord.isFinalHarvest) {
                    onHarvest?.invoke(uiState.plotId)
                    onDismiss()
                }
            }
        )
    }

    // Phase 3.5: Immediate Agronomic Diagnosis & Corrective Chores Result Modal
    if (uiState.isDiagnosisResultOpen && uiState.latestDiagnosisResult != null) {
        DiagnosisResultDialog(
            plotLabel = uiState.plotLabel,
            cropName = uiState.cropName,
            cropVariety = uiState.cropVariety,
            currentStage = uiState.currentStage,
            result = uiState.latestDiagnosisResult!!,
            onDismiss = { dssViewModel.closeDiagnosisResult() },
            onViewChores = { dssViewModel.acceptDiagnosisAndGoToTasks() }
        )
    }
}
