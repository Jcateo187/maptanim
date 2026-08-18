package com.maptanim.app.data.remote.dto

import com.maptanim.app.domain.model.CommunityReport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.community_reports`.
 */
@Serializable
data class CommunityReportDto(
    val id: String = "",
    @SerialName("reporter_id") val reporter_id: String? = null,
    @SerialName("reporter_name") val reporter_name: String = "Farmer Member",
    @SerialName("target_type") val target_type: String, // "POST", "USER", "COMMENT"
    @SerialName("target_id") val target_id: String,
    @SerialName("target_name") val target_name: String,
    @SerialName("target_content") val target_content: String? = null,
    val reason: String,
    val details: String? = null,
    val status: String = "PENDING",
    @SerialName("admin_notes") val admin_notes: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("resolved_at") val resolved_at: String? = null
)

fun CommunityReportDto.toDomain(): CommunityReport {
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
        "Just now"
    }

    return CommunityReport(
        id = id,
        reporterName = reporter_name,
        targetType = target_type,
        targetId = target_id,
        targetName = target_name,
        targetContent = target_content,
        reason = reason,
        details = details,
        status = status,
        createdAt = relativeTime
    )
}

fun CommunityReport.toDto(): CommunityReportDto {
    return CommunityReportDto(
        id = id,
        reporter_name = reporterName,
        target_type = targetType,
        target_id = targetId,
        target_name = targetName,
        target_content = targetContent,
        reason = reason,
        details = details,
        status = status
    )
}
