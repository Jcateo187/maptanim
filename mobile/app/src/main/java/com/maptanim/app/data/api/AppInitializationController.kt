package com.maptanim.app.data.api

import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.remote.dto.UserStatusDto
import com.maptanim.app.data.repository.CropPlotRepositoryImpl
import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.FarmRepositoryImpl
import com.maptanim.app.data.repository.HarvestRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.data.repository.TaskRepositoryImpl
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class AppInitializationController {

    suspend fun initialize() {
        try {
            // 0. Account Suspension Check: verify user status if authenticated
            val currentUserId = try {
                SupabaseClient.client.auth.currentUserOrNull()?.id
            } catch (e: Exception) {
                null
            }

            if (!currentUserId.isNullOrBlank()) {
                try {
                    val userRow = SupabaseClient.client.from("users").select {
                        filter { eq("id", currentUserId) }
                    }.decodeSingleOrNull<UserStatusDto>()

                    if (userRow?.status.equals("SUSPENDED", ignoreCase = true)) {
                        RepositoryProvider.userRepository.logout()
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 1. Synchronize reference crops from Supabase to local Room database on launch
            try {
                (RepositoryProvider.cropRepository as? CropRepositoryImpl)?.fetchFromRemote()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Synchronize dynamic DSS companion rules from Supabase
            try {
                RepositoryProvider.dssRuleRepository.fetchFromRemote()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Synchronize broadcast information updates & advisories from Admin
            try {
                RepositoryProvider.userRepository.refreshNotifications()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. Cloud restore: synchronize authenticated user's farms, plots, tasks, and harvests
            if (!currentUserId.isNullOrBlank()) {
                try {
                    val farmRepo = RepositoryProvider.farmRepository as? FarmRepositoryImpl
                    val remoteFarms = farmRepo?.fetchFromRemote(currentUserId) ?: emptyList()

                    val plotRepo = RepositoryProvider.cropPlotRepository as? CropPlotRepositoryImpl
                    val taskRepo = RepositoryProvider.taskRepository as? TaskRepositoryImpl
                    val harvestRepo = RepositoryProvider.harvestRepository as? HarvestRepositoryImpl

                    for (farm in remoteFarms) {
                        try {
                            plotRepo?.fetchFromRemote(farm.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            taskRepo?.fetchFromRemote(farm.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            harvestRepo?.fetchFromRemote(farm.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
