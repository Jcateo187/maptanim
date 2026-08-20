package com.maptanim.app.data.local.dao

import androidx.room.*
import com.maptanim.app.data.local.entity.HarvestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestDao {
    @Query("SELECT * FROM harvest_records WHERE farm_id = :farmId ORDER BY harvested_at DESC")
    fun observeHarvestRecords(farmId: String): Flow<List<HarvestEntity>>

    @Upsert
    fun upsertHarvest(record: HarvestEntity)

    @Query("DELETE FROM harvest_records WHERE id = :id")
    fun deleteHarvest(id: String): Int
}
