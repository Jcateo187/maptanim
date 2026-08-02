package com.maptanim.app.renderer

import com.maptanim.app.renderer.model.CropZoneRenderData
import com.maptanim.app.renderer.model.PlantInstanceRender

/**
 * PlantInstanceGenerator — Calculates individual plant positions and sprite scale factors
 * inside a CropZone.
 *
 * Positions are calculated strictly in world floating-point coordinates relative to the plot.
 * Spacing dictates how many plants fit inside the zone.
 * Zone area dictates foliage scale so larger planting areas automatically produce bigger, denser foliage.
 */
object PlantInstanceGenerator {

    /**
     * Generates a grid of plant instances inside the given crop zone using a 2D spatial grid-packing algorithm.
     *
     * Mathematical Formula:
     * - Columns = floor(Zone Width / S)
     * - Rows    = floor(Zone Height / S)
     * - Plant X = col * S + (S / 2)  [Centered horizontally in grid cell]
     * - Plant Y = row * S + (S / 2)  [Centered vertically in grid cell]
     */
    fun generate(
        zone: CropZoneRenderData,
        plotPosX: Float,
        plotPosY: Float
    ): List<PlantInstanceRender> {
        val cropName = zone.cropName ?: return emptyList()
        val spacing = if (zone.spacingM > 0f) zone.spacingM else 1.0f

        val columns = Math.floor((zone.widthM / spacing).toDouble()).toInt().coerceAtLeast(1)
        val rows = Math.floor((zone.heightM / spacing).toDouble()).toInt().coerceAtLeast(1)

        val plants = mutableListOf<PlantInstanceRender>()
        val worldOriginX = plotPosX + zone.offsetX
        val worldOriginY = plotPosY + zone.offsetY

        for (row in 0 until rows) {
            val y = row * spacing + (spacing / 2f)
            for (col in 0 until columns) {
                val x = col * spacing + (spacing / 2f)
                plants += PlantInstanceRender(
                    worldX = worldOriginX + x,
                    worldY = worldOriginY + y,
                    scaleFactor = 1.0f,
                    cropName = cropName,
                    growthStage = zone.growthStage
                )
            }
        }
        return plants
    }
}

