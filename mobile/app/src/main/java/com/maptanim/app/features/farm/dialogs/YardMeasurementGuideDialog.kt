package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val LightSurface = Color(0xFFF9FAF8)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * YardMeasurementGuideDialog — Practical real-world guide explaining how to measure
 * an actual backyard (step pacing, tape measure, benchmarks) and apply dimensions to canvas.
 */
@Composable
fun YardMeasurementGuideDialog(
    currentWidthM: Float,
    currentHeightM: Float,
    showRulers: Boolean,
    onApplyDimensions: (widthM: Float, heightM: Float, showRulers: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var widthInput by remember {
        mutableStateOf(if (currentWidthM % 1f == 0f) currentWidthM.toInt().toString() else String.format("%.1f", currentWidthM))
    }
    var heightInput by remember {
        mutableStateOf(if (currentHeightM % 1f == 0f) currentHeightM.toInt().toString() else String.format("%.1f", currentHeightM))
    }
    var enableRulers by remember { mutableStateOf(showRulers) }

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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Header ──────────────────────────────────────────────────
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
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Yard Measurement Guide",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                            Text(
                                text = "Real-world yard sizing & rulers",
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepBlack)
                    }
                }

                HorizontalDivider(color = CardBorderColor)

                // ── Educational Guide: How to Measure ───────────────────────
                Text(
                    text = "HOW TO MEASURE YOUR BACKYARD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen,
                    letterSpacing = 0.5.sp
                )

                // Method 1: Pacing Benchmark
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Step Pacing Method (No tape needed)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepBlack
                            )
                            Text(
                                text = "1 natural adult step = approx 0.85m to 0.90m. Walk along your fence and count steps:\n- 6 steps ≈ 5 meters\n- 12 steps ≈ 10 meters\n- 18 steps ≈ 15 meters",
                                fontSize = 11.sp,
                                color = Color(0xFF444444),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Method 2: Quick Backyard Presets
                Text(
                    text = "QUICK BACKYARD PRESETS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        Triple("Courtyard", 6f, 4f),
                        Triple("Backyard", 12f, 8f),
                        Triple("Spacious", 18f, 12f)
                    )
                    for ((name, w, h) in presets) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (widthInput == w.toInt().toString() && heightInput == h.toInt().toString())
                                Color(0xFFE8F5E9) else LightSurface,
                            border = BorderStroke(
                                1.dp,
                                if (widthInput == w.toInt().toString() && heightInput == h.toInt().toString())
                                    LushGreen else CardBorderColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    widthInput = w.toInt().toString()
                                    heightInput = h.toInt().toString()
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DeepBlack)
                                Text("${w.toInt()}m × ${h.toInt()}m", fontSize = 9.5.sp, color = Color(0xFF666666))
                                Text("${(w * h).toInt()}m²", fontSize = 9.sp, color = LushGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Custom Yard Dimensions Input with Steppers & Decimal Support
                Text(
                    text = "YOUR EXACT YARD DIMENSIONS (METERS)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Width Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = widthInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d*$"""))) {
                                    widthInput = input
                                }
                            },
                            label = { Text("Width (m)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    val cur = widthInput.toFloatOrNull() ?: 10f
                                    val next = (cur - 1f).coerceAtLeast(3f)
                                    widthInput = if (next % 1f == 0f) next.toInt().toString() else String.format("%.1f", next)
                                },
                                modifier = Modifier.weight(1f).height(28.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("-1m", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            FilledTonalButton(
                                onClick = {
                                    val cur = widthInput.toFloatOrNull() ?: 10f
                                    val next = (cur + 1f).coerceAtMost(45f)
                                    widthInput = if (next % 1f == 0f) next.toInt().toString() else String.format("%.1f", next)
                                },
                                modifier = Modifier.weight(1f).height(28.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+1m", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        }
                    }

                    // Length Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d*$"""))) {
                                    heightInput = input
                                }
                            },
                            label = { Text("Length (m)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    val cur = heightInput.toFloatOrNull() ?: 10f
                                    val next = (cur - 1f).coerceAtLeast(3f)
                                    heightInput = if (next % 1f == 0f) next.toInt().toString() else String.format("%.1f", next)
                                },
                                modifier = Modifier.weight(1f).height(28.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("-1m", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            FilledTonalButton(
                                onClick = {
                                    val cur = heightInput.toFloatOrNull() ?: 10f
                                    val next = (cur + 1f).coerceAtMost(45f)
                                    heightInput = if (next % 1f == 0f) next.toInt().toString() else String.format("%.1f", next)
                                },
                                modifier = Modifier.weight(1f).height(28.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+1m", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }

                // Ruler Display Toggle Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { enableRulers = !enableRulers },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = enableRulers,
                        onCheckedChange = { enableRulers = it },
                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                    )
                    Column {
                        Text(
                            text = "Display Metric Rulers on Canvas",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "Shows meter ticks along canvas edges and boundary tags",
                            fontSize = 10.5.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                // Apply Button
                Button(
                    onClick = {
                        val w = widthInput.toFloatOrNull()?.coerceIn(3f, 45f) ?: currentWidthM
                        val h = heightInput.toFloatOrNull()?.coerceIn(3f, 45f) ?: currentHeightM
                        onApplyDimensions(w, h, enableRulers)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Apply Yard Dimensions", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}
