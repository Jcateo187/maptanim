package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.features.farm.components.getCropHeroDrawable
import com.maptanim.app.features.farm.schedule.CheckUpRecorder
import com.maptanim.app.features.farm.schedule.CheckUpSeverity
import com.maptanim.app.features.profile.utils.formatActivityTime

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val Charcoal = Color(0xFF28302A)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

enum class ProfileActivityFilter(val label: String) {
    ALL("Lahat"),
    CHECK_UP("Check Up"),
    COMMUNITY("Komunidad")
}

private sealed interface UnifiedActivityItem {
    val sortKey: String
    data class Forum(val post: CommunityPost) : UnifiedActivityItem {
        override val sortKey: String get() = post.timestamp
    }
    data class CheckUp(val log: CropLog) : UnifiedActivityItem {
        override val sortKey: String get() = log.createdAt.ifBlank { log.date }
    }
}

/**
 * UserCommunityActivityCard — Displays user forum discussions and vegetable check-up logs.
 * Includes interactive filter chips (Lahat / Check Up / Komunidad).
 */
@Composable
fun UserCommunityActivityCard(
    userPosts: List<CommunityPost>,
    userProfile: UserProfile,
    checkUpLogs: List<CropLog> = emptyList(),
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeFilter by remember { mutableStateOf(ProfileActivityFilter.ALL) }

    val displayedItems = remember(activeFilter, userPosts, checkUpLogs) {
        when (activeFilter) {
            ProfileActivityFilter.ALL -> {
                val combined = userPosts.map { UnifiedActivityItem.Forum(it) } +
                    checkUpLogs.map { UnifiedActivityItem.CheckUp(it) }
                combined.sortedByDescending { it.sortKey }.take(4)
            }
            ProfileActivityFilter.CHECK_UP -> {
                checkUpLogs.map { UnifiedActivityItem.CheckUp(it) }.take(4)
            }
            ProfileActivityFilter.COMMUNITY -> {
                userPosts.map { UnifiedActivityItem.Forum(it) }.take(4)
            }
        }
    }

    val totalCount = remember(activeFilter, userPosts, checkUpLogs) {
        when (activeFilter) {
            ProfileActivityFilter.ALL -> userPosts.size + checkUpLogs.size
            ProfileActivityFilter.CHECK_UP -> checkUpLogs.size
            ProfileActivityFilter.COMMUNITY -> userPosts.size
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Heading TOP OF CARD (outside of card)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = LushGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Activity History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepBlack
                )
            }
            if (totalCount > 4) {
                TextButton(onClick = onSeeMoreClick) {
                    Text(
                        text = "See More ($totalCount)",
                        color = LushGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProfileActivityFilter.entries.forEach { filter ->
                val count = when (filter) {
                    ProfileActivityFilter.ALL -> userPosts.size + checkUpLogs.size
                    ProfileActivityFilter.CHECK_UP -> checkUpLogs.size
                    ProfileActivityFilter.COMMUNITY -> userPosts.size
                }
                val isSelected = activeFilter == filter
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) LushGreen else Color(0xFFF1F3F0),
                    border = BorderStroke(0.8.dp, if (isSelected) LushGreen else CardBorderColor),
                    modifier = Modifier.clickable { activeFilter = filter }
                ) {
                    Text(
                        text = "${filter.label} ($count)",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Charcoal,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // The card itself containing the activity list
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                if (displayedItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Walang naitalang aktibidad dito.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepBlack
                        )
                        Text(
                            text = if (activeFilter == ProfileActivityFilter.CHECK_UP) {
                                "Magsagawa ng Vegetables Check Up para maitala ang kalusugan ng iyong mga pananim."
                            } else {
                                "Ang mga post sa komunidad at check-up logs ay lalabas dito."
                            },
                            fontSize = 10.5.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        displayedItems.forEachIndexed { index, item ->
                            when (item) {
                                is UnifiedActivityItem.CheckUp -> {
                                    CheckUpActivityRow(log = item.log)
                                }
                                is UnifiedActivityItem.Forum -> {
                                    val post = item.post
                                    val isAuthoredByMe = (post.authorId != null && post.authorId == userProfile.id) ||
                                        (userProfile.nickname.isNotBlank() && post.authorName.equals(userProfile.nickname, ignoreCase = true)) ||
                                        (userProfile.boundEmail != null && post.authorName.equals(userProfile.boundEmail.substringBefore('@'), ignoreCase = true)) ||
                                        post.authorName.equals("You", ignoreCase = true)
                                    ForumActivityRow(post = post, isAuthoredByMe = isAuthoredByMe)
                                }
                            }

                            if (index < displayedItems.lastIndex) {
                                HorizontalDivider(
                                    color = CardBorderColor.copy(alpha = 0.7f),
                                    thickness = 0.8.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckUpActivityRow(log: CropLog) {
    val summary = remember(log) { CheckUpRecorder.summarize(log) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = summary.severity.color.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, summary.severity.color.copy(alpha = 0.35f)),
            modifier = Modifier.size(38.dp)
        ) {
            Image(
                painter = painterResource(id = getCropHeroDrawable(log.cropName)),
                contentDescription = log.cropName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${log.cropName} • ${summary.category}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (summary.severity != CheckUpSeverity.UNKNOWN) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = summary.severity.color.copy(alpha = 0.12f),
                        border = BorderStroke(0.8.dp, summary.severity.color.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = summary.severity.label,
                            fontSize = 9.5.sp,
                            color = summary.severity.color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            val snippet = summary.resultTitle ?: summary.details ?: "Sinuri ang kalagayan ng halaman."
            Text(
                text = snippet,
                fontSize = 12.sp,
                color = Charcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = log.currentStage.label,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = log.date,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                }
            }
        }
    }
}

@Composable
private fun ForumActivityRow(post: CommunityPost, isAuthoredByMe: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DeepBlack,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isAuthoredByMe) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Your Post",
                                fontSize = 9.sp,
                                color = LushGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFBE9E7),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Reacted",
                                fontSize = 9.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = post.category,
                    fontSize = 10.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = post.content,
            fontSize = 12.sp,
            color = DeepBlack,
            maxLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "${post.commentsCount}",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }

                if (!isAuthoredByMe && post.authorName.isNotBlank()) {
                    Text(
                        text = "by ${post.authorName}",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MutedText,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = formatActivityTime(post.timestamp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            }
        }
    }
}
