package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.CropLogDao
import com.maptanim.app.data.local.entity.CropLogEntity
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.domain.repository.CropLogRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CropLogRemoteDto(
    val id: String,
    @SerialName("crop_planting_id") val cropPlantingId: String,
    @SerialName("farm_id") val farmId: String = "",
    @SerialName("bed_id") val bedId: String? = null,
    @SerialName("crop_id") val cropId: String? = null,
    @SerialName("crop_name") val cropName: String = "",
    @SerialName("variety_id") val varietyId: String? = null,
    @SerialName("variety_name") val varietyName: String? = null,
    @SerialName("current_stage") val currentStage: String = "PREPARATION",
    @SerialName("log_context") val logContext: String = "PREPARATION",
    @SerialName("care_activity") val careActivity: String? = null,
    @SerialName("selected_choice") val selectedChoice: String = "A",
    @SerialName("selected_checkboxes") val selectedCheckboxes: List<String> = emptyList(),
    val notes: String? = null,
    @SerialName("log_date") val logDate: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

class CropLogRepositoryImpl(
    private val cropLogDao: CropLogDao?
) : CropLogRepository {

    override fun observeLogsForPlanting(cropPlantingId: String): Flow<List<CropLog>> {
        return cropLogDao?.observeLogsForPlanting(cropPlantingId)?.map { list ->
            list.map { it.toDomain() }
        } ?: flowOf(emptyList())
    }

    override fun observeLogsForBed(bedId: String): Flow<List<CropLog>> {
        return cropLogDao?.observeLogsForBed(bedId)?.map { list ->
            list.map { it.toDomain() }
        } ?: flowOf(emptyList())
    }

    override fun observeLogsForFarm(farmId: String): Flow<List<CropLog>> {
        return cropLogDao?.observeLogsForFarm(farmId)?.map { list ->
            list.map { it.toDomain() }
        } ?: flowOf(emptyList())
    }

    override suspend fun getLogsForPlanting(cropPlantingId: String): List<CropLog> = withContext(Dispatchers.IO) {
        cropLogDao?.getLogsForPlanting(cropPlantingId)?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getLatestLog(cropPlantingId: String): CropLog? = withContext(Dispatchers.IO) {
        cropLogDao?.getLatestLog(cropPlantingId)?.toDomain()
    }

    override suspend fun countLogsForStage(cropPlantingId: String, stage: ManagementStage): Int = withContext(Dispatchers.IO) {
        cropLogDao?.countLogsForStage(cropPlantingId, stage.name) ?: 0
    }

    override suspend fun insertLog(log: CropLog) = withContext(Dispatchers.IO) {
        cropLogDao?.upsertLog(log.toEntity())
        // Supabase sync (Phase 3)
        try {
            val dto = CropLogRemoteDto(
                id = log.id,
                cropPlantingId = log.cropPlantingId,
                farmId = log.farmId,
                bedId = log.bedId,
                cropId = log.cropId,
                cropName = log.cropName,
                varietyId = log.varietyId,
                varietyName = log.varietyName,
                currentStage = log.currentStage.name,
                logContext = log.logContext.name,
                careActivity = log.careActivity?.name,
                selectedChoice = log.selectedChoice,
                selectedCheckboxes = log.selectedCheckboxes,
                notes = log.notes,
                logDate = log.date,
                createdAt = log.createdAt
            )
            SupabaseClient.client.from("crop_logs").upsert(dto)
        } catch (_: Exception) {
            // Keep local Room cache resilient if offline or network fails
        }
        Unit
    }

    override suspend fun deleteLog(id: String) = withContext(Dispatchers.IO) {
        cropLogDao?.deleteLog(id)
        try {
            SupabaseClient.client.from("crop_logs").delete {
                filter {
                    eq("id", id)
                }
            }
        } catch (_: Exception) {}
        Unit
    }

    suspend fun syncLogsFromRemote(cropPlantingId: String) = withContext(Dispatchers.IO) {
        try {
            val remoteList = SupabaseClient.client.from("crop_logs").select {
                filter {
                    eq("crop_planting_id", cropPlantingId)
                }
            }.decodeList<CropLogRemoteDto>()

            val entities = remoteList.map { dto ->
                CropLogEntity(
                    id = dto.id,
                    cropPlantingId = dto.cropPlantingId,
                    farmId = dto.farmId,
                    bedId = dto.bedId ?: "",
                    cropId = dto.cropId,
                    cropName = dto.cropName,
                    varietyId = dto.varietyId,
                    varietyName = dto.varietyName,
                    currentStage = dto.currentStage,
                    logContext = dto.logContext,
                    careActivity = dto.careActivity,
                    selectedChoice = dto.selectedChoice,
                    selectedCheckboxes = dto.selectedCheckboxes.joinToString("||"),
                    notes = dto.notes,
                    date = dto.logDate,
                    createdAt = dto.createdAt ?: ""
                )
            }
            entities.forEach { cropLogDao?.upsertLog(it) }
        } catch (_: Exception) {}
    }
}
