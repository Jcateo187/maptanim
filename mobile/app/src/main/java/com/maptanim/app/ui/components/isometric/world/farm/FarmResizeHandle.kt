package com.maptanim.app.ui.components.isometric.world.farm

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun FarmResizeHandle(

    position: Offset,

    direction: HandleDirection,

    onDragStart: () -> Unit,

    onDrag: (Offset) -> Unit,

    onDragEnd: () -> Unit

) {

    //--------------------------------------
    // Drag Buffer
    //--------------------------------------

    var accumulatedDrag by remember {

        mutableStateOf(Offset.Zero)

    }

    //--------------------------------------
    // Icon
    //--------------------------------------

    val icon = when (direction) {

        HandleDirection.NORTH -> Icons.Default.ArrowUpward

        HandleDirection.SOUTH -> Icons.Default.ArrowDownward

        HandleDirection.EAST -> Icons.Default.KeyboardArrowRight

        HandleDirection.WEST -> Icons.Default.KeyboardArrowLeft

        HandleDirection.NORTH_EAST -> Icons.Default.NorthEast

        HandleDirection.NORTH_WEST -> Icons.Default.NorthWest

        HandleDirection.SOUTH_EAST -> Icons.Default.SouthEast

        HandleDirection.SOUTH_WEST -> Icons.Default.SouthWest

    }

    //--------------------------------------
    // Draw Handle
    //--------------------------------------

    Icon(

        imageVector = icon,

        contentDescription = null,

        tint = Color(0xFFFF9800),

        modifier = Modifier

            .offset {

                IntOffset(

                    (position.x - 24f).toInt(),

                    (position.y - 24f).toInt()

                )

            }

            .size(48.dp)

            .pointerInput(Unit) {

                detectDragGestures(

                    onDragStart = {

                        accumulatedDrag = Offset.Zero

                        onDragStart()

                    },

                    onDrag = { change, dragAmount ->

                        change.consume()

                        accumulatedDrag += dragAmount

                        //--------------------------------------
                        // Move one tile at a time
                        //--------------------------------------

                        if (

                            abs(accumulatedDrag.x) >= 32f ||

                            abs(accumulatedDrag.y) >= 32f

                        ) {

                            onDrag(accumulatedDrag)

                            accumulatedDrag = Offset.Zero

                        }

                    },

                    onDragEnd = {

                        accumulatedDrag = Offset.Zero

                        onDragEnd()

                    }

                )

            }

    )

}