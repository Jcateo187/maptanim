package com.maptanim.app.features.farm.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.CropGrowthStage
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.features.farm.viewmodel.DssTab
import com.maptanim.app.features.farm.viewmodel.state.FarmHubGuideState

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * GuideTab — Agronomic care guidelines, growth stage milestones, and dynamic DA-BPI tasks.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@Composable
fun GuideTab(
    state: FarmHubGuideState,
    activePlot: CropPlot? = null,
    onSelectDssTab: (DssTab) -> Unit,
    onCompleteTask: (String) -> Unit,
    onNavigateToPlan: () -> Unit = {},
    onNavigateToCheckUp: () -> Unit = {},
    onNavigateToHarvest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 0. Active Bed Orientation Header ─────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE PRODUCTION BED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = activePlot?.let { "${it.plotLabel}: ${it.cropName ?: "Unplanted"}" }
                                ?: "No Bed Selected (Showing General Guide)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                    }
                    if (activePlot?.cropName == null) {
                        TextButton(onClick = onNavigateToPlan) {
                            Text("Assign Crop →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                    }
                }
            }
        }

        // ── 1. Growth Stage Progression Card ────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "CURRENT STAGE: ${state.currentStage.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepBlack
                            )
                        }

                        Text(
                            text = "Day ${state.daysPlanted} of ${state.daysToHarvest}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LushGreen
                        )
                    }

                    val progress = if (state.daysToHarvest > 0) {
                        (state.daysPlanted.toFloat() / state.daysToHarvest.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = LushGreen,
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }
        }

        // ── 1B. 3-Phase Daily Step-by-Step Guide (§3 Pillar 2 of Doc 43) ──
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3-PHASE DAILY PRODUCTION PROTOCOL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = LushGreen
                    )

                    // Phase 1: Prepare
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightSurface, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PHASE 1: PREPARE SOIL & DOSING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Text(
                            text = "• Loosen soil 25–30 cm deep. Mix 3–5 kg/m² compost or vermicast into top 15 cm.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                        Text(
                            text = "• If soil pH < 5.5, apply 100 g/m² agricultural lime 1 week prior to planting.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                    }

                    // Phase 2: Plant
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightSurface, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PHASE 2: SOWING & TRANSPLANTING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Text(
                            text = "• Sow in shallow furrows (0.5–1 cm deep); water gently with fine rose nozzle.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                        Text(
                            text = "• Late PM Rule: Sow or transplant after 4:00 PM to protect young seedlings from solar shock.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                    }

                    // Phase 3: Care Milestones
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightSurface, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PHASE 3: CARE & MAINTENANCE MILESTONES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Text(
                            text = "• Day 0–7: Maintain consistent moisture; replant missing spots.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                        Text(
                            text = "• Day 7–14: First weeding, 5cm rice-straw mulch, scout for leaf pests.",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                        Text(
                            text = "• Day 21–28: Side-dress vermicast ring + diluted FPJ (1:1000).",
                            fontSize = 10.sp,
                            color = Color(0xFF444444),
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }

        // ── 2. DSS Tab Toggle (Tasks vs Recommendations) ─────────────────────
        item {
            TabRow(
                selectedTabIndex = if (state.selectedDssTab == DssTab.TASKS) 0 else 1,
                containerColor = Color.White,
                contentColor = LushGreen,
                divider = { HorizontalDivider(color = CardBorderColor) }
            ) {
                Tab(
                    selected = state.selectedDssTab == DssTab.TASKS,
                    onClick = { onSelectDssTab(DssTab.TASKS) },
                    text = {
                        Text(
                            text = "Active Care Tasks (${state.dynamicTasks.size})",
                            fontWeight = if (state.selectedDssTab == DssTab.TASKS) FontWeight.Bold else FontWeight.Normal,
                            color = if (state.selectedDssTab == DssTab.TASKS) LushGreen else DeepBlack
                        )
                    }
                )
                Tab(
                    selected = state.selectedDssTab == DssTab.LOGS,
                    onClick = { onSelectDssTab(DssTab.LOGS) },
                    text = {
                        Text(
                            text = "Recommendations (${state.dynamicRecommendations.size})",
                            fontWeight = if (state.selectedDssTab == DssTab.LOGS) FontWeight.Bold else FontWeight.Normal,
                            color = if (state.selectedDssTab == DssTab.LOGS) LushGreen else DeepBlack
                        )
                    }
                )
            }
        }

        // ── 3. Content: Tasks or Recommendations ─────────────────────────────
        if (state.selectedDssTab == DssTab.TASKS) {
            if (state.dynamicTasks.isEmpty()) {
                item {
                    com.maptanim.app.features.farm.components.GuideEmptyStateCard(
                        title = "All Caught Up!",
                        description = "No pending care tasks for this growth stage. Next scheduled check will appear tomorrow."
                    )
                }
            } else {
                items(state.dynamicTasks) { task ->
                    TaskCard(
                        task = task,
                        onComplete = { onCompleteTask(task.id) }
                    )
                }
            }
        } else {
            if (state.dynamicRecommendations.isEmpty()) {
                item {
                    com.maptanim.app.features.farm.components.GuideEmptyStateCard(
                        title = "Standard Protocol",
                        description = "Maintain baseline watering and aeration. Scientific advisories will appear based on weather and soil data."
                    )
                }
            } else {
                items(state.dynamicRecommendations) { rec ->
                    com.maptanim.app.features.farm.components.GuideRecommendationCard(recommendation = rec)
                }
            }
        }

        // ── 4. Workflow Navigation Shortcuts ────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "NEXT STEPS IN FARM WORKFLOW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 0.5.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToCheckUp,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text("Diagnose Plant →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                    }

                    Button(
                        onClick = onNavigateToHarvest,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Record Harvest →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: DssLogEvaluator.GeneratedLogTask,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DeepBlack
                )
                Text(
                    text = task.description,
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    lineHeight = 16.sp
                )
            }

            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = "Complete Task",
                    tint = LushGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

