package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.CropDto
import io.github.jan.supabase.postgrest.from

class CropRemoteRepository {

    suspend fun getAllCrops(): Result<List<CropDto>> {
        return try {
            val crops = SupabaseClient.client
                .from("crops")
                .select()
                .decodeList<CropDto>()
            Result.success(crops)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
