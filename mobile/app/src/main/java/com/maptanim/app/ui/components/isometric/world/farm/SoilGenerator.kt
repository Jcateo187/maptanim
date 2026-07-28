package com.maptanim.app.ui.components.isometric.world.farm

/**
 * SoilGenerator — Maps (row, column, FarmArea) to smooth soil texture indices.
 *
 * Ensures farm ground renders as a seamless, high-quality agricultural bed surface.
 */
object SoilGenerator {

    const val SOIL_01 = 1 // Interior rich soil
    const val SOIL_02 = 2 // Top border
    const val SOIL_03 = 3 // Right border
    const val SOIL_04 = 4 // Bottom border
    const val SOIL_05 = 5 // Left border
    const val SOIL_06 = 6 // Corner TL
    const val SOIL_07 = 7 // Corner TR
    const val SOIL_08 = 8 // Corner BR / BL

    fun texture(
        row: Int,
        column: Int,
        farm: FarmArea
    ): Int {
        // Corners
        if (row == farm.top && column == farm.left) return SOIL_06
        if (row == farm.top && column == farm.right) return SOIL_07
        if (row == farm.bottom && column == farm.left) return SOIL_08
        if (row == farm.bottom && column == farm.right) return SOIL_08

        // Borders
        if (row == farm.top) return SOIL_02
        if (row == farm.bottom) return SOIL_04
        if (column == farm.left) return SOIL_05
        if (column == farm.right) return SOIL_03

        // Interior smooth soil
        return SOIL_01
    }
}