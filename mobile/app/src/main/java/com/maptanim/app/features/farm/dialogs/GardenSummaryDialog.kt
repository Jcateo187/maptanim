package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.features.farm.components.getCropHeroDrawable
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.schedule.CropScheduleStatus
import com.maptanim.app.features.farm.schedule.resolveScheduleStatus
import java.util.Locale

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)
private val AmberOrange = Color(0xFFE65100)
private val AmberBg = Color(0xFFFFF3E0)
private val LightGreenBg = Color(0xFFE8F5E9)

private data class VegetableSummaryItem(
    val id: String,
    val cropName: String,
    val plantedDate: String?,
    val isGrowing: Boolean,
    val scheduleStatus: CropScheduleStatus
)

/**
 * GardenSummaryDialog — Displays Growing vs Planning/Unset date counts,
 * Size & Area dimensions, and the full Vegetables inventory for the garden.
 */
@Composable
fun GardenSummaryDialog(
    farmName: String,
    plots: List<PlotRenderData>,
    cropZones: List<CropZoneRenderData>,
    selectedPlot: PlotRenderData? = null,
    cropPlantedDates: Map<String, String> = emptyMap(),
    onDismiss: () -> Unit
) {
    val targetPlot = selectedPlot ?: plots.firstOrNull()

    // ── Dimensions & Area ────────────────────────────────────────────────────
    val widthM = targetPlot?.widthM ?: plots.sumOf { it.widthM.toDouble() }.toFloat().coerceAtLeast(1.5f)
    val heightM = targetPlot?.heightM ?: (plots.firstOrNull()?.heightM ?: 1.0f)
    val widthFt = widthM / 0.3048f
    val heightFt = heightM / 0.3048f

    val areaSqm = targetPlot?.let { (it.widthM * it.heightM).toDouble() }
        ?: plots.sumOf { (it.widthM * it.heightM).toDouble() }
    val areaSqFt = areaSqm * 10.7639

    // ── Collect Planted Crops in this Garden ─────────────────────────────────
    val allPlantedItems: List<VegetableSummaryItem> = remember(targetPlot, cropZones, cropPlantedDates) {
        val farmPrefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
        val plotCrops = if (targetPlot != null) {
            cropZones.filter {
                it.plotId == targetPlot.id &&
                !it.cropName.isNullOrBlank() &&
                !it.cropName.equals("Bed", ignoreCase = true) &&
                !it.cropName.equals("Garden", ignoreCase = true)
            }
        } else emptyList()

        if (plotCrops.isNotEmpty()) {
            plotCrops.map { zone ->
                val resolvedDate = zone.plantedDate
                    ?: cropPlantedDates[zone.id]
                    ?: cropPlantedDates["${zone.plotId}_${zone.cropName}"]
                    ?: farmPrefs.resolveCropPlantedDate(zone.id, zone.plotId, zone.cropName)
                val schedStatus = resolveScheduleStatus(
                    plantedDate = resolvedDate,
                    cropName = zone.cropName ?: "Vegetable",
                    isMonitoringStarted = !resolvedDate.isNullOrBlank()
                )
                VegetableSummaryItem(
                    id = zone.id,
                    cropName = zone.cropName ?: "Vegetable",
                    plantedDate = resolvedDate,
                    isGrowing = schedStatus.canCheckUp,
                    scheduleStatus = schedStatus
                )
            }
        } else if (targetPlot != null && !targetPlot.cropName.isNullOrBlank() && !targetPlot.cropName.equals("Bed", ignoreCase = true) && !targetPlot.cropName.equals("Garden", ignoreCase = true)) {
            val resolvedDate = cropPlantedDates[targetPlot.id]
                ?: cropPlantedDates["${targetPlot.id}_${targetPlot.cropName}"]
                ?: farmPrefs.resolveCropPlantedDate(null, targetPlot.id, targetPlot.cropName)
            val schedStatus = resolveScheduleStatus(
                plantedDate = resolvedDate,
                cropName = targetPlot.cropName ?: "Vegetable",
                isMonitoringStarted = !resolvedDate.isNullOrBlank()
            )
            listOf(
                VegetableSummaryItem(
                    id = targetPlot.id,
                    cropName = targetPlot.cropName ?: "Vegetable",
                    plantedDate = resolvedDate,
                    isGrowing = schedStatus.canCheckUp,
                    scheduleStatus = schedStatus
                )
            )
        } else {
            val validZones = cropZones.filter {
                !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) && !it.cropName.equals("Garden", ignoreCase = true)
            }
            validZones.map { zone ->
                val resolvedDate = zone.plantedDate
                    ?: cropPlantedDates[zone.id]
                    ?: cropPlantedDates["${zone.plotId}_${zone.cropName}"]
                    ?: farmPrefs.resolveCropPlantedDate(zone.id, zone.plotId, zone.cropName)
                val schedStatus = resolveScheduleStatus(
                    plantedDate = resolvedDate,
                    cropName = zone.cropName ?: "Vegetable",
                    isMonitoringStarted = !resolvedDate.isNullOrBlank()
                )
                VegetableSummaryItem(
                    id = zone.id,
                    cropName = zone.cropName ?: "Vegetable",
                    plantedDate = resolvedDate,
                    isGrowing = schedStatus.canCheckUp,
                    scheduleStatus = schedStatus
                )
            }
        }
    }

    val growingCount = allPlantedItems.count { it.isGrowing }
    val planningCount = allPlantedItems.count { !it.isGrowing }
    val groupedVegetables = remember(allPlantedItems) {
        allPlantedItems.groupBy { it.cropName }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ConfigureDialogSystemBars(isLightStatusBar = true, isLightNavigationBar = true)

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Header: Title & Close Button ─────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LightGreenBg,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "GARDEN SUMMARY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LushGreen,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = farmName.ifBlank { "My Garden" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBlack
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.DarkGray)
                    }
                }

                HorizontalDivider(color = CardBorderColor)

                // ── Top Metric Cards: Growing & Planning / Unset Date ─────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Growing Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightGreenBg,
                        border = BorderStroke(1.2.dp, LushGreen.copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "GROWING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LushGreen,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Text(
                                text = "$growingCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LushGreen
                            )
                            Text(
                                text = "Active Schedule",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = LushGreen.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // 2. Planning / Unset Date Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberBg,
                        border = BorderStroke(1.2.dp, AmberOrange.copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = AmberOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "PLANNING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberOrange,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Text(
                                text = "$planningCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberOrange
                            )
                            Text(
                                text = "Unset date / Soon",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = AmberOrange.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // ── Size: and Area: Section ──────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Size row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Size:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                            }
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%.1f", widthFt)} ft × ${String.format(Locale.ENGLISH, "%.1f", heightFt)} ft (${String.format(Locale.ENGLISH, "%.2f", widthM)}m × ${String.format(Locale.ENGLISH, "%.2f", heightM)}m)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LushGreen
                            )
                        }

                        HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f))

                        // Area row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SquareFoot,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Area:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                            }
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%.1f", areaSqFt)} sq ft (${String.format(Locale.ENGLISH, "%.2f", areaSqm)} m²)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepBlack
                            )
                        }
                    }
                }

                // ── VEGETABLES Section ───────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VEGETABLES",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LushGreen,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${allPlantedItems.size} Total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                if (groupedVegetables.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Walang nakatanim na gulay sa hardin na ito. Pindutin ang + para magtanim.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        groupedVegetables.forEach { (cName, items) ->
                            val itemGrowingCount = items.count { it.isGrowing }
                            val itemPlanningCount = items.count { !it.isGrowing }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Vegetable Avatar
                                        Image(
                                            painter = painterResource(id = getCropHeroDrawable(cName)),
                                            contentDescription = cName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White)
                                        )

                                        Column {
                                            Text(
                                                text = cName,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepBlack
                                            )
                                            Text(
                                                text = "${items.size} planted",
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Status Pills: Growing / Planning
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (itemGrowingCount > 0) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = LightGreenBg,
                                                border = BorderStroke(0.8.dp, LushGreen.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "$itemGrowingCount Growing",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = LushGreen,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        if (itemPlanningCount > 0) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = AmberBg,
                                                border = BorderStroke(0.8.dp, AmberOrange.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "$itemPlanningCount Planning",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AmberOrange,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ── Close Button ─────────────────────────────────────────────
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text(
                        text = "Isara",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
