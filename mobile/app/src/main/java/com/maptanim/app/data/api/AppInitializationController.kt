package com.maptanim.app.data.api

import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider

class AppInitializationController {

    suspend fun initialize() {
        try {
            // 1. Synchronize reference crops from Supabase to local Room database on launch
            (RepositoryProvider.cropRepository as? CropRepositoryImpl)?.fetchFromRemote()

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
