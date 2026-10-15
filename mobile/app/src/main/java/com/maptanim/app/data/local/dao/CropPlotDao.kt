package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.CropPlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropPlotDao {

    @Query("SELECT * FROM crop_plots WHERE farm_id = :farmId")
    fun observePlotsByFarmId(farmId: String): Flow<List<CropPlotEntity>>

    @Query("SELECT * FROM crop_plots WHERE farm_id = :farmId AND is_active = 1")
    fun observeActivePlotsByFarmId(farmId: String): Flow<List<CropPlotEntity>>

    @Query("SELECT * FROM crop_plots WHERE farm_id = :farmId AND is_active = 0")
    fun observeArchivedPlotsByFarmId(farmId: String): Flow<List<CropPlotEntity>>

    @Query("SELECT * FROM crop_plots WHERE id = :plotId")
    fun observePlotById(plotId: String): Flow<CropPlotEntity?>

    @Query("SELECT * FROM crop_plots WHERE id = :plotId LIMIT 1")
    fun getPlotById(plotId: String): CropPlotEntity?

    @Query("SELECT * FROM crop_plots WHERE farm_id = :farmId AND crop_name IS NOT NULL")
    fun observePlotsWithCrops(farmId: String): Flow<List<CropPlotEntity>>

    @Query("SELECT * FROM crop_plots WHERE crop_name IS NOT NULL AND crop_name != '' AND is_active = 1")
    fun observeAllPlotsWithCrop(): Flow<List<CropPlotEntity>>

    @Query("SELECT * FROM crop_plots WHERE is_active = 0")
    fun getAllArchivedPlots(): List<CropPlotEntity>

    @Query("UPDATE crop_plots SET plot_label = :newLabel, updated_at = :updatedAt WHERE id = :plotId")
    fun updatePlotLabel(plotId: String, newLabel: String, updatedAt: String): Int

    @Query("UPDATE crop_plots SET is_active = :isActive, updated_at = :updatedAt, notes = :notes WHERE id = :plotId")
    fun updatePlotActiveStatus(plotId: String, isActive: Boolean, updatedAt: String, notes: String?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertPlot(plot: CropPlotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertPlots(plots: List<CropPlotEntity>)

    @Query("DELETE FROM crop_plots WHERE id = :plotId")
    fun deletePlot(plotId: String): Int

    @Query("DELETE FROM crop_plots WHERE farm_id = :farmId AND is_active = 1 AND id NOT IN (:keepIds)")
    fun deletePlotsNotInList(farmId: String, keepIds: List<String>): Int

    @Query("DELETE FROM crop_plots WHERE farm_id = :farmId AND is_active = 1")
    fun deletePlotsByFarmId(farmId: String): Int
}
