package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.features.profile.utils.formatActivityTime

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * UserCommunityActivityCard — Displays user forum activity (posts created and reacted to) in Daylight theme.
 */
@Composable
fun UserCommunityActivityCard(
    userPosts: List<CommunityPost>,
    userProfile: UserProfile,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            if (userPosts.size > 3) {
                TextButton(onClick = onSeeMoreClick) {
                    Text(
                        text = "See More (${userPosts.size})",
                        color = LushGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
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

            if (userPosts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No activity history yet.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack
                    )
                    Text(
                        text = "Discussions you author or react to will appear here.",
                        fontSize = 10.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val visiblePosts = userPosts.take(4)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    visiblePosts.forEachIndexed { index, post ->
                        val isAuthoredByMe = (post.authorId != null && post.authorId == userProfile.id) ||
                                (userProfile.nickname.isNotBlank() && post.authorName.equals(userProfile.nickname, ignoreCase = true)) ||
                                (userProfile.boundEmail != null && post.authorName.equals(userProfile.boundEmail.substringBefore('@'), ignoreCase = true)) ||
                                post.authorName.equals("You", ignoreCase = true)

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

                        if (index < visiblePosts.lastIndex) {
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

