package com.maptanim.app.ui.components.isometric.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.editor.FarmEditorOverlay
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState
import com.maptanim.app.ui.components.isometric.world.farm.FarmRenderer
import com.maptanim.app.ui.components.isometric.world.farm.FarmState
import com.maptanim.app.ui.components.isometric.world.terrain.GrassRenderer

@Composable
fun IsometricScene(

    modifier: Modifier = Modifier,

    cameraState: CameraState,

    farmEditState: FarmEditState

) {

    val farmState = remember {

        FarmState()

    }

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        //------------------------
        // Grass
        //------------------------

        GrassRenderer(

            modifier = Modifier.fillMaxSize(),

            cameraState = cameraState

        )

        //------------------------
        // Farm
        //------------------------

        FarmRenderer(

            modifier = Modifier.fillMaxSize(),

            farmState = farmState,

            cameraState = cameraState,

            farmEditState = farmEditState

        )

        if (farmEditState.isResizeMode) {

            FarmEditorOverlay(

                modifier = Modifier.fillMaxSize(),

                farmState = farmState,

                cameraState = cameraState,

                farmEditState = farmEditState

            )

        }

    }

}