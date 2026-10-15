package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color

data class CarouselSlide(
    val title: String,
    val subtitle: String,
    val stage: String,
    val badgeColor: Color = Color(0xFF2E7D32),
    val drawableResId: Int? = null
)

data class QuickInfoItem(
    val key: String,
    val label: String,
    val value: String,
    val infoNote: String? = null,
    val isAlert: Boolean = false,
    val explanation: ExplanationData
)

data class PlantRelationItem(
    val name: String,
    val scientificName: String? = null,
    val isBeneficial: Boolean,
    val role: String,
    val explanation: ExplanationData,
    val drawableResId: Int? = null,
    val imageUrl: String? = null
)

data class NutritionBadgeItem(
    val code: String,
    val name: String,
    val percentageDaily: String,
    val role: String,
    val explanation: ExplanationData,
    val drawableResId: Int? = null,
    val imageUrl: String? = null
)

data class PestDiseaseItem(
    val name: String,
    val scientificName: String? = null,
    val severity: String,
    val symptoms: String,
    val prevention: String,
    val treatment: String,
    val explanation: ExplanationData,
    val drawableResId: Int? = null,
    val imageUrl: String? = null
)

data class CritterItem(
    val name: String,
    val role: String,
    val attractsWith: String,
    val explanation: ExplanationData,
    val drawableResId: Int? = null,
    val imageUrl: String? = null
)

data class SeasonalCalendarMonth(
    val month: String,
    val status: String,
    val notes: String = "",
    val badgeColor: Color = Color(0xFF2E7D32)
)

data class GoodBadPair(
    val plantName: String,
    val isGood: Boolean,
    val reason: String
)

data class GuideSectionItem(
    val id: String = "",
    val title: String,
    val summary: String = "",
    val fullContent: String = "",
    val tips: List<String> = emptyList()
)

data class VarietyInfo(
    val name: String,
    val producer: String = "",
    val type: String = "",
    val badge: String = "",
    val description: String = ""
)

data class PlantingCalendarData(
    val indoorRange: String = "Punlaan (Seedbed)",
    val outdoorRange: String = "Lipat-Tanim (Field)",
    val harvestRange: String = "Ani (Harvest)",
    val indoorLabel: String = "Punlaan (Seedbed)",
    val outdoorLabel: String = "Lipat-Tanim (Field)",
    val harvestLabel: String = "Ani (Harvest)",
    val plantTypeNote: String = "Pangunahing Panahon: Tag-araw (Okt–Abr); Off-season na may Rain Shelter (Mayo–Ago)",
    val monthsSchedule: List<Pair<String, String>> = emptyList()
)

data class TimelineStageItem(
    val durationDays: String? = null,
    val dayRange: String = durationDays ?: "",
    val stageName: String = "",
    val description: String = "",
    val keyAction: String = ""
)

data class SoilPrepData(
    val phRange: String,
    val soilTypes: String,
    val recommendation: String
)

data class LocationDifficultyData(
    val difficulty: String,
    val hardinessZone: String,
    val tempRange: String,
    val sunlight: String,
    val suitableAlso: String
)

data class HowToSectionItem(
    val title: String,
    val summary: String = "",
    val steps: List<String> = emptyList(),
    val tips: List<String> = emptyList(),
    val safety: String? = null
)

data class FaqItem(
    val question: String,
    val answer: String
)

data class NutritionRowData(
    val nutrient: String,
    val amount: String,
    val dailyValue: String = ""
)

data class NutritionTableData(
    val servingSize: String? = null,
    val portionSize: String = servingSize ?: "100 g",
    val calories: String = "0 kcal",
    val carbs: String = "0 g",
    val carbsPct: String = "0%",
    val sugar: String = "0 g",
    val fiber: String = "0 g",
    val fiberPct: String = "0%",
    val sucrose: String = "0 g",
    val summary: String = "",
    val macroNutrients: List<NutritionRowData> = emptyList(),
    val microNutrients: List<NutritionRowData> = emptyList()
)

data class ArticleItem(
    val title: String,
    val category: String = "Gabay",
    val excerpt: String = ""
)

data class ExplanationData(
    val title: String,
    val category: String = "IMPORMASYON",
    val description: String = "",
    val details: List<Pair<String, String>> = emptyList(),
    val growerTip: String? = null
) {
    constructor(
        title: String,
        summary: String,
        details: List<String> = emptyList(),
        growerTip: String? = null,
        category: String = "IMPORMASYON"
    ) : this(
        title = title,
        category = category,
        description = summary,
        details = details.map { "Punto" to it },
        growerTip = growerTip
    )
}

data class CropEncyclopedia(
    val cropName: String,
    val localName: String,
    val scientificName: String,
    val category: String,
    val family: String,
    val overview: String,
    val selectedVariety: VarietyInfo,
    val otherVarieties: List<String>,
    val carouselSlides: List<CarouselSlide>,
    val quickInfo: List<QuickInfoItem>,
    val companionPlants: List<PlantRelationItem>,
    val combativePlants: List<PlantRelationItem> = emptyList(),
    val nutritionBadges: List<NutritionBadgeItem>,

    // Support both pests/diseases or unified pestsAndDiseases
    val pestsAndDiseases: List<PestDiseaseItem> = emptyList(),
    val pests: List<PestDiseaseItem> = if (pestsAndDiseases.isNotEmpty()) pestsAndDiseases else emptyList(),
    val diseases: List<PestDiseaseItem> = emptyList(),

    // Support both beneficialCritters or goodAndBadCritters
    val goodAndBadCritters: List<CritterItem> = emptyList(),
    val beneficialCritters: List<CritterItem> = if (goodAndBadCritters.isNotEmpty()) goodAndBadCritters else emptyList(),

    // Support both growingGuides or careGuide
    val careGuide: List<HowToSectionItem> = emptyList(),
    val growingGuides: List<GuideSectionItem> = if (careGuide.isNotEmpty()) {
        careGuide.map {
            GuideSectionItem(
                id = it.title.lowercase().replace(" ", "_"),
                title = it.title,
                summary = it.summary,
                fullContent = it.steps.joinToString("\n• "),
                tips = it.tips
            )
        }
    } else emptyList(),

    // Support seasonalCalendar or plantingCalendar
    val seasonalCalendar: List<SeasonalCalendarMonth> = emptyList(),
    val plantingCalendar: PlantingCalendarData = PlantingCalendarData(
        indoorRange = "Punlaan (Seedbed)",
        outdoorRange = "Lipat-Tanim (Field)",
        harvestRange = "Ani (Harvest)",
        monthsSchedule = seasonalCalendar.map { it.month to it.status }
    ),

    // Location & Soil Prep defaults
    val locationData: LocationDifficultyData = LocationDifficultyData(
        difficulty = "Katamtaman (Moderate)",
        hardinessZone = "Highland / Lowland Philippines",
        tempRange = "15°C–32°C",
        sunlight = "Buong Araw (Full Sun)",
        suitableAlso = "Raised beds / Containers"
    ),
    val soilPrep: SoilPrepData = SoilPrepData(
        phRange = "6.0–6.8",
        soilTypes = "Buhaghag na Lupa (Well-drained loam)",
        recommendation = "Ihalo ang compost at Carbonized Rice Hull (CRH) bago magtanim."
    ),

    val growthTimeline: List<TimelineStageItem>,
    val howTos: List<HowToSectionItem> = emptyList(),
    val faqs: List<FaqItem>,
    val nutritionTable: NutritionTableData,
    val exploreArticles: List<ArticleItem>
)
