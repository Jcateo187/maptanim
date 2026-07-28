package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.ui.geometry.Offset

object FarmResizeController {

    const val MIN_SIZE = 16
    const val MAX_SIZE = 150

    fun handleDrag(

        direction: HandleDirection,

        drag: Offset,

        state: FarmState

    ) {

        val threshold = 8f

        when (direction) {

            //------------------------------------
            // NORTH
            //------------------------------------

            HandleDirection.NORTH -> {

                if (drag.y < -threshold)
                    expandNorth(state)

                if (drag.y > threshold)
                    shrinkNorth(state)
            }

            //------------------------------------
            // SOUTH
            //------------------------------------

            HandleDirection.SOUTH -> {

                if (drag.y > threshold)
                    expandSouth(state)

                if (drag.y < -threshold)
                    shrinkSouth(state)
            }

            //------------------------------------
            // EAST
            //------------------------------------

            HandleDirection.EAST -> {

                if (drag.x > threshold)
                    expandEast(state)

                if (drag.x < -threshold)
                    shrinkEast(state)
            }

            //------------------------------------
            // WEST
            //------------------------------------

            HandleDirection.WEST -> {

                if (drag.x < -threshold)
                    expandWest(state)

                if (drag.x > threshold)
                    shrinkWest(state)
            }

            //------------------------------------
            // NORTH EAST
            //------------------------------------

            HandleDirection.NORTH_EAST -> {

                if (drag.y < -threshold)
                    expandNorth(state)

                if (drag.x > threshold)
                    expandEast(state)
            }

            //------------------------------------
            // NORTH WEST
            //------------------------------------

            HandleDirection.NORTH_WEST -> {

                if (drag.y < -threshold)
                    expandNorth(state)

                if (drag.x < -threshold)
                    expandWest(state)
            }

            //------------------------------------
            // SOUTH EAST
            //------------------------------------

            HandleDirection.SOUTH_EAST -> {

                if (drag.y > threshold)
                    expandSouth(state)

                if (drag.x > threshold)
                    expandEast(state)
            }

            //------------------------------------
            // SOUTH WEST
            //------------------------------------

            HandleDirection.SOUTH_WEST -> {

                if (drag.y > threshold)
                    expandSouth(state)

                if (drag.x < -threshold)
                    expandWest(state)
            }

        }

    }

    //------------------------------------------------

    private fun expandNorth(state: FarmState) {

        if (state.northSize < MAX_SIZE)
            state.northSize++

    }

    private fun shrinkNorth(state: FarmState) {

        if (state.northSize > MIN_SIZE)
            state.northSize--

    }

    private fun expandSouth(state: FarmState) {

        if (state.southSize < MAX_SIZE)
            state.southSize++

    }

    private fun shrinkSouth(state: FarmState) {

        if (state.southSize > MIN_SIZE)
            state.southSize--

    }

    private fun expandEast(state: FarmState) {

        if (state.eastSize < MAX_SIZE)
            state.eastSize++

    }

    private fun shrinkEast(state: FarmState) {

        if (state.eastSize > MIN_SIZE)
            state.eastSize--

    }

    private fun expandWest(state: FarmState) {

        if (state.westSize < MAX_SIZE)
            state.westSize++

    }

    private fun shrinkWest(state: FarmState) {

        if (state.westSize > MIN_SIZE)
            state.westSize--

    }

}