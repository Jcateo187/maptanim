package com.maptanim.backend.data.repository

import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class FarmDto(
    val id: String,
    val farmer_id: String,
    val farm_name: String,
    val created_at: String? = null,
    val updated_at: String? = null
)



class FarmRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getFarmsForFarmer(farmerId: String): Result<List<FarmDto>> {
        return try {
            val farms = client.postgrest["farms"]
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
}
