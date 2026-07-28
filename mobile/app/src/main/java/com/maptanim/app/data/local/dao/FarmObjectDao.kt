package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maptanim.app.data.local.entity.FarmObjectEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FarmObjectDao {

    @Query("SELECT * FROM farm_objects WHERE farm_id = :farmId")
    fun observeObjectsByFarmId(farmId: String): Flow<List<FarmObjectEntity>>

    @Upsert
    suspend fun upsertObject(obj: FarmObjectEntity)

    @Upsert
    suspend fun upsertObjects(objects: List<FarmObjectEntity>)

    @Query("DELETE FROM farm_objects WHERE id = :objectId")
    suspend fun deleteObject(objectId: String)
}
