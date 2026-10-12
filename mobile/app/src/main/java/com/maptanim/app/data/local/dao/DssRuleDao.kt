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

    @Query("SELECT * FROM dss_rules WHERE (LOWER(crop_a) = LOWER(:cropA) AND LOWER(crop_b) = LOWER(:cropB)) OR (LOWER(crop_a) = LOWER(:cropB) AND LOWER(crop_b) = LOWER(:cropA)) LIMIT 1")
    fun findRelationship(cropA: String, cropB: String): DssRuleEntity?

    @Query("SELECT * FROM dss_rules WHERE LOWER(crop_a) = LOWER(:cropName) OR LOWER(crop_b) = LOWER(:cropName)")
    fun findRulesForCrop(cropName: String): List<DssRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertRules(rules: List<DssRuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertRule(rule: DssRuleEntity)

    @Query("DELETE FROM dss_rules WHERE id = :id")
    fun deleteRule(id: String): Int

    @Query("DELETE FROM dss_rules")
    fun deleteAllRules(): Int
}
