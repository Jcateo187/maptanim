package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraGesture
import com.maptanim.app.ui.components.isometric.camera.CameraState

@Composable
fun IsometricViewport(

    modifier: Modifier = Modifier,

    cameraState: CameraState

) {

    CameraGesture(

        cameraState = cameraState

    ) {

        Box(

            modifier = modifier.fillMaxSize()

        ) {

            IsometricScene(

                modifier = Modifier.fillMaxSize(),

                cameraState = cameraState

            )

        }

    }

}