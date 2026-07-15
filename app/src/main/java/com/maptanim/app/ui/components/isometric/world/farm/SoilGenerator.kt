package com.maptanim.app.ui.components.isometric.world.farm

import kotlin.math.abs

object SoilGenerator {

    fun texture(

        row: Int,

        column: Int

    ): Int {

        val seed = abs(

            row * 92821 +
                    column * 68917

        )

        return seed % 8 + 1

    }

}