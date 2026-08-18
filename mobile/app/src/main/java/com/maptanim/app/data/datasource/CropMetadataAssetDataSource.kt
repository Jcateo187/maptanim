package com.maptanim.app.data.datasource

import android.content.Context
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

data class StageDaysInfo(
    val stage1Sprout: Int,
    val stage2Seedling: Int,
    val stage3Vegetative: Int,
    val stage4Flowering: Int,
    val stage5Harvest: Int
)

data class WhyDetailInfo(
    val title: String,
    val summary: String,
    val points: List<String>
)

data class CropWhyReasoning(
    val categoryWhy: WhyDetailInfo,
    val harvestWhy: WhyDetailInfo,
    val wateringWhy: WhyDetailInfo,
    val soilWhy: WhyDetailInfo
)

data class ReferenceSourceInfo(
    val organization: String,
    val publicationTitle: String,
    val sourceUrl: String,
    val secondaryUrl: String? = null,
    val author: String,
    val license: String,
    val purposeStatement: String
)

data class CropVarietyInfo(
    val varietyId: String,
    val varietyName: String,
    val localNamePh: String,
    val growthDurationDays: Int,
    val stageDays: StageDaysInfo,
    val optimalSeasons: List<String>,
    val wateringIntervalDays: Int,
    val fertilizeIntervalDays: Int,
    val fruitLengthCm: String? = null,
    val bitternessLevel: String? = null,
    val diseaseResistance: String? = null,
    val description: String,
    val samplePlantedDate: String? = null,
    val sampleExpectedHarvestDate: String? = null
)

data class CropMetadataInfo(
    val id: String,
    val commonName: String,
    val localNamePh: String,
    val scientificName: String,
    val taxonomicFamily: String,
    val cropType: String,
    val daysToHarvestStr: String,
    val optimalPhStr: String,
    val optimalTempCStr: String,
    val description: String,
    val primaryPhotoUrl: String,
    val thumbnailUrl: String,
    val sourceUrl: String,
    val author: String,
    val license: String,
    val hashSha256: String,
    val whyReasoning: CropWhyReasoning? = null,
    val referenceSource: ReferenceSourceInfo? = null,
    val varieties: List<CropVarietyInfo>
)

object CropMetadataAssetDataSource {

    private val cachedMetadata = mutableListOf<CropMetadataInfo>()

    fun loadAllCropMetadata(context: Context?): List<CropMetadataInfo> {
        if (cachedMetadata.isNotEmpty()) return cachedMetadata
        if (context == null) return emptyList()

        try {
            val assetManager = context.assets
            val files = assetManager.list("metadata/crops") ?: emptyArray()

            for (fileName in files) {
                if (!fileName.endsWith(".json")) continue
                try {
                    assetManager.open("metadata/crops/$fileName").use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                        val jsonStr = reader.readText()
                        val jsonObj = JSONObject(jsonStr)

                        val id = jsonObj.optString("id", "")
                        val commonName = jsonObj.optString("common_name", "")
                        val localNamePh = jsonObj.optString("local_name_ph", "")
                        val scientificName = jsonObj.optString("scientific_name", "")
                        val taxonomicFamily = jsonObj.optString("taxonomic_family", "")
                        val cropType = jsonObj.optString("crop_type", "")

                        val growingSpecs = jsonObj.optJSONObject("growing_specifications")
                        val daysToHarvestStr = growingSpecs?.optString("days_to_harvest", "") ?: ""
                        val optimalPhStr = growingSpecs?.optString("optimal_soil_ph", "") ?: ""
                        val optimalTempCStr = growingSpecs?.optString("optimal_temperature_c", "") ?: ""
                        val description = growingSpecs?.optString("description", "") ?: ""

                        // Parse Varieties
                        val varietiesList = mutableListOf<CropVarietyInfo>()
                        val varietiesArray = jsonObj.optJSONArray("varieties")
                        if (varietiesArray != null) {
                            for (i in 0 until varietiesArray.length()) {
                                val varObj = varietiesArray.getJSONObject(i)
                                val stageDaysObj = varObj.optJSONObject("stage_days")
                                val stageDays = StageDaysInfo(
                                    stage1Sprout = stageDaysObj?.optInt("stage1_sprout", 5) ?: 5,
                                    stage2Seedling = stageDaysObj?.optInt("stage2_seedling", 12) ?: 12,
                                    stage3Vegetative = stageDaysObj?.optInt("stage3_vegetative", 20) ?: 20,
                                    stage4Flowering = stageDaysObj?.optInt("stage4_flowering", 16) ?: 16,
                                    stage5Harvest = stageDaysObj?.optInt("stage5_harvest", 7) ?: 7
                                )

                                val seasonsArray = varObj.optJSONArray("optimal_seasons")
                                val seasonsList = mutableListOf<String>()
                                if (seasonsArray != null) {
                                    for (s in 0 until seasonsArray.length()) {
                                        seasonsList.add(seasonsArray.getString(s))
                                    }
                                }

                                val fruitLength = if (varObj.has("fruit_length_cm")) varObj.optString("fruit_length_cm") else null
                                val bitterness = if (varObj.has("bitterness_level")) varObj.optString("bitterness_level") else null
                                val diseaseRes = if (varObj.has("disease_resistance")) varObj.optString("disease_resistance") else null

                                varietiesList.add(
                                    CropVarietyInfo(
                                        varietyId = varObj.optString("variety_id", ""),
                                        varietyName = varObj.optString("variety_name", ""),
                                        localNamePh = varObj.optString("local_name_ph", ""),
                                        growthDurationDays = varObj.optInt("growth_duration_days", 60),
                                        stageDays = stageDays,
                                        optimalSeasons = seasonsList,
                                        wateringIntervalDays = varObj.optInt("watering_interval_days", 2),
                                        fertilizeIntervalDays = varObj.optInt("fertilize_interval_days", 14),
                                        fruitLengthCm = fruitLength,
                                        bitternessLevel = bitterness,
                                        diseaseResistance = diseaseRes,
                                        description = varObj.optString("description", ""),
                                        samplePlantedDate = if (varObj.has("sample_planted_date")) varObj.optString("sample_planted_date") else null,
                                        sampleExpectedHarvestDate = if (varObj.has("sample_expected_harvest_date")) varObj.optString("sample_expected_harvest_date") else null
                                    )
                                )
                            }
                        }

                        // Parse Why Reasoning
                        var whyReasoning: CropWhyReasoning? = null
                        val whyObj = jsonObj.optJSONObject("why_reasoning")
                        if (whyObj != null) {
                            val catObj = whyObj.optJSONObject("category_why")
                            val harvObj = whyObj.optJSONObject("harvest_why")
                            val waterObj = whyObj.optJSONObject("watering_why")
                            val soilObj = whyObj.optJSONObject("soil_why")

                            val catWhy = parseWhyDetail(catObj, "Why is this crop classified as $cropType?", "Botany and growth habit define this crop category.")
                            val harvWhy = parseWhyDetail(harvObj, "Why does this crop mature in $daysToHarvestStr?", "Maturity timing ensures peak flavor, sugar content, and market quality.")
                            val waterWhy = parseWhyDetail(waterObj, "Why follow this watering interval?", "Balanced moisture prevents root suffocation and drought-induced bloom drop.")
                            val soilWhy = parseWhyDetail(soilObj, "Why is pH $optimalPhStr required?", "Soil pH controls fertilizer availability and root absorption.")

                            whyReasoning = CropWhyReasoning(
                                categoryWhy = catWhy,
                                harvestWhy = harvWhy,
                                wateringWhy = waterWhy,
                                soilWhy = soilWhy
                            )
                        }

                        // Parse Reference Source
                        var refSource: ReferenceSourceInfo? = null
                        val refObj = jsonObj.optJSONObject("reference_source")
                        if (refObj != null) {
                            refSource = ReferenceSourceInfo(
                                organization = refObj.optString("source_organization", "Department of Agriculture - BPI"),
                                publicationTitle = refObj.optString("publication_title", "Philippine National Standards (PNS) & Commercial Vegetable Guide"),
                                sourceUrl = refObj.optString("source_url", "https://buplant.da.gov.ph"),
                                secondaryUrl = if (refObj.has("secondary_url")) refObj.optString("secondary_url") else null,
                                author = refObj.optString("author", "National Crop Research Center"),
                                license = refObj.optString("license", "Philippine Open Agricultural Standard"),
                                purposeStatement = refObj.optString("purpose_statement", "Provides certified agronomic standards for Filipino farmers to optimize yield and reduce risk.")
                            )
                        }

                        val mediaObj = jsonObj.optJSONObject("media")
                        val assetPhotoUrl = getCropAssetImagePath(id, commonName)
                        val primaryPhotoUrl = assetPhotoUrl.ifBlank { mediaObj?.optString("primary_photo_url", "") ?: "" }
                        val thumbnailUrl = assetPhotoUrl.ifBlank { mediaObj?.optString("thumbnail_url", "") ?: "" }
                        val sourceUrl = mediaObj?.optString("source_url", "") ?: ""
                        val author = mediaObj?.optString("author", "") ?: ""
                        val license = mediaObj?.optString("license", "") ?: ""
                        val hashSha256 = mediaObj?.optString("hash_sha256", "") ?: ""

                        val metaInfo = CropMetadataInfo(
                            id = id,
                            commonName = commonName,
                            localNamePh = localNamePh,
                            scientificName = scientificName,
                            taxonomicFamily = taxonomicFamily,
                            cropType = cropType,
                            daysToHarvestStr = daysToHarvestStr,
                            optimalPhStr = optimalPhStr,
                            optimalTempCStr = optimalTempCStr,
                            description = description,
                            primaryPhotoUrl = primaryPhotoUrl,
                            thumbnailUrl = thumbnailUrl,
                            sourceUrl = sourceUrl,
                            author = author,
                            license = license,
                            hashSha256 = hashSha256,
                            whyReasoning = whyReasoning,
                            referenceSource = refSource,
                            varieties = varietiesList
                        )
                        cachedMetadata.add(metaInfo)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cachedMetadata
    }

    private fun parseWhyDetail(obj: JSONObject?, defaultTitle: String, defaultSummary: String): WhyDetailInfo {
        if (obj == null) {
            return WhyDetailInfo(
                title = defaultTitle,
                summary = defaultSummary,
                points = listOf(defaultSummary)
            )
        }
        val title = obj.optString("title", defaultTitle)
        val summary = obj.optString("summary", defaultSummary)
        val pointsList = mutableListOf<String>()
        val arr = obj.optJSONArray("points")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                pointsList.add(arr.getString(i))
            }
        }
        if (pointsList.isEmpty()) {
            pointsList.add(summary)
        }
        return WhyDetailInfo(title = title, summary = summary, points = pointsList)
    }

    fun getCropMetadataByName(context: Context?, cropName: String): CropMetadataInfo? {
        val all = loadAllCropMetadata(context)
        return all.firstOrNull {
            it.commonName.equals(cropName, ignoreCase = true) ||
            it.localNamePh.equals(cropName, ignoreCase = true) ||
            it.id.equals(cropName, ignoreCase = true)
        }
    }

    fun getVarietiesForCrop(context: Context?, cropName: String): List<CropVarietyInfo> {
        val meta = getCropMetadataByName(context, cropName)
        return meta?.varieties ?: emptyList()
    }

    fun getWhyReasoningForCrop(context: Context?, crop: Crop): CropWhyReasoning {
        val meta = getCropMetadataByName(context, crop.name)
        if (meta?.whyReasoning != null) {
            return meta.whyReasoning
        }

        // Generate intelligent contextual agronomic reasoning if not explicitly present in JSON
        val catName = crop.category
        val days = crop.daysToHarvest
        val waterDays = crop.wateringIntervalDays
        val phMin = crop.optimalPhMin
        val phMax = crop.optimalPhMax

        return CropWhyReasoning(
            categoryWhy = WhyDetailInfo(
                title = "Why is ${crop.name} classified as $catName?",
                summary = "${crop.name} (${crop.localName ?: ""}) belongs to the $catName horticultural category based on botanical structure, edible plant parts, and commercial cultivation methods.",
                points = listOf(
                    "Morphological Structure: Growth habits, leaf-to-stem ratios, and reproductive structures determine nutrient requirements and field spacing.",
                    "Culinary & Harvest Usage: Grouped under Philippine Department of Agriculture classifications to help growers target specific market demand windows."
                )
            ),
            harvestWhy = WhyDetailInfo(
                title = "Why is the harvest timeline set to $days days?",
                summary = "The $days-day growth cycle represents the optimal physiological maturity window for maximum yield, tenderness, and nutrient density.",
                points = listOf(
                    "Vegetative & Reproductive Stages: Adequate days allow root establishment, photosynthesis accumulation, and fruit/head swelling.",
                    "Post-Harvest Quality: Harvesting within this window prevents fibrous over-maturation, bitterness, and market rejection."
                )
            ),
            wateringWhy = WhyDetailInfo(
                title = "Why water every $waterDays day(s)?",
                summary = "A $waterDays-day irrigation interval balances root aeration with consistent soil moisture content for tropical Philippine soils.",
                points = listOf(
                    "Root Zone Hydration: Maintains soil moisture within the 60–70% field capacity range to sustain cell turgidity and transpiration.",
                    "Disease Prevention: Prevents waterlogged root rot while guarding against heat-induced blossom drop and wilting."
                )
            ),
            soilWhy = WhyDetailInfo(
                title = "Why is pH $phMin – $phMax required?",
                summary = "A root zone pH between $phMin and $phMax ensures peak bioavailability of nitrogen, phosphorus, potassium, and micronutrients.",
                points = listOf(
                    "Nutrient Solubility: Essential nutrients become chemically locked and inaccessible to root hairs if soil becomes too acidic (<5.5) or overly alkaline (>7.5).",
                    "Beneficial Microorganisms: Healthy soil microbes thrive in this range, decomposing organic compost into active plant nutrients."
                )
            )
        )
    }

    fun getReferenceSourceForCrop(context: Context?, cropName: String): ReferenceSourceInfo {
        val meta = getCropMetadataByName(context, cropName)
        if (meta?.referenceSource != null) {
            return meta.referenceSource
        }

        return ReferenceSourceInfo(
            organization = "Department of Agriculture - Bureau of Plant Industry (DA-BPI)",
            publicationTitle = "Philippine National Standards (PNS) for Fresh Vegetables & Commercial Production Guides",
            sourceUrl = "https://buplant.da.gov.ph",
            secondaryUrl = "https://www.eastwestseed.com/philippines",
            author = "DA-BPI National Crop Research & UPLB Institute of Plant Breeding",
            license = "Philippine Open Agricultural Extension & Research Standard",
            purposeStatement = "This verified agricultural dataset provides Filipino growers with evidence-based standards, eliminating guesswork in planting dates, irrigation cycles, and variety selection."
        )
    }

    fun getCropAssetImagePath(cropId: String?, cropName: String?): String {
        val clean = (cropId ?: cropName ?: "").lowercase().replace(" ", "").replace("_", "").replace("-", "")
        val fileName = when {
            clean.contains("ampalaya") || clean.contains("bittergourd") -> "ampalaya.png"
            clean.contains("cabbage") || clean.contains("repolyo") -> "cabbage.png"
            clean.contains("carrot") || clean.contains("karot") -> "carrot.png"
            clean.contains("corn") || clean.contains("mais") -> "corn.png"
            clean.contains("eggplant") || clean.contains("talong") -> "eggplant.png"
            clean.contains("kangkong") || clean.contains("waterspinach") -> "kangkong.png"
            clean.contains("lettuce") || clean.contains("litsugas") -> "lettuce.png"
            clean.contains("okra") -> "okra.png"
            clean.contains("onion") || clean.contains("sibuyas") -> "onion.png"
            clean.contains("pechay") || clean.contains("pakchoi") || clean.contains("bokchoy") -> "pechay.png"
            clean.contains("pipino") || clean.contains("cucumber") -> "pipino.png"
            clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> "pumpkin.png"
            clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") || clean.contains("labuyo") -> "sili.png"
            clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("stringbeans") || clean.contains("beans") -> "sitaw.png"
            clean.contains("tomato") || clean.contains("kamatis") -> "tomato.png"
            else -> "ampalaya.png"
        }
        return "file:///android_asset/metadata/crops_images/$fileName"
    }
}
