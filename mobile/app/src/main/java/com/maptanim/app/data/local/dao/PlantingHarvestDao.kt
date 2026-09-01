package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.PlantingHarvestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantingHarvestDao {

    @Query("SELECT * FROM planting_harvests WHERE planting_id = :plantingId ORDER BY harvest_date DESC")
    fun observeHarvestsByPlantingId(plantingId: String): Flow<List<PlantingHarvestEntity>>

    @Query("SELECT * FROM planting_harvests ORDER BY harvest_date DESC")
    fun observeAllHarvests(): Flow<List<PlantingHarvestEntity>>

    @Query("SELECT * FROM planting_harvests WHERE id = :harvestId")
    fun observeHarvestById(harvestId: String): Flow<PlantingHarvestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertHarvest(harvest: PlantingHarvestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertHarvests(harvests: List<PlantingHarvestEntity>)

    @Query("DELETE FROM planting_harvests WHERE id = :harvestId")
    fun deleteHarvest(harvestId: String): Int

    @Query("DELETE FROM planting_harvests WHERE planting_id = :plantingId")
    fun deleteHarvestsByPlantingId(plantingId: String): Int
}
