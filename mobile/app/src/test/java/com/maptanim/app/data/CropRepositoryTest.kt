package com.maptanim.app.data

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType
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
}
