package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.features.farm.components.CropHarvestTimelineCard
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * BedTimelineDialog — Direct modal displaying starting point, expected harvest timeline,
 * picking intervals, and yield forecast for all crops in the active bed.
 */
@Composable
fun BedTimelineDialog(
    plot: PlotRenderData,
    cropZones: List<CropZoneRenderData>,
    focusedCropName: String? = null,
    onDismiss: () -> Unit
) {
    val zonesInBed = cropZones.filter {
        it.plotId == plot.id && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
    }.let { list ->
        if (focusedCropName.isNullOrBlank()) list
        else list.sortedByDescending { it.cropName.equals(focusedCropName, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${plot.plotLabel} Harvest Timelines",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Text(
                            text = "Starting points & expected picking windows",
                            fontSize = 11.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepBlack)
                    }
                }

                HorizontalDivider(color = CardBorderColor)

                if (zonesInBed.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F8E9),
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = LushGreen, modifier = Modifier.size(24.dp))
                            Text("No Crops in this Bed Yet", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepBlack)
                            Text("Plant vegetables from the Crop Tray to view starting date and harvest countdowns.", fontSize = 11.sp, color = Color(0xFF555555))
                        }
                    }
                } else {
                    val resolvedPlantedDateMillis = remember(plot.plantedDate) {
                        if (!plot.plantedDate.isNullOrBlank()) {
                            try {
                                if (plot.plantedDate.contains("T")) {
                                    java.time.ZonedDateTime.parse(plot.plantedDate).toInstant().toEpochMilli()
                                } else {
                                    java.time.LocalDate.parse(plot.plantedDate.take(10)).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }
                            } catch (_: Exception) {
                                System.currentTimeMillis()
                            }
                        } else {
                            System.currentTimeMillis()
                        }
                    }
                    val grouped = zonesInBed.groupBy { it.cropName ?: "Crop" }
                    for ((name, zones) in grouped) {
                        CropHarvestTimelineCard(
                            cropName = name,
                            plantedDateMillis = resolvedPlantedDateMillis,
                            plantCount = zones.size
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Close Timeline", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
