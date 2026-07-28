package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.maptanim.app.ui.components.isometric.world.terrain.GrassDimensions

class FarmState {

    //--------------------------------------
    // Farm Center
    //--------------------------------------

    var centerRow by mutableIntStateOf(
        GrassDimensions.START_ROW
    )

    var centerColumn by mutableIntStateOf(
        GrassDimensions.START_COLUMN
    )

    //--------------------------------------
    // Default Farm Size
    //--------------------------------------

    companion object {

        const val DEFAULT_HALF_SIZE = 16

    }

    //--------------------------------------
    // Expansion
    //--------------------------------------

    var northSize by mutableIntStateOf(DEFAULT_HALF_SIZE)

    var southSize by mutableIntStateOf(DEFAULT_HALF_SIZE)

    var eastSize by mutableIntStateOf(DEFAULT_HALF_SIZE)

    var westSize by mutableIntStateOf(DEFAULT_HALF_SIZE)

    //--------------------------------------
    // Bounds
    //--------------------------------------

    val top
        get() = centerRow - northSize

    val bottom
        get() = centerRow + southSize

    val left
        get() = centerColumn - westSize

    val right
        get() = centerColumn + eastSize

}