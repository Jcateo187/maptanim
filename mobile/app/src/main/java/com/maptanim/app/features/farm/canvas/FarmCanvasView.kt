package com.maptanim.app.features.farm.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.features.farm.renderer.canvas.BedBorder
import com.maptanim.app.features.farm.renderer.canvas.BedFill
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.renderer.canvas.FarmBorderColor
import com.maptanim.app.features.farm.renderer.canvas.GridDotColor
import com.maptanim.app.features.farm.renderer.canvas.PlantedBedFill

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CanvasSoilBg = Color(0xFF1B221A)
private val SelectionYellow = Color(0xFFFFB300)
private val BasketballCourtOrange = Color(0xFFE65100)

/**
 * FarmCanvasView — Interactive 2D Top-Down Garden Bed Viewport.
 * Supports:
 * 1. Visual bed placement with dot grid and farm boundary.
 * 2. FIBA Basketball Court overlay (28m x 15m) scale benchmark.
 * 3. Bed tap selection, touch drag-to-move, and handle drag-to-resize.
 * 4. Multi-layer filtering (Crops, Risk, Harvest).
 * 5. Zoom controls (+, -, reset) and real-time area percentage feedback.
 */
@Composable
fun FarmCanvasView(
    plots: List<CropPlot>,
    selectedPlotId: String?,
    layer: CanvasLayer,
    isResizeMode: Boolean,
    showCourtScale: Boolean,
    onSelectPlot: (String?) -> Unit,
    onMovePlot: (String, Float, Float) -> Unit,
    onResizePlot: (String, Float, Float) -> Unit,
    onToggleResizeMode: () -> Unit,
    onToggleCourtScale: () -> Unit,
    onSetLayer: (CanvasLayer) -> Unit,
    onAddNewBed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var zoom by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val selectedPlot = plots.firstOrNull { it.id == selectedPlotId }
    val ppm = 36f * zoom

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasSoilBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(plots, selectedPlotId, isResizeMode, zoom, panOffset) {
                    detectTapGestures { tapPos ->
                        val worldX = (tapPos.x - panOffset.x) / ppm
                        val worldY = (tapPos.y - panOffset.y) / ppm

                        val hit = plots.firstOrNull { p ->
                            worldX in p.posX..(p.posX + p.widthM) &&
                            worldY in p.posY..(p.posY + p.heightM)
                        }
                        onSelectPlot(hit?.id)
                    }
                }
                .pointerInput(plots, selectedPlotId, isResizeMode, zoom, panOffset) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val activePlot = plots.firstOrNull { it.id == selectedPlotId }
                        if (activePlot != null) {
                            val deltaWorldX = dragAmount.x / ppm
                            val deltaWorldY = dragAmount.y / ppm

                            if (isResizeMode) {
                                val newW = (activePlot.widthM + deltaWorldX).coerceIn(0.5f, 15f)
                                val newH = (activePlot.heightM + deltaWorldY).coerceIn(0.5f, 15f)
                                onResizePlot(activePlot.id, newW, newH)
                            } else {
                                onMovePlot(activePlot.id, deltaWorldX, deltaWorldY)
                            }
                        } else {
                            panOffset += dragAmount
                        }
                    }
                }
        ) {
            // ── 1. Dot Grid ──────────────────────────────────────────────────
            drawDotGrid(ppm, panOffset)

            // ── 2. Basketball Court Scale Overlay ────────────────────────────
            if (showCourtScale) {
                drawBasketballCourtOverlay(ppm, panOffset, textMeasurer)
            }

            // ── 3. Farm Boundary (45m x 45m) ─────────────────────────────────
            val boundarySize = Size(45f * ppm, 45f * ppm)
            drawRoundRect(
                color = FarmBorderColor.copy(alpha = 0.5f),
                topLeft = panOffset,
                size = boundarySize,
                cornerRadius = CornerRadius(8f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
            )

            // ── 4. Garden Beds ───────────────────────────────────────────────
            for (plot in plots) {
                val isSelected = plot.id == selectedPlotId
                drawGardenBed(
                    plot = plot,
                    isSelected = isSelected,
                    isResizeMode = isResizeMode && isSelected,
                    layer = layer,
                    ppm = ppm,
                    panOffset = panOffset,
                    textMeasurer = textMeasurer
                )
            }
        }

        // ── Top Canvas Floating Toolbar ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Layer Pills (Crops, Risk, Harvest)
            Row(
                modifier = Modifier
                    .background(Color(0xD9000000), RoundedCornerShape(18.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CanvasLayer.values().forEach { l ->
                    val isSel = l == layer
                    Surface(
                        onClick = { onSetLayer(l) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSel) LushGreen else Color.Transparent
                    ) {
                        Text(
                            text = l.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Canvas Controls (Court Toggle, Resize Toggle, Zoom)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Court Scale Toggle
                Surface(
                    onClick = onToggleCourtScale,
                    shape = RoundedCornerShape(6.dp),
                    color = if (showCourtScale) BasketballCourtOrange else Color(0xD9222B21),
                    border = BorderStroke(1.dp, if (showCourtScale) BasketballCourtOrange else Color(0xFF384535))
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsBasketball,
                        contentDescription = "Court Scale",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }

                // Resize Mode Toggle
                Surface(
                    onClick = onToggleResizeMode,
                    shape = RoundedCornerShape(6.dp),
                    color = if (isResizeMode) LushGreen else Color(0xD9222B21),
                    border = BorderStroke(1.dp, if (isResizeMode) LushGreen else Color(0xFF384535))
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Resize Mode",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }

                // Zoom Controls
                Surface(
                    onClick = { zoom = (zoom * 1.25f).coerceIn(0.4f, 2.8f) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD9222B21),
                    border = BorderStroke(1.dp, Color(0xFF384535))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }
                Surface(
                    onClick = { zoom = (zoom * 0.8f).coerceIn(0.4f, 2.8f) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD9222B21),
                    border = BorderStroke(1.dp, Color(0xFF384535))
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(15.dp)
                    )
                }
                Surface(
                    onClick = {
                        zoom = 1.0f
                        panOffset = Offset.Zero
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD9222B21),
                    border = BorderStroke(1.dp, Color(0xFF384535))
                ) {
                    Text(
                        text = "1×",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // ── Bottom Scale Feedback Bar ────────────────────────────────────────
        if (selectedPlot != null) {
            val areaSqm = selectedPlot.widthM * selectedPlot.heightM
            val courtPct = (areaSqm / 420.0f) * 100f
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color(0xE6111813),
                border = BorderStroke(1.dp, SelectionYellow)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${selectedPlot.plotLabel}: ${String.format("%.1f", selectedPlot.widthM)}m × ${String.format("%.1f", selectedPlot.heightM)}m (${String.format("%.1f", areaSqm)} m²)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${String.format("%.2f", courtPct)}% of Court",
                        color = SelectionYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawDotGrid(ppm: Float, panOffset: Offset) {
    val step = 1.0f * ppm
    val radius = 1.2f
    var gx = (panOffset.x % step)
    while (gx < size.width) {
        var gy = (panOffset.y % step)
        while (gy < size.height) {
            drawCircle(GridDotColor, radius, Offset(gx, gy))
            gy += step
        }
        gx += step
    }
}

private fun DrawScope.drawBasketballCourtOverlay(ppm: Float, panOffset: Offset, textMeasurer: TextMeasurer) {
    val courtW = 28f * ppm
    val courtH = 15f * ppm
    val courtTl = panOffset + Offset(2f * ppm, 2f * ppm)

    // Court outline
    drawRoundRect(
        color = BasketballCourtOrange.copy(alpha = 0.15f),
        topLeft = courtTl,
        size = Size(courtW, courtH),
        cornerRadius = CornerRadius(4f)
    )
    drawRoundRect(
        color = BasketballCourtOrange.copy(alpha = 0.7f),
        topLeft = courtTl,
        size = Size(courtW, courtH),
        cornerRadius = CornerRadius(4f),
        style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
    )

    // Center Line & Circle
    val midX = courtTl.x + courtW / 2f
    drawLine(
        color = BasketballCourtOrange.copy(alpha = 0.5f),
        start = Offset(midX, courtTl.y),
        end = Offset(midX, courtTl.y + courtH),
        strokeWidth = 1.5f
    )
    drawCircle(
        color = BasketballCourtOrange.copy(alpha = 0.5f),
        radius = 1.8f * ppm,
        center = Offset(midX, courtTl.y + courtH / 2f),
        style = Stroke(1.5f)
    )

    // Court Label
    val label = textMeasurer.measure(
        text = "Barangay Court (28m × 15m = 420 sqm)",
        style = TextStyle(color = BasketballCourtOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    )
    drawText(label, topLeft = Offset(courtTl.x + 8f, courtTl.y + 6f))
}

private fun DrawScope.drawGardenBed(
    plot: CropPlot,
    isSelected: Boolean,
    isResizeMode: Boolean,
    layer: CanvasLayer,
    ppm: Float,
    panOffset: Offset,
    textMeasurer: TextMeasurer
) {
    val tl = panOffset + Offset(plot.posX * ppm, plot.posY * ppm)
    val bedW = plot.widthM * ppm
    val bedH = plot.heightM * ppm
    val bedSize = Size(bedW, bedH)
    val hasCrop = !plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true)

    val fillColor = when (layer) {
        CanvasLayer.RISK -> if (hasCrop) Color(0xFF2E7D32).copy(alpha = 0.7f) else BedFill
        CanvasLayer.HARVEST -> if (hasCrop) Color(0xFFF57F17).copy(alpha = 0.7f) else BedFill
        CanvasLayer.CROPS -> if (hasCrop) PlantedBedFill else BedFill
    }

    // Bed soil body
    drawRoundRect(
        color = fillColor,
        topLeft = tl,
        size = bedSize,
        cornerRadius = CornerRadius(4f)
    )
    drawRoundRect(
        color = if (isSelected) SelectionYellow else BedBorder,
        topLeft = tl,
        size = bedSize,
        cornerRadius = CornerRadius(4f),
        style = Stroke(width = if (isSelected) 2.5f else 1.2f)
    )

    // Repeating SVG crop icons
    if (hasCrop) {
        val unitCols = (plot.widthM / 0.8f).toInt().coerceAtLeast(1)
        val unitRows = (plot.heightM / 0.8f).toInt().coerceAtLeast(1)
        val cellW = bedW / unitCols
        val cellH = bedH / unitRows
        val iconSize = minOf(cellW, cellH) * 0.7f

        for (r in 0 until unitRows) {
            for (c in 0 until unitCols) {
                val cx = tl.x + (c + 0.5f) * cellW
                val cy = tl.y + (r + 0.5f) * cellH
                CropSvgRenderer.drawCropSvg(
                    drawScope = this,
                    cropName = plot.cropName ?: "",
                    center = Offset(cx, cy),
                    sizePx = iconSize
                )
            }
        }
    }

    // Bed label
    val labelText = if (hasCrop) "${plot.plotLabel} • ${plot.cropName}" else plot.plotLabel
    val measured = textMeasurer.measure(
        text = labelText,
        style = TextStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    drawRoundRect(
        color = Color(0xD9000000),
        topLeft = Offset(tl.x + 2f, tl.y + 2f),
        size = Size(measured.size.width + 6f, measured.size.height + 2f),
        cornerRadius = CornerRadius(2f)
    )
    drawText(measured, topLeft = Offset(tl.x + 5f, tl.y + 3f))

    // Resize handle on bottom-right corner
    if (isResizeMode) {
        val handleCenter = Offset(tl.x + bedW, tl.y + bedH)
        drawCircle(SelectionYellow, 6f, handleCenter)
        drawCircle(Color.White, 3f, handleCenter)
    }
}
