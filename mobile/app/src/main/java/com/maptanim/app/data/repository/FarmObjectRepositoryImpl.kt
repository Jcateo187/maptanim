package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.FarmObjectDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.model.FarmObject
import com.maptanim.app.domain.repository.FarmObjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FarmObjectRepositoryImpl(
    private val farmObjectDao: FarmObjectDao? = null
) : FarmObjectRepository {

    private val inMemoryCache = mutableMapOf<String, List<FarmObject>>()

    override fun observeObjectsByFarmId(farmId: String): Flow<List<FarmObject>> {
        return farmObjectDao?.observeObjectsByFarmId(farmId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: kotlinx.coroutines.flow.flowOf(inMemoryCache[farmId] ?: emptyList())
    }

    override suspend fun saveObjects(objects: List<FarmObject>) {
        farmObjectDao?.upsertObjects(objects.map { it.toEntity() })
        objects.groupBy { it.farmId }.forEach { (farmId, farmObjs) ->
            inMemoryCache[farmId] = farmObjs
        }
    }

    override suspend fun deleteObject(objectId: String) {
        farmObjectDao?.deleteObject(objectId)
        inMemoryCache.keys.forEach { farmId ->
            inMemoryCache[farmId] = inMemoryCache[farmId]?.filter { it.id != objectId } ?: emptyList()
        }
    }
}
