package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.world.farm.FarmRenderer
import com.maptanim.app.ui.components.isometric.world.farm.FarmState
import com.maptanim.app.ui.components.isometric.world.terrain.GrassRenderer

@Composable
fun IsometricScene(

    modifier: Modifier = Modifier,

    cameraState: CameraState

) {

    val farmState = remember {

        FarmState()

    }

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        //------------------------
        // Grass Layer
        //------------------------

        GrassRenderer(

            modifier = Modifier.fillMaxSize(),

            cameraState = cameraState

        )

        //------------------------
        // Farm Layer
        //------------------------

        FarmRenderer(

            modifier = Modifier.fillMaxSize(),

            farmState = farmState,

            cameraState = cameraState

        )

    }

}