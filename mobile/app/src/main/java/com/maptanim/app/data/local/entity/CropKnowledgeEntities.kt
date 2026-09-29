package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 1. Yield Study Entity — Stores published, peer-reviewed agronomic research results.
 * Distinct studies (e.g. Camiguin 2015-2016 vs Bacnotan 2025) are strictly maintained as separate rows.
 */
@Entity(tableName = "crop_yield_studies")
data class CropYieldStudyEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "variety_name") val varietyName: String,
    @ColumnInfo(name = "study_code") val studyCode: String,
    @ColumnInfo(name = "study_title") val studyTitle: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "study_period") val studyPeriod: String,
    @ColumnInfo(name = "cultivation_condition") val cultivationCondition: String,
    @ColumnInfo(name = "treatment_description") val treatmentDescription: String,
    @ColumnInfo(name = "baseline_yield_t_per_ha") val baselineYieldTPerHa: Float?,
    @ColumnInfo(name = "reported_yield_t_per_ha") val reportedYieldTPerHa: Float,
    @ColumnInfo(name = "yield_increase_percent") val yieldIncreasePercent: Float?,
    @ColumnInfo(name = "key_findings") val keyFindings: String,
    @ColumnInfo(name = "authors") val authors: String,
    @ColumnInfo(name = "institution") val institution: String,
    @ColumnInfo(name = "publication_reference") val publicationReference: String
)

/**
 * 2. Crop Variety Entity — Stores varietal traits, timeline splits, and resistance traits.
 */
@Entity(tableName = "crop_varieties")
data class CropVarietyEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "variety_name") val varietyName: String,
    @ColumnInfo(name = "local_name_ph") val localNamePh: String?,
    @ColumnInfo(name = "breeder_organization") val breederOrganization: String?,
    @ColumnInfo(name = "growth_duration_days") val growthDurationDays: Int,
    @ColumnInfo(name = "stage1_sprout_days") val stage1SproutDays: Int,
    @ColumnInfo(name = "stage2_seedling_days") val stage2SeedlingDays: Int,
    @ColumnInfo(name = "stage3_vegetative_days") val stage3VegetativeDays: Int,
    @ColumnInfo(name = "stage4_flowering_days") val stage4FloweringDays: Int,
    @ColumnInfo(name = "stage5_harvest_days") val stage5HarvestDays: Int,
    @ColumnInfo(name = "watering_interval_days") val wateringIntervalDays: Int,
    @ColumnInfo(name = "fertilize_interval_days") val fertilizeIntervalDays: Int,
    @ColumnInfo(name = "optimal_seasons_csv") val optimalSeasonsCsv: String,
    @ColumnInfo(name = "disease_resistance") val diseaseResistance: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "source_citation") val sourceCitation: String
)

/**
 * 3. Crop Growth Stage Entity — Defines stage-by-stage agronomic actions and irrigation/nutrient protocols.
 */
@Entity(tableName = "crop_growth_stages")
data class CropGrowthStageEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "stage_index") val stageIndex: Int,
    @ColumnInfo(name = "stage_name") val stageName: String,
    @ColumnInfo(name = "day_start") val dayStart: Int,
    @ColumnInfo(name = "day_end") val dayEnd: Int,
    @ColumnInfo(name = "primary_farmer_action") val primaryFarmerAction: String,
    @ColumnInfo(name = "care_recommendation") val careRecommendation: String,
    @ColumnInfo(name = "irrigation_advice") val irrigationAdvice: String,
    @ColumnInfo(name = "nutrition_advice") val nutritionAdvice: String,
    @ColumnInfo(name = "critical_risks") val criticalRisks: String,
    @ColumnInfo(name = "source_citation") val sourceCitation: String
)

/**
 * 4. Crop Soil Compatibility Entity — Defines exact soil compatibility, amendments, and warnings.
 */
@Entity(tableName = "crop_soil_compatibilities")
data class CropSoilCompatibilityEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "soil_type") val soilType: String,
    @ColumnInfo(name = "suitability_rating") val suitabilityRating: String, // OPTIMAL, SUITABLE, MARGINAL, POOR
    @ColumnInfo(name = "suitability_score") val suitabilityScore: Float,
    @ColumnInfo(name = "agronomic_rationale") val agronomicRationale: String,
    @ColumnInfo(name = "amendment_action") val amendmentAction: String?,
    @ColumnInfo(name = "alert_warning") val alertWarning: String?,
    @ColumnInfo(name = "source_citation") val sourceCitation: String
)

/**
 * 5. Crop Pest and Disease Entity — Stores DA-BPI IPM guides with non-chemical controls.
 */
@Entity(tableName = "crop_pest_disease_guides")
data class CropPestDiseaseGuideEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "pest_disease_name") val pestDiseaseName: String,
    @ColumnInfo(name = "local_name_ph") val localNamePh: String?,
    @ColumnInfo(name = "scientific_name") val scientificName: String?,
    @ColumnInfo(name = "category") val category: String, // BACTERIAL, FUNGAL, VIRAL, INSECT
    @ColumnInfo(name = "risk_season") val riskSeason: String,
    @ColumnInfo(name = "critical_stage_index") val criticalStageIndex: Int?,
    @ColumnInfo(name = "symptoms") val symptoms: String,
    @ColumnInfo(name = "organic_biocontrol") val organicBiocontrol: String,
    @ColumnInfo(name = "cultural_prevention") val culturalPrevention: String,
    @ColumnInfo(name = "source_citation") val sourceCitation: String
)
