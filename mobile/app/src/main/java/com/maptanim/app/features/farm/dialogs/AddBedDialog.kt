package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.SoilType

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * AddBedDialog — Dialog for creating a new garden bed with standard agronomic presets.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@Composable
fun AddBedDialog(
    existingCount: Int,
    defaultSoil: SoilType = SoilType.LOAM,
    onDismiss: () -> Unit,
    onConfirm: (label: String, widthM: Float, heightM: Float, soilType: SoilType, cropName: String?) -> Unit
) {
    var label by remember { mutableStateOf("Bed #${existingCount + 1}") }
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var selectedSoil by remember { mutableStateOf(defaultSoil) }
    var selectedCropName by remember { mutableStateOf<String?>(null) }

    val presets = remember {
        listOf(
            Triple("Standard Bed (4.0m × 1.2m)", 4.0f to 1.2f, "Optimal bio-intensive double reach"),
            Triple("Long Field Bed (6.0m × 1.2m)", 6.0f to 1.2f, "High-yield market garden row"),
            Triple("Compact Bed (2.0m × 1.0m)", 2.0f to 1.0f, "Herbs, nursery, or tight space"),
            Triple("Raised Bed (3.0m × 0.8m)", 3.0f to 0.8f, "Timber / masonry raised garden")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Yard,
                    contentDescription = null,
                    tint = LushGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Add New Garden Bed",
                    color = DeepBlack,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Bed Label Field
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Bed Name / Label") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack,
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor,
                        focusedLabelColor = LushGreen
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Presets
                Text(
                    text = "BED DIMENSIONS & PRESET",
                    color = Color(0xFF555555),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEachIndexed { index, (presetName, dims, desc) ->
                        val isSel = index == selectedPresetIndex
                        Surface(
                            onClick = { selectedPresetIndex = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFFE8F5E9) else LightSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSel) LushGreen else CardBorderColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = presetName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) LushGreen else DeepBlack
                                )
                                Text(
                                    text = "${dims.first}m × ${dims.second}m • $desc",
                                    fontSize = 11.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }

                // Soil Type
                Text(
                    text = "SOIL TYPE",
                    color = Color(0xFF555555),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SoilType.entries.forEach { soil ->
                        val isSel = soil == selectedSoil
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedSoil = soil },
                            label = { Text(soil.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LushGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = DeepBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSel,
                                borderColor = if (isSel) LushGreen else CardBorderColor
                            )
                        )
                    }
                }

                // Plant Vegetable Crop
                Text(
                    text = "PLANT VEGETABLE CROP (OPTIONAL)",
                    color = Color(0xFF555555),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (selectedCropName != null) "Selected: $selectedCropName" else "Tap a crop to plant now, or leave empty to decide later:",
                    fontSize = 10.sp,
                    fontWeight = if (selectedCropName != null) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedCropName != null) LushGreen else Color(0xFF757575)
                )
                val cropOptions = listOf(
                    null to "Leave Empty",
                    "Tomato" to "Tomato",
                    "Eggplant" to "Eggplant",
                    "Sitaw" to "Sitaw",
                    "Chili" to "Chili",
                    "Okra" to "Okra",
                    "Pechay" to "Pechay",
                    "Lettuce" to "Lettuce",
                    "Kangkong" to "Kangkong",
                    "Cucumber" to "Cucumber",
                    "Sweet Corn" to "Sweet Corn"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cropOptions) { (cropVal, cropLabel) ->
                        val isSel = selectedCropName == cropVal
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedCropName = cropVal },
                            label = { Text(cropLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LushGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = DeepBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSel,
                                borderColor = if (isSel) LushGreen else CardBorderColor
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (w, h) = presets[selectedPresetIndex].second
                    onConfirm(label.ifBlank { "Bed #${existingCount + 1}" }, w, h, selectedSoil, selectedCropName)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LushGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create Bed", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Text("Cancel", color = DeepBlack)
            }
        }
    )
}
