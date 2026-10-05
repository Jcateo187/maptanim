package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.dss.evaluator.PlaceBasedCropEvaluator
import com.maptanim.app.dss.evaluator.SuitabilityTier

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

data class CropSpacingInfo(
    val plantSpacingCm: Int,
    val rowSpacingCm: Int,
    val nutrientAppetite: String
)

fun getCropSpacing(cropName: String?): CropSpacingInfo = when (cropName?.lowercase()) {
    "tomato", "kamatis" -> CropSpacingInfo(50, 80, "Heavy Feeder (5 kg/m² compost)")
    "eggplant", "talong" -> CropSpacingInfo(50, 75, "Heavy Feeder (5 kg/m² compost)")
    "chili", "sili" -> CropSpacingInfo(40, 50, "Medium Feeder (3 kg/m² compost)")
    "okra" -> CropSpacingInfo(30, 60, "Medium Feeder (3 kg/m² compost)")
    "pechay" -> CropSpacingInfo(20, 25, "Light Feeder (2–3 kg/m² compost)")
    "lettuce", "litsugas" -> CropSpacingInfo(20, 25, "Light Feeder (2–3 kg/m² compost)")
    "kangkong" -> CropSpacingInfo(15, 20, "Medium Feeder (3 kg/m² compost)")
    "cucumber", "pipino" -> CropSpacingInfo(40, 80, "Heavy Feeder (4 kg/m² compost)")
    "sitaw" -> CropSpacingInfo(30, 60, "Light Feeder (Fixes Nitrogen)")
    "corn", "mais", "sweet corn" -> CropSpacingInfo(30, 75, "Heavy Feeder (5 kg/m² compost)")
    else -> CropSpacingInfo(30, 40, "Standard Bed Spacing")
}

fun calculatePlantCapacity(widthM: Float, heightM: Float, cropName: String?): Int {
    if (cropName.isNullOrBlank()) return 0
    val spacing = getCropSpacing(cropName)
    val dim1 = maxOf(widthM, heightM) * 100f
    val dim2 = minOf(widthM, heightM) * 100f
    val plantsInRow = maxOf(1, (dim1 / spacing.plantSpacingCm).toInt())
    val numRows = maxOf(1, (dim2 / spacing.rowSpacingCm).toInt())
    return plantsInRow * numRows
}

/**
 * BedEditorCard — Contextual editor for the selected bed.
 * Calculates physical plant capacity based on crop spacing math (§2.3 of Doc 43)
 * and provides clear navigation to the Step-by-Step Daily Guide.
 */
@Composable
fun BedEditorCard(
    plot: CropPlot,
    environment: FarmEnvironment = FarmEnvironment(),
    onResize: (Float, Float) -> Unit,
    onAssignCrop: (String, String) -> Unit,
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val evaluator = remember { PlaceBasedCropEvaluator() }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
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
                Column {
                    Text(
                        text = "EDIT ${plot.plotLabel.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = LushGreen
                    )
                    Text(
                        text = "Crop: ${plot.cropName ?: "None"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "${String.format("%.1f", plot.widthM * plot.heightM)} m²",
                        color = LushGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = CardBorderColor)

            // Resizing Steppers: Width & Length
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Width Stepper
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "WIDTH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { onResize((plot.widthM - 0.5f).coerceAtLeast(0.5f), plot.heightM) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Width", tint = LushGreen)
                        }
                        Text(
                            text = "${String.format("%.1f", plot.widthM)}m",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepBlack
                        )
                        IconButton(
                            onClick = { onResize((plot.widthM + 0.5f).coerceAtMost(15f), plot.heightM) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Width", tint = LushGreen)
                        }
                    }
                }

                // Length Stepper
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "LENGTH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { onResize(plot.widthM, (plot.heightM - 0.5f).coerceAtLeast(0.5f)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Length", tint = LushGreen)
                        }
                        Text(
                            text = "${String.format("%.1f", plot.heightM)}m",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepBlack
                        )
                        IconButton(
                            onClick = { onResize(plot.widthM, (plot.heightM + 0.5f).coerceAtMost(15f)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Length", tint = LushGreen)
                        }
                    }
                }
            }

            // ── Physical Plant Capacity Math (§2.3 of Doc 43) ───────────────
            val spacing = getCropSpacing(plot.cropName)
            val dim1 = maxOf(plot.widthM, plot.heightM) * 100f
            val dim2 = minOf(plot.widthM, plot.heightM) * 100f
            val plantsInRow = maxOf(1, (dim1 / spacing.plantSpacingCm).toInt())
            val numRows = maxOf(1, (dim2 / spacing.rowSpacingCm).toInt())
            val totalCapacity = if (plot.cropName.isNullOrBlank()) 0 else plantsInRow * numRows

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "PHYSICAL PLANT CAPACITY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666)
                        )
                        if (plot.cropName.isNullOrBlank()) {
                            Text(
                                text = "Assign a crop below to compute plant count",
                                fontSize = 11.sp,
                                color = Color(0xFF888888)
                            )
                        } else {
                            Text(
                                text = "$totalCapacity Plants ($numRows rows × $plantsInRow plants)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LushGreen
                            )
                            Text(
                                text = "Spacing: ${spacing.plantSpacingCm}cm in row × ${spacing.rowSpacingCm}cm between rows",
                                fontSize = 10.sp,
                                color = DeepBlack
                            )
                        }
                    }
                    if (!plot.cropName.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = spacing.nutrientAppetite.substringBefore("(").trim(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LushGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Quick Assign Crop: 10 Canonical Philippine Crops with Place Suitability Dot
            Text(
                text = "ASSIGN CROP TO BED (WITH SUITABILITY)",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF666666)
            )
            val crops = listOf(
                "tomato" to "Tomato",
                "eggplant" to "Eggplant",
                "chili" to "Chili",
                "okra" to "Okra",
                "pechay" to "Pechay",
                "lettuce" to "Lettuce",
                "kangkong" to "Kangkong",
                "cucumber" to "Cucumber",
                "sitaw" to "Sitaw",
                "corn" to "Sweet Corn"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(crops) { (cId, cName) ->
                    val isAssigned = plot.cropName.equals(cName, ignoreCase = true)
                    val suitability = evaluator.evaluate(cName, environment)
                    val tierColor = Color(suitability.tier.colorHex)

                    FilterChip(
                        selected = isAssigned,
                        onClick = { onAssignCrop(cId, cName) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(6.dp),
                                    shape = RoundedCornerShape(3.dp),
                                    color = tierColor
                                ) {}
                                Text(
                                    text = cName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isAssigned) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LushGreen,
                            selectedLabelColor = Color.White,
                            containerColor = LightSurface,
                            labelColor = DeepBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isAssigned,
                            borderColor = if (isAssigned) LushGreen else CardBorderColor
                        )
                    )
                }
            }

            // ── Primary Guided Workflow CTA ───────────────────────────────────
            if (!plot.cropName.isNullOrBlank()) {
                Button(
                    onClick = onNavigateToGuide,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Open Step-by-Step Daily Guide for ${plot.cropName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to Guide",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
