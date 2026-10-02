package com.maptanim.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maptanim.app.data.local.dao.*
import com.maptanim.app.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CropZoneEntity::class,
        CropPlotEntity::class,
        FarmEntity::class,
        CropEntity::class,
        TaskEntity::class,
        NotificationEntity::class,
        SyncQueueEntity::class,
        HarvestEntity::class,
        ActivityEntity::class,
        DssRuleEntity::class,
        DssDecisionEntity::class,
        PolicyConsentEntity::class,
        CropYieldStudyEntity::class,
        CropVarietyEntity::class,
        CropGrowthStageEntity::class,
        CropSoilCompatibilityEntity::class,
        CropPestDiseaseGuideEntity::class,
        CropLogEntity::class
    ],
    version = 17,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cropZoneDao(): CropZoneDao
    abstract fun cropPlotDao(): CropPlotDao
    abstract fun farmDao(): FarmDao
    abstract fun cropDao(): CropDao
    abstract fun taskDao(): TaskDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun harvestDao(): HarvestDao
    abstract fun activityDao(): ActivityDao
    abstract fun dssRuleDao(): DssRuleDao
    abstract fun dssDecisionDao(): DssDecisionDao
    abstract fun policyConsentDao(): PolicyConsentDao
    abstract fun cropYieldStudyDao(): CropYieldStudyDao
    abstract fun cropVarietyDao(): CropVarietyDao
    abstract fun cropGrowthStageDao(): CropGrowthStageDao
    abstract fun cropSoilCompatibilityDao(): CropSoilCompatibilityDao
    abstract fun cropPestDiseaseGuideDao(): CropPestDiseaseGuideDao
    abstract fun cropLogDao(): CropLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maptanim_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Trigger async background seed on first creation
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            seedDefaultKnowledge(getInstance(context))
                        }
                    }
                    override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Ensure seed exists even after schema migrations
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            seedDefaultKnowledge(getInstance(context))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultKnowledge(database: AppDatabase) {
            try {
                database.cropYieldStudyDao().insertAll(CropKnowledgeSeed.tomatoYieldStudies)
                database.cropVarietyDao().insertAll(CropKnowledgeSeed.tomatoVarieties)
                database.cropGrowthStageDao().insertAll(CropKnowledgeSeed.tomatoGrowthStages)
                database.cropSoilCompatibilityDao().insertAll(CropKnowledgeSeed.tomatoSoilCompatibilities)
                database.cropPestDiseaseGuideDao().insertAll(CropKnowledgeSeed.tomatoPestDiseaseGuides)
            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "Error seeding crop knowledge: ${e.message}")
            }
        }
    }
}
