package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.Bed
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.BedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class BedRepositoryImpl(
    private val remoteRepository: BedRemoteRepository = BedRemoteRepository()
) : BedRepository {

    private val bedsCache = MutableStateFlow<Map<String, List<Bed>>>(emptyMap())

    override fun observeBeds(farmId: String): Flow<List<Bed>> {
        return bedsCache.map { map ->
            map[farmId] ?: emptyList()
        }
    }

    override fun observeBed(bedId: String): Flow<Bed?> {
        return bedsCache.map { map ->
            map.values.flatten().firstOrNull { it.id == bedId }
        }
    }

    override fun observeAllBedsWithCrop(farmerId: String): Flow<List<Bed>> {
        return bedsCache.map { map ->
            map.values.flatten().filter { it.cropName != null }
        }
    }

    override suspend fun saveBeds(beds: List<Bed>) {
        val dtos = beds.map { it.toDto() }
        remoteRepository.saveBeds(dtos)
        val farmId = beds.firstOrNull()?.farmId ?: return
        val current = bedsCache.value.toMutableMap()
        current[farmId] = beds
        bedsCache.value = current
    }

    override suspend fun deleteBed(bedId: String) {
        remoteRepository.deleteBed(bedId)
        val current = bedsCache.value.toMutableMap()
        current.keys.forEach { farmId ->
            current[farmId] = current[farmId]?.filter { it.id != bedId } ?: emptyList()
        }
        bedsCache.value = current
    }

    override suspend fun upsertBed(bed: Bed) {
        remoteRepository.upsertBed(bed.toDto())
        val current = bedsCache.value.toMutableMap()
        val list = (current[bed.farmId] ?: emptyList()).filter { it.id != bed.id } + bed
        current[bed.farmId] = list
        bedsCache.value = current
    }

    suspend fun fetchFromRemote(farmId: String) {
        val result = remoteRepository.getBedsForFarm(farmId)
        result.getOrNull()?.let { dtos ->
            val beds = dtos.map { it.toDomain() }
            val current = bedsCache.value.toMutableMap()
            current[farmId] = beds
            bedsCache.value = current
        }
    }
}

private fun Bed.toDto(): BedDto = BedDto(
    id = id,
    farm_id = farmId,
    bed_label = bedLabel,
    crop_name = cropName,
    crop_id = cropId,
    soil_type = soilType.name,
    pos_x = posX,
    pos_y = posY,
    width_m = widthM,
    height_m = heightM,
    rotation_deg = rotationDeg,
    planted_date = plantedDate,
    is_active = isActive,
    notes = notes,
    created_at = createdAt,
    updated_at = updatedAt
)

private fun BedDto.toDomain(): Bed = Bed(
    id = id,
    farmId = farm_id,
    bedLabel = bed_label,
    cropName = crop_name,
    cropId = crop_id,
    soilType = runCatching { SoilType.valueOf(soil_type) }.getOrDefault(SoilType.LOAM),
    posX = pos_x,
    posY = pos_y,
    widthM = width_m,
    heightM = height_m,
    rotationDeg = rotation_deg,
    plantedDate = planted_date,
    isActive = is_active,
    notes = notes,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: ""
)
