package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.CropGrowthStageEntity
import com.maptanim.app.data.local.entity.CropPestDiseaseGuideEntity
import com.maptanim.app.data.local.entity.CropSoilCompatibilityEntity
import com.maptanim.app.data.local.entity.CropVarietyEntity
import com.maptanim.app.data.local.entity.CropYieldStudyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropYieldStudyDao {
    @Query("SELECT * FROM crop_yield_studies WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getStudiesForCrop(cropName: String): Flow<List<CropYieldStudyEntity>>

    @Query("SELECT * FROM crop_yield_studies WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getStudiesForCropSync(cropName: String): List<CropYieldStudyEntity>

    @Query("SELECT * FROM crop_yield_studies")
    fun getAllStudiesSync(): List<CropYieldStudyEntity>

    @Query("SELECT * FROM crop_yield_studies WHERE study_code = :studyCode LIMIT 1")
    fun getStudyByCode(studyCode: String): CropYieldStudyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(studies: List<CropYieldStudyEntity>)
}

@Dao
interface CropVarietyDao {
    @Query("SELECT * FROM crop_varieties WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getVarietiesForCrop(cropName: String): Flow<List<CropVarietyEntity>>

    @Query("SELECT * FROM crop_varieties WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getVarietiesForCropSync(cropName: String): List<CropVarietyEntity>

    @Query("SELECT * FROM crop_varieties")
    fun getAllVarietiesSync(): List<CropVarietyEntity>

    @Query("SELECT * FROM crop_varieties WHERE id = :id LIMIT 1")
    fun getVarietyById(id: String): CropVarietyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(varieties: List<CropVarietyEntity>)
}

@Dao
interface CropGrowthStageDao {
    @Query("SELECT * FROM crop_growth_stages WHERE LOWER(crop_name) = LOWER(:cropName) ORDER BY stage_index ASC")
    fun getStagesForCrop(cropName: String): Flow<List<CropGrowthStageEntity>>

    @Query("SELECT * FROM crop_growth_stages WHERE LOWER(crop_name) = LOWER(:cropName) ORDER BY stage_index ASC")
    fun getStagesForCropSync(cropName: String): List<CropGrowthStageEntity>

    @Query("SELECT * FROM crop_growth_stages ORDER BY stage_index ASC")
    fun getAllStagesSync(): List<CropGrowthStageEntity>

    @Query("SELECT * FROM crop_growth_stages WHERE LOWER(crop_name) = LOWER(:cropName) AND stage_index = :stageIndex LIMIT 1")
    fun getStageByIndex(cropName: String, stageIndex: Int): CropGrowthStageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stages: List<CropGrowthStageEntity>)
}

@Dao
interface CropSoilCompatibilityDao {
    @Query("SELECT * FROM crop_soil_compatibilities WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getCompatibilitiesForCrop(cropName: String): Flow<List<CropSoilCompatibilityEntity>>

    @Query("SELECT * FROM crop_soil_compatibilities WHERE LOWER(crop_name) = LOWER(:cropName) AND UPPER(soil_type) = UPPER(:soilType) LIMIT 1")
    fun getCompatibility(cropName: String, soilType: String): CropSoilCompatibilityEntity?

    @Query("SELECT * FROM crop_soil_compatibilities")
    fun getAllCompatibilitiesSync(): List<CropSoilCompatibilityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(compatibilities: List<CropSoilCompatibilityEntity>)
}

@Dao
interface CropPestDiseaseGuideDao {
    @Query("SELECT * FROM crop_pest_disease_guides WHERE LOWER(crop_name) = LOWER(:cropName)")
    fun getGuidesForCrop(cropName: String): Flow<List<CropPestDiseaseGuideEntity>>

    @Query("SELECT * FROM crop_pest_disease_guides WHERE LOWER(crop_name) = LOWER(:cropName) AND (risk_season = :season OR risk_season = 'YEAR_ROUND')")
    fun getSeasonalGuides(cropName: String, season: String): List<CropPestDiseaseGuideEntity>

    @Query("SELECT * FROM crop_pest_disease_guides")
    fun getAllGuidesSync(): List<CropPestDiseaseGuideEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(guides: List<CropPestDiseaseGuideEntity>)
}
