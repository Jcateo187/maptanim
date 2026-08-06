package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.CropZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropZoneDao {

    @Query("SELECT * FROM crop_zones WHERE plot_id = :plotId")
    fun observeZonesByPlotId(plotId: String): Flow<List<CropZoneEntity>>

    @Query("SELECT * FROM crop_zones WHERE plot_id IN (:plotIds)")
    fun observeZonesByPlotIds(plotIds: List<String>): Flow<List<CropZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertZone(zone: CropZoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertZones(zones: List<CropZoneEntity>)

    @Query("DELETE FROM crop_zones WHERE id = :zoneId")
    fun deleteZone(zoneId: String): Int

    @Query("DELETE FROM crop_zones WHERE plot_id = :plotId")
    fun deleteZonesByPlotId(plotId: String): Int
}
