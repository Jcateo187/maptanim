package com.maptanim.app.ui.components.isometric.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.maptanim.app.ui.components.isometric.world.terrain.GrassDimensions

class CameraState {

    //--------------------------------------
    // Camera Position (World Center)
    //--------------------------------------

    var cameraRow by mutableIntStateOf(
        GrassDimensions.START_ROW
    )

    var cameraColumn by mutableIntStateOf(
        GrassDimensions.START_COLUMN
    )

    //--------------------------------------
    // Camera Transform
    //--------------------------------------

    var zoom by mutableFloatStateOf(1f)

    var offsetX by mutableFloatStateOf(0f)

    var offsetY by mutableFloatStateOf(0f)

    //--------------------------------------
    // Zoom Limits
    //--------------------------------------

    var minZoom by mutableFloatStateOf(1f)

    var maxZoom by mutableFloatStateOf(2.5f)

    //--------------------------------------
    // Pan Limits
    //--------------------------------------

    var maxPanX by mutableFloatStateOf(1200f)

    var maxPanY by mutableFloatStateOf(900f)

}