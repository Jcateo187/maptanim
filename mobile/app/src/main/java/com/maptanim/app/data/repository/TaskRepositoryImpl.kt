package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl : TaskRepository {

    private val tasksCache = MutableStateFlow<List<FarmTask>>(emptyList())

    override fun observeTodayTasks(farmId: String, today: String): Flow<List<FarmTask>> {
        return tasksCache.map { tasks ->
            tasks.filter { it.farmId == farmId && it.dueDate == today && !it.isCompleted }
        }
    }

    override fun observeAllTasks(farmerId: String): Flow<List<FarmTask>> {
        return tasksCache.map { tasks -> tasks }
    }

    override fun observeTasksForRange(farmerId: String, startDate: String, endDate: String): Flow<List<FarmTask>> {
        return tasksCache.map { tasks ->
            tasks.filter { it.dueDate in startDate..endDate }
        }
    }

    override fun observeActiveAlerts(farmId: String): Flow<List<FarmTask>> {
        return tasksCache.map { tasks ->
            tasks.filter { it.farmId == farmId && !it.isCompleted && (it.taskType == TaskType.PEST_ALERT || it.taskType == TaskType.APPLY_PESTICIDE) }
        }
    }

    override fun observeHarvestReady(farmId: String): Flow<List<FarmTask>> {
        return tasksCache.map { tasks ->
            tasks.filter { it.farmId == farmId && !it.isCompleted && it.taskType == TaskType.HARVEST }
        }
    }

    override suspend fun completeTask(taskId: String, completedAt: String) {
        tasksCache.value = tasksCache.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = true, completedAt = completedAt) else task
        }
    }

    override suspend fun upsertTasks(tasks: List<FarmTask>) {
        val existingIds = tasks.map { it.id }.toSet()
        tasksCache.value = tasksCache.value.filter { it.id !in existingIds } + tasks
    }
}
