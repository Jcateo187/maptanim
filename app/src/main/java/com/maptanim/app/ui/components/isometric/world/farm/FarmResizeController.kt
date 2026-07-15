package com.maptanim.app.ui.components.isometric.world.farm

object FarmResizeController {

    fun expandNorth(state: FarmState) {

        state.northSize++

    }

    fun expandSouth(state: FarmState) {

        state.southSize++

    }

    fun expandEast(state: FarmState) {

        state.eastSize++

    }

    fun expandWest(state: FarmState) {

        state.westSize++

    }

}