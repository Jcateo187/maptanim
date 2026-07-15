package com.maptanim.app.ui.components.isometric.world.terrain

import kotlin.random.Random

object TerrainGenerator {

    fun generateTile(

        row: Int,

        column: Int

    ): TerrainTile {

        val variant = Random(

            row * 99991L + column

        ).nextInt(1,6)

        return TerrainTile(

            row = row,

            column = column,

            grassVariant = variant

        )

    }

}