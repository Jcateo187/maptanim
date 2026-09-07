package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.DssRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DssRuleDao {

    @Query("SELECT * FROM dss_rules")
    fun observeAllRules(): Flow<List<DssRuleEntity>>

    @Query("SELECT * FROM dss_rules")
    fun getAllRules(): List<DssRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertRules(rules: List<DssRuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertRule(rule: DssRuleEntity)

    @Query("DELETE FROM dss_rules WHERE id = :id")
    fun deleteRule(id: String): Int

    @Query("DELETE FROM dss_rules")
    fun deleteAllRules(): Int
}
