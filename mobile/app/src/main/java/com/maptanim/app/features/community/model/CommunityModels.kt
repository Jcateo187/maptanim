package com.maptanim.app.features.community.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.abs

/**
 * CommunityViewMode — Top-level tab modes for Community Hub.
 */
enum class CommunityViewMode {
    FEED,
    CHAT
}

/**
 * FeedSubMode — Navigation state within the Feed tab.
 */
enum class FeedSubMode {
    FEED_LIST,
    CREATE_POST,
    POST_DETAIL
}

/**
 * CommunityChatMessage — Direct/Channel chat message.
 */
data class CommunityChatMessage(
    val sender: String,
    val senderName: String,
    val text: String,
    val timestamp: String = "Just now"
)

/**
 * ReportTarget — Context descriptor for reporting offensive content or users.
 */
data class ReportTarget(
    val type: String, // "POST", "USER", "COMMENT"
    val id: String,
    val name: String,
    val content: String? = null
)

/**
 * ChatChannel — Farmer peer conversation channel descriptor.
 * Replaces legacy emoji icons with Material 3 vector icons.
 */
data class ChatChannel(
    val id: String,
    val name: String,
    val statusText: String,
    val icon: ImageVector = Icons.Default.Agriculture,
    val unreadCount: Int = 0
)

/**
 * Generates a stable, high-contrast avatar background color based on name hash.
 */
fun getAvatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFF2E7D32),
        Color(0xFF1565C0),
        Color(0xFFE65100),
        Color(0xFF6A1B9A),
        Color(0xFF00838F),
        Color(0xFF388E3C),
        Color(0xFF0097A7),
        Color(0xFFD84315)
    )
    val hash = abs(name.hashCode())
    return colors[hash % colors.size]
}
