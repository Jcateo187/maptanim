package com.maptanim.app.data.remote.dto

import com.maptanim.app.domain.model.CommunityComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.community_comments`.
 */
@Serializable
data class CommunityCommentDto(
    val id: String = "",
    @SerialName("post_id") val post_id: String,
    @SerialName("author_id") val author_id: String? = null,
    @SerialName("author_name") val author_name: String,
    @SerialName("author_avatar_url") val author_avatar_url: String? = null,
    val content: String,
    @SerialName("created_at") val created_at: String? = null
)

fun CommunityCommentDto.toDomain(): CommunityComment {
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

    return CommunityComment(
        id = id,
        postId = post_id,
        authorName = author_name,
        content = content,
        timestamp = relativeTime
    )
}

fun CommunityComment.toDto(): CommunityCommentDto {
    return CommunityCommentDto(
        id = id,
        post_id = postId,
        author_name = authorName,
        content = content
    )
}

