package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.TaskDto
import io.github.jan.supabase.postgrest.from

class TaskRemoteDataSource {

    suspend fun getTasksForFarm(farmId: String): Result<List<TaskDto>> {
        return try {
            val tasks = SupabaseClient.client
                .from("tasks")
                .select {
                    filter {
                        eq("farm_id", farmId)
                    }
                }
                .decodeList<TaskDto>()
            Result.success(tasks)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun completeTask(taskId: String, completedAt: String): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("tasks")
                .update(
                    mapOf(
                        "is_completed" to true,
                        "completed_at" to completedAt
                    )
                ) {
                    filter {
                        eq("id", taskId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
