package com.maptanim.app.renderer.canvas

import android.content.res.Resources
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.maptanim.app.domain.model.FarmObjectType
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.renderer.PlantInstanceGenerator
import com.maptanim.app.renderer.model.*
import com.maptanim.app.ui.components.isometric.world.terrain.TerrainPainter
import com.maptanim.app.ui.components.isometric.world.farm.SoilPainter


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
        isGridEnabled: Boolean = true,
        isSnapEnabled: Boolean = true,
        isResizeMode: Boolean = false,
        resources: Resources? = null,
        context: android.content.Context? = null,
        onHandlesReady: ((String, HandlePositions) -> Unit)? = null
    ) {
        // ── Layer 0: Full Viewport Grass Terrain & Soil Tiles ─────────────
        renderGround(camera, resources, context)

        // ── Layer 1: Outer Scenery (50 Trees, 20 Rocks, 30 Flowers) ──────
        renderFarmScenery(camera, context)

        // ── Layer 1.5: CoC-Style Hover Tile Highlight Preview ─────────────
        if (hoverWorldPos != null) {
            val selectedPlot = selectedPlotId?.let { selId -> plots.firstOrNull { it.id == selId } }
            val highlightW = selectedPlot?.widthM ?: 1.0f
            val highlightH = selectedPlot?.heightM ?: 1.0f
            renderTileHighlight(hoverWorldPos, camera, isValidPlacement, widthM = highlightW, heightM = highlightH)
        }

        // ── Layer 2: Direct Planted Crops & Soil Beds & Trellises ─────────
        val sortedPlots = plots.sortedBy { it.posX + it.posY }

        sortedPlots.forEach { plot ->
            renderSoilPlot(plot, camera, resources, context)
            renderDirectCrop(plot, cropZones, camera, context)

            val plotTrellises = farmObjects.filter { it.attachedPlotId == plot.id && it.objectType == FarmObjectType.TRELLIS }
            plotTrellises.forEach { trellis ->
                renderTrellis(trellis, camera, context)
            }
        }

        // ── Layer 3: Exterior Fences and Objects ──────────────────────────
        val exteriorObjects = farmObjects.filter { it.attachedPlotId == null || it.objectType != FarmObjectType.TRELLIS }
            .sortedBy { it.worldX + it.worldY }
        
        exteriorObjects.forEach { obj ->
            renderFarmObject(obj, camera, context)
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

        // ── Layer 5: Clean Selection Border & Grid (EDIT MODE) ────────────
        if (canvasMode == com.maptanim.app.domain.model.CanvasMode.EDIT) {
            if (isGridEnabled) renderGridOverlay(camera)

            selectedPlotId?.let { selId ->
                sortedPlots.firstOrNull { it.id == selId }?.let { selPlot ->
                    val handles = renderSelectionHandles(selPlot, camera, showHandles = isResizeMode)
                    onHandlesReady?.invoke(selId, handles)
                }
            }
        }
    }


    // ── Farm Area Constants ──────────────────────────────────────────────
    private const val FARM_MIN_X = 0f
    private const val FARM_MIN_Y = 0f
    private const val FARM_MAX_X = 30f
    private const val FARM_MAX_Y = 30f

    private data class Decor(
        val wx: Float, val wy: Float,
        val asset: String,
        val tileSpan: Float = 1f,
        val isTree: Boolean = false
    )

    /**
     * LANDSCAPED SCENERY WITH 3X MASSIVE BACK FOREST & CONTINUOUS ISOMETRIC GRASS:
     * - Back Area: 3x Bigger Trees (tileSpan 24.0f..30.0f) forming a dense forest wall hiding the back void.
     * - Ground Grass: Full isometric terrain grid (-25..50 world bounds) rendering under all back trees.
     * - Front Area: Clean vegetation, water lilies, flowers, rocks & bushes — zero floating grass tile textures.
     */
    private val SCENERY_DECORATIONS: List<Decor> by lazy {
        val decors = mutableListOf<Decor>()
        val treeAssets = listOf(
            "trees_and_rocks/mango_tree.png",
            "trees_and_rocks/coconut_tree.png",
            "trees_and_rocks/banana_tree.png"
        )
        val waterLilyAssets = listOf(
            "water_lily/big_water_lily.png",
            "water_lily/midium_water_lily.png",
            "water_lily/water_lily_pink.png",
            "water_lily/water_lily_white.png"
        )

        // 1. CLEAN BACKDROP CANOPY TREES (Only ~15 well-spaced trees hiding back & left grass edges, zero collapsing)
        // Back edge canopy row (wy = -5.5f)
        for (wx in -6..36 step 5) {
            val jitterX = ((wx * 37) % 5 - 2) * 0.2f
            val treeAsset = treeAssets[Math.abs((wx * 13).toInt()) % treeAssets.size]
            decors.add(Decor(wx.toFloat() + jitterX, -5.5f, treeAsset, tileSpan = 6.5f, isTree = true))
        }
        // Left edge canopy row (wx = -5.5f)
        for (wy in 0..32 step 5) {
            val jitterY = ((wy * 41) % 5 - 2) * 0.2f
            val treeAsset = treeAssets[Math.abs((wy * 17).toInt()) % treeAssets.size]
            decors.add(Decor(-5.5f, wy.toFloat() + jitterY, treeAsset, tileSpan = 6.5f, isTree = true))
        }

        // 2. NATURAL ORGANIC OBSTACLES (ROCKS, BUSHES, FLOWERS, WATER LILIES)
        // Positioned outside fence line (-1.8m to 31.8m boundary) with natural organic scatter
        val obstacleLocations = listOf(
            // Back/Left Forest Base Scatter (wy < -2f or wx < -2f)
            Decor(-4.0f, -3.5f, "trees_and_rocks/large_rock.png", tileSpan = 1.0f),
            Decor(2.5f, -3.8f, "trees_and_rocks/bush.png", tileSpan = 0.9f),
            Decor(8.0f, -4.2f, "trees_and_rocks/flower.png", tileSpan = 0.85f),
            Decor(15.2f, -3.6f, "trees_and_rocks/small_rock.png", tileSpan = 0.9f),
            Decor(22.0f, -4.0f, "trees_and_rocks/bush.png", tileSpan = 0.9f),
            Decor(28.5f, -3.5f, "trees_and_rocks/flower.png", tileSpan = 0.85f),
            Decor(33.0f, -3.8f, "trees_and_rocks/large_rock.png", tileSpan = 1.0f),

            // Left Outer Grass Scatter (wx < -3f)
            Decor(-3.8f, 4.0f, "trees_and_rocks/bush.png", tileSpan = 0.9f),
            Decor(-4.2f, 10.5f, "trees_and_rocks/small_rock.png", tileSpan = 0.9f),
            Decor(-3.6f, 17.0f, "trees_and_rocks/flower.png", tileSpan = 0.85f),
            Decor(-4.0f, 24.2f, "trees_and_rocks/bush.png", tileSpan = 0.9f),

            // Front/Right Open Landscape Scatter (wx > 33.5f or wy > 33.5f) - organically placed outside fence
            Decor(4.5f, 34.2f, waterLilyAssets[0], tileSpan = 1.1f),
            Decor(12.0f, 34.8f, "trees_and_rocks/small_rock.png", tileSpan = 0.9f),
            Decor(18.5f, 34.0f, "trees_and_rocks/flower.png", tileSpan = 0.85f),
            Decor(25.2f, 34.6f, "trees_and_rocks/bush.png", tileSpan = 0.9f),
            Decor(34.2f, 6.0f, waterLilyAssets[1], tileSpan = 1.1f),
            Decor(34.5f, 14.2f, "trees_and_rocks/large_rock.png", tileSpan = 1.0f),
            Decor(34.0f, 21.0f, "trees_and_rocks/bush.png", tileSpan = 0.9f),
            Decor(34.6f, 28.5f, waterLilyAssets[2], tileSpan = 1.1f),
            Decor(33.8f, 33.8f, "trees_and_rocks/flower.png", tileSpan = 0.85f)
        )
        decors.addAll(obstacleLocations)

        decors.sortedBy { it.wx + it.wy }
    }

    private data class FenceSegment(val wx: Float, val wy: Float, val asset: String)

    private val FENCE_SEGMENTS: List<FenceSegment> by lazy {
        val segments = mutableListOf<FenceSegment>()
        val fenceMinX = FARM_MIN_X - 1.8f
        val fenceMaxX = FARM_MAX_X + 1.8f
        val fenceMinY = FARM_MIN_Y - 1.8f
        val fenceMaxY = FARM_MAX_Y + 1.8f

        var x = fenceMinX
        while (x <= fenceMaxX) {
            segments.add(FenceSegment(x, fenceMinY, "fences/fences_left.png"))
            x += 1f
        }
        x = fenceMinX
        while (x <= fenceMaxX) {
            segments.add(FenceSegment(x, fenceMaxY, "fences/fences_left.png"))
            x += 1f
        }
        var y = fenceMinY
        while (y <= fenceMaxY) {
            segments.add(FenceSegment(fenceMinX, y, "fences/fences_right.png"))
            y += 1f
        }
        y = fenceMinY
        while (y <= fenceMaxY) {
            segments.add(FenceSegment(fenceMaxX, y, "fences/fences_right.png"))
            y += 1f
        }
        segments.add(FenceSegment(fenceMinX, fenceMinY, "fences/fence_post.png"))
        segments.add(FenceSegment(fenceMaxX, fenceMinY, "fences/fence_post.png"))
        segments.add(FenceSegment(fenceMinX, fenceMaxY, "fences/fence_post.png"))
        segments.add(FenceSegment(fenceMaxX, fenceMaxY, "fences/fence_post.png"))
        segments.sortedBy { it.wx + it.wy }
    }


    // ── Layer 0: Full Viewport Ground Terrain ────────────────────────────

    private fun DrawScope.renderGround(camera: CameraState, resources: Resources? = null, context: android.content.Context?) {
        // 1. Instant full-screen base grass background
        drawRect(color = Color(0xFF4A7C29))

        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val tileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1)
        val renderTileW = tileW + 4
        val renderTileH = tileH + 4

        // 2. Focused grass tile rendering strictly bounded to [-17, 34]
        val bounds = camera.getVisibleWorldBounds(size.width, size.height)
        val minX = maxOf(bounds.left.toInt() - 1, -17)
        val maxX = minOf(bounds.right.toInt() + 1, 34)
        val minY = maxOf(bounds.top.toInt() - 1, -17)
        val maxY = minOf(bounds.bottom.toInt() + 1, 34)

        if (resources != null) {
            for (wx in minX..maxX) {
                for (wy in minY..maxY) {
                    val pos = IsometricProjection.toScreen(wx.toFloat(), wy.toFloat(), camera)
                    val variant = (Math.abs(wx * 31 + wy * 17) % 5) + 1
                    val grassBitmap = TerrainPainter.getTexture(resources, variant, context)
                    drawImage(
                        image = grassBitmap,
                        dstOffset = IntOffset(Math.round(pos.x - tileW / 2f - 2f), Math.round(pos.y - 2f)),
                        dstSize = IntSize(renderTileW, renderTileH)
                    )
                }
            }
        }

        // 2. Central Soil Farm Grid (0 to 30m)
        if (resources != null) {
            val farmMinXi = FARM_MIN_X.toInt()
            val farmMaxXi = (FARM_MAX_X - 1).toInt()
            val farmMinYi = FARM_MIN_Y.toInt()
            val farmMaxYi = (FARM_MAX_Y - 1).toInt()

            val startX = maxOf(minX, farmMinXi)
            val endX   = minOf(maxX, farmMaxXi)
            val startY = maxOf(minY, farmMinYi)
            val endY   = minOf(maxY, farmMaxYi)

            for (wx in startX..endX) {
                for (wy in startY..endY) {
                    val pos = IsometricProjection.toScreen(wx.toFloat(), wy.toFloat(), camera)
                    val soilVariant = if ((wx + wy) % 2 == 0) 1 else 2
                    val soilBitmap = SoilPainter.getTexture(resources, soilVariant, context)
                    drawImage(
                        image = soilBitmap,
                        dstOffset = IntOffset(Math.round(pos.x - tileW / 2f - 2f), Math.round(pos.y - 2f)),
                        dstSize = IntSize(renderTileW, renderTileH)
                    )
                }
            }
        }
    }

    private fun DrawScope.renderFarmScenery(camera: CameraState, context: android.content.Context?) {
        if (context == null) return

        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val tileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1)
        val canvasW = size.width
        val canvasH = size.height

        // 1. Render Fences with Viewport Frustum Culling (Zero lag at high zoom)
        FENCE_SEGMENTS.forEach { fence ->
            val pos = IsometricProjection.toScreen(fence.wx, fence.wy, camera)
            val targetW = tileW + 4
            val left = pos.x - tileW / 2f - 2f

            // Fast Cull Check (Skip off-screen fences outside viewport + 60px margin)
            if (left > canvasW + 60f || left + targetW < -60f || pos.y < -100f || pos.y > canvasH + 100f) {
                return@forEach
            }

            val bitmap = AssetLoader.loadFromAssets(context, fence.asset)
            if (bitmap != null) {
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        Math.round(left),
                        Math.round(pos.y - (targetH - tileH / 2f - 2f))
                    ),
                    dstSize = IntSize(targetW, targetH)
                )
            }
        }

        // 2. Render Scenery Objects (Trees, Rocks, Flowers, Bushes) with Viewport Frustum Culling
        SCENERY_DECORATIONS.forEach { decor ->
            val pos = IsometricProjection.toScreen(decor.wx, decor.wy, camera)
            val targetW = ((tileW + 4) * decor.tileSpan).toInt().coerceAtLeast(1)
            val left = pos.x - targetW / 2f

            // Fast Cull Check (Skip off-screen items; 200px top margin for massive trees)
            if (left > canvasW + 150f || left + targetW < -150f || pos.y < -250f || pos.y > canvasH + 150f) {
                return@forEach
            }

            val bitmap = AssetLoader.loadFromAssets(context, decor.asset)
            if (bitmap != null) {
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                if (decor.isTree) {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(left),
                            Math.round(pos.y + tileH / 2f - targetH)
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                } else {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(left),
                            Math.round(pos.y - (targetH - tileH / 2f))
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                }
            }
        }
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
        val progressStr = if (plot.isMonitoringStarted) {
            val pct = (plot.stageProgressRatio * 100).toInt()
            " • Day ${plot.daysPlanted}/${plot.daysToHarvest} [$pct%]"
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

    // ── Layer 2: Direct Planted Crops & Soil Plot Beds ───────────────────

    private fun DrawScope.renderSoilPlot(
        plot: PlotRenderData,
        camera: CameraState,
        resources: Resources?,
        context: android.content.Context?
    ) {
        if (context == null) return
        val res = resources ?: context.resources
        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val renderTileW = tileW + 4
        val renderTileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1) + 4

        var y = 0f
        while (y < plot.heightM) {
            var x = 0f
            while (x < plot.widthM) {
                val tileWorldX = plot.posX + x
                val tileWorldY = plot.posY + y
                val pos = IsometricProjection.toScreen(tileWorldX, tileWorldY, camera)

                val soilVariant = if ((tileWorldX.toInt() + tileWorldY.toInt()) % 2 == 0) 1 else 2
                val soilBitmap = SoilPainter.getTexture(res, soilVariant, context)

                drawImage(
                    image = soilBitmap,
                    dstOffset = IntOffset(
                        Math.round(pos.x - tileW / 2f - 2f),
                        Math.round(pos.y - 2f)
                    ),
                    dstSize = IntSize(renderTileW, renderTileH)
                )
                x += 1.0f
            }
            y += 1.0f
        }
    }

    private fun DrawScope.renderDirectCrop(
        plot: PlotRenderData,
        cropZones: List<CropZoneRenderData>,
        camera: CameraState,
        context: android.content.Context?
    ) {
        if (context == null) return

        val zone = cropZones.firstOrNull { it.plotId == plot.id }
        val plantsToRender = if (zone != null && zone.plantInstances.isNotEmpty()) {
            zone.plantInstances
        } else {
            val tempZone = CropZoneRenderData(
                id = "temp-${plot.id}",
                plotId = plot.id,
                cropName = plot.cropName ?: "Carrot",
                offsetX = 0f,
                offsetY = 0f,
                widthM = plot.widthM,
                heightM = plot.heightM,
                spacingM = 1.0f
            )
            PlantInstanceGenerator.generate(tempZone, plot.posX, plot.posY)
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
                val targetW = (baseTileW * 1.0f * plant.scaleFactor).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        Math.round(pos.x - targetW / 2f),
                        Math.round(pos.y - targetH + (baseTileW * 0.25f))
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

    private fun DrawScope.renderTrellis(
        trellis: FarmObjectRenderData,
        camera: CameraState,
        context: android.content.Context?
    ) {
        if (context == null) return
        val bitmap = AssetLoader.loadFromAssets(context, "trellis/bamboo_trellis_a.png") ?: return

        val centerPos = IsometricProjection.toScreen(trellis.worldX + trellis.widthM / 2f, trellis.worldY + trellis.heightM / 2f, camera)
        val baseTileW = IsometricProjection.TILE_W * camera.zoom
        val targetW = (baseTileW * 2.2f).toInt().coerceAtLeast(1)
        val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

        drawImage(
            image = bitmap,
            dstOffset = IntOffset(
                Math.round(centerPos.x - targetW / 2f),
                Math.round(centerPos.y - targetH * 0.85f)
            ),
            dstSize = IntSize(targetW, targetH)
        )
    }

    private fun DrawScope.renderFarmObject(
        obj: FarmObjectRenderData,
        camera: CameraState,
        context: android.content.Context?
    ) {
        if (context == null) return
        val assetPath = when (obj.objectType) {
            FarmObjectType.TRELLIS -> "trellis/bamboo_trellis_a.png"
            FarmObjectType.FENCE_SEGMENT -> "fences/fences_left.png"
            else -> "trees_and_rocks/mango_tree.png"
        }
        val bitmap = AssetLoader.loadFromAssets(context, assetPath) ?: return
        val pos = IsometricProjection.toScreen(obj.worldX, obj.worldY, camera)
        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val targetW = (tileW * obj.widthM).toInt().coerceAtLeast(1)
        val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

        drawImage(
            image = bitmap,
            dstOffset = IntOffset(
                Math.round(pos.x - targetW / 2f),
                Math.round(pos.y - targetH)
            ),
            dstSize = IntSize(targetW, targetH)
        )
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
        val gridColor = Color.White.copy(alpha = 0.12f)
        for (i in 0..30) {
            val p1 = IsometricProjection.toScreen(i.toFloat(), 0f, camera)
            val p2 = IsometricProjection.toScreen(i.toFloat(), 30f, camera)
            drawLine(gridColor, p1, p2, strokeWidth = 1f)

            val p3 = IsometricProjection.toScreen(0f, i.toFloat(), camera)
            val p4 = IsometricProjection.toScreen(30f, i.toFloat(), camera)
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
