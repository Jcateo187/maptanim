package com.maptanim.app.data

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.ui.components.editcomponents.croptray.toCropOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CropRepositoryTest {

    @Test
    fun testCropDomainModelMapping() {
        val sampleCrop = Crop(
            id = "crop_tomato",
            name = "Tomato",
            localName = "Kamatis",
            botanicalName = "Solanum lycopersicum",
            category = "FRUIT",
            daysToHarvest = 70,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            nRatio = 1.5f,
            pRatio = 1.0f,
            kRatio = 2.0f,
            optimalPhMin = 6.0f,
            optimalPhMax = 6.8f,
            idealSoils = listOf(SoilType.LOAM, SoilType.SANDY),
            suitableSoils = listOf(SoilType.SILTY),
            toleratedSoils = listOf(SoilType.CLAY),
            pestRiskSeason = listOf("DRY"),
            seasonality = listOf("YEAR_ROUND"),
            imageUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co/storage/v1/object/public/crop-images/tomato.png",
            companionPlants = listOf("Lettuce", "Carrot"),
            avoidPlants = listOf("Eggplant"),
            commonPests = listOf("Fruit Borer"),
            harvestIndicators = "Deep red color, firm fruit",
            description = "High-value fruit vegetable sensitive to moisture."
        )

        assertNotNull(sampleCrop)
        assertEquals("Tomato", sampleCrop.name)
        assertEquals("Kamatis", sampleCrop.localName)
        assertEquals(70, sampleCrop.daysToHarvest)
        assertEquals(2, sampleCrop.companionPlants.size)
        assertEquals("Lettuce", sampleCrop.companionPlants[0])
    }

    @Test
    fun testCropToCropOptionMapping() {
        val cropWithCustomUrl = Crop(
            id = "crop_dragonfruit",
            name = "Dragon Fruit",
            localName = "Pitahaya",
            botanicalName = "Selenicereus costaricensis",
            category = "Fruit",
            daysToHarvest = 180,
            wateringIntervalDays = 3,
            fertilizeIntervalDays = 14,
            nRatio = 1.0f,
            pRatio = 1.0f,
            kRatio = 2.0f,
            optimalPhMin = 6.0f,
            optimalPhMax = 7.0f,
            idealSoils = listOf(SoilType.LOAM),
            suitableSoils = listOf(SoilType.SANDY),
            toleratedSoils = emptyList(),
            pestRiskSeason = listOf("WET"),
            seasonality = listOf("YEAR_ROUND"),
            imageUrl = "https://cdn.maptanim.com/crops/dragonfruit.webp"
        )

        val cropOption = cropWithCustomUrl.toCropOption()
        assertEquals("crop_dragonfruit", cropOption.id)
        assertEquals("Dragon Fruit", cropOption.name)
        assertEquals("Pitahaya", cropOption.localName)
        assertEquals("Fruit", cropOption.category)
        assertEquals("https://cdn.maptanim.com/crops/dragonfruit.webp", cropOption.imageUrl)
        assertEquals(true, cropOption.hasAsset)
    }
}
