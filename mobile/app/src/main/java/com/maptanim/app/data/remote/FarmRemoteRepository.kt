package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.FarmDto
import io.github.jan.supabase.postgrest.from

class FarmRemoteRepository {

    suspend fun getFarmsForFarmer(farmerId: String): Result<List<FarmDto>> {
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
}
