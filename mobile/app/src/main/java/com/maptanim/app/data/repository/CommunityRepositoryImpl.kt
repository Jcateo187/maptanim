package com.maptanim.app.data.repository

import com.maptanim.app.core.preferences.CommunityPreferencesManager
import com.maptanim.app.data.remote.CommunityRemoteDataSource
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.remote.dto.CommunityCommentDto
import com.maptanim.app.data.remote.dto.CommunityPostDto
import com.maptanim.app.data.remote.dto.toDomain
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.repository.CommunityRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CommunityRepositoryImpl(
    private val remoteDataSource: CommunityRemoteDataSource = CommunityRemoteDataSource()
) : CommunityRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val postsState = MutableStateFlow<List<CommunityPost>>(emptyList())
    private val commentsState = MutableStateFlow<List<CommunityComment>>(emptyList())

    init {
        // Asynchronously fetch latest remote community posts from Supabase
        scope.launch {
            refreshPosts()
        }
    }

    override fun observePosts(): Flow<List<CommunityPost>> = postsState.map { it }

    override fun observeCommentsForPost(postId: String): Flow<List<CommunityComment>> {
        // Fetch fresh comments from Supabase for this specific post
        scope.launch {
            remoteDataSource.getCommentsForPost(postId).onSuccess { remoteComments ->
                if (remoteComments.isNotEmpty()) {
                    val domainComments = remoteComments.map { it.toDomain() }
                    val currentOtherComments = commentsState.value.filter { it.postId != postId }
                    commentsState.value = currentOtherComments + domainComments
                }
            }
        }
        return commentsState.map { list -> list.filter { it.postId == postId } }
    }

    override suspend fun refreshPosts() {
        val currentUserId = try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
        val persistedLikedIds = CommunityPreferencesManager.getInstance().getLikedPostIds(currentUserId)

        remoteDataSource.getAllPosts().onSuccess { remoteList ->
            val inMemoryLikedIds = postsState.value.filter { it.isLikedByMe }.map { it.id }.toSet()
            val allLikedIds = persistedLikedIds + inMemoryLikedIds
            val domainPosts = remoteList.map { dto ->
                dto.toDomain(isLikedByMe = dto.id in allLikedIds)
            }
            postsState.value = domainPosts
        }
    }

    override suspend fun toggleLikePost(postId: String) {
        val currentUserId = try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
        var nextLikesCount = 0
        var isNowLiked = false

        postsState.value = postsState.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLikedByMe
                isNowLiked = newLiked
                val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                val sanitized = newCount.coerceAtLeast(0)
                nextLikesCount = sanitized
                post.copy(isLikedByMe = newLiked, likesCount = sanitized)
            } else post
        }

        // Persist locally for immediate offline & profile sync
        CommunityPreferencesManager.getInstance().setPostLiked(currentUserId, postId, isNowLiked)

        // Sync like count to Supabase
        scope.launch {
            remoteDataSource.updatePostLikes(postId, nextLikesCount)
        }
    }

    override suspend fun addPost(
        title: String,
        category: String,
        content: String,
        authorName: String
    ): Result<Unit> {
        val currentUserId = try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
        val newId = "post_${System.currentTimeMillis()}"
        val sanitizedAuthor = authorName.trim()

        val dto = CommunityPostDto(
            id = newId,
            author_id = currentUserId,
            author_name = sanitizedAuthor,
            category = category,
            title = title,
            content = content,
            likes_count = 1,
            comments_count = 0,
            is_pinned = false,
            tags = listOf(category, "CropCare", "Vegetables")
        )

        val result = remoteDataSource.createPost(dto)

        if (result.isSuccess) {
            val newPost = CommunityPost(
                id = newId,
                authorId = currentUserId,
                authorName = sanitizedAuthor,
                category = category,
                title = title,
                content = content,
                likesCount = 1,
                commentsCount = 0,
                timestamp = "Just now",
                isLikedByMe = true,
                tags = listOf(category, "CropCare", "Vegetables")
            )
            postsState.value = listOf(newPost) + postsState.value

            // Record authored post & initial reaction locally
            CommunityPreferencesManager.getInstance().addMyPostId(currentUserId, newId)
            CommunityPreferencesManager.getInstance().setPostLiked(currentUserId, newId, true)
        }

        return result
    }

    override suspend fun addComment(postId: String, content: String, authorName: String) {
        val newId = "comm_${System.currentTimeMillis()}"
        val sanitizedAuthor = authorName.trim()

        val newComment = CommunityComment(
            id = newId,
            postId = postId,
            authorName = sanitizedAuthor,
            content = content,
            timestamp = "Just now"
        )
        commentsState.value = commentsState.value + newComment

        // Update comment count on local post
        postsState.value = postsState.value.map { post ->
            if (post.id == postId) {
                post.copy(commentsCount = post.commentsCount + 1)
            } else post
        }

        // Sync comment to Supabase
        scope.launch {
            val dto = CommunityCommentDto(
                id = newId,
                post_id = postId,
                author_name = sanitizedAuthor,
                content = content
            )
            remoteDataSource.addComment(dto)
        }
    }

    override suspend fun submitReport(
        targetType: String,
        targetId: String,
        targetName: String,
        targetContent: String?,
        reason: String,
        details: String?,
        reporterName: String
    ): Result<Unit> {
        val newId = "rep_${System.currentTimeMillis()}"
        val dto = com.maptanim.app.data.remote.dto.CommunityReportDto(
            id = newId,
            reporter_name = reporterName.trim(),
            target_type = targetType,
            target_id = targetId,
            target_name = targetName,
            target_content = targetContent,
            reason = reason,
            details = details,
            status = "PENDING"
        )
        return remoteDataSource.submitReport(dto)
    }
}

