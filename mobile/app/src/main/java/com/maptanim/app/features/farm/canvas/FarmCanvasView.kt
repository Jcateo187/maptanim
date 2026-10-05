package com.maptanim.app.features.farm.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.features.farm.components.EditBottomLayout
import com.maptanim.app.features.farm.renderer.canvas.TopDownCamera
import com.maptanim.app.features.farm.renderer.canvas.TopDownFarmCanvas
import com.maptanim.app.features.farm.renderer.canvas.TopDownProjection
import com.maptanim.app.features.farm.viewmodel.EditUiState
import com.maptanim.app.features.farm.viewmodel.EditViewModel

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val BasketballOrange = Color(0xFFE65100)

/**
 * FarmCanvasView — Interactive 2D Top-Down Farm Viewport.
 * Wraps TopDownFarmCanvas (full multi-touch gestures, 8-point resize handles,
 * drag-and-drop crop placement, pinch-to-zoom) and integrates the toolbar
 * (Add Bed, Duplicate, Resize, Rotate, Delete, Basketball Court Benchmark, Zoom, Expand).
 */
@Composable
fun FarmCanvasView(
    editUiState: EditUiState,
    editViewModel: EditViewModel,
    canvasLayer: CanvasLayer,
    showCourtScale: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleCourtScale: () -> Unit,
    onSelectLayer: (CanvasLayer) -> Unit,
    onRequestAddBed: () -> Unit,
    onDeletePlot: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var liveCamera by remember { mutableStateOf(TopDownCamera(zoom = 0.5f)) }
    val selectedPlot = editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1B221A))
    ) {
        // ── 1. The Full 2D Top-Down Interactive Canvas Engine ───────────────
        TopDownFarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = editUiState,
            editViewModel = editViewModel,
            showBoundary = showCourtScale,
            initialZoom = 0.5f,
            onCameraChanged = { liveCamera = it }
        )

        // ── 2. Top Canvas Floating Toolbar ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: Add Bed + Layer Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onRequestAddBed,
                    shape = RoundedCornerShape(6.dp),
                    color = LushGreen,
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Add Bed",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Layer Selector: Crops / Risk / Harvest
                Row(
                    modifier = Modifier
                        .background(Color(0xD910160F), RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CanvasLayer.values().forEach { layer ->
                        val isSel = layer == canvasLayer
                        Surface(
                            onClick = { onSelectLayer(layer) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF2E7D32) else Color.Transparent
                        ) {
                            Text(
                                text = layer.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSel) Color.White else Color(0xFFA0B09A),
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Right Group: Basketball Scale + Zoom + Maximize Canvas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Basketball Court Benchmark Toggle
                Surface(
                    onClick = onToggleCourtScale,
                    shape = RoundedCornerShape(6.dp),
                    color = if (showCourtScale) BasketballOrange else Color(0xD91E281C),
                    border = BorderStroke(1.dp, if (showCourtScale) BasketballOrange else Color(0xFF385532))
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsBasketball,
                        contentDescription = "Basketball Court Scale",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }

                // Zoom Controls
                Surface(
                    onClick = {
                        val newZoom = (liveCamera.zoom * 1.3f).coerceIn(0.2f, 3.5f)
                        liveCamera = liveCamera.copy(zoom = newZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(14.dp)
                    )
                }
                Surface(
                    onClick = {
                        val newZoom = (liveCamera.zoom * 0.75f).coerceIn(0.2f, 3.5f)
                        liveCamera = liveCamera.copy(zoom = newZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(14.dp)
                    )
                }
                Surface(
                    onClick = {
                        val targetZoom = 0.5f
                        val plots = editUiState.plots
                        val centerX = if (plots.isNotEmpty()) {
                            (plots.minOf { it.posX } + plots.maxOf { it.posX + it.widthM }) / 2f
                        } else 22.5f
                        val centerY = if (plots.isNotEmpty()) {
                            (plots.minOf { it.posY } + plots.maxOf { it.posY + it.heightM }) / 2f
                        } else 22.5f
                        val panX = 400f - centerX * TopDownProjection.PPM * targetZoom
                        val panY = 300f - centerY * TopDownProjection.PPM * targetZoom
                        liveCamera = TopDownCamera(panX = panX, panY = panY, zoom = targetZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Text(
                        text = "5×",
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Maximize / Minimize Canvas Button
                Surface(
                    onClick = onToggleExpand,
                    shape = RoundedCornerShape(6.dp),
                    color = if (isExpanded) LushGreen else Color(0xD91E281C),
                    border = BorderStroke(1.dp, if (isExpanded) LushGreen else Color(0xFF385532))
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                        contentDescription = if (isExpanded) "Minimize Canvas" else "Expand Canvas",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }
            }
        }

        // ── 3. Bottom Contextual Action Bar for Selected Bed ────────────────
        if (editUiState.selectedPlotId != null || editUiState.selectedZoneId != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Real-World Basketball Scale Pill
                if (selectedPlot != null) {
                    val areaSqm = selectedPlot.widthM * selectedPlot.heightM
                    val courtPct = (areaSqm / 420.0f) * 100f
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xE6111813),
                        border = BorderStroke(1.dp, Color(0xFFFFB300)),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "SCALE: ${selectedPlot.plotLabel} (${String.format("%.1f", selectedPlot.widthM)}m × ${String.format("%.1f", selectedPlot.heightM)}m = ${String.format("%.1f", areaSqm)}m²) • ${String.format("%.2f", courtPct)}% of Basketball Court",
                            color = Color(0xFFFFD54F),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Toolbar: Duplicate, Resize (Handles), Rotate, Delete
                EditBottomLayout(
                    uiState = editUiState,
                    onDuplicateClick = {
                        val plotId = editUiState.selectedPlotId
                        if (plotId != null) editViewModel.duplicatePlot(plotId)
                    },
                    onResizeClick = {
                        editViewModel.toggleResizeMode()
                    },
                    onRotateClick = {
                        val plotId = editUiState.selectedPlotId
                        if (plotId != null) editViewModel.rotatePlot(plotId)
                    },
                    onDeleteClick = {
                        val plotId = editUiState.selectedPlotId
                        if (plotId != null) {
                            editViewModel.deletePlot(plotId)
                            onDeletePlot(plotId)
                        }
                    },
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}
