package com.maptanim.app.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.HarvestReadiness
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * HarvestDialog — Two-step interactive crop harvest flow:
 *
 * STEP 1: Harvest Readiness Check (Not Ready / Partially Ready / Ready to Harvest)
 * STEP 2: Harvest Recording Form (Method, Quantity, Unit, Quality, Marketable %, Notes)
 */
@Composable
fun HarvestDialog(
    plotId: String,
    farmId: String = "farm-1",
    farmName: String = "MapTanim Main Farm",
    plotLabel: String,
    cropName: String,
    cropVariety: String? = null,
    cropPlantingId: String? = null,
    plantedDate: String? = null,
    onDismiss: () -> Unit,
    onSubmitHarvest: (HarvestRecord) -> Unit
) {
    var readiness by remember { mutableStateOf(HarvestReadiness.READY_TO_HARVEST) }

    // Form inputs
    val harvestMethods = listOf(
        "Hand Picking (Gentle Twist)",
        "Pruning Shears / Scissors",
        "Cutting at Base",
        "Gentle Pulling / Digging"
    )
    var selectedMethod by remember { mutableStateOf(harvestMethods.first()) }
    var quantityText by remember { mutableStateOf("") }

    val units = listOf("kg", "grams", "pieces", "bundles", "sacks")
    var selectedUnit by remember { mutableStateOf("kg") }

    var qualityRating by remember { mutableIntStateOf(5) }
    var marketablePct by remember { mutableFloatStateOf(95f) }
    var notes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val isMultiPickCrop = remember(cropName) {
        val lower = cropName.lowercase()
        lower.contains("tomato") || lower.contains("eggplant") || lower.contains("okra") ||
        lower.contains("chili") || lower.contains("pepper") || lower.contains("cucumber") ||
        lower.contains("sitaw") || lower.contains("bean") || lower.contains("kangkong") || lower.contains("ampalaya")
    }
    var isFinalHarvest by remember { mutableStateOf(!isMultiPickCrop) }

    val daysSincePlanting = remember(plantedDate) {
        if (!plantedDate.isNullOrBlank()) {
            try {
                ChronoUnit.DAYS.between(LocalDate.parse(plantedDate.take(10)), LocalDate.now()).toInt()
            } catch (_: Exception) {
                0
            }
        } else 0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF10160F)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── HEADER ──────────────────────────────────────────────────
                Surface(
                    color = Color(0xFF161E14),
                    border = BorderStroke(1.dp, Color(0xFF2B3825)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Harvest Assessment",
                                    color = White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFA000).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFFFFA000).copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = plotLabel,
                                        color = Color(0xFFFFCC80),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "$cropName ${cropVariety?.let { "• $it" } ?: ""} ${if (daysSincePlanting > 0) "• Day $daysSincePlanting" else ""}",
                                color = Color(0xFF8B9B85),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

                // ── BODY ────────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── 1. READINESS ASSESSMENT ─────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "HARVEST READINESS CHECK",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Is this crop ready for harvesting?",
                            color = White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            HarvestReadiness.entries.forEach { option ->
                                val isSelected = option == readiness
                                val tintColor = when (option) {
                                    HarvestReadiness.NOT_READY -> Color(0xFFE57373)
                                    HarvestReadiness.PARTIALLY_READY -> Color(0xFFFFB74D)
                                    HarvestReadiness.READY_TO_HARVEST -> Color(0xFF81C784)
                                }
                                Surface(
                                    onClick = { readiness = option },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) tintColor.copy(alpha = 0.2f) else Color(0xFF161E14),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isSelected) tintColor else Color(0xFF2B3825)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option.label,
                                            color = if (isSelected) tintColor else Color(0xFF8B9B85),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // If NOT READY — show advisory banner
                    if (readiness == HarvestReadiness.NOT_READY) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261919),
                            border = BorderStroke(1.dp, Color(0xFF5E2A2A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Crop is Not Ready Yet",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Allow fruits/vegetables to reach physiological maturity, proper color, and target size before harvesting. Continue regular watering and pest monitoring.",
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    } else {
                        // ── 2. HARVEST RECORDING FORM (If Partially Ready or Ready) ──
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF2B3825))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = if (readiness == HarvestReadiness.PARTIALLY_READY)
                                        "PARTIAL HARVEST DETAILS"
                                    else
                                        "HARVEST YIELD DETAILS",
                                    color = Color(0xFF81C784),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )

                                // Harvest Method
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Harvest Method", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        harvestMethods.forEach { method ->
                                            val isSelected = method == selectedMethod
                                            Surface(
                                                onClick = { selectedMethod = method },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) Color(0xFF1E3321) else Color(0xFF10160F),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF4CAF50) else Color(0xFF243221)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    RadioButton(
                                                        selected = isSelected,
                                                        onClick = { selectedMethod = method },
                                                        colors = RadioButtonDefaults.colors(
                                                            selectedColor = Color(0xFF4CAF50),
                                                            unselectedColor = Color(0xFF556850)
                                                        ),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = method,
                                                        color = if (isSelected) White else Color(0xFFC0D0BA),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Quantity & Unit Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Quantity Harvested", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                        OutlinedTextField(
                                            value = quantityText,
                                            onValueChange = { quantityText = it },
                                            placeholder = { Text("e.g. 5.5", fontSize = 12.sp, color = Color(0xFF556850)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF10160F),
                                                unfocusedContainerColor = Color(0xFF10160F),
                                                focusedBorderColor = Color(0xFF4CAF50),
                                                unfocusedBorderColor = Color(0xFF2B3825),
                                                focusedTextColor = White,
                                                unfocusedTextColor = White
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Unit", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                        var unitExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            OutlinedButton(
                                                onClick = { unitExpanded = true },
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFF2B3825)),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = Color(0xFF10160F)
                                                ),
                                                modifier = Modifier.fillMaxWidth().height(54.dp)
                                            ) {
                                                Text(selectedUnit, color = White, fontSize = 12.sp)
                                            }
                                            DropdownMenu(
                                                expanded = unitExpanded,
                                                onDismissRequest = { unitExpanded = false },
                                                modifier = Modifier.background(Color(0xFF161E14))
                                            ) {
                                                units.forEach { u ->
                                                    DropdownMenuItem(
                                                        text = { Text(u, color = White, fontSize = 12.sp) },
                                                        onClick = {
                                                            selectedUnit = u
                                                            unitExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Quality Rating (1–5 Stars)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Quality Rating", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (star in 1..5) {
                                            IconButton(
                                                onClick = { qualityRating = star },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (star <= qualityRating) Icons.Filled.Star else Icons.Outlined.Star,
                                                    contentDescription = "$star stars",
                                                    tint = if (star <= qualityRating) Color(0xFFFFB74D) else Color(0xFF556850),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = when (qualityRating) {
                                                5 -> "Excellent"
                                                4 -> "Good"
                                                3 -> "Average"
                                                2 -> "Below Average"
                                                else -> "Poor"
                                            },
                                            color = Color(0xFFFFCC80),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Marketable % Slider
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Marketable / Grade A Quality", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                        Text("${marketablePct.toInt()}%", fontSize = 12.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                                    }
                                    Slider(
                                        value = marketablePct,
                                        onValueChange = { marketablePct = it },
                                        valueRange = 0f..100f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFF4CAF50),
                                            activeTrackColor = Color(0xFF2E7D32),
                                            inactiveTrackColor = Color(0xFF243221)
                                        )
                                    )
                                }

                                // Harvest Type Selector: Ongoing / Partial vs Final Harvest
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Harvest Type & Cycle Action", fontSize = 12.sp, color = Color(0xFFB0C8AA), fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            onClick = { isFinalHarvest = false },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (!isFinalHarvest) Color(0xFF1E3A24) else Color(0xFF141C12),
                                            border = BorderStroke(1.dp, if (!isFinalHarvest) Color(0xFF4CAF50) else Color(0xFF2B3825)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "🌱 Partial / Ongoing",
                                                    color = if (!isFinalHarvest) Color(0xFF81C784) else White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Keep plant active for subsequent pickings",
                                                    color = Color(0xFFA0B09A),
                                                    fontSize = 10.sp,
                                                    lineHeight = 13.sp
                                                )
                                            }
                                        }

                                        Surface(
                                            onClick = { isFinalHarvest = true },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isFinalHarvest) Color(0xFF3E2714) else Color(0xFF141C12),
                                            border = BorderStroke(1.dp, if (isFinalHarvest) Color(0xFFFFA000) else Color(0xFF2B3825)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "🌾 Final Harvest",
                                                    color = if (isFinalHarvest) Color(0xFFFFB74D) else White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Clear bed and proceed to crop rotation",
                                                    color = Color(0xFFA0B09A),
                                                    fontSize = 10.sp,
                                                    lineHeight = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Harvest Notes
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Notes (Optional)", fontSize = 12.sp, color = Color(0xFFB0C8AA))
                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        placeholder = {
                                            Text(
                                                "e.g. Clean harvest, firm fruits, no sign of rot...",
                                                fontSize = 12.sp,
                                                color = Color(0xFF556850)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF10160F),
                                            unfocusedContainerColor = Color(0xFF10160F),
                                            focusedBorderColor = Color(0xFF4CAF50),
                                            unfocusedBorderColor = Color(0xFF2B3825),
                                            focusedTextColor = White,
                                            unfocusedTextColor = White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        minLines = 2,
                                        maxLines = 4,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    if (validationError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF331614),
                            border = BorderStroke(1.dp, Color(0xFF8B2C24)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = validationError ?: "",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // ── BOTTOM ACTION BAR ───────────────────────────────────────
                HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

                Surface(
                    color = Color(0xFF161E14),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF3B4D35))
                        ) {
                            Text(
                                "Cancel",
                                color = Color(0xFFB0C8AA),
                                fontSize = 13.sp
                            )
                        }

                        if (readiness != HarvestReadiness.NOT_READY) {
                            Button(
                                onClick = {
                                    val qty = quantityText.trim().toFloatOrNull()
                                    if (qty == null || qty <= 0f) {
                                        validationError = "Please enter a valid harvest quantity."
                                        return@Button
                                    }
                                    validationError = null

                                    val yieldInKg = when (selectedUnit) {
                                        "kg" -> qty
                                        "grams" -> qty / 1000f
                                        else -> qty // default numeric representation
                                    }

                                    val record = HarvestRecord(
                                        id = "harvest-${UUID.randomUUID().toString().take(8)}",
                                        plotId = plotId,
                                        farmId = farmId,
                                        farmName = farmName,
                                        plotLabel = plotLabel,
                                        cropName = cropName,
                                        cropVariety = cropVariety,
                                        plantedDate = plantedDate,
                                        harvestedAt = Instant.now().toString(),
                                        growingDurationDays = daysSincePlanting,
                                        yieldKg = yieldInKg,
                                        qualityRating = qualityRating,
                                        notes = notes.trim().ifBlank { null },
                                        harvestMethod = selectedMethod,
                                        quantity = qty,
                                        unit = selectedUnit,
                                        marketablePct = marketablePct,
                                        cropPlantingId = cropPlantingId,
                                        isFinalHarvest = isFinalHarvest
                                    )

                                    onSubmitHarvest(record)
                                },
                                modifier = Modifier.weight(2f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFinalHarvest) Color(0xFFFFA000) else Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isFinalHarvest) "Record Final Harvest 🌾" else "Record Picking (Keep Plant) 🌱",
                                    color = if (isFinalHarvest) Color.Black else White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
