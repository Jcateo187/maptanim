package com.maptanim.app.renderer

import com.maptanim.app.renderer.model.CropZoneRenderData
import com.maptanim.app.renderer.model.PlantInstanceRender

/**
 * PlantInstanceGenerator — Calculates individual plant positions and sprite scale factors
 * inside a CropZone.
 *
 * Positions are calculated strictly in world floating-point coordinates relative to the bed.
 * Spacing dictates how many plants fit inside the zone.
 * Zone area dictates foliage scale so larger planting areas automatically produce bigger, denser foliage.
 */
object PlantInstanceGenerator {

    /**
     * Generates a grid of plant instances inside the given crop zone.
     *
     * @param zone CropZoneRenderData with offset and dimensions
     * @param bedPosX Absolute world X of the parent bed origin
     * @param bedPosY Absolute world Y of the parent bed origin
     * @return List of PlantInstanceRender with world coordinates and scale factors
     */
    fun generate(
        zone: CropZoneRenderData,
        bedPosX: Float,
        bedPosY: Float
    ): List<PlantInstanceRender> {
        val cropName = zone.cropName ?: return emptyList()
        val spacing = zone.spacingM.coerceAtLeast(0.15f)
        
        // Calculate foliage scale factor based on zone area
        val zoneArea = (zone.widthM * zone.heightM).coerceAtLeast(0.1f)
        // Area of 1m² = scale 1.0f; scales smoothly between 0.6f and 2.5f
        val scaleFactor = (Math.sqrt(zoneArea.toDouble()).toFloat() * 0.8f).coerceIn(0.6f, 2.5f)

        val plants = mutableListOf<PlantInstanceRender>()
        val worldOriginX = bedPosX + zone.offsetX
        val worldOriginY = bedPosY + zone.offsetY

        var y = spacing / 2f
        while (y < zone.heightM) {
            var x = spacing / 2f
            while (x < zone.widthM) {
                plants += PlantInstanceRender(
                    worldX = worldOriginX + x,
                    worldY = worldOriginY + y,
                    scaleFactor = scaleFactor,
                    cropName = cropName
                )
                x += spacing
            }
            y += spacing
        }
        return plants
    }
}
