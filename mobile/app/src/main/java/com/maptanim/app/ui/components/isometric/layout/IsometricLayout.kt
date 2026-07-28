package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraGesture
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState

@Composable
fun IsometricLayout(

    modifier: Modifier = Modifier,

    farmEditState: FarmEditState? = null

) {

    val cameraState = remember {

        CameraState()

    }

    val currentFarmEditState = remember {
        farmEditState ?: FarmEditState()
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

                cameraState = cameraState,

                farmEditState = currentFarmEditState

            )

        }

    }

}