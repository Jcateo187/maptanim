package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.math.IsoMath

@Composable
fun FarmRenderer(

    modifier: Modifier = Modifier,

    farmState: FarmState,

    cameraState: CameraState

) {

    val resources = LocalContext.current.resources

    Canvas(

        modifier = modifier.fillMaxSize()

    ) {

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

            val farm = farmState.getFarmArea()

            for (row in farm.top..farm.bottom) {

                for (column in farm.left..farm.right) {

                    val position = IsoMath.worldToScreen(

                        row = row,

                        column = column,

                        cameraRow = cameraState.cameraRow,

                        cameraColumn = cameraState.cameraColumn,

                        screenCenterX = size.width / 2f,

                        screenCenterY = size.height / 2f

                    )

                    val texture = SoilPainter.getTexture(

                        resources,

                        SoilGenerator.texture(

                            row,

                            column

                        )

                    )

                    drawImage(

                        image = texture,

                        topLeft = Offset(

                            x = position.x - texture.width / 2f,

                            y = position.y - texture.height / 2f

                        )

                    )

                }

            }

        }

    }

}