package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssPriority

@Entity(tableName = "dss_cached_decisions")
data class DssDecisionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "plot_id") val plotId: String?,
    @ColumnInfo(name = "plot_label") val plotLabel: String?,
    @ColumnInfo(name = "crop_name") val cropName: String?,
    @ColumnInfo(name = "decision_type") val decisionType: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "priority") val priority: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "summary") val summary: String,
    @ColumnInfo(name = "explanation") val explanation: String,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "action_text") val actionText: String?,
    @ColumnInfo(name = "action_task_type") val actionTaskType: String?,
    @ColumnInfo(name = "rule_id") val ruleId: String?,
    @ColumnInfo(name = "is_actionable") val isActionable: Boolean = true,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "evaluated_at") val evaluatedAt: String,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)

fun DssDecisionEntity.toDomain(): DssDecision = DssDecision(
    id = id,
    farmId = farmId,
    plotId = plotId,
    plotLabel = plotLabel,
    cropName = cropName,
    decisionType = runCatching { DssDecisionType.valueOf(decisionType) }.getOrDefault(DssDecisionType.RECOMMENDATION),
    category = runCatching { DssCategory.valueOf(category) }.getOrDefault(DssCategory.GROWTH_CARE),
    priority = runCatching { DssPriority.valueOf(priority) }.getOrDefault(DssPriority.MEDIUM),
    title = title,
    summary = summary,
    explanation = explanation,
    source = source,
    actionText = actionText,
    actionTaskType = actionTaskType?.let { runCatching { TaskType.valueOf(it) }.getOrNull() },
    ruleId = ruleId,
    isActionable = isActionable,
    isCompleted = isCompleted,
    evaluatedAt = evaluatedAt
)

fun DssDecision.toEntity(): DssDecisionEntity = DssDecisionEntity(
    id = id,
    farmId = farmId,
    plotId = plotId,
    plotLabel = plotLabel,
    cropName = cropName,
    decisionType = decisionType.name,
    category = category.name,
    priority = priority.name,
    title = title,
    summary = summary,
    explanation = explanation,
    source = source,
    actionText = actionText,
    actionTaskType = actionTaskType?.name,
    ruleId = ruleId,
    isActionable = isActionable,
    isCompleted = isCompleted,
    evaluatedAt = evaluatedAt
)
