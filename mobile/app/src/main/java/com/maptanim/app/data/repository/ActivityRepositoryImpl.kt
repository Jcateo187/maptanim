package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.ActivityDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.model.Activity
import com.maptanim.app.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun observeActivities(plotId: String): Flow<List<Activity>> {
        return activityDao.observeActivities(plotId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun logActivity(activity: Activity) {
        activityDao.upsertActivity(activity.toEntity())
    }
}
