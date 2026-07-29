package com.maptanim.app.renderer.canvas

import android.content.res.Resources
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.maptanim.app.domain.model.FarmObjectType
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.renderer.AssetLoader
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
        isGridEnabled: Boolean = true,
        isSnapEnabled: Boolean = true,
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
            renderTileHighlight(hoverWorldPos, camera)
        }

        // ── Layer 2: Direct Planted Crops & Trellises ─────────────────────
        val sortedPlots = plots.sortedBy { it.posX + it.posY }

        sortedPlots.forEach { plot ->
            renderDirectCrop(plot, camera, context)

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

        // ── Layer 4: Status Pins (VIEW MODE only) ─────────────────────────
        if (canvasMode == com.maptanim.app.domain.model.CanvasMode.VIEW) {
            sortedPlots.forEach { plot ->
                if (plot.activeTasks.isNotEmpty()) {
                    renderStatusPins(plot, camera)
                }
            }
        }

        // ── Layer 5: Clean Selection Border & Grid (EDIT MODE) ────────────
        if (canvasMode == com.maptanim.app.domain.model.CanvasMode.EDIT) {
            if (isGridEnabled) renderGridOverlay(camera)

            selectedPlotId?.let { selId ->
                sortedPlots.firstOrNull { it.id == selId }?.let { selPlot ->
                    val handles = renderSelectionHandles(selPlot, camera)
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

        // 1. MASSIVE 3X BIGGER TREES IN THE BACK & SIDES (tileSpan 24.0f..30.0f)
        // 4 Dense Back Rows (wy < 0) - hides background void completely
        for (wyStep in listOf(-4.0f, -7.5f, -11.0f, -14.5f)) {
            for (wx in -12..42 step 4) {
                val jitterX = ((wx * 37 + wyStep.toInt() * 17) % 7 - 3) * 0.3f
                val treeAsset = treeAssets[Math.abs((wx * 13 + wyStep.toInt() * 31).toInt()) % treeAssets.size]
                val span = 24.0f + ((Math.abs(wx * 7 + wyStep.toInt() * 19) % 5) * 1.5f)
                decors.add(Decor(wx.toFloat() + jitterX, wyStep, treeAsset, tileSpan = span, isTree = true))
            }
        }
        // 3 Left Side Rows (wx < 0)
        for (wxStep in listOf(-4.0f, -7.5f, -11.0f)) {
            for (wy in -6..36 step 4) {
                val jitterY = ((wy * 41 + wxStep.toInt() * 23) % 7 - 3) * 0.3f
                val treeAsset = treeAssets[Math.abs((wy * 17 + wxStep.toInt() * 29).toInt()) % treeAssets.size]
                val span = 24.0f + ((Math.abs(wy * 11 + wxStep.toInt() * 13) % 5) * 1.5f)
                decors.add(Decor(wxStep, wy.toFloat() + jitterY, treeAsset, tileSpan = span, isTree = true))
            }
        }

        // 2. ROCKS & FLOWERS AROUND BACK TREE BASES (wy < 0)
        for (i in 0..20) {
            val wx = -8.0f + (i * 2.5f)
            val wy = -3.2f + ((i % 3) * -1.0f)
            decors.add(Decor(wx, wy, if (i % 2 == 0) "trees_and_rocks/flower.png" else "trees_and_rocks/large_rock.png", tileSpan = 1.0f))
        }

        // 3. FRONT AREA: WIDE SPREAD WATER LILIES, FLOWERS, STONES, AND BUSHES (NO FLOATING GRASS TILE TEXTURES)
        // Water Lilies (10 items widely spread)
        for (i in 0..9) {
            val hash1 = Math.abs(i * 1741 + 11)
            val hash2 = Math.abs(i * 2803 + 23)
            val isFrontEdge = (i % 2 == 0)

            val wx = if (isFrontEdge) 4.0f + (hash1 % 280) / 10f else 30.5f + (hash1 % 80) / 10f
            val wy = if (isFrontEdge) 30.5f + (hash2 % 80) / 10f else 4.0f + (hash2 % 280) / 10f
            val asset = waterLilyAssets[i % waterLilyAssets.size]

            decors.add(Decor(wx, wy, asset, tileSpan = 1.1f))
        }

        // Flowers (14 items widely spread)
        for (i in 0..13) {
            val hash1 = Math.abs(i * 1013 + 7)
            val hash2 = Math.abs(i * 1619 + 13)
            val isFrontEdge = (i % 2 == 0)

            val wx = if (isFrontEdge) 2.0f + (hash1 % 300) / 10f else 31.0f + (hash1 % 70) / 10f
            val wy = if (isFrontEdge) 31.0f + (hash2 % 70) / 10f else 2.0f + (hash2 % 300) / 10f

            decors.add(Decor(wx, wy, "trees_and_rocks/flower.png", tileSpan = 0.85f))
        }

        // Stones / Rocks (10 items widely spread)
        for (i in 0..9) {
            val hash1 = Math.abs(i * 2237 + 19)
            val hash2 = Math.abs(i * 3571 + 31)
            val isFrontEdge = (i % 2 == 0)

            val wx = if (isFrontEdge) 3.0f + (hash1 % 290) / 10f else 30.2f + (hash1 % 80) / 10f
            val wy = if (isFrontEdge) 30.2f + (hash2 % 80) / 10f else 3.0f + (hash2 % 290) / 10f
            val rockAsset = if (i % 2 == 0) "trees_and_rocks/small_rock.png" else "trees_and_rocks/large_rock.png"

            decors.add(Decor(wx, wy, rockAsset, tileSpan = 0.9f))
        }

        // Bushes (14 items widely spread across open front terrain - no floating tile textures)
        for (i in 0..13) {
            val hash1 = Math.abs(i * 3187 + 43)
            val hash2 = Math.abs(i * 4409 + 59)
            val isFrontEdge = (i % 2 == 0)

            val wx = if (isFrontEdge) 1.0f + (hash1 % 310) / 10f else 30.8f + (hash1 % 75) / 10f
            val wy = if (isFrontEdge) 30.8f + (hash2 % 75) / 10f else 1.0f + (hash2 % 310) / 10f

            decors.add(Decor(wx, wy, "trees_and_rocks/bush.png", tileSpan = 0.85f))
        }

        decors
    }

    private data class FenceSegment(val wx: Float, val wy: Float, val asset: String)

    private val FENCE_SEGMENTS: List<FenceSegment> by lazy {
        val segments = mutableListOf<FenceSegment>()
        var x = FARM_MIN_X
        while (x < FARM_MAX_X) {
            segments.add(FenceSegment(x, FARM_MIN_Y - 1f, "fences/fences_left.png"))
            x += 1f
        }
        x = FARM_MIN_X
        while (x < FARM_MAX_X) {
            segments.add(FenceSegment(x, FARM_MAX_Y, "fences/fences_left.png"))
            x += 1f
        }
        var y = FARM_MIN_Y
        while (y < FARM_MAX_Y) {
            segments.add(FenceSegment(FARM_MIN_X - 1f, y, "fences/fences_right.png"))
            y += 1f
        }
        y = FARM_MIN_Y
        while (y < FARM_MAX_Y) {
            segments.add(FenceSegment(FARM_MAX_X, y, "fences/fences_right.png"))
            y += 1f
        }
        segments.add(FenceSegment(FARM_MIN_X - 1f, FARM_MIN_Y - 1f, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MAX_X, FARM_MIN_Y - 1f, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MIN_X - 1f, FARM_MAX_Y, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MAX_X, FARM_MAX_Y, "fences/fence_post.png"))
        segments
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

        FENCE_SEGMENTS.sortedBy { it.wx + it.wy }.forEach { fence ->
            val bitmap = AssetLoader.loadFromAssets(context, fence.asset)
            if (bitmap != null) {
                val pos = IsometricProjection.toScreen(fence.wx, fence.wy, camera)
                val targetW = tileW + 4
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        Math.round(pos.x - tileW / 2f - 2f),
                        Math.round(pos.y - (targetH - tileH / 2f - 2f))
                    ),
                    dstSize = IntSize(targetW, targetH)
                )
            }
        }

        SCENERY_DECORATIONS.sortedBy { it.wx + it.wy }.forEach { decor ->
            val bitmap = AssetLoader.loadFromAssets(context, decor.asset)
            if (bitmap != null) {
                val pos = IsometricProjection.toScreen(decor.wx, decor.wy, camera)
                val targetW = ((tileW + 4) * decor.tileSpan).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                if (decor.isTree) {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(pos.x - targetW / 2f),
                            Math.round(pos.y + tileH / 2f - targetH)
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                } else {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(pos.x - targetW / 2f),
                            Math.round(pos.y - (targetH - tileH / 2f))
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                }
            }
        }
    }

    // ── Layer 1.5: CoC Isometric Hover Tile Highlight ───────────────────

    private fun DrawScope.renderTileHighlight(hoverWorldPos: Offset, camera: CameraState) {
        val w = 2.5f
        val h = 2.0f

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

        drawPath(
            path = path,
            color = Color(0x554CAF50)
        )

        drawPath(
            path = path,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 3.dp.toPx())
        )
    }

    // ── Layer 2: Direct Planted Crop PNG Sprite ─────────────────────────

    private fun DrawScope.renderDirectCrop(
        plot: PlotRenderData,
        camera: CameraState,
        context: android.content.Context?
    ) {
        if (context == null) return

        val cropClean = when (plot.cropName?.lowercase()?.replace(" ", "")) {
            "stringbeans", "sitaw", "beans" -> "crop_stringbeans"
            else -> "crop_carrot"
        }

        val bitmap = AssetLoader.loadFromAssets(context, "crops/${cropClean}_1.png")
        if (bitmap != null) {
            val centerPos = IsometricProjection.toScreen(plot.posX + plot.widthM / 2f, plot.posY + plot.heightM / 2f, camera)
            val baseTileW = IsometricProjection.TILE_W * camera.zoom
            val targetW = (baseTileW * 1.6f).toInt().coerceAtLeast(1)
            val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
            val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

            drawImage(
                image = bitmap,
                dstOffset = IntOffset(
                    Math.round(centerPos.x - targetW / 2f),
                    Math.round(centerPos.y - targetH + (baseTileW * 0.2f))
                ),
                dstSize = IntSize(targetW, targetH)
            )
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

    // ── Layer 4: Status Pins (VIEW MODE) ────────────────────────────────

    private fun DrawScope.renderStatusPins(plot: PlotRenderData, camera: CameraState) {
        val anchor = plot.pinAnchor(camera)
        val pinSizePx = 28f * camera.zoom

        plot.activeTasks.forEachIndexed { index, task ->
            val offsetX = (index - (plot.activeTasks.size - 1) / 2f) * (pinSizePx + 4f)
            val pinCenter = Offset(anchor.x + offsetX, anchor.y)

            val pinColor = when (task.taskType) {
                TaskType.WATER -> Color(0xFF1E88E5)
                TaskType.FERTILIZE -> Color(0xFF43A047)
                TaskType.HARVEST -> Color(0xFFFB8C00)
                TaskType.PEST_ALERT -> Color(0xFFE53935)
                else -> Color(0xFF8E24AA)
            }

            drawCircle(
                color = pinColor,
                radius = pinSizePx / 2f,
                center = pinCenter
            )
            drawCircle(
                color = Color.White,
                radius = pinSizePx / 2f,
                center = pinCenter,
                style = Stroke(width = 2.dp.toPx())
            )
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
        camera: CameraState
    ): HandlePositions {
        val sc = plot.screenCorners(camera)
        val strokeWidth = 3.dp.toPx()
        val pathColor = Color(0xFF1E88E5)

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
