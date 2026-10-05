package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.library.model.GrowthStageItem

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * GrowthStageTimeline — Interactive phenology timeline for crop life stages.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun GrowthStageTimeline(
    stages: List<GrowthStageItem>,
    selectedIndex: Int,
    onSelectStage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GROWTH STAGES & PHENOLOGY TIMELINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap stage to view",
                    fontSize = 10.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Horizontal Stage Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stages.forEachIndexed { idx, stage ->
                    val isSel = idx == selectedIndex
                    Surface(
                        onClick = { onSelectStage(idx) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) LushGreen else LightSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSel) LushGreen else CardBorderColor
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${stage.stageNumber}.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else LushGreen
                            )
                            Text(
                                text = stage.stageName.take(16),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else DeepBlack
                            )
                        }
                    }
                }
            }

            // Expanded Active Stage Card
            val activeStage = stages.getOrNull(selectedIndex) ?: stages.firstOrNull()
            if (activeStage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stage ${activeStage.stageNumber}: ${activeStage.stageName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LushGreen
                            )
                            Text(
                                text = activeStage.durationDays,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                        }
                        Text(
                            text = "Phenology: ${activeStage.description}",
                            fontSize = 11.sp,
                            color = DeepBlack,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Farmer Priority: ${activeStage.farmerAction}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LushGreen,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
