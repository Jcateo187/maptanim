package com.maptanim.app.domain.repository

import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun observePosts(): Flow<List<CommunityPost>>
    fun observeCommentsForPost(postId: String): Flow<List<CommunityComment>>
    suspend fun refreshPosts()
    suspend fun toggleLikePost(postId: String)
    suspend fun addPost(title: String, category: String, content: String, authorName: String)
    suspend fun addComment(postId: String, content: String, authorName: String)
    suspend fun submitReport(
        targetType: String,
        targetId: String,
        targetName: String,
        targetContent: String?,
        reason: String,
        details: String?,
        reporterName: String = "Farmer Member"
    ): Result<Unit>
}

