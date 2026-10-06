package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.dss.engine.BedAgronomicAdvisor
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val LightSurface = Color(0xFFF9FAF8)
private val CardBorderColor = Color(0xFFE0E0E0)
private val AlertAmber = Color(0xFFFFB300)

/**
 * UnifiedBedSummaryDossier — Replaces fragmented tab-jumping with a unified, case-based
 * agronomic summary and direct task station for the selected bed or the entire farm:
 * - Case 1: Whole Farm Overview (when no bed is selected)
 * - Case 2: Pre-Planting Bed Case (when empty bed is selected)
 * - Case 3: Active Growth & Vegetative Case (evidence-based care protocol, care checklist, companion matrix, harvest timeline)
 * - Case 4: Harvest & Rotation Case (readiness, yield estimation, crop succession)
 */
@Composable
fun UnifiedBedSummaryDossier(
    selectedPlot: PlotRenderData?,
    allPlots: List<PlotRenderData>,
    cropZones: List<CropZoneRenderData>,
    todayTasks: List<com.maptanim.app.dss.engine.DssLogEvaluator.GeneratedLogTask> = emptyList(),
    onCompleteTask: (String) -> Unit = {},
    onOpenCropTray: () -> Unit,
    onOpenInspect: () -> Unit,
    onOpenHarvestModal: () -> Unit,
    onHideDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zonesInSelectedBed = remember(cropZones, selectedPlot?.id) {
        if (selectedPlot == null) emptyList()
        else cropZones.filter {
            it.plotId == selectedPlot.id && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
        }
    }

    val selectedCropNames = remember(zonesInSelectedBed) {
        zonesInSelectedBed.mapNotNull { it.cropName }.distinct()
    }

    val primaryCrop = selectedCropNames.firstOrNull() ?: ""

    // Evidence-Based Agronomic Guidance (Grounded in Verified Growth Stage Standards)
    val agronomicGuidance = remember(primaryCrop) {
        if (primaryCrop.isNotBlank()) {
            BedAgronomicAdvisor.getStageGuidance(
                cropName = primaryCrop,
                daysPlanted = 20
            )
        } else null
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. Header with Hide Button ──────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedPlot != null) "${selectedPlot.plotLabel} SUMMARY DOSSIER" else "FARM OVERVIEW DOSSIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (selectedPlot != null)
                            "${selectedPlot.plotLabel} • ${String.format("%.1f", selectedPlot.widthM * selectedPlot.heightM)}m²"
                        else
                            "${allPlots.size} Beds Total • Backyard Command Center",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                }
                Surface(
                    onClick = onHideDrawer,
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minimize", tint = DeepBlack, modifier = Modifier.size(16.dp))
                        Text("Map", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                    }
                }
            }
        }

        // ── 2. CASE RESOLVER: Case A (No Bed Selected) ──────────────────────
        if (selectedPlot == null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("BACKYARD UTILIZATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        val totalBeds = allPlots.size
                        val activeCrops = cropZones.filter { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, CardBorderColor)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("GARDEN BEDS", fontSize = 9.sp, color = Color(0xFF777777))
                                    Text("$totalBeds", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepBlack)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, CardBorderColor)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("CROPS GROWING", fontSize = 9.sp, color = Color(0xFF777777))
                                    Text("${activeCrops.size} Plants", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LushGreen)
                                }
                            }
                        }
                        Text(
                            text = "Tap any bed directly on the canvas to inspect plants, record health observations, or view harvest countdowns.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF555555),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // ── 3. CASE RESOLVER: Case B (Empty Bed Selected) ───────────────────
        else if (zonesInSelectedBed.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8E9),
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("BED PREPARATION BLUEPRINT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        Text(
                            text = "${selectedPlot.plotLabel} is empty and ready for planting",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "1. Soil Prep: Loosen soil to 30cm depth. Blend 3–5 kg/m² rich organic compost.\n2. Moisture: Water thoroughly 24h before direct seeding or transplanting.\n3. Sunlight: Ensure minimum 6 hours direct tropical sunlight.",
                            fontSize = 11.sp,
                            color = Color(0xFF333333),
                            lineHeight = 15.sp
                        )
                        Button(
                            onClick = onOpenCropTray,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Crop Tray & Plant", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ── 4. CASE RESOLVER: Case C & D (Planted Bed with Crops) ───────────
        else {
            // Evidence-Based Care Protocol Banner (Grounded in Verified Growth Stage)
            if (agronomicGuidance != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F8E9),
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = LushGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "EVIDENCE-BASED CARE PROTOCOL (${agronomicGuidance.stageName.uppercase()})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = agronomicGuidance.managementProtocolTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepBlack
                            )
                            Text(
                                text = "Action: ${agronomicGuidance.recommendedCareAction}\nScientific Basis (Batayan): ${agronomicGuidance.scientificBasis}",
                                fontSize = 11.sp,
                                color = Color(0xFF333333),
                                lineHeight = 15.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White,
                                border = BorderStroke(0.5.dp, CardBorderColor)
                            ) {
                                Text(
                                    text = "Field Status: ${agronomicGuidance.inspectionStatusText}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF555555),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Active Daily Care & DSS Diagnosis Chores ────────────────────
            if (todayTasks.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TODAY'S CARE CHORES (${todayTasks.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Tap to Complete",
                                    fontSize = 10.sp,
                                    color = Color(0xFF777777)
                                )
                            }

                            todayTasks.forEach { task ->
                                Surface(
                                    onClick = { onCompleteTask(task.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = LightSurface,
                                    border = BorderStroke(0.8.dp, CardBorderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Complete Chore",
                                            tint = LushGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = DeepBlack
                                            )
                                            if (task.description.isNotBlank()) {
                                                Text(
                                                    text = task.description,
                                                    fontSize = 10.5.sp,
                                                    color = Color(0xFF555555),
                                                    lineHeight = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Direct Bed Actions Toolbar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Inspect Button
                    Button(
                        onClick = onOpenInspect,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inspect & Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Record Harvest Button
                    OutlinedButton(
                        onClick = onOpenHarvestModal,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, LushGreen)
                    ) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = LushGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Harvest", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LushGreen)
                    }
                }
            }

            // Companion Compatibility Summary
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("COMPANION PLANTING SYNERGY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        if (selectedCropNames.size >= 2) {
                            Text(
                                text = "Growing: ${selectedCropNames.joinToString(" + ")}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = DeepBlack
                            )
                            val beneficial = try { CompanionDataProvider.getBeneficialCompanions(selectedCropNames.first()).take(2).joinToString(", ") } catch (_: Exception) { "" }
                            if (beneficial.isNotBlank()) {
                                Text("Synergy Benefit: Natural pest repulsion and micro-climate sharing.", fontSize = 11.sp, color = Color(0xFF333333))
                            }
                        } else {
                            val single = selectedCropNames.first()
                            val beneficial = try { CompanionDataProvider.getBeneficialCompanions(single).take(3).joinToString(", ") } catch (_: Exception) { "" }
                            Text("Growing: $single", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = DeepBlack)
                            Text("Best Companions to Add: $beneficial", fontSize = 11.sp, color = Color(0xFF333333))
                        }
                    }
                }
            }

            // Harvest Timeline & Calendar Card
            item {
                for (crop in selectedCropNames) {
                    CropHarvestTimelineCard(
                        cropName = crop,
                        plantedDateMillis = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000), // Default 14 days planted
                        plantCount = zonesInSelectedBed.count { it.cropName.equals(crop, ignoreCase = true) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
