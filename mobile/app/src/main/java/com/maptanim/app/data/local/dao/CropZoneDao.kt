package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maptanim.app.data.local.entity.CropZoneEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CropZoneDao {

    @Query("SELECT * FROM crop_zones WHERE bed_id = :bedId")
    fun observeZonesByBedId(bedId: String): Flow<List<CropZoneEntity>>

    @Query("SELECT * FROM crop_zones WHERE bed_id IN (:bedIds)")
    fun observeZonesByBedIds(bedIds: List<String>): Flow<List<CropZoneEntity>>

    @Upsert
    suspend fun upsertZone(zone: CropZoneEntity)

    @Upsert
    suspend fun upsertZones(zones: List<CropZoneEntity>)

    @Query("DELETE FROM crop_zones WHERE id = :zoneId")
    suspend fun deleteZone(zoneId: String)

    @Query("DELETE FROM crop_zones WHERE bed_id = :bedId")
    suspend fun deleteZonesByBedId(bedId: String)
}
