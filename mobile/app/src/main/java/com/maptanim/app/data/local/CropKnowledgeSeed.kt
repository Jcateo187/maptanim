package com.maptanim.app.data.local

import com.maptanim.app.data.local.entity.*
import com.maptanim.app.data.local.seeds.*

/**
 * Unified Crop Knowledge Seed registry.
 * Seeds all 10 canonical Philippine crops into Room DB across 5 knowledge domains:
 * - Varieties (commercial & open-pollinated cultivars)
 * - Phenology growth stages (with stage-by-stage farmer actions)
 * - Soil compatibilities (ratings & amendment guidelines)
 * - Pest & disease guides (IPM biological & cultural controls)
 * - Published Philippine yield studies (benchmarks from Camiguin, UPLB, CLSU, BSU, etc.)
 */
object CropKnowledgeSeed {

    val allYieldStudies: List<CropYieldStudyEntity> = CropYieldStudySeeds.list
    val allVarieties: List<CropVarietyEntity> = CropVarietySeeds.list
    val allGrowthStages: List<CropGrowthStageEntity> = CropGrowthStageSeeds.list
    val allSoilCompatibilities: List<CropSoilCompatibilityEntity> = CropSoilCompatibilitySeeds.list
    val allPestDiseaseGuides: List<CropPestDiseaseGuideEntity> = CropPestDiseaseSeeds.list

    // Backward-compatible accessors
    val tomatoYieldStudies: List<CropYieldStudyEntity>
        get() = allYieldStudies.filter { it.cropName.equals("Tomato", ignoreCase = true) }

    val tomatoVarieties: List<CropVarietyEntity>
        get() = allVarieties.filter { it.cropName.equals("Tomato", ignoreCase = true) }

    val tomatoGrowthStages: List<CropGrowthStageEntity>
        get() = allGrowthStages.filter { it.cropName.equals("Tomato", ignoreCase = true) }

    val tomatoSoilCompatibilities: List<CropSoilCompatibilityEntity>
        get() = allSoilCompatibilities.filter { it.cropName.equals("Tomato", ignoreCase = true) }

    val tomatoPestDiseaseGuides: List<CropPestDiseaseGuideEntity>
        get() = allPestDiseaseGuides.filter { it.cropName.equals("Tomato", ignoreCase = true) }
}
