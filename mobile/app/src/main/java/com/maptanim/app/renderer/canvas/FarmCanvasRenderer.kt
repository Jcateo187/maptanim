package com.maptanim.app.renderer.canvas

import android.content.res.Resources
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.renderer.model.*


/**
 * FarmCanvasRenderer — main entry point for all isometric rendering.
 * Features exact scenery counts (50 Trees, 20 Rocks, 30 Flowers) and zero front tree occlusion.
 */
object FarmCanvasRenderer {

    fun snapToGrid(worldPos: Offset, snap: Float = 0.5f): Offset {
        val snappedX = Math.round(worldPos.x / snap) * snap
        val snappedY = Math.round(worldPos.y / snap) * snap
        return Offset(snappedX, snappedY)
    }

    fun DrawScope.render(
        plots: List<PlotRenderData>,
        cropZones: List<CropZoneRenderData> = emptyList(),
        farmObjects: List<FarmObjectRenderData> = emptyList(),
        camera: CameraState,
        canvasMode: com.maptanim.app.domain.model.CanvasMode,
        selectedPlotId: String? = null,
        selectedZoneId: String? = null,
        hoverWorldPos: Offset? = null,
        isValidPlacement: Boolean = true,
        isDraggingCrop: Boolean = false,
        isGridEnabled: Boolean = true,
        isSnapEnabled: Boolean = true,
        isResizeMode: Boolean = false,
        resources: Resources? = null,
        context: android.content.Context? = null,
        onHandlesReady: ((String, HandlePositions) -> Unit)? = null
    ) {
        // ── Layer 0: Full Viewport Grass Terrain & Background Image ────────
        renderGround(camera, resources, context)

        // ── Layer 1.5: CoC-Style Hover Tile Highlight Preview (Only during crop dragging) ──
        if (hoverWorldPos != null && isDraggingCrop) {
            renderTileHighlight(hoverWorldPos, camera, isValidPlacement, widthM = 1.0f, heightM = 1.0f)
        }

        // ── Layer 2: Direct Planted Crops ─────────────────────────────────
        val sortedPlots = plots.sortedBy { it.posX + it.posY }

        sortedPlots.forEach { plot ->
            renderDirectCrop(plot, cropZones, camera, context)
        }

        // ── Layer 3.5: Floating Crop Zone Top Labels ─────────────────────
        sortedPlots.forEach { plot ->
            renderCropZoneLabel(plot, camera, isSelected = (plot.id == selectedPlotId))
        }

        // ── Layer 4: Status Pins & Calendar Monitoring Badges ─────────────
        sortedPlots.forEach { plot ->
            if (!plot.cropName.isNullOrEmpty() || plot.activeTasks.isNotEmpty()) {
                renderStatusPins(plot, camera)
            }
        }

        // ── Layer 5: Clean Selection Border & Grid Overlay ────────────────
        val showGrid = (canvasMode == com.maptanim.app.domain.model.CanvasMode.EDIT) || (selectedPlotId != null) || isDraggingCrop
        if (showGrid && isGridEnabled) renderGridOverlay(camera)

        selectedPlotId?.let { selId ->
            sortedPlots.firstOrNull { it.id == selId }?.let { selPlot ->
                val handles = renderSelectionHandles(selPlot, camera, showHandles = isResizeMode)
                onHandlesReady?.invoke(selId, handles)
            }
        }
    }


    // ── Farm Area Constants ──────────────────────────────────────────────
    private const val FARM_MIN_X = 0f
    private const val FARM_MIN_Y = 0f
    private const val FARM_MAX_X = 45f
    private const val FARM_MAX_Y = 45f

    // ── Layer 0: Full Viewport Ground Terrain & Background Image ────────
    private fun DrawScope.renderGround(
        camera: CameraState,
        resources: Resources? = null,
        context: android.content.Context?
    ): Boolean {
        drawRect(color = Color(0xFF38651B))

        if (context != null) {
            val bgBitmap = AssetLoader.getBackgroundTexture(context, "background_scenery/backgound_1.png")
            if (bgBitmap != null) {
                val centerPos = IsometricProjection.toScreen(22.5f, 22.5f + 1.0f, camera)
                val gridWidthPx = 45f * IsometricProjection.TILE_W * camera.zoom
                val gridHeightPx = 45f * IsometricProjection.TILE_H * camera.zoom

                val isBg1 = (bgBitmap.width in 1700..1850 && bgBitmap.height in 800..950) ||
                        (Math.abs((bgBitmap.width.toFloat() / bgBitmap.height.toFloat()) - 2.0f) < 0.1f)

                val diamondWidthRatio = if (isBg1) 0.7050f else 0.7578f
                val diamondHeightRatio = if (isBg1) 0.6050f else 0.7578f
                val centerXRatio = 0.4960f
                val centerYRatio = if (isBg1) 0.5420f else 0.50f

                val scaleX = gridWidthPx / (bgBitmap.width * diamondWidthRatio)
                val scaleY = gridHeightPx / (bgBitmap.height * diamondHeightRatio)

                val bgScaleBoost = 1.50f
                val targetW = (bgBitmap.width * scaleX * bgScaleBoost).toInt().coerceAtLeast(1)
                val targetH = (bgBitmap.height * scaleY * bgScaleBoost).toInt().coerceAtLeast(1)

                val left = Math.round(centerPos.x - (targetW * centerXRatio))
                val top = Math.round(centerPos.y - (targetH * centerYRatio))

                drawImage(
                    image = bgBitmap,
                    dstOffset = IntOffset(left, top),
                    dstSize = IntSize(targetW, targetH)
                )
                return true
            }
        }
        return false
    }



    // ── Layer 1.5: CoC Isometric Hover Tile Highlight (MD 34 Blue/Red Preview) ─

    private fun DrawScope.renderTileHighlight(
        hoverWorldPos: Offset,
        camera: CameraState,
        isValidPlacement: Boolean = true,
        widthM: Float = 1.0f,
        heightM: Float = 1.0f
    ) {
        val w = widthM
        val h = heightM

        val topLeft     = IsometricProjection.toScreen(hoverWorldPos.x,     hoverWorldPos.y,     camera)
        val topRight    = IsometricProjection.toScreen(hoverWorldPos.x + w, hoverWorldPos.y,     camera)
        val bottomLeft  = IsometricProjection.toScreen(hoverWorldPos.x,     hoverWorldPos.y + h, camera)
        val bottomRight = IsometricProjection.toScreen(hoverWorldPos.x + w, hoverWorldPos.y + h, camera)

        val path = Path().apply {
            moveTo(topLeft.x, topLeft.y)
            lineTo(topRight.x, topRight.y)
            lineTo(bottomRight.x, bottomRight.y)
            lineTo(bottomLeft.x, bottomLeft.y)
            close()
        }

        val strokeColor = if (isValidPlacement) Color(0xFF1E88E5) else Color(0xFFE53935)
        val fillColor   = if (isValidPlacement) Color(0x331E88E5) else Color(0x44E53935)

        drawPath(
            path = path,
            color = fillColor
        )

        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }

    // ── Layer 1.8: Floating Crop Zone Top Label Badge ────────────────────

    private fun DrawScope.renderCropZoneLabel(
        plot: PlotRenderData,
        camera: CameraState,
        isSelected: Boolean = false
    ) {
        val topPos = plot.topEdgeCenter(camera)
        val cropName = plot.cropName ?: "Crop Zone"
        val emoji = when (cropName.lowercase().replace(" ", "")) {
            "stringbeans", "sitaw", "beans" -> "🫘"
            "eggplant", "talong" -> "🍆"
            "tomato", "kamatis" -> "🍅"
            "onion", "sibuyas" -> "🧅"
            "pumpkin", "kalabasa" -> "🎃"
            "corn", "mais" -> "🌽"
            "pechay" -> "🥬"
            "repolyo", "cabbage" -> "🥬"
            "sili", "chili" -> "🌶️"
            "okra" -> "🌿"
            else -> "🥕"
        }
        val varietyStr = if (!plot.cropVariety.isNullOrBlank()) " - ${plot.cropVariety}" else ""
        val isSim = plot.cropName?.lowercase()?.contains("ampalaya") == true || plot.cropVariety?.contains("10s", ignoreCase = true) == true
        val progressStr = if (isSim) {
            val liveProgress = plot.currentStageProgressRatio
            val pct = (liveProgress * 100).toInt()
            val sec = (liveProgress * 10f).toInt().coerceIn(0, 10)
            if (plot.growthStage == 5) " • HARVEST READY 🌾 (${sec}s / 10s) [$pct%]"
            else " • Stage ${plot.growthStage}/5 (${sec}s / 10s) [$pct%]"
        } else if (plot.isMonitoringStarted) {
            val pct = (plot.stageProgressRatio * 100).toInt()
            when {
                plot.isHarvestOverdue -> " • HARVEST OVERDUE ⚠️ (Day ${plot.daysPlanted}/${plot.daysToHarvest})"
                plot.isHarvestReady -> " • HARVEST READY 🌾 (Day ${plot.daysPlanted}/${plot.daysToHarvest})"
                else -> " • Day ${plot.daysPlanted}/${plot.daysToHarvest} [$pct%]"
            }
        } else {
            " • Pending Start"
        }
        val labelText = "$emoji $cropName$varietyStr$progressStr"

        val nativeCanvas = drawContext.canvas.nativeCanvas
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = (12f * camera.zoom).coerceIn(10f, 20f)
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            textAlign = android.graphics.Paint.Align.CENTER
        }

        val textWidth = textPaint.measureText(labelText)
        val fontMetrics = textPaint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top

        val padH = 12f * camera.zoom
        val padV = 5f * camera.zoom
        val rectW = textWidth + (padH * 2f)
        val rectH = textHeight + (padV * 2f)

        val centerY = topPos.y - (rectH / 2f) - (14f * camera.zoom)
        val rectLeft = topPos.x - (rectW / 2f)
        val rectTop = centerY - (rectH / 2f)
        val rectRight = topPos.x + (rectW / 2f)
        val rectBottom = centerY + (rectH / 2f)

        val bgPaint = android.graphics.Paint().apply {
            color = if (isSelected) android.graphics.Color.parseColor("#E61B5E20") else android.graphics.Color.parseColor("#CC000000")
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
        }
        val borderPaint = android.graphics.Paint().apply {
            color = if (isSelected) android.graphics.Color.parseColor("#FF81C784") else android.graphics.Color.parseColor("#44FFFFFF")
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = (2f * camera.zoom).coerceIn(1f, 4f)
        }

        val cornerRadius = rectH / 2f
        val rectF = android.graphics.RectF(rectLeft, rectTop, rectRight, rectBottom)

        nativeCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)
        nativeCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)

        val textY = centerY - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
        nativeCanvas.drawText(labelText, topPos.x, textY, textPaint)
    }

    // ── Layer 2: Direct Planted Crops & Trellises ───────────────────────

    private fun DrawScope.renderDirectCrop(
        plot: PlotRenderData,
        cropZones: List<CropZoneRenderData>,
        camera: CameraState,
        context: android.content.Context?
    ) {
        if (context == null) return

        val zone = cropZones.firstOrNull { it.plotId == plot.id }
        val plantsToRender = if (zone != null && zone.plantInstances.isNotEmpty()) {
            zone.plantInstances.map { it.copy(growthStage = plot.growthStage) }
        } else {
            // No saved crop zone for this plot — skip rendering plants
            return
        }

        plantsToRender.forEach { plant ->
            val cropClean = when (plant.cropName.lowercase().replace(" ", "")) {
                "stringbeans", "sitaw", "beans" -> "crop_stringbeans"
                "eggplant", "talong" -> "crop_eggplant"
                "tomato", "kamatis" -> "crop_tomato"
                "onion", "sibuyas" -> "crop_onion"
                "pumpkin", "squash", "kalabasa" -> "crop_pumpkin"
                "corn", "mais" -> "crop_corn"
                "cabbage", "repolyo" -> "crop_cabbage"
                "pechay", "bokchoy" -> "crop_pechay"
                "ampalaya", "bittergourd" -> "crop_ampalaya"
                "okra" -> "crop_okra"
                "sili", "chili", "chilipepper", "pepper" -> "crop_sili"
                "cucumber", "pipino" -> "crop_pipino"
                "kangkong", "waterspinach" -> "crop_kangkong"
                "lettuce", "litsugas" -> "crop_lettuce"
                else -> "crop_carrot"
            }

            val stage = plant.growthStage.coerceIn(1, 5)
            val bitmap = AssetLoader.loadFromAssets(context, "crops/${cropClean}_${stage}.png")
                ?: AssetLoader.loadFromAssets(context, "crops/${cropClean}_1.png")
                ?: AssetLoader.loadFromAssets(context, "crops/crop_carrot_1.png")
            val pos = IsometricProjection.toScreen(plant.worldX, plant.worldY, camera)
            val baseTileW = IsometricProjection.TILE_W * camera.zoom

            if (bitmap != null) {
                val targetW = (baseTileW * 0.72f * plant.scaleFactor).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                val baseTileH = IsometricProjection.TILE_H * camera.zoom
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        Math.round(pos.x - targetW / 2f),
                        Math.round(pos.y - targetH + (baseTileH * 0.1f))
                    ),
                    dstSize = IntSize(targetW, targetH)
                )
            } else {
                drawCircle(
                    color = Color(0xFF4CAF50),
                    radius = (baseTileW * 0.35f),
                    center = pos
                )
            }
        }
    }

    // ── Layer 4: Status Pins & Calendar Monitoring Badges (VIEW MODE) ────

    private fun DrawScope.renderStatusPins(plot: PlotRenderData, camera: CameraState) {
        val topPos = plot.topEdgeCenter(camera)

        // ── 1. Unstarted Crop Calendar Pin Badge (📅) ────────────────────
        if (!plot.cropName.isNullOrEmpty() && !plot.isMonitoringStarted) {
            val pinSizePx = 28f * camera.zoom
            val pinCenter = Offset(topPos.x, topPos.y - (44f * camera.zoom))

            drawCircle(
                color = Color(android.graphics.Color.parseColor("#FFE65100")), // Amber Orange
                radius = pinSizePx / 2f,
                center = pinCenter
            )
            drawCircle(
                color = Color.White,
                radius = pinSizePx / 2f,
                center = pinCenter,
                style = Stroke(width = 2.dp.toPx())
            )

            val nativeCanvas = drawContext.canvas.nativeCanvas
            val iconPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = (14f * camera.zoom).coerceIn(10f, 22f)
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val fontMetrics = iconPaint.fontMetrics
            val textY = pinCenter.y - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
            nativeCanvas.drawText("📅", pinCenter.x, textY, iconPaint)
        }

        // ── 2. Active Task Pin Badges (💧 🌿 🌾 🐛) ───────────────────────
        if (plot.activeTasks.isNotEmpty()) {
            val pinSizePx = 28f * camera.zoom
            val anchorY = topPos.y - (70f * camera.zoom)

            plot.activeTasks.forEachIndexed { index, task ->
                val offsetX = (index - (plot.activeTasks.size - 1) / 2f) * (pinSizePx + 6f)
                val pinCenter = Offset(topPos.x + offsetX, anchorY)

                val (pinColorHex, iconSymbol) = when (task.taskType) {
                    TaskType.WATER -> "#FF1E88E5" to "💧"
                    TaskType.FERTILIZE -> "#FF43A047" to "🌿"
                    TaskType.HARVEST -> "#FFFB8C00" to "🌾"
                    TaskType.PEST_ALERT -> "#FFE53935" to "🐛"
                    else -> "#FF8E24AA" to "📋"
                }

                drawCircle(
                    color = Color(android.graphics.Color.parseColor(pinColorHex)),
                    radius = pinSizePx / 2f,
                    center = pinCenter
                )
                drawCircle(
                    color = Color.White,
                    radius = pinSizePx / 2f,
                    center = pinCenter,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Draw Icon/Emoji Symbol inside the pin circle
                val nativeCanvas = drawContext.canvas.nativeCanvas
                val iconPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = (14f * camera.zoom).coerceIn(10f, 22f)
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val fontMetrics = iconPaint.fontMetrics
                val textY = pinCenter.y - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
                nativeCanvas.drawText(iconSymbol, pinCenter.x, textY, iconPaint)
            }
        }
    }

    // ── Layer 5: Clean Selection Border & Grid Overlay (EDIT MODE) ──────

    private fun DrawScope.renderGridOverlay(camera: CameraState) {
        val gridColor = Color.White.copy(alpha = 0.35f)
        for (i in 0..45) {
            val p1 = IsometricProjection.toScreen(i.toFloat(), 0f, camera)
            val p2 = IsometricProjection.toScreen(i.toFloat(), 45f, camera)
            drawLine(gridColor, p1, p2, strokeWidth = 1f)

            val p3 = IsometricProjection.toScreen(0f, i.toFloat(), camera)
            val p4 = IsometricProjection.toScreen(45f, i.toFloat(), camera)
            drawLine(gridColor, p3, p4, strokeWidth = 1f)
        }
    }

    private fun DrawScope.renderSelectionHandles(
        plot: PlotRenderData,
        camera: CameraState,
        showHandles: Boolean = false
    ): HandlePositions {
        val sc = plot.screenCorners(camera)
        val strokeWidth = 3.dp.toPx()
        val pathColor = Color.White
        val handleStrokeColor = Color(0xFF1E88E5)

        val path = Path().apply {
            moveTo(sc.topLeft.x, sc.topLeft.y)
            lineTo(sc.topRight.x, sc.topRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y)
            lineTo(sc.bottomLeft.x, sc.bottomLeft.y)
            close()
        }

        drawPath(
            path = path,
            color = pathColor,
            style = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
            )
        )

        // Draw 8 Bounding Box Handle Control Points ONLY when Resize mode is active (MD 34 specification)
        if (showHandles) {
            val handleRadius = 6.dp.toPx()
            val handleBorderWidth = 2.dp.toPx()
            val handlePoints = listOf(
                sc.topLeft, sc.topCenter, sc.topRight,
                sc.leftMid, sc.rightMid,
                sc.bottomLeft, sc.bottomCenter, sc.bottomRight
            )

            for (pt in handlePoints) {
                drawCircle(
                    color = Color.White,
                    radius = handleRadius,
                    center = pt
                )
                drawCircle(
                    color = handleStrokeColor,
                    radius = handleRadius,
                    center = pt,
                    style = Stroke(width = handleBorderWidth)
                )
            }
        }

        return HandlePositions(
            dragHandle = plot.centerScreen(camera),
            cornerTL = sc.topLeft,
            cornerTR = sc.topRight,
            cornerBL = sc.bottomLeft,
            cornerBR = sc.bottomRight,
            midTop = sc.topCenter,
            midBottom = sc.bottomCenter,
            midLeft = sc.leftMid,
            midRight = sc.rightMid,
            actionBtn = plot.centerScreen(camera)
        )
    }
}
