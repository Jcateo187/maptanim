package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.*

/**
 * Room entity for the crop_logs table.
 * Stores farmer observation/action logs from the Crop Management "Add Log" flow.
 *
 * Columns use snake_case to match Supabase conventions for future sync.
 * JSON list fields (selected_checkboxes) are stored as comma-separated strings.
 */
@Entity(tableName = "crop_logs")
data class CropLogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_planting_id") val cropPlantingId: String,
    @ColumnInfo(name = "farm_id") val farmId: String = "farm-1",
    @ColumnInfo(name = "bed_id") val bedId: String = "",
    @ColumnInfo(name = "crop_id") val cropId: String? = null,
    @ColumnInfo(name = "crop_name") val cropName: String = "",
    @ColumnInfo(name = "variety_id") val varietyId: String? = null,
    @ColumnInfo(name = "variety_name") val varietyName: String? = null,
    @ColumnInfo(name = "current_stage") val currentStage: String = ManagementStage.PREPARATION.name,
    @ColumnInfo(name = "log_context") val logContext: String = LogContext.PREPARATION.name,
    @ColumnInfo(name = "selected_choice") val selectedChoice: String = "A",
    @ColumnInfo(name = "selected_checkboxes") val selectedCheckboxes: String = "",
    @ColumnInfo(name = "care_activity") val careActivity: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "created_at") val createdAt: String = ""
)

// ─── Entity ↔ Domain Mappers ──────────────────────────────────────────────

fun CropLogEntity.toDomain() = CropLog(
    id = id,
    cropPlantingId = cropPlantingId,
    farmId = farmId,
    bedId = bedId,
    cropId = cropId,
    cropName = cropName,
    varietyId = varietyId,
    varietyName = varietyName,
    currentStage = try { ManagementStage.valueOf(currentStage) } catch (_: Exception) { ManagementStage.PREPARATION },
    logContext = try { LogContext.valueOf(logContext) } catch (_: Exception) { LogContext.PREPARATION },
    selectedChoice = selectedChoice,
    selectedCheckboxes = if (selectedCheckboxes.isBlank()) emptyList() else selectedCheckboxes.split("||"),
    careActivity = careActivity?.let { try { CareActivity.valueOf(it) } catch (_: Exception) { null } },
    notes = notes,
    date = date,
    createdAt = createdAt
)

fun CropLog.toEntity() = CropLogEntity(
    id = id,
    cropPlantingId = cropPlantingId,
    farmId = farmId,
    bedId = bedId,
    cropId = cropId,
    cropName = cropName,
    varietyId = varietyId,
    varietyName = varietyName,
    currentStage = currentStage.name,
    logContext = logContext.name,
    selectedChoice = selectedChoice,
    selectedCheckboxes = selectedCheckboxes.joinToString("||"),
    careActivity = careActivity?.name,
    notes = notes,
    date = date,
    createdAt = createdAt
)
