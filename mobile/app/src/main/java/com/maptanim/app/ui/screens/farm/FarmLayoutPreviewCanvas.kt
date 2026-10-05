package com.maptanim.app.ui.screens.farm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.renderer.canvas.BedBorder
import com.maptanim.app.renderer.canvas.BedFill
import com.maptanim.app.renderer.canvas.CropSvgRenderer
import com.maptanim.app.renderer.canvas.FarmBorderColor
import com.maptanim.app.renderer.canvas.FarmInnerBg
import com.maptanim.app.renderer.canvas.GridDotColor
import com.maptanim.app.renderer.canvas.LabelBg
import com.maptanim.app.renderer.canvas.PlantedBedFill
import com.maptanim.app.renderer.canvas.cropColor

enum class CanvasLayer {
    CROPS,
    RISK,
    HARVEST
}

/**
 * FarmLayoutPreviewCanvas — 2D top-down preview of the actual farm layout.
 * Shows the whole canvas top-down with beds and crops with no cut or edge clipping.
 * Renders bed soil, child crop zones, and vector SVG crop icons.
 * Supports interactive selection and CROPS, RISK, HARVEST layers.
 */
@Composable
fun FarmLayoutPreviewCanvas(
    plots: List<CropPlot>,
    zones: List<CropZone> = emptyList(),
    selectedPlotId: String? = null,
    layer: CanvasLayer = CanvasLayer.CROPS,
    onPlotClick: (CropPlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(plots, zones) {
                detectTapGestures { tapOffset ->
                    if (plots.isEmpty()) return@detectTapGestures

                    val minX = plots.minOf { it.posX }
                    val maxX = plots.maxOf { it.posX + it.widthM }
                    val minY = plots.minOf { it.posY }
                    val maxY = plots.maxOf { it.posY + it.heightM }

                    val margin = 3.5f
                    val bMinX = minX - margin
                    val bMaxX = maxX + margin
                    val bMinY = minY - margin
                    val bMaxY = maxY + margin

                    val contentW = (bMaxX - bMinX).coerceAtLeast(14f)
                    val contentH = (bMaxY - bMinY).coerceAtLeast(10f)

                    val screenPad = 18f
                    val availW = (size.width - screenPad * 2).coerceAtLeast(20f)
                    val availH = (size.height - screenPad * 2).coerceAtLeast(20f)

                    val ppm = 40f
                    val zoom = minOf(availW / (contentW * ppm), availH / (contentH * ppm))
                    val centerWorldX = (bMinX + bMaxX) / 2f
                    val centerWorldY = (bMinY + bMaxY) / 2f
                    val panX = size.width / 2f - centerWorldX * ppm * zoom
                    val panY = size.height / 2f - centerWorldY * ppm * zoom

                    val worldX = (tapOffset.x - panX) / (ppm * zoom)
                    val worldY = (tapOffset.y - panY) / (ppm * zoom)

                    val hit = plots.firstOrNull { plot ->
                        worldX in plot.posX..(plot.posX + plot.widthM) &&
                        worldY in plot.posY..(plot.posY + plot.heightM)
                    }
                    if (hit != null) {
                        onPlotClick(hit)
                    }
                }
            }
    ) {
        // ── 1. Canvas Background ──────────────────────────────────────────────
        drawRect(Color(0xFF1B221A), Offset.Zero, size)

        val ppm = 40f

        if (plots.isEmpty()) {
            // ── EMPTY STATE CANVAS ────────────────────────────────────────────
            val pad = 16f
            val farmSize = Size(size.width - pad * 2, size.height - pad * 2)
            drawRoundRect(
                color = FarmInnerBg,
                topLeft = Offset(pad, pad),
                size = farmSize,
                cornerRadius = CornerRadius(6f)
            )
            drawRoundRect(
                color = FarmBorderColor.copy(alpha = 0.5f),
                topLeft = Offset(pad, pad),
                size = farmSize,
                cornerRadius = CornerRadius(6f),
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                )
            )

            // Centered Empty State Text
            val titleStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA0B09A),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            val subStyle = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7C66),
                textAlign = TextAlign.Center
            )

            val mTitle = textMeasurer.measure("NO BEDS PLACED YET", titleStyle)
            val mSub = textMeasurer.measure("Tap 'Garden Layout' to layout beds & plant crops", subStyle)

            val totalH = mTitle.size.height + mSub.size.height + 6f
            val startY = (size.height - totalH) / 2f

            drawText(
                mTitle,
                topLeft = Offset((size.width - mTitle.size.width) / 2f, startY)
            )
            drawText(
                mSub,
                topLeft = Offset((size.width - mSub.size.width) / 2f, startY + mTitle.size.height + 4f)
            )
            return@Canvas
        }

        // ── 2. POPULATED ACTUAL FARM LAYOUT ───────────────────────────────────
        // Calculate bounding box that encloses all beds with safe margin so NO CUT occurs
        val minX = plots.minOf { it.posX }
        val maxX = plots.maxOf { it.posX + it.widthM }
        val minY = plots.minOf { it.posY }
        val maxY = plots.maxOf { it.posY + it.heightM }

        val margin = 3.5f
        val bMinX = minX - margin
        val bMaxX = maxX + margin
        val bMinY = minY - margin
        val bMaxY = maxY + margin

        val contentW = (bMaxX - bMinX).coerceAtLeast(14f)
        val contentH = (bMaxY - bMinY).coerceAtLeast(10f)

        val screenPad = 18f
        val availW = (size.width - screenPad * 2).coerceAtLeast(20f)
        val availH = (size.height - screenPad * 2).coerceAtLeast(20f)

        val zoom = minOf(availW / (contentW * ppm), availH / (contentH * ppm))
        val centerWorldX = (bMinX + bMaxX) / 2f
        val centerWorldY = (bMinY + bMaxY) / 2f
        val panX = size.width / 2f - centerWorldX * ppm * zoom
        val panY = size.height / 2f - centerWorldY * ppm * zoom

        // Draw Farm Ground Surface & Boundary
        val groundTL = Offset(bMinX * ppm * zoom + panX, bMinY * ppm * zoom + panY)
        val groundSize = Size(contentW * ppm * zoom, contentH * ppm * zoom)

        drawRoundRect(
            color = FarmInnerBg,
            topLeft = groundTL,
            size = groundSize,
            cornerRadius = CornerRadius(8f)
        )
        drawRoundRect(
            color = FarmBorderColor.copy(alpha = 0.6f),
            topLeft = groundTL,
            size = groundSize,
            cornerRadius = CornerRadius(8f),
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )
        )

        // Subtle Dot Grid across visible ground
        val gridStep = 1.0f
        var gx = bMinX
        while (gx <= bMaxX) {
            var gy = bMinY
            while (gy <= bMaxY) {
                val sx = gx * ppm * zoom + panX
                val sy = gy * ppm * zoom + panY
                drawCircle(GridDotColor.copy(alpha = 0.55f), 1.2f, Offset(sx, sy))
                gy += gridStep
            }
            gx += gridStep
        }

        // Draw Beds & Crops
        for (plot in plots) {
            val tl = Offset(plot.posX * ppm * zoom + panX, plot.posY * ppm * zoom + panY)
            val bedW = plot.widthM * ppm * zoom
            val bedH = plot.heightM * ppm * zoom
            val bedSize = Size(bedW, bedH)
            val cornerR = CornerRadius(4f)

            // Child crop zones associated with this bed
            val bedZones = zones.filter {
                it.plotId == plot.id &&
                !it.cropName.isNullOrBlank() &&
                !it.cropName.equals("Bed", ignoreCase = true)
            }

            val hasSingleCrop = !plot.cropName.isNullOrBlank() &&
                !plot.cropName.equals("Bed", ignoreCase = true) &&
                !plot.cropId.equals("bed", ignoreCase = true)

            val isPlanted = bedZones.isNotEmpty() || hasSingleCrop

            if (!isPlanted) {
                // ── Plain unplanted bed — brown garden soil ─────────────────
                drawRoundRect(BedFill, tl, bedSize, cornerR)
                drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f))

                // Subtle furrow lines
                val lineCount = (plot.heightM * 1.5f).toInt().coerceIn(1, 8)
                val lineSpacing = bedH / (lineCount + 1)
                for (i in 1..lineCount) {
                    val y = tl.y + i * lineSpacing
                    drawLine(
                        BedBorder.copy(alpha = 0.4f),
                        Offset(tl.x + 3f, y),
                        Offset(tl.x + bedW - 3f, y),
                        strokeWidth = 1f
                    )
                }
            } else {
                // ── Planted bed — rich soil with actual crops ───────────────
                drawRoundRect(PlantedBedFill, tl, bedSize, cornerR)
                drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f))

                if (bedZones.isNotEmpty()) {
                    // Render each child crop zone inside this bed
                    bedZones.forEach { zone ->
                        val cropCol = cropColor(zone.cropName ?: "")
                        val zoneWorldX = plot.posX + zone.offsetX
                        val zoneWorldY = plot.posY + zone.offsetY
                        val ztl = Offset(zoneWorldX * ppm * zoom + panX, zoneWorldY * ppm * zoom + panY)
                        val zW = zone.widthM * ppm * zoom
                        val zH = zone.heightM * ppm * zoom

                        // Crop zone container boundary
                        val zoneCornerR = CornerRadius(3f)
                        drawRoundRect(
                            color = cropCol.copy(alpha = 0.22f),
                            topLeft = ztl,
                            size = Size(zW, zH),
                            cornerRadius = zoneCornerR
                        )
                        drawRoundRect(
                            color = cropCol.copy(alpha = 0.6f),
                            topLeft = ztl,
                            size = Size(zW, zH),
                            cornerRadius = zoneCornerR,
                            style = Stroke(
                                width = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f), 0f)
                            )
                        )

                        // Render SVG Crops inside the crop zone
                        val unitCols = (zone.widthM / 0.8f).toInt().coerceAtLeast(1)
                        val unitRows = (zone.heightM / 0.8f).toInt().coerceAtLeast(1)

                        if (unitCols <= 1 && unitRows <= 1) {
                            val svgSize = (minOf(zW, zH) * 0.72f).coerceAtLeast(12f)
                            CropSvgRenderer.drawCropSvg(
                                drawScope = this,
                                cropName = zone.cropName ?: "",
                                center = Offset(ztl.x + zW / 2f, ztl.y + zH / 2f),
                                sizePx = svgSize
                            )
                        } else {
                            val cellW = zW / unitCols
                            val cellH = zH / unitRows
                            val svgSize = (minOf(cellW, cellH) * 0.72f).coerceAtLeast(12f)
                            for (r in 0 until unitRows) {
                                for (c in 0 until unitCols) {
                                    val cx = ztl.x + (c + 0.5f) * cellW
                                    val cy = ztl.y + (r + 0.5f) * cellH
                                    CropSvgRenderer.drawCropSvg(
                                        drawScope = this,
                                        cropName = zone.cropName ?: "",
                                        center = Offset(cx, cy),
                                        sizePx = svgSize
                                    )
                                }
                            }
                        }
                    }
                } else if (hasSingleCrop) {
                    // Legacy or single-crop bed: render SVG crops in a grid across the bed
                    val cols = (plot.widthM / 1.0f).toInt().coerceAtLeast(1)
                    val rows = (plot.heightM / 1.0f).toInt().coerceAtLeast(1)
                    val cellW = bedW / cols
                    val cellH = bedH / rows
                    val svgSize = (minOf(cellW, cellH) * 0.68f).coerceAtLeast(12f)
                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val cx = tl.x + (c + 0.5f) * cellW
                            val cy = tl.y + (r + 0.5f) * cellH
                            CropSvgRenderer.drawCropSvg(
                                drawScope = this,
                                cropName = plot.cropName ?: "",
                                center = Offset(cx, cy),
                                sizePx = svgSize
                            )
                        }
                    }
                }
            }

            // Bed Label Badge
            val displayName = when {
                !isPlanted -> plot.plotLabel.ifBlank { "BED" }
                bedZones.size == 1 -> "${plot.plotLabel}: ${(bedZones.first().cropName ?: "").uppercase()}"
                bedZones.size > 1 -> "${plot.plotLabel} (${bedZones.size} CROPS)"
                else -> (plot.cropName ?: plot.plotLabel).uppercase()
            }

            if (bedW > 28f && bedH > 18f) {
                val fontSize = (9.5f * (zoom / 0.5f)).coerceIn(8f, 12f)
                val textStyle = TextStyle(
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.6.sp
                )
                val measured = textMeasurer.measure(
                    text = displayName,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val labelW = measured.size.width.toFloat() + 8f
                val labelH = measured.size.height.toFloat() + 4f
                val labelX = tl.x + (bedW - labelW) / 2f
                val labelY = tl.y + 4f

                if (labelW < bedW && labelH < bedH) {
                    drawRoundRect(
                        LabelBg.copy(alpha = 0.85f),
                        Offset(labelX, labelY),
                        Size(labelW, labelH),
                        CornerRadius(3f)
                    )
                    drawText(
                        measured,
                        topLeft = Offset(labelX + 4f, labelY + 2f)
                    )
                }
            }

            // Layer-Specific Overlays
            if (layer == CanvasLayer.RISK) {
                val isWetSeason = java.time.LocalDate.now().monthValue in 5..10
                val cropLower = (plot.cropName ?: "").lowercase()
                val isHighRisk = isWetSeason && (cropLower.contains("tomato") || cropLower.contains("eggplant") || cropLower.contains("chili"))
                val riskColor = if (isHighRisk) Color(0xFFE53935) else if (!isPlanted) Color(0xFFFFA000) else Color(0xFF43A047)
                drawRoundRect(
                    color = riskColor,
                    topLeft = tl,
                    size = bedSize,
                    cornerRadius = cornerR,
                    style = Stroke(width = 2.5f)
                )
            } else if (layer == CanvasLayer.HARVEST && isPlanted) {
                val pDateStr = plot.plantedDate?.take(10)
                val daysPlanted = if (!pDateStr.isNullOrBlank()) {
                    try {
                        val pDate = java.time.LocalDate.parse(pDateStr)
                        java.time.temporal.ChronoUnit.DAYS.between(pDate, java.time.LocalDate.now()).toInt().coerceAtLeast(0)
                    } catch (_: Exception) { 0 }
                } else 0
                val dth = 60
                val left = dth - daysPlanted
                val badgeText = if (left <= 0) "READY 🌾" else "${left}d"
                val badgeBg = if (left <= 0) Color(0xFF2E7D32) else if (left <= 7) Color(0xFFFFA000) else Color(0xFF1976D2)

                val badgeMeasured = textMeasurer.measure(
                    text = badgeText,
                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                val bw = badgeMeasured.size.width.toFloat() + 8f
                val bh = badgeMeasured.size.height.toFloat() + 4f
                val bx = tl.x + bedW - bw - 4f
                val by = tl.y + bedH - bh - 4f
                if (bw < bedW && bh < bedH) {
                    drawRoundRect(badgeBg, Offset(bx, by), Size(bw, bh), CornerRadius(3f))
                    drawText(badgeMeasured, topLeft = Offset(bx + 4f, by + 2f))
                }
            }

            // Selection Highlight Outline
            if (plot.id == selectedPlotId) {
                drawRoundRect(
                    color = Color(0xFF81C784),
                    topLeft = tl,
                    size = bedSize,
                    cornerRadius = cornerR,
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}
