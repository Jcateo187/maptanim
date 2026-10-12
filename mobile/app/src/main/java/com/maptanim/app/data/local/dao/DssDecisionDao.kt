package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.maptanim.app.data.local.entity.DssDecisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DssDecisionDao {

    @Query("SELECT * FROM dss_cached_decisions WHERE farm_id = :farmId ORDER BY cached_at DESC")
    fun observeDecisions(farmId: String): Flow<List<DssDecisionEntity>>

    @Query("SELECT * FROM dss_cached_decisions WHERE farm_id = :farmId ORDER BY cached_at DESC")
    fun getDecisions(farmId: String): List<DssDecisionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDecisions(decisions: List<DssDecisionEntity>)

    @Query("DELETE FROM dss_cached_decisions WHERE farm_id = :farmId")
    fun clearDecisionsForFarm(farmId: String): Int

    @Transaction
    fun replaceDecisions(farmId: String, decisions: List<DssDecisionEntity>) {
        clearDecisionsForFarm(farmId)
        insertDecisions(decisions)
    }

    @Query("UPDATE dss_cached_decisions SET is_completed = 1 WHERE id = :decisionId")
    fun markDecisionCompleted(decisionId: String): Int
}
