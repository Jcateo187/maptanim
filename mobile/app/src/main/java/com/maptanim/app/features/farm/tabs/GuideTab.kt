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
    onSelectDssTab: (DssTab) -> Unit,
    onCompleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    EmptyGuidanceCard(
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
                    EmptyGuidanceCard(
                        title = "Standard Protocol",
                        description = "Maintain baseline watering and aeration. Scientific advisories will appear based on weather and soil data."
                    )
                }
            } else {
                items(state.dynamicRecommendations) { rec ->
                    RecommendationCard(recommendation = rec)
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

@Composable
private fun RecommendationCard(recommendation: DssLogEvaluator.LogRecommendation) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F8E9),
        border = BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = LushGreen,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = recommendation.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = recommendation.content,
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyGuidanceCard(title: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LightSurface,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LushGreen,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepBlack
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
