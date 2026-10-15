package com.maptanim.app.features.farm.dialogs

import com.maptanim.app.features.farm.dialogs.vegetables_information.*

/**
 * Re-exports and backwards compatibility facade for Vegetable Encyclopedia Dossier models.
 * The implementation has been cleanly modularized into individual vegetable dossiers inside:
 * [com.maptanim.app.features.farm.dialogs.vegetables_information]
 *
 * Separate files:
 * - [CropEncyclopediaModels.kt] -> Core data models & interfaces
 * - [CropEncyclopediaRegistry.kt] -> Dispatcher & lookup service
 * - [TomatoInformation.kt] -> Comprehensive Philippine Tomato (Kamatis) dossier
 * - [EggplantInformation.kt] -> Comprehensive Philippine Eggplant (Talong) dossier
 * - [CabbageInformation.kt] -> Comprehensive Philippine Cabbage (Repolyo) dossier
 * - [CarrotInformation.kt] -> Philippine Carrot (Karot) dossier
 * - [PechayInformation.kt] -> Philippine Pechay / Bok Choy dossier
 * - [SiliInformation.kt] -> Philippine Chili / Sili dossier
 * - [KalabasaInformation.kt] -> Philippine Squash / Kalabasa dossier
 */

typealias CarouselSlide = com.maptanim.app.features.farm.dialogs.vegetables_information.CarouselSlide
typealias QuickInfoItem = com.maptanim.app.features.farm.dialogs.vegetables_information.QuickInfoItem
typealias PlantRelationItem = com.maptanim.app.features.farm.dialogs.vegetables_information.PlantRelationItem
typealias NutritionBadgeItem = com.maptanim.app.features.farm.dialogs.vegetables_information.NutritionBadgeItem
typealias PestDiseaseItem = com.maptanim.app.features.farm.dialogs.vegetables_information.PestDiseaseItem
typealias CritterItem = com.maptanim.app.features.farm.dialogs.vegetables_information.CritterItem
typealias SeasonalCalendarMonth = com.maptanim.app.features.farm.dialogs.vegetables_information.SeasonalCalendarMonth
typealias GoodBadPair = com.maptanim.app.features.farm.dialogs.vegetables_information.GoodBadPair
typealias TimelineStageItem = com.maptanim.app.features.farm.dialogs.vegetables_information.TimelineStageItem
typealias HowToSectionItem = com.maptanim.app.features.farm.dialogs.vegetables_information.HowToSectionItem
typealias FaqItem = com.maptanim.app.features.farm.dialogs.vegetables_information.FaqItem
typealias NutritionRowData = com.maptanim.app.features.farm.dialogs.vegetables_information.NutritionRowData
typealias NutritionTableData = com.maptanim.app.features.farm.dialogs.vegetables_information.NutritionTableData
typealias ArticleItem = com.maptanim.app.features.farm.dialogs.vegetables_information.ArticleItem
typealias ExplanationData = com.maptanim.app.features.farm.dialogs.vegetables_information.ExplanationData
typealias VarietyInfo = com.maptanim.app.features.farm.dialogs.vegetables_information.VarietyInfo
typealias CropEncyclopedia = com.maptanim.app.features.farm.dialogs.vegetables_information.CropEncyclopedia

/**
 * Facade registry forwarding to [com.maptanim.app.features.farm.dialogs.vegetables_information.CropEncyclopediaRegistry].
 */
object CropEncyclopediaRegistry {

    fun getCropEncyclopedia(cropName: String): CropEncyclopedia =
        com.maptanim.app.features.farm.dialogs.vegetables_information.CropEncyclopediaRegistry.getCropEncyclopedia(cropName)

    val tomatoData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.tomatoData
    val eggplantData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.eggplantData
    val cabbageData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.cabbageData
    val carrotData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.carrotData
    val pechayData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.pechayData
    val bellPepperData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.bellPepperData
    val sweetPepperData get() = bellPepperData
    val siliData get() = bellPepperData
    val potatoData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.potatoData
    val sitawData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.sitawData
    val greenOnionData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.greenOnionData
    val pumpkinData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.pumpkinData
    val squashData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.squashData
    val lettuceData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.lettuceData
    val ampalayaData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.ampalayaData
    val bitterGourdData get() = ampalayaData
    val cucumberData get() = com.maptanim.app.features.farm.dialogs.vegetables_information.cucumberData
    val pipinoData get() = cucumberData
}
