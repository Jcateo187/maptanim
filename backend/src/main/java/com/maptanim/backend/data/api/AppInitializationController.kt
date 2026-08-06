package com.maptanim.backend.data.api

import com.maptanim.backend.data.repository.CropPlotRemoteRepository
import com.maptanim.backend.data.repository.CropRemoteRepository
import com.maptanim.backend.data.repository.FarmRemoteRepository
import com.maptanim.backend.data.repository.TaskRemoteRepository
import java.time.LocalDate

/**
 * AppInitializationController
 *
 * Replaces old mock delay sequence with real data pre-fetching from Supabase PostgREST endpoints.
 * Executed during LoadingScreen on app launch.
 */
class AppInitializationController {

    private val cropRepository = CropRemoteRepository()
    private val farmRepository = FarmRemoteRepository()
    private val cropPlotRepository = CropPlotRemoteRepository()
    private val taskRepository = TaskRemoteRepository()

    suspend fun initialize(farmerId: String? = null) {
        // 1. Fetch reference crop catalog from Supabase
        cropRepository.getAllCrops()

        // 2. If user is authenticated, fetch their farms, plots, and tasks
        farmerId?.let { uid ->
            val farmsResult = farmRepository.getFarmsForFarmer(uid)
            val farms = farmsResult.getOrNull() ?: emptyList()

            farms.forEach { farm ->
                cropPlotRepository.getPlotsForFarm(farm.id)
                taskRepository.getTodayTasks(farm.id, LocalDate.now().toString())
            }
        }
    }
}
