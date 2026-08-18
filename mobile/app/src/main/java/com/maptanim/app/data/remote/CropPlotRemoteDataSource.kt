package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.CropPlotDto
import io.github.jan.supabase.postgrest.from

/**
 * Remote Data Source for direct-planted crop plot entities.
 * Handles network operations against Supabase PostgREST table `crop_plots`.
 */
class CropPlotRemoteDataSource {

    suspend fun getPlotsByFarm(farmId: String): Result<List<CropPlotDto>> {
        return try {
            val plots = SupabaseClient.client
                .from("crop_plots")
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
            SupabaseClient.client
                .from("crop_plots")
                .upsert(plot)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deletePlot(plotId: String): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("crop_plots")
                .delete {
                    filter {
                        eq("id", plotId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
