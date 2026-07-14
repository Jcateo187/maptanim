package com.maptanim.app.ui.components.isometric.world.terrain

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState

@Composable
fun GrassWorld(

    modifier: Modifier = Modifier,

    cameraState: CameraState

) {

    GrassRenderer(

        modifier = modifier,

        cameraState = cameraState

    )

}