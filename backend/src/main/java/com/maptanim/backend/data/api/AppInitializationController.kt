package com.maptanim.app.data.api

import com.maptanim.app.data.repository.BedRemoteRepository
import com.maptanim.app.data.repository.CropRemoteRepository
import com.maptanim.app.data.repository.FarmRemoteRepository
import com.maptanim.app.data.repository.TaskRemoteRepository
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
    private val bedRepository = BedRemoteRepository()
    private val taskRepository = TaskRemoteRepository()

    suspend fun initialize(farmerId: String? = null) {
        // 1. Fetch reference crop catalog from Supabase
        cropRepository.getAllCrops()

        // 2. If user is authenticated, fetch their farms, beds, and tasks
        farmerId?.let { uid ->
            val farmsResult = farmRepository.getFarmsForFarmer(uid)
            val farms = farmsResult.getOrNull() ?: emptyList()

            farms.forEach { farm ->
                bedRepository.getBedsForFarm(farm.id)
                taskRepository.getTodayTasks(farm.id, LocalDate.now().toString())
            }
        }
    }
}
