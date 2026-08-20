package com.maptanim.app.dss

import com.maptanim.app.domain.model.*
import com.maptanim.app.dss.engine.CompanionPlantsMatrix
import com.maptanim.app.dss.engine.DssRule
import com.maptanim.app.dss.engine.GrowthStageCalculator
import com.maptanim.app.dss.engine.SoilSuitabilityScorer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DssEngineTest {

    private val growthStageCalculator = GrowthStageCalculator()
    private val soilSuitabilityScorer = SoilSuitabilityScorer()
    private val companionPlantsMatrix = CompanionPlantsMatrix()

    @Test
    fun testGrowthStageCalculations() {
        val today = LocalDate.of(2026, 8, 6)
        val daysToHarvest = 60

        // Day 3 -> GERMINATION
        val germinationStage = growthStageCalculator.calculate(today.minusDays(3), daysToHarvest, today)
        assertEquals(GrowthStage.GERMINATION, germinationStage)

        // Day 14 -> EARLY_VEGETATIVE
        val earlyVegStage = growthStageCalculator.calculate(today.minusDays(14), daysToHarvest, today)
        assertEquals(GrowthStage.EARLY_VEGETATIVE, earlyVegStage)

        // Day 28 -> MID_VEGETATIVE
        val midVegStage = growthStageCalculator.calculate(today.minusDays(28), daysToHarvest, today)
        assertEquals(GrowthStage.MID_VEGETATIVE, midVegStage)

        // Day 42 -> FLOWERING
        val floweringStage = growthStageCalculator.calculate(today.minusDays(42), daysToHarvest, today)
        assertEquals(GrowthStage.FLOWERING, floweringStage)

        // Day 55 -> FRUITING
        val fruitingStage = growthStageCalculator.calculate(today.minusDays(55), daysToHarvest, today)
        assertEquals(GrowthStage.FRUITING, fruitingStage)

        // Day 60 -> HARVEST_READY
        val harvestReadyStage = growthStageCalculator.calculate(today.minusDays(60), daysToHarvest, today)
        assertEquals(GrowthStage.HARVEST_READY, harvestReadyStage)

        // Day 70 -> OVERDUE
        val overdueStage = growthStageCalculator.calculate(today.minusDays(70), daysToHarvest, today)
        assertEquals(GrowthStage.OVERDUE, overdueStage)
    }

    @Test
    fun testSoilSuitabilityScoring() {
        val crop = Crop(
            id = "crop_tomato",
            name = "Tomato",
            localName = "Kamatis",
            botanicalName = "Solanum lycopersicum",
            category = "FRUIT",
            daysToHarvest = 70,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            nRatio = 1f, pRatio = 1f, kRatio = 1f,
            optimalPhMin = 6.0f, optimalPhMax = 6.8f,
            idealSoils = listOf(SoilType.LOAM),
            suitableSoils = listOf(SoilType.SANDY),
            toleratedSoils = listOf(SoilType.CLAY),
            pestRiskSeason = listOf("DRY"),
            seasonality = listOf("YEAR_ROUND"),
            imageUrl = null
        )

        assertEquals(1.00f, soilSuitabilityScorer.score(SoilType.LOAM, crop), 0.01f)
        assertEquals(0.75f, soilSuitabilityScorer.score(SoilType.SANDY, crop), 0.01f)
        assertEquals(0.50f, soilSuitabilityScorer.score(SoilType.CLAY, crop), 0.01f)
        assertEquals(0.25f, soilSuitabilityScorer.score(SoilType.CHALKY, crop), 0.01f)
    }

    @Test
    fun testCompanionPlantingAntagonistAlert() {
        val plotA = CropPlot(
            id = "plot-1", farmId = "farm-1", plotLabel = "PLOT 1", cropName = "Tomato", cropId = "c1",
            soilType = SoilType.LOAM, posX = 0f, posY = 0f, widthM = 2f, heightM = 2f, rotationDeg = 0f,
            plantedDate = "2026-08-01", isActive = true, notes = null, createdAt = "", updatedAt = ""
        )

        val plotB = CropPlot(
            id = "plot-2", farmId = "farm-1", plotLabel = "PLOT 2", cropName = "Eggplant", cropId = "c2",
            soilType = SoilType.LOAM, posX = 1.0f, posY = 0f, widthM = 2f, heightM = 2f, rotationDeg = 0f,
            plantedDate = "2026-08-01", isActive = true, notes = null, createdAt = "", updatedAt = ""
        )

        val rule = DssRule(
            id = "rule-1", cropA = "Tomato", cropB = "Eggplant",
            relationship = CompanionRelation.ANTAGONIST,
            notes = "Same family, shared pests (fruit borer)"
        )

        val alerts = companionPlantsMatrix.evaluate(listOf(plotA, plotB), listOf(rule))
        assertEquals(1, alerts.size)
        assertEquals("Tomato", alerts[0].cropA)
        assertEquals("Eggplant", alerts[0].cropB)
    }
}
