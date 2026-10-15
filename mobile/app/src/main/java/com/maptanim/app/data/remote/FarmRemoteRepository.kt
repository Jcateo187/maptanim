package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.FarmDto
import io.github.jan.supabase.postgrest.from

class FarmRemoteRepository {

    suspend fun getFarmsForFarmer(farmerId: String): Result<List<FarmDto>> {
        if (farmerId.equals("guest", ignoreCase = true)) {
            return Result.success(emptyList())
        }
        return try {
            val farms = SupabaseClient.client
                .from("farms")
                .select {
                    filter {
                        eq("farmer_id", farmerId)
                    }
                }
                .decodeList<FarmDto>()
            Result.success(farms)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun upsertFarm(farm: FarmDto): Result<Unit> {
        if (farm.farmer_id.equals("guest", ignoreCase = true)) {
            return Result.success(Unit)
        }
        return try {
            SupabaseClient.client
                .from("farms")
                .upsert(farm)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteFarm(farmId: String): Result<Unit> {
        if (farmId.contains("guest", ignoreCase = true)) {
            return Result.success(Unit)
        }
        return try {
            SupabaseClient.client
                .from("farms")
                .delete {
                    filter {
                        eq("id", farmId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
