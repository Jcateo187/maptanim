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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 0. Place Suitability & Alternative Methods Card ─────────────────
        item {
            val selectedPlot = state.rawPlots.firstOrNull { it.id == state.selectedPlotId }
                ?: state.rawPlots.firstOrNull()
            CropPlaceSuitabilityCard(
                cropName = selectedPlot?.cropName,
                environment = state.farmEnvironment,
                onOpenSetupDialog = onOpenSetupDialog
            )
        }

        // ── 1. Basketball Court Physical Scale Card ──────────────────────────
        item {
            val selectedPlot = state.rawPlots.firstOrNull { it.id == state.selectedPlotId }
                ?: state.rawPlots.firstOrNull()
            BasketballCourtScaleCard(
                widthM = selectedPlot?.widthM ?: 2.0f,
                heightM = selectedPlot?.heightM ?: 4.0f,
                cropName = selectedPlot?.cropName ?: "Vegetable Bed",
                plotLabel = selectedPlot?.plotLabel ?: "Bed #1"
            )
        }

        // ── 1B. Bed Resizing & Crop Assignment (Active Bed Controls) ─────────
        if (state.selectedPlotId != null) {
            val activePlot = state.rawPlots.firstOrNull { it.id == state.selectedPlotId }
            if (activePlot != null) {
                item {
                    BedEditorCard(
                        plot = activePlot,
                        environment = state.farmEnvironment,
                        onResize = { w, h -> onResizeBed(activePlot.id, w, h) },
                        onAssignCrop = { id, name -> onAssignCrop(activePlot.id, id, name) }
                    )
                }
            }
        }

        // ── 2. Toolbar & Undo/Redo Controls ──────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onUndo,
                            enabled = state.canUndo
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = if (state.canUndo) LushGreen else Color(0xFF9E9E9E)
                            )
                        }
                        IconButton(
                            onClick = onRedo,
                            enabled = state.canRedo
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo",
                                tint = if (state.canRedo) LushGreen else Color(0xFF9E9E9E)
                            )
                        }
                    }

                    Button(
                        onClick = onAddNewBed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LushGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Bed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // ── 3. Canvas Layer Filters ──────────────────────────────────────────
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CANVAS LAYERS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF555555)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CanvasLayer.values()) { layer ->
                        val isSelected = state.canvasLayer == layer
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetCanvasLayer(layer) },
                            label = {
                                Text(
                                    text = layer.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LushGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = DeepBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) LushGreen else CardBorderColor
                            )
                        )
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Yard,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No Garden Beds Planned",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "Tap 'Add Bed' above to start planning your vegetable layout.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
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
