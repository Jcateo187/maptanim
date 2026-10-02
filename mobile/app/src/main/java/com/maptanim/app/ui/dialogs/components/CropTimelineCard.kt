package com.maptanim.app.ui.dialogs.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.ui.theme.White

@Composable
fun CropTimelineCard(
    currentStage: ManagementStage,
    isExpanded: Boolean,
    canGoPrevious: Boolean = false,
    canGoNext: Boolean = false,
    onPreviousStage: (() -> Unit)? = null,
    onNextStage: (() -> Unit)? = null,
    onToggleExpand: () -> Unit,
    onRecordObservation: () -> Unit,
    onHarvest: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
        border = BorderStroke(1.dp, Color(0xFF2B3825)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header with +/- toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CROP TIMELINE",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isExpanded) "−" else "+",
                    color = Color(0xFF81C784),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    ManagementStage.entries.forEachIndexed { index, stage ->
                        val isCurrent = stage == currentStage
                        val isPast = stage.stageNumber < currentStage.stageNumber

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Timeline dot & connector
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isCurrent) 14.dp else 10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> Color(0xFF81C784)
                                                isPast -> Color(0xFF2E7D32)
                                                else -> Color(0xFF384A33)
                                            }
                                        )
                                )
                                if (index < ManagementStage.entries.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(20.dp)
                                            .background(if (isPast) Color(0xFF2E7D32) else Color(0xFF243020))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stage.label,
                                    color = if (isCurrent) White else if (isPast) Color(0xFFA0B09A) else Color(0xFF6B7B67),
                                    fontSize = 13.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )

                                if (isCurrent) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E2D1A),
                                        border = BorderStroke(1.dp, Color(0xFF2E7D32))
                                    ) {
                                        Text(
                                            text = "CURRENT",
                                            color = Color(0xFF81C784),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Manual Stage Navigation: [◀ Prev Stage] [Next Stage ▶] ─────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onPreviousStage?.invoke() },
                            enabled = canGoPrevious,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (canGoPrevious) Color(0xFF384A33) else Color(0xFF20281E)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF81C784),
                                disabledContentColor = Color(0xFF42503E)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("◀ Prev Stage", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            text = "Stage ${currentStage.stageNumber} of ${ManagementStage.entries.size}",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        OutlinedButton(
                            onClick = { onNextStage?.invoke() },
                            enabled = canGoNext,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (canGoNext) Color(0xFF384A33) else Color(0xFF20281E)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF81C784),
                                disabledContentColor = Color(0xFF42503E)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Next Stage ▶", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons: Record Observation + Harvest (only visible in final Stage 6)
                    val isHarvestStage = currentStage == ManagementStage.HARVEST
                    if (isHarvestStage) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onRecordObservation,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = White
                                )
                            ) {
                                Text(
                                    text = "Record Observation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onHarvest,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE65100),
                                    contentColor = White
                                )
                            ) {
                                Text(
                                    text = "Record Harvest 🌾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onRecordObservation,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = White
                            )
                        ) {
                            Text(
                                text = "Record Observation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
