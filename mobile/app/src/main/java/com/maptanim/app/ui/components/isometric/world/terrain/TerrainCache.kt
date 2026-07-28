package com.maptanim.app.ui.components.isometric.world.terrain

object TerrainCache {

    private val cache = HashMap<Pair<Int, Int>, TerrainTile>()

    fun get(

        row: Int,

        column: Int

    ): TerrainTile {

        val key = row to column

        return cache.getOrPut(key) {

            TerrainGenerator.generateTile(

                row,

                column

            )

        }

    }

}