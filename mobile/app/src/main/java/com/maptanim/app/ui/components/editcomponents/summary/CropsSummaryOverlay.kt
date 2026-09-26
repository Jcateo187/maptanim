package com.maptanim.app.ui.components.editcomponents.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.ui.screens.edit.CropPlantingDraft
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Categorized variety data structure.
 */
data class CategorizedVarietyGroup(
    val categoryName: String,
    val iconEmoji: String,
    val varieties: List<String>
)

/**
 * CropsSummaryOverlay — Full in-composition overlay for reviewing planting dates and varieties.
 *
 * Features:
 * - 100% full-screen translucent background with no window cuts or padding discrepancies.
 * - Interactive Monthly Calendar selector (identical to Monitoring reschedule flow).
 * - Categorized Dropdown/Picker for Crop Varieties (Commercial F1, Traditional OP, 10s Simulation).
 * - Direct Save button to persist layout, planting schedules, and chosen varieties.
 */
@Composable
fun CropsSummaryOverlay(
    farmName: String,
    cropPlantings: List<CropPlantingDraft>,
    errorMessage: String? = null,
    isSaving: Boolean = false,
    onCancel: () -> Unit,
    onSave: (List<CropPlantingDraft>) -> Unit
) {
    var drafts by remember(cropPlantings) {
        mutableStateOf(cropPlantings)
    }

    var localValidationError by remember { mutableStateOf<String?>(null) }
    var calendarTargetDraftId by remember { mutableStateOf<String?>(null) }
    var varietyTargetDraftId by remember { mutableStateOf<String?>(null) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d, yyyy") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040A06).copy(alpha = 0.50f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* block outside clicks */ }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 360.dp, max = 540.dp)
                .fillMaxWidth(0.70f)
                .fillMaxHeight(0.96f)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0C1E13).copy(alpha = 0.96f),
            border = BorderStroke(1.2.dp, Color(0xFF2E7D32).copy(alpha = 0.75f)),
            shadowElevation = 18.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF1B5E20))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.White.copy(alpha = 0.20f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Crops Planting Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "$farmName • ${drafts.size} New / Changed Crops",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC8E6C9)
                                )
                            }
                        }

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Error or Validation Banner
                val activeError = localValidationError ?: errorMessage
                if (!activeError.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFB71C1C).copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(activeError, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // List of Crop Plantings
                if (drafts.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Yard, null, tint = Color(0xFF4CAF50).copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                            Text("Walang bago o binagong pananim.", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(drafts, key = { it.id }) { draft ->
                            val cropName = draft.cropName
                            val cropId = draft.cropId
                            val imageUri = remember(cropId, cropName) {
                                CropMetadataAssetDataSource.resolveCropImage(cropId, cropName)
                            }
                            val currentDateStr = draft.plantingDate
                            val currentLocalDate = remember(currentDateStr) {
                                try { LocalDate.parse(currentDateStr) } catch (_: Exception) { LocalDate.now() }
                            }
                            val isSim = draft.variety.contains("10s", ignoreCase = true)
                            val daysToHarvest = remember(cropName, isSim) {
                                if (isSim) 1 else getDaysToHarvestEstimate(cropName)
                            }
                            val estimatedHarvestDate = remember(currentLocalDate, daysToHarvest) {
                                currentLocalDate.plusDays(daysToHarvest.toLong())
                            }

                            CropSummaryCard(
                                draft = draft,
                                cropName = cropName,
                                variety = draft.variety,
                                bedLabel = draft.bedLabel,
                                plantCount = draft.plantCount,
                                notes = draft.notes,
                                imageUri = imageUri,
                                plantedDate = currentLocalDate,
                                estimatedHarvestDate = estimatedHarvestDate,
                                daysToHarvest = daysToHarvest,
                                dateFormatter = dateFormatter,
                                onOpenCalendar = { calendarTargetDraftId = draft.id },
                                onOpenVarietyDropdown = { varietyTargetDraftId = draft.id },
                                onPlantCountChanged = { newCount ->
                                    drafts = drafts.map { if (it.id == draft.id) it.copy(plantCount = newCount) else it }
                                },
                                onNotesChanged = { newNotes ->
                                    drafts = drafts.map { if (it.id == draft.id) it.copy(notes = newNotes) else it }
                                }
                            )
                        }
                    }
                }

                // Fixed Bottom Actions
                Surface(
                    color = Color(0xFF07140B),
                    border = BorderStroke(1.dp, Color(0xFF1E3A24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val missing = mutableListOf<String>()
                                drafts.forEach { d ->
                                    if (d.cropName.isBlank()) missing.add("Crop")
                                    if (d.variety.isBlank()) missing.add("Variety (${d.cropName})")
                                    if (d.plantingDate.isBlank()) {
                                        missing.add("Petsa (${d.cropName})")
                                    } else {
                                        val valid = try { LocalDate.parse(d.plantingDate.take(10)) != null } catch (_: Exception) { false }
                                        if (!valid) missing.add("Wastong Petsa (${d.cropName})")
                                    }
                                    if (d.plantCount <= 0) missing.add("Bilang (${d.cropName})")
                                    if (d.bedId.isBlank()) missing.add("Bed ID (${d.cropName})")
                                }
                                if (missing.isNotEmpty()) {
                                    localValidationError = "⚠️ Pakiusap punan ang lahat ng kinakailangang impormasyon: ${missing.distinct().joinToString(", ")}"
                                } else {
                                    localValidationError = null
                                    onSave(drafts)
                                }
                            },
                            enabled = !isSaving,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1.3f).height(38.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Saving...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            } else {
                                Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Save Plantings", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    calendarTargetDraftId?.let { targetId ->
        val target = drafts.firstOrNull { it.id == targetId }
        if (target != null) {
            val initialDateStr = target.plantingDate
            val isSim = target.variety.contains("10s", ignoreCase = true)
            val daysToHarvest = if (isSim) 1 else getDaysToHarvestEstimate(target.cropName)
            PlantingCalendarModal(
                cropName = target.cropName,
                plotLabel = target.bedLabel,
                initialDateStr = initialDateStr,
                daysToHarvest = daysToHarvest,
                onDismiss = { calendarTargetDraftId = null },
                onDateSelected = { newDate ->
                    val isValid = try {
                        val parsed = LocalDate.parse(newDate.take(10))
                        !parsed.isBefore(LocalDate.now().minusYears(1)) && !parsed.isAfter(LocalDate.now().plusYears(1))
                    } catch (_: Exception) {
                        false
                    }
                    if (isValid) {
                        drafts = drafts.map { if (it.id == targetId) it.copy(plantingDate = newDate) else it }
                        localValidationError = null
                        calendarTargetDraftId = null
                    } else {
                        localValidationError = "⚠️ Hindi wasto ang napiling petsa ng pagtatanim para sa ${target.cropName}. Pakiusap pumili ng wastong petsa."
                    }
                }
            )
        }
    }

    varietyTargetDraftId?.let { targetId ->
        val target = drafts.firstOrNull { it.id == targetId }
        if (target != null) {
            val categorizedGroups = remember(target.cropName) {
                getCategorizedVarietiesForCrop(target.cropName)
            }
            CropVarietyPickerModal(
                cropName = target.cropName,
                plotLabel = target.bedLabel,
                currentVariety = target.variety,
                categorizedGroups = categorizedGroups,
                onDismiss = { varietyTargetDraftId = null },
                onVarietySelected = { newVariety ->
                    drafts = drafts.map { if (it.id == targetId) it.copy(variety = newVariety, varietyId = newVariety.lowercase().replace(" ", "_")) else it }
                    varietyTargetDraftId = null
                }
            )
        }
    }
}

/**
 * Legacy overload for CropsSummaryOverlay for backwards compatibility.
 */
@Composable
fun CropsSummaryOverlay(
    farmName: String,
    plots: List<PlotRenderData>,
    onCancel: () -> Unit,
    onSave: (updatedPlantedDates: Map<String, String>, updatedVarieties: Map<String, String>) -> Unit
) {
    val drafts = remember(plots) {
        plots.filter { !it.cropName.isNullOrBlank() }.map { plot ->
            val cropName = plot.cropName ?: "Vegetable"
            CropPlantingDraft(
                id = plot.id,
                cropName = cropName,
                cropId = plot.cropId ?: cropName.lowercase(),
                bedId = plot.id,
                bedLabel = plot.plotLabel,
                variety = plot.cropVariety?.ifBlank { null } ?: getDefaultVariety(cropName),
                varietyId = (plot.cropVariety?.ifBlank { null } ?: getDefaultVariety(cropName)).lowercase().replace(" ", "_"),
                plantingDate = plot.plantedDate?.take(10)?.ifBlank { null } ?: LocalDate.now().toString(),
                plantCount = (plot.widthM * plot.heightM).toInt().coerceAtLeast(1),
                notes = "",
                isNew = false,
                isChanged = true
            )
        }
    }
    CropsSummaryOverlay(
        farmName = farmName,
        cropPlantings = drafts,
        onCancel = onCancel,
        onSave = { updatedDrafts ->
            val dates = updatedDrafts.associate { it.id to it.plantingDate }
            val varieties = updatedDrafts.associate { it.id to it.variety }
            onSave(dates, varieties)
        }
    )
}

/**
 * Compact Crop Plot Card inside CropsSummaryOverlay.
 */
@Composable
private fun CropSummaryCard(
    draft: CropPlantingDraft,
    cropName: String,
    variety: String,
    bedLabel: String,
    plantCount: Int,
    notes: String,
    imageUri: String,
    plantedDate: LocalDate,
    estimatedHarvestDate: LocalDate,
    daysToHarvest: Int,
    dateFormatter: DateTimeFormatter,
    onOpenCalendar: () -> Unit,
    onOpenVarietyDropdown: () -> Unit,
    onPlantCountChanged: (Int) -> Unit,
    onNotesChanged: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF132A1C),
        border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // ── Bed Number Header ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1B3D23),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(13.dp))
                        Text(
                            text = "Bed Number: $bedLabel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8F5E9)
                        )
                    }
                }

                Surface(
                    color = if (draft.isNew) Color(0xFF1B5E20) else Color(0xFF004D40),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (draft.isNew) "✨ BAGO" else if (draft.isChanged) "✏️ BINAGO" else "🌱 NAKATANIM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5D6A7),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Left Side: Crop Image ──────────────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(64.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.2.dp, Color(0xFF43A047), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = cropName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(42.dp).padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // ── Right Side: Crop Info & Form Fields ──────────────────
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cropName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Variety Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Variety:",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.20f),
                            border = BorderStroke(0.8.dp, Color(0xFFFFB300).copy(alpha = 0.7f)),
                            modifier = Modifier.clickable { onOpenVarietyDropdown() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = variety.ifBlank { "Pumili ng Variety" },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFE082)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Variety",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Planting Date Picker
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF09140E),
                        border = BorderStroke(0.8.dp, Color(0xFF2E7D32).copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCalendar() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(13.dp)
                                )

                                Column {
                                    Text(
                                        text = "Plant Date",
                                        fontSize = 9.sp,
                                        color = Color(0xFF81C784),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = plantedDate.format(dateFormatter),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Baguhin",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF81C784)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Pick Date",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Plant Count Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bilang ng Halaman (Count):",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (plantCount > 1) onPlantCountChanged(plantCount - 1) },
                                modifier = Modifier.size(24.dp).background(Color(0xFF1E3A24), CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, "-", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            Text(
                                text = "$plantCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.widthIn(min = 24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            IconButton(
                                onClick = { onPlantCountChanged(plantCount + 1) },
                                modifier = Modifier.size(24.dp).background(Color(0xFF2E7D32), CircleShape)
                            ) {
                                Icon(Icons.Default.Add, "+", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Notes Field
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChanged,
                        placeholder = { Text("Tala / Notes ukol sa pananim...", fontSize = 10.sp, color = Color.Gray) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color.White),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.4f),
                            focusedContainerColor = Color(0xFF09140E),
                            unfocusedContainerColor = Color(0xFF09140E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Harvest Estimate Window
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🌾 Inaasahang Ani:",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.55f)
                        )
                        Text(
                            text = "${estimatedHarvestDate.format(dateFormatter)} ($daysToHarvest d)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Monthly Calendar Modal (Matching the Monitoring Reschedule Flow).
 */
@Composable
private fun PlantingCalendarModal(
    cropName: String,
    plotLabel: String,
    initialDateStr: String,
    daysToHarvest: Int,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val initialDate = remember(initialDateStr) {
        try {
            LocalDate.parse(initialDateStr.take(10))
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    val today = LocalDate.now()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(min = 320.dp, max = 390.dp)
                    .fillMaxWidth(0.82f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF102014).copy(alpha = 0.90f),
                border = BorderStroke(1.2.dp, Color(0xFF2E7D32).copy(alpha = 0.85f)),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Schedule Planting Date",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "$cropName • Plot $plotLabel",
                                fontSize = 11.sp,
                                color = Color(0xFF81C784)
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Month Navigation Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "${currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Days of Week Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                            Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
                        }
                    }

                    // Calendar Days Grid
                    val firstDayOfMonth = currentYearMonth.atDay(1)
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                    val daysInMonth = currentYearMonth.lengthOfMonth()
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..6) {
                                    val dayNum = r * 7 + c - firstDayOfWeek + 1
                                    if (dayNum in 1..daysInMonth) {
                                        val dateObj = currentYearMonth.atDay(dayNum)
                                        val isSelected = dateObj == selectedDate
                                        val isToday = dateObj == today
                                        val harvestDate = selectedDate.plusDays(daysToHarvest.toLong())
                                        val isHarvestDay = dateObj == harvestDate

                                        val bgColor = when {
                                            isSelected -> Color(0xFF2E7D32)
                                            isHarvestDay -> Color(0xFFD48806)
                                            isToday -> Color(0xFF2A3424)
                                            else -> Color.Transparent
                                        }

                                        val textColor = when {
                                            isSelected || isHarvestDay -> Color.White
                                            isToday -> Color(0xFF81C784)
                                            else -> Color.White.copy(alpha = 0.85f)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(bgColor)
                                                .border(
                                                    width = if (isToday && !isSelected) 1.dp else 0.dp,
                                                    color = if (isToday) Color(0xFF81C784) else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    selectedDate = dateObj
                                                    onDateSelected(dateObj.toString())
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = textColor
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(28.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Quick Shortcut Chips (Exact Labels: Today, Tomorrow, 3 Days, 7 Days)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "Today" to today,
                            "Tomorrow" to today.plusDays(1),
                            "3 Days" to today.plusDays(3),
                            "7 Days" to today.plusDays(7)
                        ).forEach { (label, dVal) ->
                            val isSel = selectedDate == dVal
                            Surface(
                                onClick = {
                                    selectedDate = dVal
                                    currentYearMonth = YearMonth.from(dVal)
                                    onDateSelected(dVal.toString())
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF2E7D32) else Color(0xFF203625),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 5.dp), contentAlignment = Alignment.Center) {
                                    Text(label, fontSize = 9.sp, color = Color.White, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    // Footer Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        ) {
                            Text("Cancel", fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = { onDateSelected(selectedDate.toString()) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        ) {
                            Text("Confirm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Categorized Crop Variety Picker Modal Overlay.
 */
@Composable
private fun CropVarietyPickerModal(
    cropName: String,
    plotLabel: String,
    currentVariety: String,
    categorizedGroups: List<CategorizedVarietyGroup>,
    onDismiss: () -> Unit,
    onVarietySelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(min = 320.dp, max = 420.dp)
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF102014).copy(alpha = 0.92f),
                border = BorderStroke(1.2.dp, Color(0xFF2E7D32).copy(alpha = 0.85f)),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Select Crop Variety",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "$cropName • Plot $plotLabel",
                                fontSize = 11.sp,
                                color = Color(0xFF81C784)
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Grouped Variety List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categorizedGroups) { group ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(group.iconEmoji, fontSize = 12.sp)
                                    Text(
                                        text = group.categoryName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }

                                group.varieties.forEach { varietyName ->
                                    val isSelected = varietyName == currentVariety
                                    Surface(
                                        onClick = {
                                            onVarietySelected(varietyName)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF1E3323),
                                        border = BorderStroke(
                                            0.8.dp,
                                            if (isSelected) Color(0xFF81C784) else Color(0xFF2E7D32).copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = varietyName,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = Color.White
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text("Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Returns categorized variety groupings for any crop.
 */
internal fun getCategorizedVarietiesForCrop(cropName: String): List<CategorizedVarietyGroup> {
    val clean = cropName.lowercase()
    return when {
        clean.contains("ampalaya") || clean.contains("bitter") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Galaxy Max F1", "Jade Star XL F1", "Trident F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Bonito F1", "Sta. Rita", "Pinakbet Special")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Ampalaya 10s Simulation Test ⚡"))
        )
        clean.contains("tomato") || clean.contains("kamatis") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Diamante Max F1", "Avatar F1", "Marimar F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Rosanna", "Apollo", "Kamatis Tagalog")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Tomato 10s Simulation Test ⚡"))
        )
        clean.contains("eggplant") || clean.contains("talong") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Fortuner F1", "Morena F1", "Banate King F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Dumaguete Long Purple", "Dingras Multiple Purple")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Eggplant 10s Simulation Test ⚡"))
        )
        clean.contains("carrot") || clean.contains("karot") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Terracotta F1", "Chantenay Supreme")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Kuroda Improved", "Early Nantes")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Carrot 10s Simulation Test ⚡"))
        )
        clean.contains("cabbage") || clean.contains("repolyo") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Rare Ball F1", "K-S Cross F1", "Kyross F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Scorpio", "Golden Acre")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Cabbage 10s Simulation Test ⚡"))
        )
        clean.contains("pechay") || clean.contains("bokchoy") -> listOf(
            CategorizedVarietyGroup("Commercial & Popular", "🌟", listOf("Black Behi", "Pavon", "Ching-Chiang")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Baby Bokchoy Local", "Native Pechay")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Pechay 10s Simulation Test ⚡"))
        )
        clean.contains("onion") || clean.contains("sibuyas") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Red Pinoy F1", "Yellow Granex F1", "Superpex F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Batanes Red", "Tanduyong Red Shallot")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Onion 10s Simulation Test ⚡"))
        )
        clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Suprema F1", "Horizon F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Rizalina", "Native Tagalog Kalabasa")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Squash 10s Simulation Test ⚡"))
        )
        clean.contains("corn") || clean.contains("mais") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Macho Sweet F1", "Machismo F1", "Sweet Pearl F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("IPB Var 6 (White)", "Lagkitan Glutinous")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Corn 10s Simulation Test ⚡"))
        )
        clean.contains("okra") -> listOf(
            CategorizedVarietyGroup("Commercial & Popular", "🌟", listOf("Smooth Green F1", "Kamiling Green")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Native Deep Green", "Clemson Spineless")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Okra 10s Simulation Test ⚡"))
        )
        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Django F1 (Siling Haba)", "Hot Pepper F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Siling Labuyo Native", "Taiwan Hot")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Chili 10s Simulation Test ⚡"))
        )
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Sandigan F1", "Galante F1", "Negros Dark Green")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("UPLB Green", "Bongabon Striped")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("Sitaw 10s Simulation Test ⚡"))
        )
        else -> listOf(
            CategorizedVarietyGroup("Standard Cultivar", "🌟", listOf("East-West Standard F1", "Local Standard Cultivar")),
            CategorizedVarietyGroup("Simulation Fast Track", "⚡", listOf("10s Fast Simulation Test ⚡"))
        )
    }
}

/**
 * Returns standard Philippine agricultural variety recommendation.
 */
private fun getDefaultVariety(cropName: String): String {
    val clean = cropName.lowercase()
    return when {
        clean.contains("ampalaya") || clean.contains("bitter") -> "Galaxy Max F1"
        clean.contains("tomato") || clean.contains("kamatis") -> "Diamante Max F1"
        clean.contains("eggplant") || clean.contains("talong") -> "Fortuner F1"
        clean.contains("carrot") || clean.contains("karot") -> "Terracotta F1"
        clean.contains("cabbage") || clean.contains("repolyo") -> "Rare Ball F1"
        clean.contains("pechay") || clean.contains("bokchoy") -> "Black Behi"
        clean.contains("onion") || clean.contains("sibuyas") -> "Red Pinoy F1"
        clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> "Suprema F1"
        clean.contains("corn") || clean.contains("mais") -> "Macho Sweet F1"
        clean.contains("okra") -> "Smooth Green F1"
        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> "Django F1"
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> "Sandigan F1"
        clean.contains("lettuce") -> "General F1"
        clean.contains("kangkong") -> "Upland Green"
        else -> "East-West Standard F1"
    }
}

/**
 * Returns estimated days to maturity / harvest for the crop.
 */
internal fun getDaysToHarvestEstimate(cropName: String): Int {
    val clean = cropName.lowercase()
    return when {
        clean.contains("pechay") || clean.contains("kangkong") -> 30
        clean.contains("lettuce") -> 45
        clean.contains("okra") -> 50
        clean.contains("sitaw") || clean.contains("stringbean") -> 55
        clean.contains("ampalaya") || clean.contains("bitter") -> 60
        clean.contains("tomato") || clean.contains("kamatis") -> 65
        clean.contains("eggplant") || clean.contains("talong") -> 70
        clean.contains("corn") || clean.contains("mais") -> 75
        clean.contains("carrot") || clean.contains("karot") -> 85
        clean.contains("cabbage") || clean.contains("repolyo") -> 85
        clean.contains("pumpkin") || clean.contains("squash") -> 90
        clean.contains("onion") || clean.contains("sibuyas") -> 100
        clean.contains("sili") || clean.contains("chili") -> 75
        else -> 60
    }
}
