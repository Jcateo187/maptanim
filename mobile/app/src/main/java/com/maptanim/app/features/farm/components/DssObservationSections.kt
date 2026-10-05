package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

data class BackyardSizePreset(
    val label: String,
    val description: String,
    val widthM: Float,
    val heightM: Float
)

val BACKYARD_SIZE_PRESETS = listOf(
    BackyardSizePreset("Courtyard (24 m²)", "Urban / suburban side-garden or courtyard", 6.0f, 4.0f),
    BackyardSizePreset("Standard Backyard (96 m²)", "Typical residential backyard garden", 12.0f, 8.0f),
    BackyardSizePreset("Spacious Plot (216 m²)", "Large backyard (e.g. Murcia rural plot)", 18.0f, 12.0f),
    BackyardSizePreset("Compact Bed Area (15 m²)", "Compact patio or small raised bed patch", 5.0f, 3.0f),
    BackyardSizePreset("Mini Patch (8 m²)", "Balcony or side pathway planting strip", 4.0f, 2.0f)
)

val ZERO_BUDGET_HOUSEHOLD_MATERIALS = listOf(
    "Wood Ash (Abo)" to "Potassium source, neutralizes acidic soil, slug barrier",
    "Crushed Eggshells" to "Slow-release Calcium (prevents Blossom-End Rot)",
    "Dried Leaves / Straw" to "Mulch against rain-splash leaf rot & moisture loss",
    "Bamboo Stakes" to "Physical upright support for flopping tomato/bean vines",
    "Chili & Garlic" to "Kitchen spray against caterpillars, aphids & worms"
)

val SEASONS_LIST = listOf(
    "Wet / Rainy Season" to "Frequent rainfall; elevated beds & drainage needed",
    "Dry / Hot Sunny Season" to "High tropical heat; requires mulching & shade net"
)

/**
 * SeasonDropdownSelector — Allows beginner to set current weather condition.
 */
@Composable
fun SeasonDropdownSelector(
    selectedSeason: String,
    onSelectSeason: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "CURRENT SEASON / WEATHER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = DeepBlack
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SEASONS_LIST.forEach { (seasonName, note) ->
                val isSelected = selectedSeason == seasonName
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectSeason(seasonName) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) LushGreen.copy(alpha = 0.08f) else LightSurface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) LushGreen else CardBorderColor
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = seasonName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSelected) LushGreen else DeepBlack
                        )
                        Text(
                            text = note,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

/**
 * BackyardScalePresetChips — Quick 1-tap presets based on familiar basketball court dimensions.
 */
@Composable
fun BackyardScalePresetChips(
    currentWidth: Float,
    currentHeight: Float,
    onSelectPreset: (Float, Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "BACKYARD SIZE PRESETS (BASKETBALL BENCHMARK)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = DeepBlack
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BACKYARD_SIZE_PRESETS.forEach { preset ->
                val isSelected = (currentWidth == preset.widthM && currentHeight == preset.heightM) ||
                        (currentWidth == preset.heightM && currentHeight == preset.widthM)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPreset(preset.widthM, preset.heightM) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) LushGreen.copy(alpha = 0.08f) else LightSurface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) LushGreen else CardBorderColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = preset.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) LushGreen else DeepBlack
                            )
                            Text(
                                text = preset.description,
                                fontSize = 10.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        Text(
                            text = "${preset.widthM.toInt()}m × ${preset.heightM.toInt()}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) LushGreen else Color(0xFF757575)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ZeroBudgetMaterialsSelector — Checklist of available household items to inform DSS prescriptions.
 */
@Composable
fun ZeroBudgetMaterialsSelector(
    selectedMaterials: Set<String>,
    onToggleMaterial: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "AVAILABLE ₱0 HOUSEHOLD ITEMS (NO MONEY NEEDED)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = DeepBlack
        )
        Text(
            text = "Select what you already have at home so the DSS prescribes zero-cost solutions:",
            fontSize = 10.sp,
            color = Color(0xFF666666)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ZERO_BUDGET_HOUSEHOLD_MATERIALS.forEach { (materialName, usage) ->
                val isSelected = selectedMaterials.contains(materialName)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleMaterial(materialName) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) LushGreen.copy(alpha = 0.08f) else LightSurface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) LushGreen else CardBorderColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = materialName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isSelected) LushGreen else DeepBlack
                            )
                            Text(
                                text = usage,
                                fontSize = 9.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        Text(
                            text = if (isSelected) "AVAILABLE" else "NOT AVAILABLE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) LushGreen else Color(0xFF9E9E9E)
                        )
                    }
                }
            }
        }
    }
}

/**
 * FarmBoundariesScaleSection — Direct decimal inputs and stepper controls for yard dimensions (guide only).
 */
@Composable
fun FarmBoundariesScaleSection(
    widthM: Float,
    heightM: Float,
    onWidthChange: (Float) -> Unit,
    onHeightChange: (Float) -> Unit
) {
    val areaSqM = widthM * heightM
    var widthText by remember(widthM) {
        mutableStateOf(if (widthM % 1f == 0f) widthM.toInt().toString() else String.format("%.1f", widthM))
    }
    var heightText by remember(heightM) {
        mutableStateOf(if (heightM % 1f == 0f) heightM.toInt().toString() else String.format("%.1f", heightM))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = LightSurface,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "YARD MEASUREMENT & SCALE (GUIDE ONLY)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Text(
                        text = "Step Pacing: 1 natural step ≈ 0.85m | Yard boundary is an outer guide",
                        fontSize = 9.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Width Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "WIDTH (METERS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
                    OutlinedTextField(
                        value = widthText,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d*$"""))) {
                                widthText = input
                                input.toFloatOrNull()?.let { onWidthChange(it.coerceIn(2f, 50f)) }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalButton(
                            onClick = {
                                val next = (widthM - 1f).coerceAtLeast(2f)
                                onWidthChange(next)
                            },
                            modifier = Modifier.weight(1f).height(28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-1m", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = {
                                val next = (widthM + 1f).coerceAtMost(50f)
                                onWidthChange(next)
                            },
                            modifier = Modifier.weight(1f).height(28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+1m", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Length Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "LENGTH (METERS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d*$"""))) {
                                heightText = input
                                input.toFloatOrNull()?.let { onHeightChange(it.coerceIn(2f, 50f)) }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalButton(
                            onClick = {
                                val next = (heightM - 1f).coerceAtLeast(2f)
                                onHeightChange(next)
                            },
                            modifier = Modifier.weight(1f).height(28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-1m", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = {
                                val next = (heightM + 1f).coerceAtMost(50f)
                                onHeightChange(next)
                            },
                            modifier = Modifier.weight(1f).height(28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+1m", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Yard Area & Capacity Pill
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Total Yard Footprint: ${String.format("%.1f", areaSqM)} m² (${if (widthM % 1f == 0f) widthM.toInt() else String.format("%.1f", widthM)}m × ${if (heightM % 1f == 0f) heightM.toInt() else String.format("%.1f", heightM)}m)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = LushGreen
                    )
                    val bedsCapacity = (areaSqM / 6.0f).toInt().coerceAtLeast(1)
                    Text(
                        text = when {
                            areaSqM < 15.0f -> "Compact space: ideal for 1–2 raised beds ($bedsCapacity bed capacity) or container gardening."
                            areaSqM < 60.0f -> "Standard residential backyard: fits approx $bedsCapacity standard beds with walking pathways."
                            areaSqM < 150.0f -> "Spacious backyard: ample space for up to $bedsCapacity beds, composting zone, and trellises."
                            else -> "Farmlet / large garden: supports $bedsCapacity+ beds with multi-crop rotation zones."
                        },
                        fontSize = 10.sp,
                        color = Color(0xFF555555)
                    )
                }
            }
        }
    }
}

