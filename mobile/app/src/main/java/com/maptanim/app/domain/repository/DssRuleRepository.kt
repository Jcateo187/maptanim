package com.maptanim.app.domain.repository

import com.maptanim.app.dss.engine.DssRule
import kotlinx.coroutines.flow.Flow

interface DssRuleRepository {
    fun observeRules(): Flow<List<DssRule>>
    suspend fun getAllRules(): List<DssRule>
    suspend fun fetchFromRemote(): Result<Unit>
    suspend fun upsertRules(rules: List<DssRule>)
}
