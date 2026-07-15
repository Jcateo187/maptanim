package com.maptanim.app.ui.components.isometric.camera

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CameraGesture(

    modifier: Modifier = Modifier,

    cameraState: CameraState,

    content: @Composable () -> Unit

) {

    Box(

        modifier = modifier.pointerInput(Unit) {

            detectTransformGestures {

                    _,

                    pan,

                    zoom,

                    _ ->

                //--------------------------------
                // Zoom
                //--------------------------------

                cameraState.zoom =
                    (cameraState.zoom * zoom)
                        .coerceIn(0.5f, 4f)

                //--------------------------------
                // Pan
                //--------------------------------

                cameraState.offsetX += pan.x

                cameraState.offsetY += pan.y

            }

        }

    ) {

        content()

    }

}