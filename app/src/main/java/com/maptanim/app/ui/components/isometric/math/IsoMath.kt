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

}