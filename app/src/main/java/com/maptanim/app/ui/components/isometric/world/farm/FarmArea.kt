package com.maptanim.app.ui.components.isometric.world.farm

data class FarmArea(

    val top: Int,

    val bottom: Int,

    val left: Int,

    val right: Int

)

fun FarmState.getFarmArea(): FarmArea {

    return FarmArea(

        top = centerRow - northSize,

        bottom = centerRow + southSize,

        left = centerColumn - westSize,

        right = centerColumn + eastSize

    )

}