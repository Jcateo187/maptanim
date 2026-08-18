package com.maptanim.app.data.local.dao

import androidx.room.*
import com.maptanim.app.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE plot_id = :plotId ORDER BY performed_at DESC")
    fun observeActivities(plotId: String): Flow<List<ActivityEntity>>

    @Upsert
    fun upsertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    fun deleteActivity(id: String): Int
}
