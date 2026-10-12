package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * GardenSummaryDialog — Displays total beds, dimensions, surface area, and crop inventory.
 */
@Composable
fun GardenSummaryDialog(
    farmName: String,
    plots: List<PlotRenderData>,
    cropZones: List<CropZoneRenderData>,
    onDismiss: () -> Unit
) {
    val totalAreaSqm = plots.sumOf { (it.widthM * it.heightM).toDouble() }
    val totalAreaSqFt = totalAreaSqm * 10.7639

    val plantedCrops = cropZones.filter {
        !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
    }
    val cropCounts = plantedCrops.groupBy { it.cropName ?: "Vegetable" }.mapValues { it.value.size }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Assessment, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("GARDEN SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                            Text(farmName.ifBlank { "My Farm" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.DarkGray)
                    }
                }

                HorizontalDivider(color = CardBorderColor)

                // Key Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("TOTAL GARDENS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${plots.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PLANTED CROPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${plantedCrops.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                    }
                }

                // Surface Area
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F8E9),
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SURFACE AREA (SQ FT)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${String.format("%.1f", totalAreaSqFt)} sq ft", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("METRIC EQUIVALENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${String.format("%.1f", totalAreaSqm)} m²", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                    }
                }

                // Crops Breakdown List
                Text(
                    text = "VEGETABLE INVENTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen,
                    letterSpacing = 0.5.sp
                )

                if (cropCounts.isEmpty()) {
                    Text("No vegetables planted yet. Tap + ADD VEGETABLES to start planting.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        cropCounts.forEach { (cName, count) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(cName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = LushGreen
                                    ) {
                                        Text(
                                            text = "$count planted",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}
