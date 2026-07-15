package com.maptanim.app.ui.components.isometric.world.terrain

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath

@Composable
fun GrassRenderer(

    modifier: Modifier = Modifier,

    cameraState: CameraState

) {

    val resources = LocalContext.current.resources

    //------------------------------------
    // Load grass textures ONCE
    //------------------------------------

    val grassTextures = remember {

        listOf(

            TerrainPainter.getTexture(resources, 1),
            TerrainPainter.getTexture(resources, 2),
            TerrainPainter.getTexture(resources, 3),
            TerrainPainter.getTexture(resources, 4),
            TerrainPainter.getTexture(resources, 5)

        )

    }

    Canvas(

        modifier = modifier.fillMaxSize()

    ) {

        //------------------------------------
        // Screen Center
        //------------------------------------

        val screenCenterX = size.width / 2f
        val screenCenterY = size.height / 2f

        //------------------------------------
        // Calculate visible tiles
        //------------------------------------

        val visibleColumns =
            (size.width / GrassDimensions.CELL_WIDTH).toInt() +
                    GrassDimensions.PADDING

        val visibleRows =
            (size.height / GrassDimensions.CELL_HEIGHT).toInt() +
                    GrassDimensions.PADDING

        //------------------------------------
        // Camera Window
        //------------------------------------

        val startRow =
            (cameraState.cameraRow - visibleRows)
                .coerceAtLeast(0)

        val endRow =
            (cameraState.cameraRow + visibleRows)
                .coerceAtMost(GrassDimensions.WORLD_ROWS)

        val startColumn =
            (cameraState.cameraColumn - visibleColumns)
                .coerceAtLeast(0)

        val endColumn =
            (cameraState.cameraColumn + visibleColumns)
                .coerceAtMost(GrassDimensions.WORLD_COLUMNS)

        //------------------------------------
        // Draw World
        //------------------------------------

        withTransform({

            translate(

                left = cameraState.offsetX,

                top = cameraState.offsetY

            )

            scale(

                scaleX = cameraState.zoom,

                scaleY = cameraState.zoom,

                pivot = center

            )

        }) {

            // draw world



            for (row in startRow..endRow) {

                for (column in startColumn..endColumn) {

                    //------------------------------------
                    // Tile data
                    //------------------------------------

                    val tile = TerrainGenerator.generateTile(

                        row,

                        column

                    )

                    //------------------------------------
                    // World Position
                    //------------------------------------

                    val position = IsoMath.worldToScreen(

                        row = row,

                        column = column,

                        cameraRow = cameraState.cameraRow,

                        cameraColumn = cameraState.cameraColumn,

                        screenCenterX = screenCenterX,

                        screenCenterY = screenCenterY

                    )

                    //------------------------------------
                    // Texture
                    //------------------------------------

                    val texture: ImageBitmap =
                        grassTextures[tile.grassVariant - 1]

                    //------------------------------------
                    // Center image on tile
                    //------------------------------------

                    val drawPosition = Offset(

                        x = position.x - texture.width / 2f,

                        y = position.y - texture.height / 2f

                    )

                    //------------------------------------
                    // Draw
                    //------------------------------------

                    drawImage(

                        image = texture,

                        topLeft = drawPosition

                    )

                }

            }

        }

    }

}