package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.library.model.VegetableAgronomicGuide

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * TabHarvestMethodContent — Harvest indicators, cutting techniques, and storage specs.
 */
@Composable
fun TabHarvestMethodContent(guide: VegetableAgronomicGuide) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "HARVEST TIME",
                value = guide.harvest.timeOfDay,
                icon = Icons.Default.WbTwilight,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "HARVEST FREQ",
                value = guide.harvest.frequency,
                icon = Icons.Default.Repeat,
                modifier = Modifier.weight(1f)
            )
        }

        // Maturity & Harvesting Protocol
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Maturity Indicators & Timing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = "Primary Sign: ${guide.harvest.maturityIndicators}",
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 15.sp
                )
                Text(
                    text = "Harvest Days Window: ${guide.harvest.daysRange}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = LushGreen
                )
                Text(
                    text = "Technique: ${guide.harvest.harvestingMethod}",
                    fontSize = 11.sp,
                    color = MutedText,
                    lineHeight = 15.sp
                )

                if (guide.harvest.indicatorsList.isNotEmpty()) {
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Text("Readiness Checklist:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = DeepBlack)
                    guide.harvest.indicatorsList.forEach { indicator ->
                        BulletItem(text = indicator)
                    }
                }
            }
        }

        // Post-Harvest Curing, Washing & Storage
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Post-Harvest Handling & Storage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = "Sorting & Grading: ${guide.postHarvest.sortingGrading}",
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 15.sp
                )
                Text(
                    text = "Washing & Cleaning: ${guide.postHarvest.washingCleaning}",
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 15.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecMetricCard(
                        title = "TEMP / HUMIDITY",
                        value = "${guide.postHarvest.optimalTemperature} | ${guide.postHarvest.relativeHumidity}",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f)
                    )
                    SpecMetricCard(
                        title = "SHELF LIFE",
                        value = guide.postHarvest.shelfLife,
                        icon = Icons.Default.Timer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Packaging: ${guide.postHarvest.packagingTransport}",
                    fontSize = 11.sp,
                    color = MutedText,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
