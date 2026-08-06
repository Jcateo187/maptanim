package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.FarmObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmObjectDao {

    @Query("SELECT * FROM farm_objects WHERE farm_id = :farmId")
    fun observeObjectsByFarmId(farmId: String): Flow<List<FarmObjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertObject(obj: FarmObjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertObjects(objects: List<FarmObjectEntity>)

    @Query("DELETE FROM farm_objects WHERE id = :objectId")
    fun deleteObject(objectId: String): Int
}
