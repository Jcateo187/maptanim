package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

private val LushGreen = Color(0xFF2E7D32)
private val AlertAmber = Color(0xFFFFB300)
private val AlertRed = Color(0xFFD32F2F)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

@Composable
fun DirectionalBedSizingDialog(
    initialWidthM: Float,
    initialHeightM: Float,
    plotLabel: String,
    onApplyDimensions: (newWidthM: Float, newHeightM: Float) -> Unit,
    onDismiss: () -> Unit
) {
    var widthM by remember { mutableFloatStateOf(initialWidthM.coerceIn(0.6f, 4.0f)) }
    var heightM by remember { mutableFloatStateOf(initialHeightM.coerceIn(0.6f, 10.0f)) }

    val areaSqm = remember(widthM, heightM) { widthM * heightM }
    val gridCols = remember(widthM) { (widthM / 0.30f).roundToInt().coerceAtLeast(1) }
    val gridRows = remember(heightM) { (heightM / 0.30f).roundToInt().coerceAtLeast(1) }
    val totalCells = remember(gridCols, gridRows) { gridCols * gridRows }

    val isErgonomicSafe = widthM <= 1.25f

    // Biological Capacity Estimates
    val tomatoCapacity = remember(totalCells) { (totalCells / 4).coerceAtLeast(1) }
    val pepperCapacity = remember(totalCells) { totalCells }
    val pechayCapacity = remember(totalCells) { totalCells * 4 }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DIRECTIONAL BED SIZING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = plotLabel,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepBlack)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Ergonomic Reach Warning / Safe Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isErgonomicSafe) Color(0xFFF1F8E9) else Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, if (isErgonomicSafe) LushGreen.copy(alpha = 0.5f) else AlertRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (isErgonomicSafe) Icons.Default.Info else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isErgonomicSafe) LushGreen else AlertRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = if (isErgonomicSafe) "OPTIMAL HUMAN REACH (1.2m GOLDEN RULE)" else "OVER-WIDTH SOIL COMPACTION WARNING",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isErgonomicSafe) LushGreen else AlertRed
                                )
                                Text(
                                    text = if (isErgonomicSafe)
                                        "Width of ${String.format("%.1f", widthM)}m allows you to comfortably reach the center from both sides without stepping into the bed."
                                    else
                                        "Width of ${String.format("%.1f", widthM)}m exceeds 1.25m arm reach. You will be forced to step onto the bed, crushing soil pores and suffocating roots.",
                                    fontSize = 11.5.sp,
                                    color = DeepBlack,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Live Metric & Grid Matrix
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SURFACE AREA", fontSize = 9.sp, color = Color.Gray)
                                Text("${String.format("%.2f", areaSqm)} m²", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepBlack)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PLANTER GRID", fontSize = 9.sp, color = Color.Gray)
                                Text("$gridCols × $gridRows cells", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LushGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL 30CM CELLS", fontSize = 9.sp, color = Color.Gray)
                                Text("$totalCells Units", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepBlack)
                            }
                        }
                    }

                    // Dimension Sliders
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bed Width (Cross-Reach)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                            Text("${String.format("%.2f", widthM)} m", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                        Slider(
                            value = widthM,
                            onValueChange = { widthM = (it * 10).roundToInt() / 10f },
                            valueRange = 0.6f..2.5f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = if (isErgonomicSafe) LushGreen else AlertRed,
                                activeTrackColor = if (isErgonomicSafe) LushGreen else AlertRed
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bed Length (Walking Run)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                            Text("${String.format("%.2f", heightM)} m", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                        Slider(
                            value = heightM,
                            onValueChange = { heightM = (it * 10).roundToInt() / 10f },
                            valueRange = 1.0f..6.0f,
                            steps = 24,
                            colors = SliderDefaults.colors(thumbColor = LushGreen, activeTrackColor = LushGreen)
                        )
                    }

                    // Live Biological Capacity Preview
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("BIO-INTENSIVE CAPACITY AT THIS SIZE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                            Text("• Large Nightshades (Tomato, Eggplant): Up to $tomatoCapacity plants (2×2 cells)", fontSize = 11.5.sp, color = DeepBlack)
                            Text("• Medium Bush (Pepper, Okra): Up to $pepperCapacity plants (1 cell)", fontSize = 11.5.sp, color = DeepBlack)
                            Text("• Dense Leafy (Pechay, Lettuce): Up to $pechayCapacity heads (4 per cell)", fontSize = 11.5.sp, color = DeepBlack)
                        }
                    }

                    // Recommended Presets
                    Text("STANDARD PHILIPPINE BED PRESETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { widthM = 1.2f; heightM = 3.0f },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Standard\n1.2m × 3.0m", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                        OutlinedButton(
                            onClick = { widthM = 1.2f; heightM = 1.2f },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Square Box\n1.2m × 1.2m", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                        OutlinedButton(
                            onClick = { widthM = 0.8f; heightM = 4.0f },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Narrow Row\n0.8m × 4.0m", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Apply Button
                Button(
                    onClick = {
                        onApplyDimensions(widthM, heightM)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply Bed Size (${String.format("%.1f", widthM)}m × ${String.format("%.1f", heightM)}m)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
