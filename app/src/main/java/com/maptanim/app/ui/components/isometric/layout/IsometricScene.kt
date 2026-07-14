package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.world.terrain.GrassWorld

@Composable
fun IsometricScene(

    modifier: Modifier = Modifier,

    cameraState: CameraState

) {

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        GrassWorld(

            modifier = Modifier.fillMaxSize(),

            cameraState = cameraState

        )

    }

}