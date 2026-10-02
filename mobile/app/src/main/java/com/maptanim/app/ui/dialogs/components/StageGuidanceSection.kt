package com.maptanim.app.ui.dialogs.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.LogContext
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.ui.theme.White

@Composable
fun StageGuidanceSection(
    currentStage: ManagementStage,
    observedLogs: List<CropLog>,
    expandedSections: Set<String>,
    onToggleSection: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (currentStage) {
            ManagementStage.PREPARATION -> {
                val prepLogs = observedLogs.filter { it.logContext == LogContext.PREPARATION }
                StageAccordionItem(
                    title = "STAGE 1 — SOIL PREPARATION PROTOCOL & LOGS",
                    isExpanded = expandedSections.contains("PREP"),
                    onToggle = { onToggleSection("PREP") }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Standard Protocol: Aerate bed soil to 20–30 cm depth. Incorporate 2–3 kg/m² well-cured compost or vermicast. Maintain optimal moisture (top 5 cm moist, not flooded).",
                            color = Color(0xFFC0CDC0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        if (prepLogs.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFF222C1F), thickness = 0.8.dp)
                            Text("Recorded Soil Preparation Logs:", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            prepLogs.forEach { log ->
                                Text(
                                    text = "• ${log.date}: Choice ${log.selectedChoice} — ${log.notes?.ifBlank { "Completed" } ?: "Completed"}",
                                    color = White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            ManagementStage.PLANTING -> {
                val plantLogs = observedLogs.filter { it.logContext == LogContext.PLANTING }
                StageAccordionItem(
                    title = "STAGE 2 — PLANTING SETUP GUIDANCE & LOGS",
                    isExpanded = expandedSections.contains("SETUP"),
                    onToggle = { onToggleSection("SETUP") }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Standard Protocol: Check seedling root integrity before transplanting. Plant during late afternoon to reduce transplant shock. Irrigate gently immediately after planting.",
                            color = Color(0xFFC0CDC0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        if (plantLogs.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFF222C1F), thickness = 0.8.dp)
                            Text("Recorded Planting Logs:", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            plantLogs.forEach { log ->
                                Text(
                                    text = "• ${log.date}: Method ${log.selectedChoice} — ${log.notes?.ifBlank { "Executed" } ?: "Executed"}",
                                    color = White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            ManagementStage.EARLY_GROWTH, ManagementStage.VEGETATIVE_GROWTH -> {
                val observeLogs = observedLogs.filter { it.logContext == LogContext.OBSERVE }
                StageAccordionItem(
                    title = "STAGE ${currentStage.stageNumber} — OBSERVATION & SCOUTING GUIDANCE",
                    isExpanded = expandedSections.contains("OBSERVE"),
                    onToggle = { onToggleSection("OBSERVE") }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Standard Protocol: Conduct regular morning pest scouting on lower leaf surfaces. Check for leaf curl, flea beetles, or early blight lesions. Ensure mulch depth remains 3–5 cm.",
                            color = Color(0xFFC0CDC0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        if (observeLogs.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFF222C1F), thickness = 0.8.dp)
                            Text("Recent Scouting Records:", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            observeLogs.take(5).forEach { log ->
                                Text(
                                    text = "• ${log.date}: ${log.notes?.ifBlank { "Observation recorded" } ?: "Observation recorded"}",
                                    color = White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            ManagementStage.FLOWERING_FRUIT_DEVELOPMENT, ManagementStage.HARVEST -> {
                val careLogs = observedLogs.filter { it.logContext == LogContext.CARE_MAINTENANCE }
                StageAccordionItem(
                    title = "STAGE ${currentStage.stageNumber} — FLOWERING & HARVEST MANAGEMENT",
                    isExpanded = expandedSections.contains("CARE"),
                    onToggle = { onToggleSection("CARE") }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Standard Protocol: Maintain regular drip irrigation to prevent blossom end rot. Prune diseased lower suckers. Check for color break to determine initial harvest readiness.",
                            color = Color(0xFFC0CDC0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        if (careLogs.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFF222C1F), thickness = 0.8.dp)
                            Text("Recorded Care & Maintenance:", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            careLogs.take(5).forEach { log ->
                                Text(
                                    text = "• ${log.date}: ${log.careActivity?.name ?: "Care"} — ${log.notes ?: "Completed"}",
                                    color = White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageAccordionItem(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF141C12),
        border = BorderStroke(1.dp, Color(0xFF243020)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = Color(0xFF81C784),
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    content()
                }
            }
        }
    }
}
