package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.CropEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDao {

    @Query("SELECT * FROM crops")
    fun observeAllCrops(): Flow<List<CropEntity>>

    @Query("SELECT * FROM crops WHERE LOWER(name) = LOWER(:name) OR LOWER(local_name) = LOWER(:name) LIMIT 1")
    fun observeCropByName(name: String): Flow<CropEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCrop(crop: CropEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCrops(crops: List<CropEntity>)

    @Query("DELETE FROM crops")
    fun deleteAllCrops(): Int
}
