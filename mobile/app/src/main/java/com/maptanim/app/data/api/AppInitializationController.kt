package com.maptanim.app.data.api

import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider

class AppInitializationController {

    suspend fun initialize() {
        try {
            // Synchronize reference crops from Supabase to local Room database on launch
            (RepositoryProvider.cropRepository as? CropRepositoryImpl)?.fetchFromRemote()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
