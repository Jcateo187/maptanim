package com.maptanim.app.ui.components.isometric.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath
import com.maptanim.app.ui.components.isometric.world.farm.*

@Composable
fun FarmArrowRenderer(

    modifier: Modifier = Modifier,

    farmState: FarmState,

    farmEditState: FarmEditState,

    cameraState: CameraState

) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        val farm = farmState.getFarmArea()

        val screenCenterX = 540f
        val screenCenterY = 960f

        val north = IsoMath.worldToScreen(
            farm.top,
            farmState.centerColumn,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val south = IsoMath.worldToScreen(
            farm.bottom,
            farmState.centerColumn,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val west = IsoMath.worldToScreen(
            farmState.centerRow,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val east = IsoMath.worldToScreen(
            farmState.centerRow,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val northWest = IsoMath.worldToScreen(
            farm.top,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val northEast = IsoMath.worldToScreen(
            farm.top,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val southWest = IsoMath.worldToScreen(
            farm.bottom,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        val southEast = IsoMath.worldToScreen(
            farm.bottom,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            screenCenterX,
            screenCenterY
        )

        Handle(north, HandleDirection.NORTH, farmState, farmEditState)
        Handle(south, HandleDirection.SOUTH, farmState, farmEditState)
        Handle(east, HandleDirection.EAST, farmState, farmEditState)
        Handle(west, HandleDirection.WEST, farmState, farmEditState)

        Handle(northWest, HandleDirection.NORTH_WEST, farmState, farmEditState)
        Handle(northEast, HandleDirection.NORTH_EAST, farmState, farmEditState)
        Handle(southWest, HandleDirection.SOUTH_WEST, farmState, farmEditState)
        Handle(southEast, HandleDirection.SOUTH_EAST, farmState, farmEditState)
    }
}

@Composable
private fun Handle(

    position: Offset,

    direction: HandleDirection,

    farmState: FarmState,

    farmEditState: FarmEditState

) {

    FarmResizeHandle(

        position = position,

        direction = direction,

        onDragStart = {

            farmEditState.activeHandle = direction

        },

        onDrag = { drag ->

            FarmResizeController.handleDrag(

                direction = direction,

                drag = drag,

                state = farmState

            )

        },

        onDragEnd = {

            farmEditState.activeHandle = null

        }

    )

}