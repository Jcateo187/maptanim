package com.maptanim.app.features.library.model

import com.maptanim.app.domain.model.Crop

data class OverviewInfo(
    val summary: String,
    val botanicalName: String,
    val family: String,
    val culinaryUses: String,
    val regionalSuitability: String,
    val agriculturalImportance: String
)

data class VarietyDetail(
    val name: String,
    val localName: String,
    val daysToHarvest: Int,
    val characteristics: String,
    val diseaseResistance: String,
    val optimalSeason: String,
    val stageDays: Map<String, Int>? = null
)

data class GrowingSeasonInfo(
    val drySeasonStatus: String,
    val wetSeasonStatus: String,
    val optimalTemperature: String,
    val peakMonths: String,
    val climateRisks: String,
    val weatherTips: List<String>
)

data class SoilInfo(
    val idealSoilTypes: String,
    val optimalPh: String,
    val drainage: String,
    val landPrep: String,
    val organicMatter: String,
    val recommendations: List<String>
)

data class PlantingInfo(
    val method: String,
    val germinationDays: String,
    val transplantAge: String,
    val plantSpacing: String,
    val rowSpacing: String,
    val plantingDepth: String,
    val trellisingNeeded: Boolean,
    val trellisingAdvice: String?,
    val tips: List<String>
)

data class WateringInfo(
    val frequency: String,
    val bestTime: String,
    val criticalStages: String,
    val irrigationType: String,
    val moistureConservation: String,
    val warnings: List<String>
)

data class FertilizationInfo(
    val npkRatio: String,
    val basalApplication: String,
    val sideDressing: String,
    val organicOptions: String,
    val micronutrients: String,
    val schedule: List<String>
)

data class GrowthStageItem(
    val stageNumber: Int,
    val stageName: String,
    val durationDays: String,
    val description: String,
    val farmerAction: String
)

data class PestDiseaseItem(
    val name: String,
    val type: String, // "Insect Pest" or "Disease"
    val symptoms: String,
    val organicControl: String,
    val prevention: String,
    val chemicalControl: String? = null,
    val imageAsset: String? = null
)

data class CompanionInfo(
    val beneficialCompanions: List<String>,
    val companionBenefits: String,
    val plantsToAvoid: List<String>,
    val avoidReasons: String
)

data class IntercroppingInfo(
    val recommendedCrops: List<String>,
    val spatialLayout: String,
    val benefits: String,
    val managementAdvice: String
)

data class HarvestInfo(
    val maturityIndicators: String,
    val daysRange: String,
    val harvestingMethod: String,
    val timeOfDay: String,
    val frequency: String,
    val indicatorsList: List<String>
)

data class PostHarvestInfo(
    val sortingGrading: String,
    val washingCleaning: String,
    val storageConditions: String,
    val optimalTemperature: String,
    val relativeHumidity: String,
    val packagingTransport: String,
    val shelfLife: String
)

data class VegetableAgronomicGuide(
    val crop: Crop,
    val overview: OverviewInfo,
    val varieties: List<VarietyDetail>,
    val growingSeason: GrowingSeasonInfo,
    val soil: SoilInfo,
    val planting: PlantingInfo,
    val watering: WateringInfo,
    val fertilization: FertilizationInfo,
    val growthStages: List<GrowthStageItem>,
    val pestsAndDiseases: List<PestDiseaseItem>,
    val companionPlants: CompanionInfo,
    val intercropping: IntercroppingInfo,
    val harvest: HarvestInfo,
    val postHarvest: PostHarvestInfo
)

object AgronomicAssetHelper {
    fun resolvePestImage(pestName: String, explicitAsset: String? = null): String? {
        if (!explicitAsset.isNullOrBlank()) return explicitAsset
        val lower = pestName.lowercase()
        return when {
            lower.contains("fruit borer") || (lower.contains("borer") && !lower.contains("shoot")) -> "file:///android_asset/metadata/pest/Fruit_borer.png"
            lower.contains("leaf curl") || lower.contains("tylcv") -> "file:///android_asset/metadata/pest/Tomato_leaf_curlvirus.png"
            lower.contains("bacterial wilt") || lower.contains("ralstonia") -> "file:///android_asset/metadata/pest/Bacterial_wilt.png"
            lower.contains("leafminer") || lower.contains("miner") -> "file:///android_asset/metadata/pest/Vegetable_leafminer.png"
            lower.contains("flea beetle") || lower.contains("fleabeetle") -> "file:///android_asset/metadata/pest/Eggplant_and_brassica_fleabeetle.png"
            lower.contains("diamondback") || lower.contains("dbm") -> "file:///android_asset/metadata/pest/Diamondback_moth.png"
            lower.contains("aphid") -> "file:///android_asset/metadata/pest/Melon_and_cotton_aphids.png"
            lower.contains("anthracnose") -> "file:///android_asset/metadata/pest/Chilli_anthracnose_fruit_rot.png"
            lower.contains("downy mildew") || lower.contains("downy") -> "file:///android_asset/metadata/pest/Cucurbit_downy_mildew.png"
            lower.contains("powdery mildew") || lower.contains("powdery") -> "file:///android_asset/metadata/pest/Powdery_mildew.png"
            lower.contains("armyworm") || lower.contains("spodoptera") -> "file:///android_asset/metadata/pest/Fall_armyworm.png"
            lower.contains("thrip") -> "file:///android_asset/metadata/pest/Onion_thrips.png"
            else -> null
        }
    }

    fun resolveSoilImage(soilTypes: String): String {
        val lower = soilTypes.lowercase()
        return when {
            lower.contains("sandy loam") || lower.contains("sandy") -> "file:///android_asset/metadata/soil_images/Sandy_soil.png"
            lower.contains("clay loam") || lower.contains("clay") -> "file:///android_asset/metadata/soil_images/Clay_soil.png"
            lower.contains("silt") -> "file:///android_asset/metadata/soil_images/Silty_soil.png"
            lower.contains("peat") -> "file:///android_asset/metadata/soil_images/Peaty_soil.png"
            lower.contains("chalk") -> "file:///android_asset/metadata/soil_images/Chalky_soil.png"
            else -> "file:///android_asset/metadata/soil_images/Loam_soil.png"
        }
    }

    fun resolveCompanionCropImage(plantName: String): String? {
        val lower = plantName.lowercase()
        return when {
            lower.contains("tomato") || lower.contains("kamatis") -> "file:///android_asset/metadata/crops_images/tomato.png"
            lower.contains("eggplant") || lower.contains("talong") -> "file:///android_asset/metadata/crops_images/eggplant.png"
            lower.contains("pechay") || lower.contains("petsay") -> "file:///android_asset/metadata/crops_images/pechay.png"
            lower.contains("carrot") || lower.contains("karot") -> "file:///android_asset/metadata/crops_images/carrot.png"
            lower.contains("corn") || lower.contains("mais") -> "file:///android_asset/metadata/crops_images/corn.png"
            lower.contains("onion") || lower.contains("sibuyas") || lower.contains("garlic") -> "file:///android_asset/metadata/crops_images/onion.png"
            lower.contains("pipino") || lower.contains("cucumber") -> "file:///android_asset/metadata/crops_images/pipino.png"
            lower.contains("sitaw") || lower.contains("bean") -> "file:///android_asset/metadata/crops_images/sitaw.png"
            lower.contains("cabbage") || lower.contains("repolyo") -> "file:///android_asset/metadata/crops_images/cabbage.png"
            lower.contains("pepper") || lower.contains("sili") -> "file:///android_asset/metadata/crops_images/sili.png"
            lower.contains("ampalaya") || lower.contains("bitter") -> "file:///android_asset/metadata/crops_images/ampalaya.png"
            lower.contains("okra") -> "file:///android_asset/metadata/crops_images/okra.png"
            lower.contains("lettuce") || lower.contains("litsugas") -> "file:///android_asset/metadata/crops_images/lettuce.png"
            lower.contains("kangkong") -> "file:///android_asset/metadata/crops_images/kangkong.png"
            lower.contains("pumpkin") || lower.contains("kalabasa") || lower.contains("squash") -> "file:///android_asset/metadata/crops_images/pumpkin.png"
            else -> null
        }
    }
}
