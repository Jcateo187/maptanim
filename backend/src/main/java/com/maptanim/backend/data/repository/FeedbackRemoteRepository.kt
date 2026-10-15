package com.maptanim.backend.data.repository

import com.maptanim.backend.data.model.FeedbackModel
import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class FeedbackRemoteRepository {
    private val client = SupabaseClient.client

    suspend fun getAllFeedback(): Result<List<FeedbackModel>> {
        return try {
            val list = client.postgrest["feedback"]
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<FeedbackModel>()
            Result.success(list)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getFeedbackByUserId(userId: String): Result<List<FeedbackModel>> {
        return try {
            val list = client.postgrest["feedback"]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<FeedbackModel>()
            Result.success(list)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createFeedback(feedback: FeedbackModel): Result<Boolean> {
        return try {
            client.postgrest["feedback"].insert(feedback)
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun replyToFeedback(id: String, reply: String, status: String = "RESOLVED"): Result<Boolean> {
        return try {
            client.postgrest["feedback"].update(
                mapOf(
                    "admin_reply" to reply,
                    "status" to status
                )
            ) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
