package com.maptanim.app.features.farm.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.features.farm.viewmodel.state.FarmHubHarvestState

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * HarvestTab — Harvest readiness indicators, yield recording, and past harvest logs.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@Composable
fun HarvestTab(
    state: FarmHubHarvestState,
    onOpenHarvestModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Action Button: Record Harvest ─────────────────────────────────
        item {
            Button(
                onClick = onOpenHarvestModal,
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
                    text = "Record Harvest Yield",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // ── 2. Metrics Summary ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "TOTAL HARVESTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF777777),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${state.harvestCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "TOTAL YIELD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF777777),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = String.format("%.1f kg", state.totalYieldKg),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen
                        )
                    }
                }
            }
        }

        // ── 3. Overdue Harvest Warning (if any) ──────────────────────────────
        state.overdueHarvestWarning?.let { warning ->
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, Color(0xFFEF9A9A))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = warning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }
            }
        }

        // ── 4. Harvest Records History ───────────────────────────────────────
        item {
            Text(
                text = "HARVEST HISTORY (${state.harvestHistory.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF555555)
            )
        }

        if (state.harvestHistory.isEmpty()) {
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
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No Harvests Recorded Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "When crops reach maturity, log your yield here to track productivity.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        } else {
            items(state.harvestHistory) { record ->
                HarvestRecordCard(record = record)
            }
        }
    }
}

@Composable
private fun HarvestRecordCard(record: HarvestRecord) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = record.cropName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LushGreen
                )
                Text(
                    text = "Harvested on ${record.harvestedAt.take(10)}",
                    fontSize = 11.sp,
                    color = Color(0xFF777777)
                )
                record.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                    Text(
                        text = notes,
                        fontSize = 11.sp,
                        color = DeepBlack
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Text(
                    text = "${record.yieldKg} kg",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LushGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
