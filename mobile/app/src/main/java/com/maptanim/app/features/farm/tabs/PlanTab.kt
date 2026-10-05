package com.maptanim.app.features.farm.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.features.farm.canvas.BasketballCourtScaleCard
import com.maptanim.app.features.farm.canvas.CanvasLayer
import com.maptanim.app.features.farm.viewmodel.state.FarmHubPlanState

import com.maptanim.app.features.farm.components.BedEditorCard
import com.maptanim.app.features.farm.components.CropPlaceSuitabilityCard

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * PlanTab — Spatial layout planning, bed dimensions, zoning, and basketball court scale reference.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@Composable
fun PlanTab(
    state: FarmHubPlanState,
    onSelectPlot: (String?) -> Unit,
    onAddNewBed: () -> Unit,
    onDeleteBed: (String) -> Unit,
    onResizeBed: (String, Float, Float) -> Unit = { _, _, _ -> },
    onAssignCrop: (String, String, String) -> Unit = { _, _, _ -> },
    onSetCanvasLayer: (CanvasLayer) -> Unit,
    onSetTool: (EditTool) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenSetupDialog: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val activePlot = state.rawPlots.firstOrNull { it.id == state.selectedPlotId }
            ?: state.rawPlots.firstOrNull()

        // ── 1. PRIMARY ACTION: Bed Editor & Crop Assignment ─────────────────
        if (activePlot != null) {
            item {
                BedEditorCard(
                    plot = activePlot,
                    environment = state.farmEnvironment,
                    onResize = { w, h -> onResizeBed(activePlot.id, w, h) },
                    onAssignCrop = { id, name -> onAssignCrop(activePlot.id, id, name) },
                    onNavigateToGuide = onNavigateToGuide
                )
            }
        }

        // ── 2. Place Suitability & Climate Alignment Card ────────────────────
        item {
            CropPlaceSuitabilityCard(
                cropName = activePlot?.cropName,
                environment = state.farmEnvironment,
                onOpenSetupDialog = onOpenSetupDialog
            )
        }

        // ── 3. Basketball Court Physical Scale Benchmark ─────────────────────
        item {
            BasketballCourtScaleCard(
                widthM = activePlot?.widthM ?: 2.0f,
                heightM = activePlot?.heightM ?: 4.0f,
                cropName = activePlot?.cropName ?: "Vegetable Bed",
                plotLabel = activePlot?.plotLabel ?: "Bed #1"
            )
        }

        // ── 2. Canvas Edit History (Undo/Redo) ──────────────────────────────
        if (state.canUndo || state.canRedo) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EDIT HISTORY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = onUndo, enabled = state.canUndo, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Undo",
                                    tint = if (state.canUndo) LushGreen else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = onRedo, enabled = state.canRedo, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Redo,
                                    contentDescription = "Redo",
                                    tint = if (state.canRedo) LushGreen else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── 4. Beds List & Selection ─────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FARM BEDS (${state.rawPlots.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF555555)
                )
                TextButton(
                    onClick = onAddNewBed,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bed",
                        tint = LushGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Bed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                }
            }
        }

        if (state.rawPlots.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Yard,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No Garden Beds Planned Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "Create your first garden bed to select crops, check soil suitability, and generate your daily care guide.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Button(
                            onClick = onAddNewBed,
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Create Bed & Pick Crop", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else {
            items(state.rawPlots) { plot ->
                BedCard(
                    plot = plot,
                    isSelected = state.selectedPlotId == plot.id,
                    onSelect = { onSelectPlot(plot.id) },
                    onDelete = { onDeleteBed(plot.id) }
                )
            }
            item {
                Button(
                    onClick = onNavigateToGuide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Proceed to Step 3: Daily Production Guide →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun BedCard(
    plot: CropPlot,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) LushGreen else CardBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = plot.plotLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepBlack
                )
                Text(
                    text = "${plot.cropName ?: "Unplanted"} • ${plot.widthM}m × ${plot.heightM}m • ${plot.soilType.name}",
                    fontSize = 12.sp,
                    color = if (plot.cropName != null) LushGreen else Color(0xFF666666),
                    fontWeight = if (plot.cropName != null) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Bed",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
