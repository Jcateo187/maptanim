package com.maptanim.backend.data.repository

import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CropPlotDto(
    val id: String = "",
    @SerialName("farm_id") val farm_id: String,
    @SerialName("plot_label") val plot_label: String,
    @SerialName("crop_name") val crop_name: String? = null,
    @SerialName("crop_id") val crop_id: String? = null,
    @SerialName("crop_variety") val crop_variety: String? = null,
    @SerialName("soil_type") val soil_type: String = "LOAM",
    @SerialName("pos_x") val pos_x: Float = 0f,
    @SerialName("pos_y") val pos_y: Float = 0f,
    @SerialName("width_m") val width_m: Float = 2.0f,
    @SerialName("height_m") val height_m: Float = 3.0f,
    @SerialName("rotation_deg") val rotation_deg: Float = 0f,
    @SerialName("planted_date") val planted_date: String? = null,
    @SerialName("is_active") val is_active: Boolean = true,
    val notes: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)

class CropPlotRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getPlotsForFarm(farmId: String): Result<List<CropPlotDto>> {
        return try {
            val plots = client.postgrest["crop_plots"]
                .select {
                    filter {
                        eq("farm_id", farmId)
                        eq("is_active", true)
                    }
                }
                .decodeList<CropPlotDto>()
            Result.success(plots)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun upsertPlot(plot: CropPlotDto): Result<Unit> {
        return try {
            client.postgrest["crop_plots"].upsert(plot)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun savePlots(plots: List<CropPlotDto>): Result<Unit> {
        return try {
            client.postgrest["crop_plots"].upsert(plots)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deletePlot(plotId: String): Result<Unit> {
        return try {
            client.postgrest["crop_plots"].delete {
                filter { eq("id", plotId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
