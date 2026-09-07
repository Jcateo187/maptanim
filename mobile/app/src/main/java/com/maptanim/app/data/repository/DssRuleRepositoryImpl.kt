package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.DssRuleDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.DssRuleRemoteRepository
import com.maptanim.app.data.remote.dto.DssRuleDto
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.repository.DssRuleRepository
import com.maptanim.app.dss.engine.DssRule
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.dss.knowledgebase.CompanionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DssRuleRepositoryImpl(
    private val dssRuleDao: DssRuleDao? = null,
    private val remoteRepository: DssRuleRemoteRepository = DssRuleRemoteRepository()
) : DssRuleRepository {

    private val baselineRules: List<DssRule> by lazy {
        CompanionDataProvider.defaultCompanionMatrix.mapIndexed { index, entry ->
            DssRule(
                id = "baseline_rule_$index",
                cropA = entry.cropA,
                cropB = entry.cropB,
                relationship = entry.relationship,
                notes = entry.reason
            )
        }
    }

    private val inMemoryFallback = MutableStateFlow(baselineRules)

    override fun observeRules(): Flow<List<DssRule>> {
        return dssRuleDao?.observeAllRules()?.map { entities ->
            val rules = if (entities.isEmpty()) baselineRules else entities.map { it.toDomain() }
            syncToCompanionDataProvider(rules)
            rules
        } ?: inMemoryFallback.map { rules ->
            syncToCompanionDataProvider(rules)
            rules
        }
    }

    override suspend fun getAllRules(): List<DssRule> = withContext(Dispatchers.IO) {
        val entities = dssRuleDao?.getAllRules()
        val rules = if (entities.isNullOrEmpty()) baselineRules else entities.map { it.toDomain() }
        syncToCompanionDataProvider(rules)
        rules
    }

    override suspend fun fetchFromRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        val result = remoteRepository.getAllRules()
        result.mapCatching { dtos ->
            if (dtos.isNotEmpty()) {
                val domainRules = dtos.map { it.toDomain() }
                dssRuleDao?.upsertRules(domainRules.map { it.toEntity(source = "SUPABASE_ADMIN_SYNC") })
                inMemoryFallback.value = domainRules
                syncToCompanionDataProvider(domainRules)
            }
        }
    }

    override suspend fun upsertRules(rules: List<DssRule>) = withContext(Dispatchers.IO) {
        dssRuleDao?.upsertRules(rules.map { it.toEntity() })
        inMemoryFallback.value = rules
        syncToCompanionDataProvider(rules)
    }

    private fun syncToCompanionDataProvider(rules: List<DssRule>) {
        val entries = rules.map { rule ->
            CompanionEntry(
                cropA = rule.cropA,
                cropB = rule.cropB,
                relationship = rule.relationship,
                reason = rule.notes ?: ""
            )
        }
        CompanionDataProvider.updateMatrix(entries)
    }
}

private fun DssRuleDto.toDomain(): DssRule {
    val rel = try {
        CompanionRelation.valueOf(relationship.uppercase().trim())
    } catch (_: Exception) {
        CompanionRelation.NEUTRAL
    }
    return DssRule(
        id = id.ifBlank { "rule_${crop_a}_${crop_b}" },
        cropA = crop_a,
        cropB = crop_b,
        relationship = rel,
        notes = reason
    )
}
