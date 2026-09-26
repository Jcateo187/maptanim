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
import com.maptanim.app.renderer.canvas.BedBorder
import com.maptanim.app.renderer.canvas.BedFill
import com.maptanim.app.renderer.canvas.CanvasBg
import com.maptanim.app.renderer.canvas.FarmBorderColor
import com.maptanim.app.renderer.canvas.FarmInnerBg
import com.maptanim.app.renderer.canvas.GridDotColor
import com.maptanim.app.renderer.canvas.LabelBg
import com.maptanim.app.renderer.canvas.PlantedBedFill
import com.maptanim.app.renderer.canvas.cropColor

/**
 * FarmLayoutPreviewCanvas — Scaled 2D top-down visual preview of the actual farm layout
 * referencing the exact visual styling, design tokens, and bed representation from the Farm Editor.
 */
@Composable
fun FarmLayoutPreviewCanvas(
    plots: List<CropPlot>,
    onPlotClick: (CropPlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(plots) {
                detectTapGestures { tapOffset ->
                    if (plots.isEmpty()) return@detectTapGestures

                    // Compute current camera mapping
                    val minX = plots.minOf { it.posX }.coerceAtLeast(0f)
                    val maxX = plots.maxOf { it.posX + it.widthM }.coerceAtLeast(8f)
                    val minY = plots.minOf { it.posY }.coerceAtLeast(0f)
                    val maxY = plots.maxOf { it.posY + it.heightM }.coerceAtLeast(8f)

                    val margin = 2.5f
                    val farmMinX = (minX - margin).coerceAtLeast(0f)
                    val farmMinY = (minY - margin).coerceAtLeast(0f)
                    val farmMaxX = (maxX + margin).coerceAtMost(45f)
                    val farmMaxY = (maxY + margin).coerceAtMost(45f)
                    val farmW = (farmMaxX - farmMinX).coerceAtLeast(14f)
                    val farmH = (farmMaxY - farmMinY).coerceAtLeast(10f)

                    val ppm = 40f
                    val zoom = minOf(size.width / (farmW * ppm), size.height / (farmH * ppm))
                    val centerWorldX = (farmMinX + farmMaxX) / 2f
                    val centerWorldY = (farmMinY + farmMaxY) / 2f
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
            val mSub = textMeasurer.measure("Tap 'Edit Farm' to layout beds & plant crops", subStyle)

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
        val minX = plots.minOf { it.posX }.coerceAtLeast(0f)
        val maxX = plots.maxOf { it.posX + it.widthM }.coerceAtLeast(8f)
        val minY = plots.minOf { it.posY }.coerceAtLeast(0f)
        val maxY = plots.maxOf { it.posY + it.heightM }.coerceAtLeast(8f)

        val margin = 2.5f
        val farmMinX = (minX - margin).coerceAtLeast(0f)
        val farmMinY = (minY - margin).coerceAtLeast(0f)
        val farmMaxX = (maxX + margin).coerceAtMost(45f)
        val farmMaxY = (maxY + margin).coerceAtMost(45f)
        val farmW = (farmMaxX - farmMinX).coerceAtLeast(14f)
        val farmH = (farmMaxY - farmMinY).coerceAtLeast(10f)

        val zoom = minOf(size.width / (farmW * ppm), size.height / (farmH * ppm))
        val centerWorldX = (farmMinX + farmMaxX) / 2f
        val centerWorldY = (farmMinY + farmMaxY) / 2f
        val panX = size.width / 2f - centerWorldX * ppm * zoom
        val panY = size.height / 2f - centerWorldY * ppm * zoom

        // Draw Farm Ground Surface & Boundary
        val farmScreenTL = Offset(farmMinX * ppm * zoom + panX, farmMinY * ppm * zoom + panY)
        val farmScreenSize = Size(farmW * ppm * zoom, farmH * ppm * zoom)

        drawRoundRect(
            color = FarmInnerBg,
            topLeft = farmScreenTL,
            size = farmScreenSize,
            cornerRadius = CornerRadius(6f)
        )
        drawRoundRect(
            color = FarmBorderColor.copy(alpha = 0.55f),
            topLeft = farmScreenTL,
            size = farmScreenSize,
            cornerRadius = CornerRadius(6f),
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )
        )

        // Subtle Dot Grid
        val gridStep = 1.0f
        var gx = farmMinX
        while (gx <= farmMaxX) {
            var gy = farmMinY
            while (gy <= farmMaxY) {
                val sx = gx * ppm * zoom + panX
                val sy = gy * ppm * zoom + panY
                drawCircle(GridDotColor.copy(alpha = 0.6f), 1.2f, Offset(sx, sy))
                gy += gridStep
            }
            gx += gridStep
        }

        // Draw Plots
        for (plot in plots) {
            val tl = Offset(plot.posX * ppm * zoom + panX, plot.posY * ppm * zoom + panY)
            val bedW = plot.widthM * ppm * zoom
            val bedH = plot.heightM * ppm * zoom
            val bedSize = Size(bedW, bedH)
            val cornerR = CornerRadius(4f)

            val isPlainBed = plot.cropName.isNullOrBlank() ||
                plot.cropName.equals("Bed", ignoreCase = true) ||
                plot.cropId.equals("bed", ignoreCase = true)

            if (isPlainBed) {
                // Plain unplanted bed — brown garden soil
                drawRoundRect(BedFill, tl, bedSize, cornerR)
                drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f))

                // Furrow lines
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
                // Planted bed — rich soil with crop patterns
                drawRoundRect(PlantedBedFill, tl, bedSize, cornerR)
                drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f))

                // Colored crop dots
                val spacing = 0.9f // meters
                val iconRadius = (spacing * 0.28f * ppm * zoom).coerceIn(2f, 10f)
                val cropCol = cropColor(plot.cropName ?: "")
                val marginM = 0.3f

                var cx = plot.posX + marginM + spacing / 2f
                while (cx < plot.posX + plot.widthM - marginM) {
                    var cy = plot.posY + marginM + spacing / 2f
                    while (cy < plot.posY + plot.heightM - marginM) {
                        val sx = cx * ppm * zoom + panX
                        val sy = cy * ppm * zoom + panY
                        if (sx > tl.x + 2 && sx < tl.x + bedW - 2 &&
                            sy > tl.y + 2 && sy < tl.y + bedH - 2) {
                            drawCircle(cropCol, iconRadius, Offset(sx, sy))
                            drawCircle(cropCol.copy(alpha = 0.4f), iconRadius + 0.8f, Offset(sx, sy), style = Stroke(0.6f))
                        }
                        cy += spacing
                    }
                    cx += spacing
                }
            }

            // Crop / Bed Name Label
            val displayName = when {
                isPlainBed -> plot.plotLabel.ifBlank { "BED" }
                else -> (plot.cropName ?: plot.plotLabel).uppercase()
            }

            if (bedW > 24f && bedH > 16f) {
                val fontSize = (10f * (zoom / 0.5f)).coerceIn(8f, 13f)
                val textStyle = TextStyle(
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.8.sp
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
                val labelY = tl.y + (bedH - labelH) / 2f

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
        }
    }
}
