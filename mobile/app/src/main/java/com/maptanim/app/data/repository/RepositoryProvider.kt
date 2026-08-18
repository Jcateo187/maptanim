package com.maptanim.app.data.repository

import android.content.Context
import com.maptanim.app.data.local.AppDatabase
import com.maptanim.app.domain.repository.CommunityRepository
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.CropZoneRepository
import com.maptanim.app.domain.repository.FarmObjectRepository
import com.maptanim.app.domain.repository.FarmRepository
import com.maptanim.app.domain.repository.KnowledgeBaseRepository
import com.maptanim.app.domain.repository.NotificationRepository
import com.maptanim.app.domain.repository.SyncRepository
import com.maptanim.app.domain.repository.TaskRepository
import com.maptanim.app.domain.repository.UserRepository

object RepositoryProvider {
    private var database: AppDatabase? = null
    var appContext: Context? = null
        private set

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (database == null) {
            database = AppDatabase.getInstance(context)
        }
    }

    val cropPlotRepository: CropPlotRepository by lazy {
        CropPlotRepositoryImpl(database?.cropPlotDao())
    }
    val cropZoneRepository: CropZoneRepository by lazy {
        CropZoneRepositoryImpl(database?.cropZoneDao())
    }
    val farmObjectRepository: FarmObjectRepository by lazy {
        FarmObjectRepositoryImpl(database?.farmObjectDao())
    }
    val farmRepository: FarmRepository by lazy {
        FarmRepositoryImpl(database?.farmDao())
    }
    val cropRepository: CropRepository by lazy {
        CropRepositoryImpl(database?.cropDao(), context = appContext)
    }
    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database?.taskDao())
    }
    val communityRepository: CommunityRepository by lazy {
        CommunityRepositoryImpl()
    }
    val knowledgeBaseRepository: KnowledgeBaseRepository by lazy {
        KnowledgeBaseRepositoryImpl()
    }
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(database?.notificationDao())
    }
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl.instance
    }
    val syncRepository: SyncRepository by lazy {
        SyncRepositoryImpl(database!!.syncQueueDao())
    }
    val harvestRepository: com.maptanim.app.domain.repository.HarvestRepository by lazy {
        HarvestRepositoryImpl(database!!.harvestDao())
    }
    val activityRepository: com.maptanim.app.domain.repository.ActivityRepository by lazy {
        ActivityRepositoryImpl(database!!.activityDao())
    }

    suspend fun clearAllLocalCache() {
        try {
            database?.clearAllTables()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
