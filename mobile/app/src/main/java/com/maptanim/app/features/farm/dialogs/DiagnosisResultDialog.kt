package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.ui.theme.White

private val DarkSurface = Color(0xFF141C12)
private val DeepBorder = Color(0xFF2B3825)
private val EmeraldPrimary = Color(0xFF2E7D32)
private val LightGreenText = Color(0xFFA5D6A7)
private val MutedText = Color(0xFFC0CDC0)
private val AlertAmber = Color(0xFFFFB74D)
private val AlertRed = Color(0xFFFF8A80)

/**
 * DiagnosisResultDialog — Pop-up modal immediately answering the farmer's problem
 * right after submitting an observation or scouting log.
 */
@Composable
fun DiagnosisResultDialog(
    plotLabel: String,
    cropName: String,
    cropVariety: String,
    currentStage: ManagementStage,
    result: DssLogEvaluator.LogEvaluationResult,
    onDismiss: () -> Unit,
    onViewChores: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF10160F),
            border = BorderStroke(1.5.dp, Color(0xFF388E3C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // ── 1. HEADER ──────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = Color(0xFF1B3B1B),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFF388E3C))
                        ) {
                            Text(
                                text = "🩺 DSS AGRONOMIC DIAGNOSIS",
                                color = LightGreenText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$cropName ($plotLabel)",
                            color = White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stage ${currentStage.stageNumber}: ${currentStage.label} • $cropVariety",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", color = Color(0xFFA0B09A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(
                    color = DeepBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // ── 2. SCROLLABLE DIAGNOSIS BODY ──────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section A: Prescribed Agronomic Recommendations
                    if (result.newRecommendations.isNotEmpty()) {
                        Text(
                            text = "PRESCRIBED AGRONOMIC ACTIONS",
                            color = LightGreenText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        result.newRecommendations.forEach { rec ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, if (rec.priority >= 2) Color(0xFFD32F2F) else Color(0xFF388E3C)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "💡 ${rec.title}",
                                            color = if (rec.priority >= 2) AlertAmber else Color(0xFF81C784),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (rec.priority >= 2) {
                                            Surface(
                                                color = Color(0xFF3E1E1E),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color(0xFFD32F2F))
                                            ) {
                                                Text(
                                                    text = "URGENT",
                                                    color = AlertRed,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = rec.content,
                                        color = MutedText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Section B: Alerts Triggered
                    if (result.newAlerts.isNotEmpty()) {
                        Text(
                            text = "ACTIVE ALERTS",
                            color = AlertAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        result.newAlerts.forEach { alert ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2C1E14),
                                border = BorderStroke(1.dp, Color(0xFF8D5320)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "⚠️ ${alert.title}",
                                        color = AlertAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = alert.content,
                                        color = Color(0xFFFFE0B2),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Section C: Generated Follow-Up Tasks
                    if (result.newTasks.isNotEmpty()) {
                        Text(
                            text = "NEW CHORES ADDED TO TODAY'S SCHEDULE (${result.newTasks.size})",
                            color = LightGreenText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        result.newTasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DeepBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "📋", fontSize = 16.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            color = White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = task.description,
                                            color = Color(0xFFA0B09A),
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFF1E351E),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color(0xFF388E3C))
                                    ) {
                                        Text(
                                            text = "Due Today",
                                            color = LightGreenText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section D: Stage Progression Impact
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (result.stageAdvanced) Color(0xFF1B3B1B) else Color(0xFF162015),
                        border = BorderStroke(1.dp, if (result.stageAdvanced) Color(0xFF388E3C) else DeepBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = if (result.stageAdvanced) "🚀" else "🌱", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (result.stageAdvanced) "Milestone Reached! Stage Ready to Advance" else "Crop Development Tracking",
                                    color = if (result.stageAdvanced) LightGreenText else White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (result.stageAdvanced) "Eligible to advance to ${result.newStage?.label ?: "Next Stage"}." else "Observation recorded in activity history. Stage remains in ${currentStage.label}.",
                                    color = Color(0xFFA0B09A),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = DeepBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // ── 3. BOTTOM ACTION BUTTONS ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, DeepBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA0B09A))
                    ) {
                        Text(text = "Dismiss", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onViewChores,
                        modifier = Modifier.weight(1.5f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(
                            text = "📋 View Chores in Guide Tab →",
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
