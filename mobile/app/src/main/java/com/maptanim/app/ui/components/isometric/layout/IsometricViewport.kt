package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState

@Composable
fun IsometricViewport(

    modifier: Modifier = Modifier,

    cameraState: CameraState,

    farmEditState: FarmEditState

) {

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        IsometricScene(

            modifier = Modifier.fillMaxSize(),

            cameraState = cameraState,

            farmEditState = farmEditState

        )

    }

}