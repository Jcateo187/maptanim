package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraGesture
import com.maptanim.app.ui.components.isometric.camera.CameraState

@Composable
fun IsometricLayout(

    modifier: Modifier = Modifier

) {

    val cameraState = remember {

        CameraState()

    }

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        CameraGesture(

            modifier = Modifier.fillMaxSize(),

            cameraState = cameraState

        ) {

            IsometricViewport(

                modifier = Modifier.fillMaxSize(),

                cameraState = cameraState

            )

        }

    }

}