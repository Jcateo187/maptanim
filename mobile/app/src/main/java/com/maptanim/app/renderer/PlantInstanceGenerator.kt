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

        val effectiveSpacingX = if (spacing < zone.widthM) spacing else zone.widthM
        val effectiveSpacingY = if (spacing < zone.heightM) spacing else zone.heightM

        val columns = Math.floor((zone.widthM / effectiveSpacingX).toDouble()).toInt().coerceAtLeast(1)
        val rows = Math.floor((zone.heightM / effectiveSpacingY).toDouble()).toInt().coerceAtLeast(1)

        val stepX = zone.widthM / columns
        val stepY = zone.heightM / rows

        val plants = mutableListOf<PlantInstanceRender>()
        // The caller passes the exact world origin of the zone (e.g. parentPlot.posX + zone.offsetX)
        val worldOriginX = plotPosX
        val worldOriginY = plotPosY

        for (row in 0 until rows) {
            val y = (row + 0.5f) * stepY
            for (col in 0 until columns) {
                val x = (col + 0.5f) * stepX
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

