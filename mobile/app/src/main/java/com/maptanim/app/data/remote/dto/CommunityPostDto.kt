package com.maptanim.app.data.remote.dto

import com.maptanim.app.domain.model.CommunityPost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.community_posts`.
 */
@Serializable
data class CommunityPostDto(
    val id: String = "",
    @SerialName("author_id") val author_id: String? = null,
    @SerialName("author_name") val author_name: String,
    @SerialName("author_avatar_url") val author_avatar_url: String? = null,
    val category: String = "GENERAL",
    val title: String,
    val content: String,
    @SerialName("likes_count") val likes_count: Int = 0,
    @SerialName("comments_count") val comments_count: Int = 0,
    @SerialName("is_pinned") val is_pinned: Boolean = false,
    val tags: List<String> = emptyList(),
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)

fun CommunityPostDto.toDomain(isLikedByMe: Boolean = false): CommunityPost {
    val relativeTime = if (!created_at.isNullOrBlank()) {
        try {
            val createdInstant = java.time.Instant.parse(created_at)
            val now = java.time.Instant.now()
            val minutes = java.time.Duration.between(createdInstant, now).toMinutes()
            when {
                minutes < 2 -> "Just now"
                minutes < 60 -> "$minutes mins ago"
                minutes < 1440 -> "${minutes / 60} hours ago"
                minutes < 2880 -> "Yesterday"
                else -> "${minutes / 1440} days ago"
            }
        } catch (e: Exception) {
            "Recently"
        }
    } else {
        "Recently"
    }

    return CommunityPost(
        id = id,
        authorName = author_name,
        authorAvatarUrl = author_avatar_url,
        category = category,
        title = title,
        content = content,
        likesCount = likes_count,
        commentsCount = comments_count,
        timestamp = relativeTime,
        isLikedByMe = isLikedByMe,
        tags = tags
    )
}

fun CommunityPost.toDto(): CommunityPostDto {
    return CommunityPostDto(
        id = id,
        author_name = authorName,
        author_avatar_url = authorAvatarUrl,
        category = category,
        title = title,
        content = content,
        likes_count = likesCount,
        comments_count = commentsCount,
        is_pinned = false,
        tags = tags
    )
}

