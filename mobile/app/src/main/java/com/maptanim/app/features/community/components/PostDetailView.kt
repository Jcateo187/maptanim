package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.features.community.model.getAvatarColor
import java.io.File

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * PostDetailView — Edge-to-edge discussion thread screen.
 * Clean, full-size post layout without card boxes, seamless comment thread, and response editor.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun PostDetailView(
    post: CommunityPost,
    comments: List<CommunityComment>,
    relatedPosts: List<CommunityPost> = emptyList(),
    currentUserName: String = "You",
    onBack: () -> Unit,
    onLikeToggle: (String) -> Unit,
    onAddComment: (postId: String, content: String, authorName: String) -> Unit,
    onReportPost: (CommunityPost) -> Unit,
    onReportComment: (CommunityComment) -> Unit,
    onSelectRelatedPost: (CommunityPost) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── TOP: Back Navigation & Author info & Report ──────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(getAvatarColor(post.authorName)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = post.authorName,
                            color = DeepBlack,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val hashtag = when (post.category.uppercase()) {
                            "GENERAL" -> "#General"
                            "PEST_ALERT" -> "#PestControl"
                            "FARMING_TIP" -> "#FarmingTips"
                            "EQUIPMENT" -> "#Equipment"
                            else -> if (post.category.startsWith("#")) post.category else "#${post.category.replace("_", "")}"
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(
                                text = hashtag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LushGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = post.timestamp,
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = { onReportPost(post) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Report post",
                    tint = MutedText,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── SCROLLABLE BODY: Full Width Post Details + Comments ──────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Full Width Post Details (Edge-to-Edge)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (post.title.isNotBlank() && !post.content.trim().startsWith(post.title.trim()) && post.title != "Community Thought") {
                        Text(
                            text = post.title,
                            color = LushGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }

                    Text(
                        text = post.content,
                        color = DeepBlack,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )

                    // Full Size Photo (Bounded height, never cut)
                    if (!post.imageUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 140.dp, max = 280.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightSurface)
                                .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val imageModel: Any = if (post.imageUrl.startsWith("/") || post.imageUrl.startsWith("content://") || post.imageUrl.startsWith("file://")) {
                                File(post.imageUrl)
                            } else {
                                post.imageUrl
                            }
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "Post image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 280.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Reactions row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onLikeToggle(post.id) }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Helpful",
                                tint = if (post.isLikedByMe) Color(0xFFD32F2F) else MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${post.likesCount} Helpful",
                                color = if (post.isLikedByMe) Color(0xFFD32F2F) else DeepBlack,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = LushGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${comments.size} Comments",
                                color = LushGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
            }

            // Comments Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightSurface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Responses & Insights (${comments.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                }
                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
            }

            // Comments List (Edge-to-Edge)
            if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No replies yet. Be the first to share your farming insight!",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(comments.size, key = { comments[it].id }) { index ->
                    val comment = comments[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(getAvatarColor(comment.authorName)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = comment.authorName.take(1).uppercase(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = comment.authorName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                                Text(
                                    text = comment.timestamp,
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                            IconButton(
                                onClick = { onReportComment(comment) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Report",
                                    tint = MutedText,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = comment.content,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = DeepBlack,
                            modifier = Modifier.padding(start = 36.dp)
                        )
                    }
                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f), thickness = 1.dp)
                }
            }
        }

        // ── BOTTOM: COMMENT REPLY INPUT ──────────────────────────────────────
        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newCommentText.isEmpty()) {
                        Text(
                            text = "Add a helpful comment...",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                    BasicTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = DeepBlack,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(LushGreen),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        onAddComment(post.id, newCommentText.trim(), currentUserName)
                        newCommentText = ""
                    }
                },
                enabled = newCommentText.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .background(if (newCommentText.isNotBlank()) LushGreen else CardBorderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
