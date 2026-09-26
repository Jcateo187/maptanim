package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.DssDecisionDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.repository.DssRepository
import com.maptanim.app.dss.model.DssDecision
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DssRepositoryImpl(
    private val dssDecisionDao: DssDecisionDao?
) : DssRepository {

    override fun observeDecisions(farmId: String): Flow<List<DssDecision>> {
        return dssDecisionDao?.observeDecisions(farmId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: flowOf(emptyList())
    }

    override suspend fun getDecisions(farmId: String): List<DssDecision> = withContext(Dispatchers.IO) {
        dssDecisionDao?.getDecisions(farmId)?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun saveDecisions(farmId: String, decisions: List<DssDecision>): Unit = withContext(Dispatchers.IO) {
        val entities = decisions.map { it.toEntity() }
        dssDecisionDao?.replaceDecisions(farmId, entities)
        Unit
    }

    override suspend fun markDecisionCompleted(decisionId: String) = withContext(Dispatchers.IO) {
        dssDecisionDao?.markDecisionCompleted(decisionId)
        Unit
    }
}
