package com.maptanim.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maptanim.app.data.local.dao.*
import com.maptanim.app.data.local.entity.*

@Database(
    entities = [
        CropZoneEntity::class,
        FarmObjectEntity::class,
        CropPlotEntity::class,
        FarmEntity::class,
        CropEntity::class,
        TaskEntity::class,
        NotificationEntity::class,
        SyncQueueEntity::class,
        HarvestEntity::class,
        ActivityEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cropZoneDao(): CropZoneDao
    abstract fun farmObjectDao(): FarmObjectDao
    abstract fun cropPlotDao(): CropPlotDao
    abstract fun farmDao(): FarmDao
    abstract fun cropDao(): CropDao
    abstract fun taskDao(): TaskDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun harvestDao(): HarvestDao
    abstract fun activityDao(): ActivityDao

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
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
