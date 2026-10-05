package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.features.profile.utils.formatActivityTime

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * UserHarvestHistoryCard — Displays recent harvest yield records in Daylight theme (no emojis).
 */
@Composable
fun UserHarvestHistoryCard(
    harvestHistory: List<HarvestRecord>,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Farm Harvest History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepBlack
                    )
                }
                if (harvestHistory.size > 3) {
                    TextButton(onClick = onSeeMoreClick) {
                        Text(
                            text = "See More (${harvestHistory.size})",
                            color = LushGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Text(
                text = "Preserved crop yield records for rotation planning & management decisions.",
                fontSize = 11.sp,
                color = MutedText
            )

            if (harvestHistory.isEmpty()) {
                Text(
                    text = "No harvest records stored yet. Harvest ready crops from active monitoring to record history.",
                    fontSize = 12.sp,
                    color = MutedText,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                val visibleHarvests = harvestHistory.take(3)
                visibleHarvests.forEach { record ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = record.cropName.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DeepBlack
                                    )
                                    if (!record.cropVariety.isNullOrBlank()) {
                                        Text(
                                            text = " (${record.cropVariety})",
                                            fontSize = 12.sp,
                                            color = LushGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9),
                                    border = BorderStroke(1.dp, LushGreen)
                                ) {
                                    Text(
                                        text = record.farmName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LushGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Plot: ${record.plotLabel}",
                                        fontSize = 11.sp,
                                        color = DeepBlack
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Scale,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Yield: ${if (record.yieldKg > 0f) "${record.yieldKg} kg" else "N/A"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MutedText,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Planted: ${record.plantedDate?.take(10) ?: "N/A"}",
                                        fontSize = 10.sp,
                                        color = MutedText
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = MutedText,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Harvested: ${record.harvestedAt.take(10)}",
                                        fontSize = 10.sp,
                                        color = MutedText
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${record.growingDurationDays} ${if (record.cropName.lowercase().contains("ampalaya") || record.cropVariety?.contains("10s", ignoreCase = true) == true) "Secs" else "Days"}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LushGreen
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MutedText,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "Activity: ${formatActivityTime(record.harvestedAt)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MutedText
                                )
                            }

                            if (!record.notes.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Notes,
                                        contentDescription = null,
                                        tint = MutedText,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Notes: ${record.notes}",
                                        fontSize = 11.sp,
                                        color = DeepBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
