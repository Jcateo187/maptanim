package com.maptanim.app.ui.components.isometric.camera

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CameraGesture(

    modifier: Modifier = Modifier,

    cameraState: CameraState,

    content: @Composable () -> Unit

) {

    //--------------------------------------
    // Camera Controller
    //--------------------------------------

    val controller = remember(cameraState) {

        CameraController(cameraState)

    }

    Box(

        modifier = modifier.pointerInput(cameraState) {

            detectTransformGestures {

                    _,
                    pan,
                    zoom,
                    _ ->

                //--------------------------------
                // Zoom
                //--------------------------------

                controller.zoomBy(zoom)

                //--------------------------------
                // Pan
                //--------------------------------

                controller.pan(

                    pan.x,

                    pan.y

                )

            }

        }

    ) {

        content()

    }

}