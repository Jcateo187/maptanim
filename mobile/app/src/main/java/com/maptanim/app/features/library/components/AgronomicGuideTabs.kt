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
 * TabPreparationContent — Bed preparation, basal fertilization, and soil profile specs.
 */
@Composable
fun TabPreparationContent(guide: VegetableAgronomicGuide, isOrganic: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "SOIL TYPE",
                value = guide.soil.idealSoilTypes,
                icon = Icons.Default.Terrain,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "OPTIMAL PH",
                value = guide.soil.optimalPh,
                icon = Icons.Default.Science,
                modifier = Modifier.weight(1f)
            )
        }

        // Land Preparation Card
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
                    text = "Land & Bed Preparation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = guide.soil.landPrep,
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 16.sp
                )
                Text(
                    text = "Drainage: ${guide.soil.drainage}",
                    fontSize = 11.sp,
                    color = MutedText
                )
                Text(
                    text = "Organic Matter: ${guide.soil.organicMatter}",
                    fontSize = 11.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Basal Fertilization Protocol Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Basal Soil Enrichment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepBlack
                    )
                    Text(
                        text = if (isOrganic) "ORGANIC" else "BALANCED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                }
                Text(
                    text = guide.fertilization.basalApplication,
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 16.sp
                )
                if (isOrganic) {
                    Text(
                        text = "Suggested Inputs: ${guide.fertilization.organicOptions}",
                        fontSize = 11.sp,
                        color = LushGreen
                    )
                }
            }
        }

        // Soil Optimization Recommendations
        if (guide.soil.recommendations.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Soil Optimization Tips",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepBlack
                    )
                    guide.soil.recommendations.forEach { tip ->
                        BulletItem(text = tip)
                    }
                }
            }
        }
    }
}

/**
 * TabPlantingContent — In-row spacing, row spacing, planting depth, and execution checklist.
 */
@Composable
fun TabPlantingContent(guide: VegetableAgronomicGuide, plantingMethod: String) {
    val isDirect = plantingMethod == "Direct Seeding"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "IN-ROW SPACING",
                value = guide.planting.plantSpacing,
                icon = Icons.Default.FormatLineSpacing,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "ROW SPACING",
                value = guide.planting.rowSpacing,
                icon = Icons.Default.ViewColumn,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "PLANTING DEPTH",
                value = guide.planting.plantingDepth,
                icon = Icons.Default.VerticalAlignBottom,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = if (isDirect) "GERMINATION" else "TRANSPLANT AGE",
                value = if (isDirect) guide.planting.germinationDays else guide.planting.transplantAge,
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )
        }

        // Trellising Requirement Card
        if (guide.planting.trellisingNeeded) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Trellis Support Required",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LushGreen
                        )
                        guide.planting.trellisingAdvice?.let {
                            Text(
                                text = it,
                                fontSize = 11.sp,
                                color = DeepBlack,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Planting Execution Steps
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
                    text = if (isDirect) "Direct Seeding Execution" else "Transplanting Protocol",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                guide.planting.tips.forEachIndexed { index, tip ->
                    ProcedureCard(
                        stepNumber = index + 1,
                        title = tip.substringBefore(":"),
                        instruction = tip.substringAfter(":").trim()
                    )
                }
            }
        }
    }
}
