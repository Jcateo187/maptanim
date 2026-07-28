package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val remoteRepository: TaskRemoteRepository = TaskRemoteRepository()
) : TaskRepository {

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
        remoteRepository.completeTask(taskId, completedAt)
        tasksCache.value = tasksCache.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = true, completedAt = completedAt) else task
        }
    }

    override suspend fun upsertTasks(tasks: List<FarmTask>) {
        val existingIds = tasks.map { it.id }.toSet()
        tasksCache.value = tasksCache.value.filter { it.id !in existingIds } + tasks
    }

    suspend fun fetchFromRemote(farmId: String, today: String) {
        val result = remoteRepository.getTodayTasks(farmId, today)
        result.getOrNull()?.let { dtos ->
            val tasks = dtos.map { it.toDomain() }
            upsertTasks(tasks)
        }
    }
}

private fun TaskDto.toDomain(): FarmTask = FarmTask(
    id = id,
    farmId = farm_id,
    bedId = bed_id,
    bedLabel = bed_label,
    cropName = crop_name,
    taskType = runCatching { TaskType.valueOf(task_type) }.getOrDefault(TaskType.WATER),
    title = title,
    subLabel = sub_label,
    dueDate = due_date,
    isCompleted = is_completed,
    completedAt = completed_at
)
