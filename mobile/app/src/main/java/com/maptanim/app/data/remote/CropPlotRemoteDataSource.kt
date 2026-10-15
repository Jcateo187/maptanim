package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.CropPlotDto
import io.github.jan.supabase.postgrest.from
import java.util.UUID

/**
 * Remote Data Source for direct-planted crop plot entities.
 * Handles network operations against Supabase PostgREST table `crop_plots`.
 */
class CropPlotRemoteDataSource {

    suspend fun getPlotsByFarm(farmId: String): Result<List<CropPlotDto>> {
        if (farmId.contains("guest", ignoreCase = true) || farmId.isBlank()) {
            return Result.success(emptyList())
        }
        return try {
            val plots = SupabaseClient.client
                .from("crop_plots")
                .select {
                    filter {
                        eq("farm_id", farmId)
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
        if (plot.farm_id.contains("guest", ignoreCase = true) || 
            plot.id.contains("guest", ignoreCase = true) ||
            plot.id.isBlank() ||
            plot.farm_id.isBlank()) {
            return Result.success(Unit)
        }
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
        if (plotId.contains("guest", ignoreCase = true) || plotId.isBlank()) {
            return Result.success(Unit)
        }
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
