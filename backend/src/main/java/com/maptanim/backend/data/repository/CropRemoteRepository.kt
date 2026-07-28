package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class CropDto(
    val id: String,
    val name: String,
    val local_name: String? = null,
    val category: String = "FRUIT",
    val days_to_harvest: Int = 60,
    val watering_interval_days: Int = 2,
    val fertilize_interval_days: Int = 14,
    val image_url: String? = null
)

class CropRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getAllCrops(): Result<List<CropDto>> {
        return try {
            val crops = client.postgrest["crops"]
                .select()
                .decodeList<CropDto>()
            Result.success(crops)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
