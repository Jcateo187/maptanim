package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.TilePlantingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TilePlantingDao {

    @Query("SELECT * FROM tile_plantings WHERE tile_id = :tileId AND is_active = 1")
    fun observePlantingsByTileId(tileId: String): Flow<List<TilePlantingEntity>>

    @Query("SELECT * FROM tile_plantings WHERE id = :plantingId")
    fun observePlantingById(plantingId: String): Flow<TilePlantingEntity?>

    @Query("SELECT * FROM tile_plantings WHERE is_active = 1")
    fun observeAllActivePlantings(): Flow<List<TilePlantingEntity>>

    @Query("SELECT * FROM tile_plantings WHERE current_stage = 'HARVEST' AND is_active = 1")
    fun observeReadyToHarvest(): Flow<List<TilePlantingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertPlanting(planting: TilePlantingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertPlantings(plantings: List<TilePlantingEntity>)

    @Query("DELETE FROM tile_plantings WHERE id = :plantingId")
    fun deletePlanting(plantingId: String): Int

    @Query("DELETE FROM tile_plantings WHERE tile_id = :tileId")
    fun deletePlantingsByTileId(tileId: String): Int
}
