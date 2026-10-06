package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.TaskDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.TaskRemoteDataSource
import com.maptanim.app.data.remote.dto.TaskDto
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao? = null,
    private val remoteDataSource: TaskRemoteDataSource = TaskRemoteDataSource()
) : TaskRepository {

    private val inMemoryFallback = MutableStateFlow<List<FarmTask>>(emptyList())

    override fun observeTodayTasks(farmId: String, today: String): Flow<List<FarmTask>> {
        return taskDao?.observeTodayTasks(farmId, today)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryFallback.map { tasks ->
            tasks.filter { it.farmId == farmId && it.dueDate == today && !it.isCompleted }
        }
    }

    override fun observeAllTasks(farmerId: String): Flow<List<FarmTask>> {
        return taskDao?.observeAllTasks(farmerId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryFallback
    }

    override fun observeTasksForRange(farmerId: String, startDate: String, endDate: String): Flow<List<FarmTask>> {
        return taskDao?.observeTasksForRange(farmerId, startDate, endDate)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryFallback.map { tasks ->
            tasks.filter { it.dueDate in startDate..endDate }
        }
    }

    override fun observeActiveAlerts(farmId: String): Flow<List<FarmTask>> {
        return taskDao?.observeActiveAlerts(farmId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryFallback.map { tasks ->
            tasks.filter { it.farmId == farmId && !it.isCompleted && (it.taskType == TaskType.PEST_ALERT || it.taskType == TaskType.APPLY_PESTICIDE) }
        }
    }

    override fun observeHarvestReady(farmId: String): Flow<List<FarmTask>> {
        return taskDao?.observeHarvestReady(farmId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryFallback.map { tasks ->
            tasks.filter { it.farmId == farmId && !it.isCompleted && it.taskType == TaskType.HARVEST }
        }
    }

    override suspend fun completeTask(taskId: String, completedAt: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        taskDao?.markTaskCompleted(taskId, completedAt)
        inMemoryFallback.value = inMemoryFallback.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = true, completedAt = completedAt) else task
        }

        val remoteRes = remoteDataSource.completeTask(taskId, completedAt)
        if (remoteRes.isFailure) {
            try {
                RepositoryProvider.syncRepository.enqueueSyncItem(
                    tableName = "tasks",
                    recordId = taskId,
                    operation = "UPDATE",
                    payload = "{\"id\":\"$taskId\",\"is_completed\":true,\"completed_at\":\"$completedAt\"}"
                )
            } catch (_: Exception) {}
        }
    }

    override suspend fun upsertTasks(tasks: List<FarmTask>) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        taskDao?.upsertTasks(tasks.map { it.toEntity() })
        val existingIds = tasks.map { it.id }.toSet()
        inMemoryFallback.value = inMemoryFallback.value.filter { it.id !in existingIds } + tasks

        if (tasks.isNotEmpty()) {
            val dtos = tasks.map { task ->
                TaskDto(
                    id = task.id,
                    farm_id = task.farmId,
                    plot_id = task.plotId.ifBlank { null },
                    task_type = task.taskType.name,
                    title = task.title,
                    sub_label = task.subLabel,
                    due_date = task.dueDate,
                    is_completed = task.isCompleted,
                    completed_at = task.completedAt
                )
            }
            val remoteRes = remoteDataSource.upsertTasks(dtos)
            if (remoteRes.isFailure) {
                for (dto in dtos) {
                    try {
                        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                        val payload = json.encodeToString(TaskDto.serializer(), dto)
                        RepositoryProvider.syncRepository.enqueueSyncItem(
                            tableName = "tasks",
                            recordId = dto.id,
                            operation = "INSERT",
                            payload = payload
                        )
                    } catch (_: Exception) {}
                }
            }
        }
    }

    suspend fun fetchFromRemote(farmId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = remoteDataSource.getTasksForFarm(farmId)
        result.getOrNull()?.let { dtos ->
            val domainTasks = dtos.map { dto ->
                FarmTask(
                    id = dto.id,
                    farmId = dto.farm_id,
                    plotId = dto.plot_id ?: "",
                    plotLabel = "PLOT",
                    cropName = dto.sub_label,
                    taskType = try { TaskType.valueOf(dto.task_type) } catch (e: Exception) { TaskType.WATER },
                    title = dto.title,
                    subLabel = dto.sub_label,
                    dueDate = dto.due_date,
                    isCompleted = dto.is_completed,
                    completedAt = dto.completed_at
                )
            }
            taskDao?.upsertTasks(domainTasks.map { it.toEntity() })
            val existingIds = domainTasks.map { it.id }.toSet()
            inMemoryFallback.value = inMemoryFallback.value.filter { it.id !in existingIds } + domainTasks
        }
    }
}
