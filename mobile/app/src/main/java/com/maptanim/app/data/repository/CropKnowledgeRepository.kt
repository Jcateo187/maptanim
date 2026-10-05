package com.maptanim.app.data.repository

import com.maptanim.app.data.local.AppDatabase
import com.maptanim.app.data.local.CropKnowledgeSeed
import com.maptanim.app.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CropKnowledgeRepository {
    suspend fun getYieldStudies(cropName: String): List<CropYieldStudyEntity>
    suspend fun getAllYieldStudies(): List<CropYieldStudyEntity>
    suspend fun getVarieties(cropName: String): List<CropVarietyEntity>
    suspend fun getAllVarieties(): List<CropVarietyEntity>
    suspend fun getGrowthStages(cropName: String): List<CropGrowthStageEntity>
    suspend fun getAllGrowthStages(): List<CropGrowthStageEntity>
    suspend fun getSoilCompatibility(cropName: String, soilType: String): CropSoilCompatibilityEntity?
    suspend fun getAllSoilCompatibilities(): List<CropSoilCompatibilityEntity>
    suspend fun getPestGuides(cropName: String, season: String): List<CropPestDiseaseGuideEntity>
    suspend fun getAllPestGuides(): List<CropPestDiseaseGuideEntity>
}

class CropKnowledgeRepositoryImpl(
    private val database: AppDatabase?
) : CropKnowledgeRepository {

    override suspend fun getYieldStudies(cropName: String): List<CropYieldStudyEntity> = withContext(Dispatchers.IO) {
        val dbStudies = database?.cropYieldStudyDao()?.getStudiesForCropSync(cropName).orEmpty()
        if (dbStudies.isNotEmpty()) {
            dbStudies
        } else {
            CropKnowledgeSeed.allYieldStudies.filter { it.cropName.equals(cropName, ignoreCase = true) }
        }
    }

    override suspend fun getAllYieldStudies(): List<CropYieldStudyEntity> = withContext(Dispatchers.IO) {
        val dbStudies = database?.cropYieldStudyDao()?.getAllStudiesSync().orEmpty()
        if (dbStudies.isNotEmpty()) dbStudies else CropKnowledgeSeed.allYieldStudies
    }

    override suspend fun getVarieties(cropName: String): List<CropVarietyEntity> = withContext(Dispatchers.IO) {
        val dbVarieties = database?.cropVarietyDao()?.getVarietiesForCropSync(cropName).orEmpty()
        if (dbVarieties.isNotEmpty()) {
            dbVarieties
        } else {
            CropKnowledgeSeed.allVarieties.filter { it.cropName.equals(cropName, ignoreCase = true) }
        }
    }

    override suspend fun getAllVarieties(): List<CropVarietyEntity> = withContext(Dispatchers.IO) {
        val dbVarieties = database?.cropVarietyDao()?.getAllVarietiesSync().orEmpty()
        if (dbVarieties.isNotEmpty()) dbVarieties else CropKnowledgeSeed.allVarieties
    }

    override suspend fun getGrowthStages(cropName: String): List<CropGrowthStageEntity> = withContext(Dispatchers.IO) {
        val dbStages = database?.cropGrowthStageDao()?.getStagesForCropSync(cropName).orEmpty()
        if (dbStages.isNotEmpty()) {
            dbStages
        } else {
            CropKnowledgeSeed.allGrowthStages.filter { it.cropName.equals(cropName, ignoreCase = true) }
        }
    }

    override suspend fun getAllGrowthStages(): List<CropGrowthStageEntity> = withContext(Dispatchers.IO) {
        val dbStages = database?.cropGrowthStageDao()?.getAllStagesSync().orEmpty()
        if (dbStages.isNotEmpty()) dbStages else CropKnowledgeSeed.allGrowthStages
    }

    override suspend fun getSoilCompatibility(cropName: String, soilType: String): CropSoilCompatibilityEntity? = withContext(Dispatchers.IO) {
        val dbCompat = database?.cropSoilCompatibilityDao()?.getCompatibility(cropName, soilType)
        if (dbCompat != null) {
            dbCompat
        } else {
            CropKnowledgeSeed.allSoilCompatibilities.firstOrNull {
                it.cropName.equals(cropName, ignoreCase = true) && it.soilType.equals(soilType, ignoreCase = true)
            }
        }
    }

    override suspend fun getAllSoilCompatibilities(): List<CropSoilCompatibilityEntity> = withContext(Dispatchers.IO) {
        val dbCompat = database?.cropSoilCompatibilityDao()?.getAllCompatibilitiesSync().orEmpty()
        if (dbCompat.isNotEmpty()) dbCompat else CropKnowledgeSeed.allSoilCompatibilities
    }

    override suspend fun getPestGuides(cropName: String, season: String): List<CropPestDiseaseGuideEntity> = withContext(Dispatchers.IO) {
        val dbGuides = database?.cropPestDiseaseGuideDao()?.getSeasonalGuides(cropName, season).orEmpty()
        if (dbGuides.isNotEmpty()) {
            dbGuides
        } else {
            CropKnowledgeSeed.allPestDiseaseGuides.filter {
                it.cropName.equals(cropName, ignoreCase = true) &&
                (it.riskSeason.equals(season, ignoreCase = true) || it.riskSeason.equals("YEAR_ROUND", ignoreCase = true))
            }
        }
    }

    override suspend fun getAllPestGuides(): List<CropPestDiseaseGuideEntity> = withContext(Dispatchers.IO) {
        val dbGuides = database?.cropPestDiseaseGuideDao()?.getAllGuidesSync().orEmpty()
        if (dbGuides.isNotEmpty()) dbGuides else CropKnowledgeSeed.allPestDiseaseGuides
    }
}
