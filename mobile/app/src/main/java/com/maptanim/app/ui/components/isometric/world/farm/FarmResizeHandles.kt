package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath

@Composable
fun FarmResizeHandles(

    modifier: Modifier = Modifier,

    farmState: FarmState,

    cameraState: CameraState,

    farmEditState: FarmEditState

) {

    BoxWithConstraints(

        modifier = modifier.fillMaxSize()

    ) {

        val density = LocalDensity.current

        val screenWidth = with(density) {
            maxWidth.toPx()
        }

        val screenHeight = with(density) {
            maxHeight.toPx()
        }

        val farm = farmState.getFarmArea()

        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        val north = IsoMath.worldToScreen(
            farm.top,
            farmState.centerColumn,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val south = IsoMath.worldToScreen(
            farm.bottom,
            farmState.centerColumn,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val west = IsoMath.worldToScreen(
            farmState.centerRow,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val east = IsoMath.worldToScreen(
            farmState.centerRow,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val northWest = IsoMath.worldToScreen(
            farm.top,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val northEast = IsoMath.worldToScreen(
            farm.top,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val southWest = IsoMath.worldToScreen(
            farm.bottom,
            farm.left,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        val southEast = IsoMath.worldToScreen(
            farm.bottom,
            farm.right,
            cameraState.cameraRow,
            cameraState.cameraColumn,
            centerX,
            centerY
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Handle(
                north,
                HandleDirection.NORTH,
                farmState,
                farmEditState
            )

            Handle(
                south,
                HandleDirection.SOUTH,
                farmState,
                farmEditState
            )

            Handle(
                west,
                HandleDirection.WEST,
                farmState,
                farmEditState
            )

            Handle(
                east,
                HandleDirection.EAST,
                farmState,
                farmEditState
            )

            Handle(
                northWest,
                HandleDirection.NORTH_WEST,
                farmState,
                farmEditState
            )

            Handle(
                northEast,
                HandleDirection.NORTH_EAST,
                farmState,
                farmEditState
            )

            Handle(
                southWest,
                HandleDirection.SOUTH_WEST,
                farmState,
                farmEditState
            )

            Handle(
                southEast,
                HandleDirection.SOUTH_EAST,
                farmState,
                farmEditState
            )

        }

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

        onDrag = {

            FarmResizeController.handleDrag(

                direction = direction,

                drag = it,

                state = farmState

            )

        },

        onDragEnd = {

            farmEditState.activeHandle = null

        }

    )

}