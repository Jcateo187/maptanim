package com.maptanim.app.ui.components.isometric.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath
import com.maptanim.app.ui.components.isometric.world.farm.FarmState
import com.maptanim.app.ui.components.isometric.world.farm.getFarmArea

@Composable
fun FarmBorderRenderer(

    modifier: Modifier = Modifier,

    farmState: FarmState,

    cameraState: CameraState

) {

    Canvas(

        modifier = modifier.fillMaxSize()

    ) {

        val farm = farmState.getFarmArea()

        val screenCenterX = size.width / 2f
        val screenCenterY = size.height / 2f

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

            val topLeft = IsoMath.worldToScreen(

                farm.top,
                farm.left,

                cameraState.cameraRow,
                cameraState.cameraColumn,

                screenCenterX,
                screenCenterY

            )

            val topRight = IsoMath.worldToScreen(

                farm.top,
                farm.right,

                cameraState.cameraRow,
                cameraState.cameraColumn,

                screenCenterX,
                screenCenterY

            )

            val bottomRight = IsoMath.worldToScreen(

                farm.bottom,
                farm.right,

                cameraState.cameraRow,
                cameraState.cameraColumn,

                screenCenterX,
                screenCenterY

            )

            val bottomLeft = IsoMath.worldToScreen(

                farm.bottom,
                farm.left,

                cameraState.cameraRow,
                cameraState.cameraColumn,

                screenCenterX,
                screenCenterY

            )

            drawLine(
                color = Color(0xFFFF9800),
                start = topLeft,
                end = topRight,
                strokeWidth = 4f
            )

            drawLine(
                color = Color(0xFFFF9800),
                start = topRight,
                end = bottomRight,
                strokeWidth = 4f
            )

            drawLine(
                color = Color(0xFFFF9800),
                start = bottomRight,
                end = bottomLeft,
                strokeWidth = 4f
            )

            drawLine(
                color = Color(0xFFFF9800),
                start = bottomLeft,
                end = topLeft,
                strokeWidth = 4f
            )

        }

    }

}