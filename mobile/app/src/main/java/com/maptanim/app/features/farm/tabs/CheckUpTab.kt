package com.maptanim.app.features.farm.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.features.farm.viewmodel.state.FarmHubCheckUpState

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CheckUpTab — Field health inspections, pest & disease alerts, and crop observation logs.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@Composable
fun CheckUpTab(
    state: FarmHubCheckUpState,
    onOpenAddLog: () -> Unit,
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 0. Workflow Back Navigation ───────────────────────────────────────
        item {
            OutlinedButton(
                onClick = onNavigateToGuide,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Text("← Return to Daily Production Guide", fontSize = 11.sp, color = DeepBlack, fontWeight = FontWeight.Bold)
            }
        }
        // ── 1. Action Button: Log Observation ────────────────────────────────
        item {
            Button(
                onClick = onOpenAddLog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LushGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Record Field Observation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // ── 2. Pest & Disease Alert Status ───────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (state.pestRiskAlertCount > 0) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                border = BorderStroke(
                    1.dp,
                    if (state.pestRiskAlertCount > 0) Color(0xFFFFB74D) else Color(0xFFA5D6A7)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.pestRiskAlertCount > 0) Icons.Default.WarningAmber else Icons.Default.Healing,
                        contentDescription = null,
                        tint = if (state.pestRiskAlertCount > 0) Color(0xFFE65100) else LushGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = if (state.pestRiskAlertCount > 0) {
                                "${state.pestRiskAlertCount} Active Pest Warning(s)"
                            } else {
                                "Crop Health: Nominal"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = if (state.pestRiskAlertCount > 0) {
                                "Check leaves for infestation signs and apply biological remedies."
                            } else {
                                "No active pest or fungal symptoms reported in recent logs."
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        }

        // ── 3. Observation Logs Header ───────────────────────────────────────
        item {
            Text(
                text = "OBSERVATION HISTORY (${state.observedLogs.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF555555)
            )
        }

        if (state.observedLogs.isEmpty()) {
            item {
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
                            imageVector = Icons.Default.Healing,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No Observations Logged",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "Log observations during your morning routine to feed the DSS engine.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        } else {
            items(state.observedLogs) { log ->
                ObservationLogCard(log = log)
            }
        }
    }
}

@Composable
private fun ObservationLogCard(log: CropLog) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.cropName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LushGreen
                )
                val displayDate = if (log.date.isNotBlank()) log.date else log.createdAt.take(10)
                Text(
                    text = displayDate,
                    fontSize = 11.sp,
                    color = Color(0xFF777777)
                )
            }

            log.notes?.takeIf { it.isNotBlank() }?.let { notesText ->
                Text(
                    text = notesText,
                    fontSize = 12.sp,
                    color = DeepBlack,
                    lineHeight = 16.sp
                )
            }

            if (log.selectedCheckboxes.isNotEmpty()) {
                Text(
                    text = "Observations: ${log.selectedCheckboxes.joinToString(", ")}",
                    fontSize = 11.sp,
                    color = Color(0xFF555555)
                )
            }
        }
    }
}
