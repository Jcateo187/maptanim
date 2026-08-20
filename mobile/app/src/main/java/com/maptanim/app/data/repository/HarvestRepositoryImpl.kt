package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.HarvestDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.repository.HarvestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HarvestRepositoryImpl(
    private val harvestDao: HarvestDao
) : HarvestRepository {

    override fun observeHarvestRecords(farmId: String): Flow<List<HarvestRecord>> {
        return harvestDao.observeHarvestRecords(farmId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun recordHarvest(record: HarvestRecord) {
        harvestDao.upsertHarvest(record.toEntity())
    }
}
