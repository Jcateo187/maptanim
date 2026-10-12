package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.ui.theme.White

@Composable
fun CropInfoCard(
    plotLabel: String,
    cropName: String,
    cropVariety: String,
    plantedDate: String,
    plantingMethod: String,
    growingApproach: String,
    currentStage: ManagementStage,
    isPlanted: Boolean,
    onOpenSettings: () -> Unit
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bed Label + Stage / Planned Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plotLabel.uppercase(),
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    if (!isPlanted) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF332A15),
                            border = BorderStroke(1.dp, Color(0xFF5C4A1C))
                        ) {
                            Text(
                                text = "PLANNED / UNPLANTED",
                                color = Color(0xFFFFD54F),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E281B)
                ) {
                    Text(
                        text = "Stage ${currentStage.stageNumber}/6",
                        color = Color(0xFFA0B09A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Crop Name with botanical emoji + Settings gear button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val cropEmoji = when (cropName.lowercase()) {
                        "tomato" -> "🍅"
                        "eggplant" -> "🍆"
                        "carrot" -> "🥕"
                        "chili", "pepper" -> "🌶️"
                        "corn" -> "🌽"
                        "cucumber" -> "🥒"
                        "onion" -> "🧅"
                        "garlic" -> "🧄"
                        "lettuce", "cabbage", "pechay" -> "🥬"
                        else -> "🌱"
                    }
                    Text(
                        text = "$cropEmoji $cropName",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cropVariety,
                        color = Color(0xFFA0B09A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Crop Details",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp)

            // Planting Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Planting Date",
                    color = Color(0xFF8B9B85),
                    fontSize = 12.sp
                )
                Text(
                    text = plantedDate,
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Method",
                    color = Color(0xFF8B9B85),
                    fontSize = 12.sp
                )
                Text(
                    text = plantingMethod,
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Growing Approach",
                    color = Color(0xFF8B9B85),
                    fontSize = 12.sp
                )
                Text(
                    text = growingApproach,
                    color = Color(0xFF81C784),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
