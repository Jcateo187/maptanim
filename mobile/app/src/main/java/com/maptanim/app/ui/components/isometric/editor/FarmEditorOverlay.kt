package com.maptanim.app.ui.components.isometric.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.isometric.camera.CameraState
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState
import com.maptanim.app.ui.components.isometric.world.farm.FarmState

@Composable
fun FarmEditorOverlay(

    modifier: Modifier = Modifier,

    farmState: FarmState,

    cameraState: CameraState,

    farmEditState: FarmEditState

) {

    Box(

        modifier = modifier.fillMaxSize()

    ) {

        //--------------------------------------
        // Farm Border
        //--------------------------------------

        FarmBorderRenderer(

            modifier = Modifier.fillMaxSize(),

            farmState = farmState,

            cameraState = cameraState

        )

        //--------------------------------------
        // Resize Arrows
        //--------------------------------------

        FarmArrowRenderer(

            modifier = Modifier.fillMaxSize(),

            farmState = farmState,

            cameraState = cameraState,

            farmEditState = farmEditState

        )

    }

}