package com.maptanim.app.renderer.canvas

import android.content.res.Resources
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
 *
 * Called from FarmCanvas Composable inside a Canvas { } block.
 * Render order (bottom to top):
 *   0. Ground tiles (grass)
 *   1. Path tiles (dirt walkways)
 *   2. Bed frames (wooden borders)
 *   3. Soil fill (texture per bed.soil_type from Room)
 *   4. Crop sprites (loaded from Supabase Storage by crop_name + growth_stage)
 *   5. Bed label chips (bed_label + crop_name from Room)
 *   6a. Status pins (tasks from Room — VIEW MODE only)
 *   6b. Selection handles (EDIT MODE only, when selectedBedId != null)
 *   6c. Grid overlay (EDIT MODE only, when isGridEnabled)
 *
 * NO layer uses hardcoded bed data, crop names, or task lists.
 * All data flows from: Supabase → Room → BedRepository → ViewModel → here.
 */
object FarmCanvasRenderer {

    /**
     * Main render pass. Called every frame from Canvas { } recomposition.
     *
     * @param beds         Live bed data from Room (BedRenderData list)
     * @param cropZones    Crop zones within beds with generated plant instances
     * @param farmObjects  Exterior structures (trellises, fences, trees, decorations)
     * @param camera       Current camera pan/zoom state
     * @param canvasMode   VIEW or EDIT (drives which layers are shown)
     * @param selectedBedId  Currently selected bed ID (EDIT MODE only)
     * @param isGridEnabled  Drives grid overlay visibility
     * @param isSnapEnabled  State stored in EditViewModel (not Room)
     * @param onHandlesReady Callback reporting handle screen positions to gesture handler
     */
    fun DrawScope.render(
        beds: List<BedRenderData>,
        cropZones: List<CropZoneRenderData> = emptyList(),
        farmObjects: List<FarmObjectRenderData> = emptyList(),
        camera: CameraState,
        canvasMode: com.maptanim.app.domain.model.CanvasMode,
        selectedBedId: String? = null,
        selectedZoneId: String? = null,
        isGridEnabled: Boolean = true,
        isSnapEnabled: Boolean = true,
        resources: Resources? = null,
        context: android.content.Context? = null,
        onHandlesReady: ((String, HandlePositions) -> Unit)? = null
    ) {
        // ── Layer 0: Ground soil tiles ────────────────────────────────────
        renderGround(camera, resources, context)

        // ── Layer 1: Farm scenery (fences, trees, rocks, bushes, flowers) ─
        renderFarmScenery(camera, context)

        // ── Layer 2: Direct Planted Crops & Trellises ─────────────────────
        val sortedBeds = beds.sortedBy { it.posX + it.posY }

        sortedBeds.forEach { bed ->
            // Render crop zones & PNG plant instances belonging to this plot directly on soil
            val bedZones = cropZones.filter { it.bedId == bed.id }
            bedZones.forEach { zone ->
                renderCropZone(zone, bed, camera, isSelected = zone.id == selectedZoneId, context = context)
            }

            renderBedLabel(bed, camera)

            // Render trellises attached to climbing crops
            val bedTrellises = farmObjects.filter { it.attachedBedId == bed.id && it.objectType == FarmObjectType.TRELLIS }
            bedTrellises.forEach { trellis ->
                renderTrellis(trellis, camera, context)
            }
        }

        // ── Layer 3: Exterior fences, trees, and decorations ──────────────
        val exteriorObjects = farmObjects.filter { it.attachedBedId == null || it.objectType != FarmObjectType.TRELLIS }
            .sortedBy { it.worldX + it.worldY }
        
        exteriorObjects.forEach { obj ->
            renderFarmObject(obj, camera, context)
        }

        // ── Layer 4: Status pins (VIEW MODE only) ─────────────────────────
        if (canvasMode == com.maptanim.app.domain.model.CanvasMode.VIEW) {
            sortedBeds.forEach { bed ->
                if (bed.activeTasks.isNotEmpty()) {
                    renderStatusPins(bed, camera)
                }
            }
        }

        // ── Layer 5: Selection handles & Grid (EDIT MODE) ───────────────
        if (canvasMode == com.maptanim.app.domain.model.CanvasMode.EDIT) {
            if (isGridEnabled) renderGridOverlay(camera)

            selectedBedId?.let { selId ->
                sortedBeds.firstOrNull { it.id == selId }?.let { selBed ->
                    val handles = renderSelectionHandles(selBed, camera)
                    onHandlesReady?.invoke(selId, handles)
                }
            }
        }
    }


    // ── Farm Area Constants ──────────────────────────────────────────────
    // Large farm soil planting region (30m x 30m = 900 sq meters)
    private const val FARM_MIN_X = 0f
    private const val FARM_MIN_Y = 0f
    private const val FARM_MAX_X = 30f
    private const val FARM_MAX_Y = 30f

    /**
     * Decoration item placed on the grass border around the farm.
     * tileSpan = how many tile widths the asset should cover (1 = small item, 2 = tree).
     * isTree = true for tall assets (anchored at bottom, extends upward).
     */
    private data class Decor(
        val wx: Float, val wy: Float,
        val asset: String,
        val tileSpan: Float = 1f,
        val isTree: Boolean = false
    )

    private val SCENERY_DECORATIONS: List<Decor> by lazy {
        val decors = mutableListOf<Decor>()
        val treeAssets = listOf(
            "trees_and_rocks/mango_tree.png",
            "trees_and_rocks/coconut_tree.png",
            "trees_and_rocks/banana_tree.png"
        )
        val rockAssets = listOf(
            "trees_and_rocks/large_rock.png",
            "trees_and_rocks/small_rock.png"
        )

        // ── Top & Bottom outer borders ──
        for (i in -4..34 step 4) {
            val treeTop = treeAssets[Math.abs(i * 7) % treeAssets.size]
            val treeBot = treeAssets[Math.abs(i * 13) % treeAssets.size]

            decors.add(Decor(i.toFloat(), -3f, treeTop, tileSpan = 3.5f, isTree = true))
            decors.add(Decor((i + 1).toFloat(), -2f, "trees_and_rocks/bush.png"))
            decors.add(Decor((i + 2).toFloat(), -2f, if (i % 2 == 0) "trees_and_rocks/flower.png" else "trees_and_rocks/small_rock.png"))

            decors.add(Decor(i.toFloat(), 32f, treeBot, tileSpan = 3.5f, isTree = true))
            decors.add(Decor((i + 1).toFloat(), 31f, "trees_and_rocks/bush.png"))
            decors.add(Decor((i + 2).toFloat(), 31f, if (i % 2 == 0) "trees_and_rocks/large_rock.png" else "trees_and_rocks/flower.png"))
        }

        // ── Left & Right outer borders ──
        for (j in 0..30 step 4) {
            val treeLeft = treeAssets[Math.abs(j * 11) % treeAssets.size]
            val treeRight = treeAssets[Math.abs(j * 17) % treeAssets.size]

            decors.add(Decor(-3f, j.toFloat(), treeLeft, tileSpan = 3.5f, isTree = true))
            decors.add(Decor(-2f, (j + 1).toFloat(), "trees_and_rocks/flower.png"))
            decors.add(Decor(-2f, (j + 2).toFloat(), "trees_and_rocks/bush.png"))

            decors.add(Decor(32f, j.toFloat(), treeRight, tileSpan = 3.5f, isTree = true))
            decors.add(Decor(31f, (j + 1).toFloat(), "trees_and_rocks/small_rock.png"))
            decors.add(Decor(31f, (j + 2).toFloat(), "trees_and_rocks/flower.png"))
        }

        decors
    }

    // Fence segments along the farm perimeter (world coords)
    // Each fence asset includes its own grass-tile base and spans 1 tile.
    private data class FenceSegment(val wx: Float, val wy: Float, val asset: String)

    private val FENCE_SEGMENTS: List<FenceSegment> by lazy {
        val segments = mutableListOf<FenceSegment>()
        // Top edge fences (left-facing): Y = FARM_MIN_Y, step every 1 tile
        var x = FARM_MIN_X
        while (x < FARM_MAX_X) {
            segments.add(FenceSegment(x, FARM_MIN_Y - 1f, "fences/fences_left.png"))
            x += 1f
        }
        // Bottom edge fences (left-facing): Y = FARM_MAX_Y
        x = FARM_MIN_X
        while (x < FARM_MAX_X) {
            segments.add(FenceSegment(x, FARM_MAX_Y, "fences/fences_left.png"))
            x += 1f
        }
        // Left edge fences (right-facing): X = FARM_MIN_X
        var y = FARM_MIN_Y
        while (y < FARM_MAX_Y) {
            segments.add(FenceSegment(FARM_MIN_X - 1f, y, "fences/fences_right.png"))
            y += 1f
        }
        // Right edge fences (right-facing): X = FARM_MAX_X
        y = FARM_MIN_Y
        while (y < FARM_MAX_Y) {
            segments.add(FenceSegment(FARM_MAX_X, y, "fences/fences_right.png"))
            y += 1f
        }
        // Corner posts
        segments.add(FenceSegment(FARM_MIN_X - 1f, FARM_MIN_Y - 1f, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MAX_X, FARM_MIN_Y - 1f, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MIN_X - 1f, FARM_MAX_Y, "fences/fence_post.png"))
        segments.add(FenceSegment(FARM_MAX_X, FARM_MAX_Y, "fences/fence_post.png"))
        segments
    }


    // ── Layer 0: Ground ─────────────────────────────────────────────────

    private fun DrawScope.renderGround(camera: CameraState, resources: Resources? = null, context: android.content.Context? = null) {
        // Solid green base fill — always visible as the deepest background
        drawRect(color = Color(0xFF388E3C))

        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val tileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1)
        // 4px overlap to fully eliminate subpixel seam gaps between adjacent isometric tiles
        val renderTileW = tileW + 4
        val renderTileH = tileH + 4

        val bounds = camera.getVisibleWorldBounds(size.width, size.height)
        val minX = bounds.left.toInt()
        val maxX = bounds.right.toInt()
        val minY = bounds.top.toInt()
        val maxY = bounds.bottom.toInt()

        // ── Pass 1: Grass tiles EVERYWHERE across full visible canvas (infinite tiling) ──
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

        // ── Pass 2: Soil tiles INSIDE the farm boundary (30x30 area) ──
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
                    // Alternate between 2 matching soil variants for a clean, uniform farm floor
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

    // ── Layer 1: Paths ──────────────────────────────────────────────────

    private fun DrawScope.renderPaths(beds: List<BedRenderData>, camera: CameraState, context: android.content.Context? = null) {
        if (context == null) return

        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val tileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1)
        val renderTileW = tileW + 4
        val renderTileH = tileH + 4

        // Draw walkway path tiles adjacent to each bed (left and bottom side)
        beds.forEach { bed ->
            // Path tile on the left of the bed
            val leftPathX = bed.posX - 1f
            if (leftPathX >= FARM_MIN_X - 1f) {
                val pathBitmap = com.maptanim.app.renderer.AssetLoader.loadFromAssets(context, "tiles/path_straight_v.png")
                if (pathBitmap != null) {
                    for (dy in 0 until bed.heightM.toInt()) {
                        val pathPos = IsometricProjection.toScreen(leftPathX, bed.posY + dy.toFloat(), camera)
                        drawImage(
                            image = pathBitmap,
                            dstOffset = IntOffset(Math.round(pathPos.x - tileW / 2f - 2f), Math.round(pathPos.y - 2f)),
                            dstSize = IntSize(renderTileW, renderTileH)
                        )
                    }
                }
            }
            // Path tile below the bed
            val bottomPathY = bed.posY + bed.heightM
            if (bottomPathY <= FARM_MAX_Y) {
                val pathBitmap = com.maptanim.app.renderer.AssetLoader.loadFromAssets(context, "tiles/path_straight_h.png")
                if (pathBitmap != null) {
                    for (dx in 0 until bed.widthM.toInt()) {
                        val pathPos = IsometricProjection.toScreen(bed.posX + dx.toFloat(), bottomPathY, camera)
                        drawImage(
                            image = pathBitmap,
                            dstOffset = IntOffset(Math.round(pathPos.x - tileW / 2f - 2f), Math.round(pathPos.y - 2f)),
                            dstSize = IntSize(renderTileW, renderTileH)
                        )
                    }
                }
            }
        }
    }

    // ── Layer 1.5: Farm Scenery (Fences + Decorations) ──────────────────

    private fun DrawScope.renderFarmScenery(camera: CameraState, context: android.content.Context?) {
        if (context == null) return

        val tileW = (IsometricProjection.TILE_W * camera.zoom).toInt().coerceAtLeast(1)
        val tileH = (IsometricProjection.TILE_H * camera.zoom).toInt().coerceAtLeast(1)

        // ── Fence perimeter ──
        // Each fence asset has a built-in grass-tile base. Render at 1-tile size
        // so the base blends seamlessly with the grass tiles underneath.
        FENCE_SEGMENTS.sortedBy { it.wx + it.wy }.forEach { fence ->
            val bitmap = com.maptanim.app.renderer.AssetLoader.loadFromAssets(context, fence.asset)
            if (bitmap != null) {
                val pos = IsometricProjection.toScreen(fence.wx, fence.wy, camera)
                // Scale to fit 1 tile width; maintain aspect ratio
                val targetW = tileW + 4
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        Math.round(pos.x - tileW / 2f - 2f),
                        // Anchor so the bottom of the grass base aligns with tile position
                        Math.round(pos.y - (targetH - tileH / 2f - 2f))
                    ),
                    dstSize = IntSize(targetW, targetH)
                )
            }
        }

        // ── Scenery decorations (trees, rocks, bushes, flowers) ──
        // Each asset includes its own isometric grass-tile base.
        // Render them AT tile size so the base seamlessly replaces the grass tile.
        SCENERY_DECORATIONS.sortedBy { it.wx + it.wy }.forEach { decor ->
            val bitmap = com.maptanim.app.renderer.AssetLoader.loadFromAssets(context, decor.asset)
            if (bitmap != null) {
                val pos = IsometricProjection.toScreen(decor.wx, decor.wy, camera)
                // Scale to cover tileSpan tiles in width; maintain aspect ratio
                val targetW = ((tileW + 4) * decor.tileSpan).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                if (decor.isTree) {
                    // Trees: anchor at bottom-center of the grass base, tree extends upward
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(pos.x - targetW / 2f),
                            Math.round(pos.y + tileH / 2f - targetH)
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                } else {
                    // Small items (rocks, bushes, flowers): render like a tile
                    // The grass base in the asset replaces the grass tile at this position
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            Math.round(pos.x - targetW / 2f),
                            // Align bottom of asset's grass base with tile position
                            Math.round(pos.y - (targetH - tileH / 2f))
                        ),
                        dstSize = IntSize(targetW, targetH)
                    )
                }
            }
        }
    }

    // ── Layer 2: Bed Frame ──────────────────────────────────────────────

    private fun DrawScope.renderBedFrame(bed: BedRenderData, camera: CameraState, context: android.content.Context? = null) {
        val sc = bed.screenCorners(camera)
        val zoom = camera.zoom

        // Front-left face (shaded — sun from top-left)
        val leftFace = Path().apply {
            moveTo(sc.bottomLeft.x, sc.bottomLeft.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y + BED_HEIGHT_PX * camera.zoom)
            lineTo(sc.bottomLeft.x, sc.bottomLeft.y + BED_HEIGHT_PX * camera.zoom)
            close()
        }
        drawPath(leftFace, Color(0xFF5D4037))   // Dark brown left face

        // Front-right face (darkest — far shadow side)
        val rightFace = Path().apply {
            moveTo(sc.topRight.x, sc.topRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y + BED_HEIGHT_PX * camera.zoom)
            lineTo(sc.topRight.x, sc.topRight.y + BED_HEIGHT_PX * camera.zoom)
            close()
        }
        drawPath(rightFace, Color(0xFF3E2723))   // Darkest brown right face

        // Top rim (lightest — directly lit)
        val topFace = Path().apply {
            moveTo(sc.topLeft.x, sc.topLeft.y)
            lineTo(sc.topRight.x, sc.topRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y)
            lineTo(sc.bottomLeft.x, sc.bottomLeft.y)
            close()
        }
        drawPath(topFace, Color(0xFF8D6E63))     // Light brown rim top

        // Render bed corner post assets if available
        if (context != null) {
            val postWidth = (24.dp.toPx() * zoom).coerceAtLeast(1f).toInt()
            val postHeight = (32.dp.toPx() * zoom).coerceAtLeast(1f).toInt()

            val topPost = AssetLoader.loadFromAssets(context, "bed/top_bed_post.png")
            val leftPost = AssetLoader.loadFromAssets(context, "bed/left_bed_post.png")
            val rightPost = AssetLoader.loadFromAssets(context, "bed/right_bed_post.png")
            val frontPost = AssetLoader.loadFromAssets(context, "bed/front_bed_post.png")

            topPost?.let { img ->
                drawImage(
                    image = img,
                    dstOffset = IntOffset((sc.topLeft.x - postWidth / 2f).toInt(), (sc.topLeft.y - postHeight * 0.7f).toInt()),
                    dstSize = IntSize(postWidth, postHeight)
                )
            }
            leftPost?.let { img ->
                drawImage(
                    image = img,
                    dstOffset = IntOffset((sc.bottomLeft.x - postWidth / 2f).toInt(), (sc.bottomLeft.y - postHeight * 0.7f).toInt()),
                    dstSize = IntSize(postWidth, postHeight)
                )
            }
            rightPost?.let { img ->
                drawImage(
                    image = img,
                    dstOffset = IntOffset((sc.topRight.x - postWidth / 2f).toInt(), (sc.topRight.y - postHeight * 0.7f).toInt()),
                    dstSize = IntSize(postWidth, postHeight)
                )
            }
            frontPost?.let { img ->
                drawImage(
                    image = img,
                    dstOffset = IntOffset((sc.bottomRight.x - postWidth / 2f).toInt(), (sc.bottomRight.y - postHeight * 0.7f).toInt()),
                    dstSize = IntSize(postWidth, postHeight)
                )
            }
        }
    }

    // ── Layer 3: Soil Fill ──────────────────────────────────────────────

    private fun DrawScope.renderSoilFill(bed: BedRenderData, camera: CameraState, resources: Resources? = null, context: android.content.Context? = null) {
        val sc = bed.screenCorners(camera)
        val soilColor = soilColor(bed.soilType)

        // Top face rhombus filled with soil color
        val topFacePath = Path().apply {
            moveTo(sc.topLeft.x, sc.topLeft.y)
            lineTo(sc.topRight.x, sc.topRight.y)
            lineTo(sc.bottomRight.x, sc.bottomRight.y)
            lineTo(sc.bottomLeft.x, sc.bottomLeft.y)
            close()
        }
        drawPath(topFacePath, soilColor.copy(alpha = 0.9f))

        if (resources != null) {
            val soilIndex = when (bed.soilType) {
                SoilType.LOAM   -> 1
                SoilType.CLAY   -> 2
                SoilType.SANDY  -> 3
                SoilType.SILTY  -> 4
                SoilType.PEATY  -> 5
                SoilType.CHALKY -> 6
            }
            val soilBitmap = SoilPainter.getTexture(resources, soilIndex, context)
            val minX = minOf(sc.topLeft.x, sc.topRight.x, sc.bottomLeft.x, sc.bottomRight.x).toInt()
            val minY = minOf(sc.topLeft.y, sc.topRight.y, sc.bottomLeft.y, sc.bottomRight.y).toInt()
            val maxX = maxOf(sc.topLeft.x, sc.topRight.x, sc.bottomLeft.x, sc.bottomRight.x).toInt()
            val maxY = maxOf(sc.topLeft.y, sc.topRight.y, sc.bottomLeft.y, sc.bottomRight.y).toInt()
            val w = (maxX - minX).coerceAtLeast(1)
            val h = (maxY - minY).coerceAtLeast(1)

            drawContext.canvas.save()
            drawContext.canvas.clipPath(topFacePath)
            drawImage(
                image = soilBitmap,
                dstOffset = IntOffset(minX, minY),
                dstSize = IntSize(w, h)
            )
            drawContext.canvas.restore()
        }
    }

    /** Maps SoilType to base soil color. Texture bitmaps loaded from res/drawable in production. */
    private fun soilColor(soilType: SoilType): Color = when (soilType) {
        SoilType.LOAM   -> Color(0xFF6D4C41)
        SoilType.CLAY   -> Color(0xFF8D6E63)
        SoilType.SANDY  -> Color(0xFFD7CCC8)
        SoilType.SILTY  -> Color(0xFF90A4AE)
        SoilType.PEATY  -> Color(0xFF212121)
        SoilType.CHALKY -> Color(0xFFECEFF1)
    }

    // ── Layer 3.5: Crop Zone & Plant Instances ──────────────────────────

    private fun DrawScope.renderCropZone(
        zone: CropZoneRenderData,
        bed: BedRenderData,
        camera: CameraState,
        isSelected: Boolean = false,
        context: android.content.Context? = null
    ) {
        // Draw zone outline/fill (subtle green tint)
        val zTopLeft = IsometricProjection.toScreen(bed.posX + zone.offsetX, bed.posY + zone.offsetY, camera)
        val zTopRight = IsometricProjection.toScreen(bed.posX + zone.offsetX + zone.widthM, bed.posY + zone.offsetY, camera)
        val zBottomLeft = IsometricProjection.toScreen(bed.posX + zone.offsetX, bed.posY + zone.offsetY + zone.heightM, camera)
        val zBottomRight = IsometricProjection.toScreen(bed.posX + zone.offsetX + zone.widthM, bed.posY + zone.offsetY + zone.heightM, camera)

        val zonePath = Path().apply {
            moveTo(zTopLeft.x, zTopLeft.y)
            lineTo(zTopRight.x, zTopRight.y)
            lineTo(zBottomRight.x, zBottomRight.y)
            lineTo(zBottomLeft.x, zBottomLeft.y)
            close()
        }
        drawPath(zonePath, Color(0x224CAF50))

        // Selection highlight for active zone
        if (isSelected) {
            drawPath(
                path = zonePath,
                color = Color(0xFF00BCD4),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 3.dp.toPx()))
                )
            )
        }

        // Render generated plant instances inside this zone
        zone.plantInstances.forEach { plant ->
            val pos = IsometricProjection.toScreen(plant.worldX, plant.worldY, camera)
            var assetLoaded = false

            if (context != null) {
                val cropClean = when (plant.cropName.lowercase().replace(" ", "").replace("_", "").replace("-", "")) {
                    "stringbeans", "sitaw", "stringbean", "beans" -> "crop_stringbeans"
                    "carrot", "carrots" -> "crop_carrot"
                    "tomato", "tomatoes" -> "crop_tomato"
                    "eggplant", "eggplants", "talong" -> "crop_eggplant"
                    "pechay" -> "crop_pechay"
                    "kangkong" -> "crop_kangkong"
                    "lettuce" -> "crop_lettuce"
                    "onion", "onions" -> "crop_onion"
                    "okra" -> "crop_okra"
                    "squash" -> "crop_squash"
                    "cucumber" -> "crop_cucumber"
                    "pepper", "peppers", "sili" -> "crop_pepper"
                    else -> "crop_" + plant.cropName.lowercase().replace(" ", "")
                }

                val stageNum = when (bed.growthStage) {
                    GrowthStage.GERMINATION -> 1
                    GrowthStage.EARLY_VEGETATIVE -> 2
                    GrowthStage.MID_VEGETATIVE -> 2
                    GrowthStage.FLOWERING -> 3
                    GrowthStage.FRUITING -> 4
                    GrowthStage.HARVEST_READY, GrowthStage.OVERDUE -> 4
                    null -> 4
                }

                val stagesToTry = listOf(stageNum, 4, 3, 2, 1).distinct()
                var cropBitmap: androidx.compose.ui.graphics.ImageBitmap? = null
                for (st in stagesToTry) {
                    cropBitmap = AssetLoader.loadFromAssets(context, "crops/${cropClean}_$st.png")
                    if (cropBitmap != null) break
                }

                if (cropBitmap != null) {
                    val tileW = IsometricProjection.TILE_W * camera.zoom
                    val cropSize = (tileW * plant.scaleFactor * 0.8f).coerceAtLeast(16f).toInt()
                    drawImage(
                        image = cropBitmap,
                        dstOffset = IntOffset(
                            (pos.x - cropSize / 2f).toInt(),
                            (pos.y - cropSize * 0.85f).toInt()
                        ),
                        dstSize = IntSize(cropSize, cropSize)
                    )
                    assetLoaded = true
                }
            }

            if (!assetLoaded) {
                val plantRadius = 8.dp.toPx() * camera.zoom * plant.scaleFactor
                drawCircle(Color(0xFF2E7D32), plantRadius, pos)
                drawCircle(Color(0xFF81C784), plantRadius * 0.6f, pos)
            }
        }
    }

    // ── Layer 4.5: Trellis ─────────────────────────────────────────────

    private fun DrawScope.renderTrellis(
        trellis: FarmObjectRenderData,
        camera: CameraState,
        context: android.content.Context? = null
    ) {
        val pos = IsometricProjection.toScreen(trellis.worldX, trellis.worldY, camera)
        var assetLoaded = false

        if (context != null) {
            val bitmap = AssetLoader.loadFromAssets(context, "structures/trillis.png")
                ?: AssetLoader.loadFromAssets(context, "structures/trellis_aframe.png")

            if (bitmap != null) {
                val tileW = IsometricProjection.TILE_W * camera.zoom
                val targetW = (tileW * 1.6f * trellis.widthM).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(
                        (pos.x - targetW / 2f).toInt(),
                        (pos.y - targetH * 0.85f).toInt()
                    ),
                    dstSize = IntSize(targetW, targetH)
                )
                assetLoaded = true
            }
        }

        if (!assetLoaded) {
            val h = 30.dp.toPx() * camera.zoom
            val w = (trellis.widthM * IsometricProjection.TILE_W / 2f) * camera.zoom

            // Draw wooden A-frame trellis
            val leftLeg = Path().apply {
                moveTo(pos.x - w / 2f, pos.y)
                lineTo(pos.x, pos.y - h)
                lineTo(pos.x + w / 2f, pos.y)
            }
            drawPath(leftLeg, Color(0xFF8D6E63), style = Stroke(width = 3.dp.toPx() * camera.zoom))
        }
    }

    // ── Layer 5 & 6: Farm Object (Fence, Tree, Decoration) ─────────────

    private fun DrawScope.renderFarmObject(obj: FarmObjectRenderData, camera: CameraState, context: android.content.Context? = null) {
        val pos = IsometricProjection.toScreen(obj.worldX, obj.worldY, camera)
        val zoom = camera.zoom

        if (context != null) {
            val assetPath = when (obj.objectType) {
                FarmObjectType.FENCE_SEGMENT -> "fences/fences_left.png"
                FarmObjectType.TREE -> "trees_and_rocks/mango_tree.png"
                FarmObjectType.DECORATION -> "trees_and_rocks/large_rock.png"
                FarmObjectType.TRELLIS -> "structures/trillis.png"
            }
            val bitmap = com.maptanim.app.renderer.AssetLoader.loadFromAssets(context, assetPath)
            if (bitmap != null) {
                val tileW = (IsometricProjection.TILE_W * zoom).toInt().coerceAtLeast(1)
                val tileH = (IsometricProjection.TILE_H * zoom).toInt().coerceAtLeast(1)

                val tileSpan = if (obj.objectType == FarmObjectType.TREE) 3.5f else 1f
                val targetW = ((tileW + 4) * tileSpan).toInt().coerceAtLeast(1)
                val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
                val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

                val dstOffset = if (obj.objectType == FarmObjectType.TREE) {
                    IntOffset(
                        Math.round(pos.x - targetW / 2f),
                        Math.round(pos.y + tileH / 2f - targetH)
                    )
                } else {
                    IntOffset(
                        Math.round(pos.x - targetW / 2f),
                        Math.round(pos.y - (targetH - tileH / 2f))
                    )
                }

                drawImage(
                    image = bitmap,
                    dstOffset = dstOffset,
                    dstSize = IntSize(targetW, targetH)
                )
                return
            }
        }

        when (obj.objectType) {
            FarmObjectType.FENCE_SEGMENT -> {
                val endPos = IsometricProjection.toScreen(obj.worldX + obj.widthM, obj.worldY + obj.heightM, camera)
                drawLine(Color(0xFF795548), pos, endPos, strokeWidth = 4.dp.toPx() * zoom)
            }
            FarmObjectType.TREE -> {
                val trunkRadius = 4.dp.toPx() * zoom
                val crownRadius = 18.dp.toPx() * zoom
                drawCircle(Color(0xFF5D4037), trunkRadius, pos)
                drawCircle(Color(0xFF1B5E20), crownRadius, pos.copy(y = pos.y - crownRadius))
            }
            FarmObjectType.DECORATION -> {
                val r = 8.dp.toPx() * zoom
                drawCircle(Color(0xFF78909C), r, pos)
            }
            FarmObjectType.TRELLIS -> renderTrellis(obj, camera)
        }
    }


    // ── Layer 5: Bed Label Chip ─────────────────────────────────────────

    private fun DrawScope.renderBedLabel(bed: BedRenderData, camera: CameraState) {
        // In production: drawText via Canvas nativeCanvas with green rounded rect background
        // bed.bedLabel and bed.cropName sourced from Room — never hardcoded
    }

    // ── Layer 6a: Status Pins ───────────────────────────────────────────

    private fun DrawScope.renderStatusPins(bed: BedRenderData, camera: CameraState) {
        val anchor = bed.pinAnchor(camera)
        bed.activeTasks.forEachIndexed { i, task ->
            val pinCenter = anchor.copy(x = anchor.x + i * PIN_SPACING_PX)
            renderPin(task.taskType, pinCenter, camera.zoom)
        }
    }

    private fun DrawScope.renderPin(taskType: TaskType, center: androidx.compose.ui.geometry.Offset, zoom: Float) {
        val color = when (taskType) {
            TaskType.WATER          -> Color(0xFF1E88E5)
            TaskType.FERTILIZE      -> Color(0xFF43A047)
            TaskType.HARVEST        -> Color(0xFFFFA000)
            TaskType.PEST_ALERT     -> Color(0xFFE53935)
            TaskType.APPLY_PESTICIDE -> Color(0xFFFF6F00)
        }
        val radius = PIN_RADIUS_DP.dp.toPx() * zoom

        // Draw teardrop path (circle top, point at bottom)
        val tearPath = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(
                left   = center.x - radius,
                top    = center.y - radius * 2f,
                right  = center.x + radius,
                bottom = center.y
            ))
            moveTo(center.x - radius * 0.5f, center.y - radius * 0.5f)
            lineTo(center.x, center.y + radius * 0.5f)
            lineTo(center.x + radius * 0.5f, center.y - radius * 0.5f)
            close()
        }
        drawPath(tearPath, color)
        // Icon drawn via Canvas.nativeCanvas in production
    }

    // ── Layer 6b: Selection Handles ─────────────────────────────────────

    private fun DrawScope.renderSelectionHandles(
        bed: BedRenderData,
        camera: CameraState
    ): HandlePositions {
        val sc = bed.screenCorners(camera)

        // Dashed blue selection border (isometric rhombus shape)
        val borderPath = Path().apply {
            moveTo(sc.topLeft.x,  sc.topLeft.y)
            lineTo(sc.topCenter.x, sc.topCenter.y - BED_HEIGHT_PX * camera.zoom / 2f)
            lineTo(sc.topRight.x,  sc.topRight.y)
            lineTo(sc.topCenter.x, sc.topCenter.y)
            close()
        }
        drawPath(
            path = borderPath,
            color = Color(0xFF1E88E5),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 4.dp.toPx()))
            )
        )

        val dragPos    = sc.topCenter.copy(y = sc.topCenter.y - BED_HEIGHT_PX * camera.zoom / 2f)
        val deletePos  = sc.topRight
        val centerPos  = bed.centerScreen(camera)

        // Drag handle — blue filled circle
        drawCircle(Color(0xFF1E88E5), 12.dp.toPx() * camera.zoom, dragPos)

        // Delete quick — red filled circle
        drawCircle(Color(0xFFEF5350), 10.dp.toPx() * camera.zoom, deletePos)

        // ── 4 Corner handles — white filled circles with blue outline ──
        listOf(sc.topLeft, sc.topRight, sc.bottomLeft, sc.bottomRight).forEach { pt ->
            drawCircle(Color.White, 8.dp.toPx() * camera.zoom, pt)
            drawCircle(Color(0xFF1E88E5), 8.dp.toPx() * camera.zoom, pt,
                style = Stroke(1.5.dp.toPx()))
        }

        // ── 4 Mid-edge handles (for resizing) — smaller white/blue circles ──
        listOf(sc.topCenter, sc.bottomCenter, sc.leftMid, sc.rightMid).forEach { pt ->
            drawCircle(Color.White, 6.dp.toPx() * camera.zoom, pt)
            drawCircle(Color(0xFF1E88E5), 6.dp.toPx() * camera.zoom, pt,
                style = Stroke(1.5.dp.toPx()))
        }

        // Action button — green filled circle (center)
        drawCircle(Color(0xFF43A047), 14.dp.toPx() * camera.zoom, centerPos)

        return HandlePositions(
            dragHandle  = dragPos,
            deleteQuick = deletePos,
            cornerTL    = sc.topLeft,
            cornerTR    = sc.topRight,
            cornerBL    = sc.bottomLeft,
            cornerBR    = sc.bottomRight,
            midTop      = sc.topCenter,
            midBottom   = sc.bottomCenter,
            midLeft     = sc.leftMid,
            midRight    = sc.rightMid,
            actionBtn   = centerPos
        )
    }

    // ── Layer 6c: Grid Overlay ──────────────────────────────────────────

    private fun DrawScope.renderGridOverlay(camera: CameraState) {
        val bounds = camera.getVisibleWorldBounds(size.width, size.height)
        val gridColor = Color(0x33000000)   // black at 20% opacity

        for (wx in bounds.left.toInt()..bounds.right.toInt()) {
            val start = IsometricProjection.toScreen(wx.toFloat(), bounds.top, camera)
            val end   = IsometricProjection.toScreen(wx.toFloat(), bounds.bottom, camera)
            drawLine(gridColor, start, end, strokeWidth = 1.dp.toPx())
        }
        for (wy in bounds.top.toInt()..bounds.bottom.toInt()) {
            val start = IsometricProjection.toScreen(bounds.left,  wy.toFloat(), camera)
            val end   = IsometricProjection.toScreen(bounds.right, wy.toFloat(), camera)
            drawLine(gridColor, start, end, strokeWidth = 1.dp.toPx())
        }
    }

    // ── Snap ────────────────────────────────────────────────────────────

    fun snapToGrid(worldPos: androidx.compose.ui.geometry.Offset): androidx.compose.ui.geometry.Offset {
        val snap = SNAP_GRID_SIZE
        return androidx.compose.ui.geometry.Offset(
            x = (worldPos.x / snap).toLong() * snap,
            y = (worldPos.y / snap).toLong() * snap
        )
    }

    // ── Constants ────────────────────────────────────────────────────────

    private const val BED_HEIGHT_PX  = 20f   // Raised bed wall height in screen pixels (pre-zoom)
    private const val PIN_RADIUS_DP  = 14f   // Status pin teardrop radius in dp
    private const val PIN_SPACING_PX = 36f   // Horizontal spacing between multiple pins on same bed
    private const val SNAP_GRID_SIZE = 0.5f  // Snap to 0.5m world units
}
