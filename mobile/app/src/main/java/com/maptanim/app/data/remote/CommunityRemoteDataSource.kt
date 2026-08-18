package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.CommunityCommentDto
import com.maptanim.app.data.remote.dto.CommunityPostDto
import com.maptanim.app.data.remote.dto.CommunityReportDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/**
 * Remote Data Source for Community Hub forum discussions & official advisories.
 * Handles network operations against Supabase PostgREST tables `community_posts` and `community_comments`.
 */
class CommunityRemoteDataSource {

    suspend fun getAllPosts(): Result<List<CommunityPostDto>> {
        return try {
            val posts = SupabaseClient.client
                .from("community_posts")
                .select {
                    order(column = "is_pinned", order = Order.DESCENDING)
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<CommunityPostDto>()
            Result.success(posts)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getCommentsForPost(postId: String): Result<List<CommunityCommentDto>> {
        return try {
            val comments = SupabaseClient.client
                .from("community_comments")
                .select {
                    filter {
                        eq("post_id", postId)
                    }
                    order(column = "created_at", order = Order.ASCENDING)
                }
                .decodeList<CommunityCommentDto>()
            Result.success(comments)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createPost(post: CommunityPostDto): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_posts")
                .insert(post)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun addComment(comment: CommunityCommentDto): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_comments")
                .insert(comment)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updatePostLikes(postId: String, newLikesCount: Int): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_posts")
                .update(
                    mapOf("likes_count" to newLikesCount)
                ) {
                    filter {
                        eq("id", postId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_posts")
                .delete {
                    filter {
                        eq("id", postId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_comments")
                .delete {
                    filter {
                        eq("id", commentId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun submitReport(report: CommunityReportDto): Result<Unit> {
        return try {
            SupabaseClient.client
                .from("community_reports")
                .insert(report)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

