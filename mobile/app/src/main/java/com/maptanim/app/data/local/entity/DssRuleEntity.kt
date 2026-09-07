package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.dss.engine.DssRule

@Entity(tableName = "dss_rules")
data class DssRuleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_a") val cropA: String,
    @ColumnInfo(name = "crop_b") val cropB: String,
    @ColumnInfo(name = "relationship") val relationship: String,
    @ColumnInfo(name = "reason") val reason: String?,
    @ColumnInfo(name = "source") val source: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

fun DssRuleEntity.toDomain(): DssRule = DssRule(
    id = id,
    cropA = cropA,
    cropB = cropB,
    relationship = try {
        CompanionRelation.valueOf(relationship.uppercase().trim())
    } catch (_: Exception) {
        CompanionRelation.NEUTRAL
    },
    notes = reason
)

fun DssRule.toEntity(source: String? = null): DssRuleEntity = DssRuleEntity(
    id = id,
    cropA = cropA,
    cropB = cropB,
    relationship = relationship.name,
    reason = notes,
    source = source
)
