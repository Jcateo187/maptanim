package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FarmState {

    // Center of world
    var centerRow by mutableStateOf(1000)

    var centerColumn by mutableStateOf(1000)

    // Small farm for testing
    var northSize by mutableStateOf(16)

    var southSize by mutableStateOf(16)

    var eastSize by mutableStateOf(16)

    var westSize by mutableStateOf(16)

}