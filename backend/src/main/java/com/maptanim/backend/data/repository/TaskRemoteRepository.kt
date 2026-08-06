package com.maptanim.backend.data.repository

import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String = "",
    val farm_id: String,
    val bed_id: String? = null,
    val bed_label: String? = null,
    val crop_name: String? = null,
    val task_type: String,
    val title: String,
    val sub_label: String? = null,
    val due_date: String,
    val is_completed: Boolean = false,
    val completed_at: String? = null,
    val notes: String? = null
)

class TaskRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getTodayTasks(farmId: String, today: String): Result<List<TaskDto>> {
        return try {
            val tasks = client.postgrest["tasks"]
                .select {
                    filter {
                        eq("farm_id", farmId)
                        eq("due_date", today)
                        eq("is_completed", false)
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
            client.postgrest["tasks"].update({
                set("is_completed", true)
                set("completed_at", completedAt)
            }) {
                filter { eq("id", taskId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
