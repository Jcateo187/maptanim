package com.maptanim.app.data.api

import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.CropPlotRepositoryImpl
import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.FarmRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.data.repository.TaskRepositoryImpl
import io.github.jan.supabase.auth.auth

class AppInitializationController {

    suspend fun initialize() {
        try {
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

            // 4. Cloud restore: synchronize authenticated user's farms, plots, and tasks
            try {
                val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
                if (!currentUserId.isNullOrBlank()) {
                    val farmRepo = RepositoryProvider.farmRepository as? FarmRepositoryImpl
                    val remoteFarms = farmRepo?.fetchFromRemote(currentUserId) ?: emptyList()

                    val plotRepo = RepositoryProvider.cropPlotRepository as? CropPlotRepositoryImpl
                    val taskRepo = RepositoryProvider.taskRepository as? TaskRepositoryImpl

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
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
