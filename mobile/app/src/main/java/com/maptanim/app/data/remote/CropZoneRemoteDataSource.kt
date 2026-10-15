package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.CropZoneDto
import io.github.jan.supabase.postgrest.from

/**
 * Remote Data Source for crop zone sub-regions.
 * Handles network operations against Supabase PostgREST table `crop_zones`.
 */
class CropZoneRemoteDataSource {

    suspend fun getZonesByPlotIds(plotIds: List<String>): Result<List<CropZoneDto>> {
        val validPlotIds = plotIds.filter { !it.contains("guest", ignoreCase = true) }
        if (validPlotIds.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            val zones = SupabaseClient.client
                .from("crop_zones")
                .select {
                    filter {
                        isIn("plot_id", validPlotIds)
                    }
                }
                .decodeList<CropZoneDto>()
            Result.success(zones)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun upsertZones(zones: List<CropZoneDto>): Result<Unit> {
        val validZones = zones.filter {
            !it.plot_id.contains("guest", ignoreCase = true) &&
            !it.id.contains("guest", ignoreCase = true)
        }
        if (validZones.isEmpty()) {
            return Result.success(Unit)
        }

        return try {
            SupabaseClient.client
                .from("crop_zones")
                .upsert(validZones)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteZone(zoneId: String): Result<Unit> {
        if (zoneId.contains("guest", ignoreCase = true)) {
            return Result.success(Unit)
        }

        return try {
            SupabaseClient.client
                .from("crop_zones")
                .delete {
                    filter {
                        eq("id", zoneId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
