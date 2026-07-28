package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FarmEditState {

    //--------------------------------------
    // Resize Mode
    //--------------------------------------

    var isResizeMode by mutableStateOf(false)

    //--------------------------------------
    // Active Handle
    //--------------------------------------

    var activeHandle by mutableStateOf<HandleDirection?>(null)

}