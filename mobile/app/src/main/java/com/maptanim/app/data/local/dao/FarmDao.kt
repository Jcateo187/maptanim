package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.FarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    @Query("SELECT * FROM farms WHERE farmer_id = :farmerId")
    fun observeFarmsByFarmerId(farmerId: String): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE id = :farmId")
    fun observeFarmById(farmId: String): Flow<FarmEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFarm(farm: FarmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFarms(farms: List<FarmEntity>)

    @Query("DELETE FROM farms WHERE id = :farmId")
    fun deleteFarm(farmId: String): Int
}
