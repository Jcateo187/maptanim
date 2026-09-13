package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.HarvestRecordDto
import io.github.jan.supabase.postgrest.from

/**
 * Remote Data Source for harvest records.
 * Handles network operations against Supabase PostgREST table `harvest_records`.
 */
class HarvestRemoteDataSource {

    suspend fun getHarvestsByFarm(farmId: String): Result<List<HarvestRecordDto>> {
        return try {
            val records = SupabaseClient.client
                .from("harvest_records")
                .select {
                    filter {
                        eq("farm_id", farmId)
                    }
                }
                .decodeList<HarvestRecordDto>()
            Result.success(records)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun recordHarvest(record: HarvestRecordDto): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("harvest_records")
                .upsert(record)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
