package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.features.community.model.getAvatarColor

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CommentSection — Comments discussion stream and add-reply input bar.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CommentSection(
    comments: List<CommunityComment>,
    postId: String,
    currentUserName: String,
    onAddComment: (postId: String, content: String, authorName: String) -> Unit,
    onReportComment: (CommunityComment) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }
    var isCommentFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Comments Header
        Text(
            text = "Discussion & Replies (${comments.size})",
            color = DeepBlack,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        if (comments.isEmpty()) {
            Text(
                text = "No comments yet. Be the first to share your agronomic insights!",
                color = MutedText,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                comments.forEach { comment ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(getAvatarColor(comment.authorName)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = comment.authorName.take(1).uppercase(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = comment.authorName,
                                        color = LushGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "• ${comment.timestamp}",
                                        color = MutedText,
                                        fontSize = 10.sp
                                    )
                                }

                                IconButton(
                                    onClick = { onReportComment(comment) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Report comment",
                                        tint = MutedText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = comment.content,
                                color = DeepBlack,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add Comment Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(LushGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUserName.take(1).uppercase().ifBlank { "Y" },
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, if (isCommentFocused) LushGreen else CardBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newCommentText.isEmpty()) {
                        Text(
                            text = "Write a reply or comment...",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                    BasicTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = DeepBlack, fontSize = 12.sp),
                        cursorBrush = SolidColor(LushGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isCommentFocused = it.isFocused }
                    )
                }
            }

            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        onAddComment(postId, newCommentText.trim(), currentUserName)
                        newCommentText = ""
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LushGreen)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
