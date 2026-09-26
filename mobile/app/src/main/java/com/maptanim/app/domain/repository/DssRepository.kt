package com.maptanim.app.domain.repository

import com.maptanim.app.dss.model.DssDecision
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing cached DSS decisions, recommendations,
 * alerts, and tasks. Decouples the DSS Engine from Room/Supabase implementation details.
 */
interface DssRepository {
    fun observeDecisions(farmId: String): Flow<List<DssDecision>>
    suspend fun getDecisions(farmId: String): List<DssDecision>
    suspend fun saveDecisions(farmId: String, decisions: List<DssDecision>)
    suspend fun markDecisionCompleted(decisionId: String)
}
