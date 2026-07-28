package com.maptanim.app.ui.components.isometric.math

import androidx.compose.ui.geometry.Offset
import com.maptanim.app.ui.components.isometric.world.terrain.GrassDimensions

object IsoMath {

    /**
     * Converts a world tile position into a screen position.
     *
     * screenCenterX/Y = center of the phone
     * cameraRow/Column = tile currently at the center
     */
    fun worldToScreen(

        row: Int,

        column: Int,

        cameraRow: Int,

        cameraColumn: Int,

        screenCenterX: Float,

        screenCenterY: Float

    ): Offset {

        val dx = column - cameraColumn
        val dy = row - cameraRow

        val x = (dx - dy) * (GrassDimensions.CELL_WIDTH / 2f)

        val y = (dx + dy) * (GrassDimensions.CELL_HEIGHT / 2f)

        return Offset(
            screenCenterX + x,
            screenCenterY + y
        )
    }

    /**
     * Converts float world coordinates (meters) to screen pixel coordinates.
     * Prevents pixel drift by doing all coordinate math in Float precision.
     */
    fun worldToScreenF(
        worldX: Float,
        worldY: Float,
        cameraWorldX: Float,
        cameraWorldY: Float,
        screenCenterX: Float,
        screenCenterY: Float
    ): Offset {
        val dx = worldX - cameraWorldX
        val dy = worldY - cameraWorldY
        val x = (dx - dy) * (GrassDimensions.CELL_WIDTH / 2f)
        val y = (dx + dy) * (GrassDimensions.CELL_HEIGHT / 2f)
        return Offset(
            screenCenterX + x,
            screenCenterY + y
        )
    }

    /**
     * Converts screen pixel position back to float world coordinates (meters).
     */
    fun screenToWorldF(
        screenX: Float,
        screenY: Float,
        cameraWorldX: Float,
        cameraWorldY: Float,
        screenCenterX: Float,
        screenCenterY: Float
    ): Offset {
        val relX = (screenX - screenCenterX) / (GrassDimensions.CELL_WIDTH / 2f)
        val relY = (screenY - screenCenterY) / (GrassDimensions.CELL_HEIGHT / 2f)
        val worldX = (relX + relY) / 2f + cameraWorldX
        val worldY = (relY - relX) / 2f + cameraWorldY
        return Offset(worldX, worldY)
    }
}