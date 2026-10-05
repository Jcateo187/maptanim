package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * SoilCompatibilityCard — Detailed soil texture, pH range, and land preparation requirements.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun SoilCompatibilityCard(
    soilInfo: SoilInfo,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOIL COMPATIBILITY & LAND PREPARATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "pH ${soilInfo.optimalPh}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Ideal Soil & Drainage Metric Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Landscape,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Ideal Soil",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = soilInfo.idealSoilTypes,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Drainage",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = soilInfo.drainage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }
                }
            }

            // Land Prep & Organic Matter
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Land Preparation Protocol:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen
                )
                Text(
                    text = soilInfo.landPrep,
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 16.sp
                )
            }

            if (soilInfo.organicMatter.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Organic Matter & Compost Requirements:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                    Text(
                        text = soilInfo.organicMatter,
                        fontSize = 11.sp,
                        color = DeepBlack,
                        lineHeight = 16.sp
                    )
                }
            }

            // Agronomic Recommendations
            if (soilInfo.recommendations.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "DA-BPI Best Practice Advice:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    soilInfo.recommendations.forEach { rec ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                            )
                            Text(
                                text = rec,
                                fontSize = 11.sp,
                                color = DeepBlack,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
