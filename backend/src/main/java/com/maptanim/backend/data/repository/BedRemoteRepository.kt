package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class BedDto(
    val id: String,
    val farm_id: String,
    val bed_label: String,
    val crop_name: String? = null,
    val crop_id: String? = null,
    val soil_type: String = "LOAM",
    val pos_x: Float = 0f,
    val pos_y: Float = 0f,
    val width_m: Float = 2.0f,
    val height_m: Float = 3.0f,
    val rotation_deg: Float = 0f,
    val planted_date: String? = null,
    val is_active: Boolean = true,
    val notes: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

class BedRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getBedsForFarm(farmId: String): Result<List<BedDto>> {
        return try {
            val beds = client.postgrest["beds"]
                .select {
                    filter {
                        eq("farm_id", farmId)
                        eq("is_active", true)
                    }
                }
                .decodeList<BedDto>()
            Result.success(beds)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun upsertBed(bed: BedDto): Result<Unit> {
        return try {
            client.postgrest["beds"].upsert(bed)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun saveBeds(beds: List<BedDto>): Result<Unit> {
        return try {
            client.postgrest["beds"].upsert(beds)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteBed(bedId: String): Result<Unit> {
        return try {
            client.postgrest["beds"].update({ set("is_active", false) }) {
                filter { eq("id", bedId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
