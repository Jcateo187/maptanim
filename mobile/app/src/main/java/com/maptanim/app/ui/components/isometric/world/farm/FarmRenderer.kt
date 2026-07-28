package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath
import com.maptanim.app.ui.components.isometric.world.terrain.GrassDimensions

/**
 * FarmRenderer — High-performance 60 FPS isometric farm soil and bed renderer.
 *
 * Performance Optimizations:
 *   - Pre-loads all 8 soil bitmaps ONCE via `remember` (zero allocation during drag gesture)
 *   - Clips tile loop to camera viewport range (startRow..endRow, startColumn..endColumn)
 *   - Smooth ground tiling for easy bed placement
 */
@Composable
fun FarmRenderer(
    modifier: Modifier = Modifier,
    farmState: FarmState,
    cameraState: CameraState,
    farmEditState: FarmEditState
) {
    val resources = LocalContext.current.resources

    // ── 1. Pre-load soil textures ONCE (Zero allocation during drag) ───────
    val soilTextures = remember {
        listOf(
            SoilPainter.getTexture(resources, 1),
            SoilPainter.getTexture(resources, 2),
            SoilPainter.getTexture(resources, 3),
            SoilPainter.getTexture(resources, 4),
            SoilPainter.getTexture(resources, 5),
            SoilPainter.getTexture(resources, 6),
            SoilPainter.getTexture(resources, 7),
            SoilPainter.getTexture(resources, 8)
        )
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val screenCenterX = size.width / 2f
        val screenCenterY = size.height / 2f
        val farm = farmState.getFarmArea()

        // ── 2. Calculate Viewport Bounds for 60 FPS Drag Performance ─────
        val dynamicPadding = when {
            cameraState.zoom <= 0.75f -> 25
            cameraState.zoom <= 1.0f  -> 18
            else                       -> 12
        }

        val visibleColumns = (size.width / GrassDimensions.CELL_WIDTH).toInt() + dynamicPadding
        val visibleRows = (size.height / GrassDimensions.CELL_HEIGHT).toInt() + dynamicPadding

        val startRow = (cameraState.cameraRow - visibleRows).coerceAtLeast(farm.top)
        val endRow = (cameraState.cameraRow + visibleRows).coerceAtMost(farm.bottom)
        val startColumn = (cameraState.cameraColumn - visibleColumns).coerceAtLeast(farm.left)
        val endColumn = (cameraState.cameraColumn + visibleColumns).coerceAtMost(farm.right)

        withTransform({
            translate(left = cameraState.offsetX, top = cameraState.offsetY)
            scale(scaleX = cameraState.zoom, scaleY = cameraState.zoom, pivot = center)
        }) {
            // ── 3. Render Smooth Soil Tiles ──────────────────────────────
            for (row in startRow..endRow) {
                for (column in startColumn..endColumn) {
                    val position = IsoMath.worldToScreen(
                        row = row,
                        column = column,
                        cameraRow = cameraState.cameraRow,
                        cameraColumn = cameraState.cameraColumn,
                        screenCenterX = screenCenterX,
                        screenCenterY = screenCenterY
                    )

                    val texIndex = SoilGenerator.texture(row, column, farm) - 1
                    val texture = soilTextures[texIndex.coerceIn(0, 7)]

                    val drawPosition = Offset(
                        x = position.x - texture.width / 2f,
                        y = position.y - texture.height / 2f
                    )

                    drawImage(
                        image = texture,
                        topLeft = drawPosition
                    )
                }
            }

            // ── 4. Render Resize Guidelines in Edit Mode ─────────────────
            if (farmEditState.isResizeMode) {
                val topLeft = IsoMath.worldToScreen(farm.top, farm.left, cameraState.cameraRow, cameraState.cameraColumn, screenCenterX, screenCenterY)
                val topRight = IsoMath.worldToScreen(farm.top, farm.right, cameraState.cameraRow, cameraState.cameraColumn, screenCenterX, screenCenterY)
                val bottomRight = IsoMath.worldToScreen(farm.bottom, farm.right, cameraState.cameraRow, cameraState.cameraColumn, screenCenterX, screenCenterY)
                val bottomLeft = IsoMath.worldToScreen(farm.bottom, farm.left, cameraState.cameraRow, cameraState.cameraColumn, screenCenterX, screenCenterY)

                val lineColor = Color(0xFF43A047)
                drawLine(color = lineColor, start = topLeft, end = topRight, strokeWidth = 3f)
                drawLine(color = lineColor, start = topRight, end = bottomRight, strokeWidth = 3f)
                drawLine(color = lineColor, start = bottomRight, end = bottomLeft, strokeWidth = 3f)
                drawLine(color = lineColor, start = bottomLeft, end = topLeft, strokeWidth = 3f)
            }
        }
    }
}