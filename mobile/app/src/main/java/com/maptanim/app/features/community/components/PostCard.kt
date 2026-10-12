package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.features.community.model.getAvatarColor
import java.io.File

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * PostCard — Edge-to-edge forum post item.
 * Clean, full-width, no card box:
 * - Proper spacing & gap between posts.
 * - Non-destructive image bounds: max height capped, full image visible (Fit, not cut).
 * - "See more..." truncation toggle for long text.
 * - Daylight High-Contrast Theme compliant.
 */
@Composable
fun PostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onReportClick: (CommunityPost) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isLongContent = remember(post.content) {
        post.content.length > 200 || post.content.count { it == '\n' } >= 4
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── TOP: Author Avatar & Name & Timestamp + Hashtag + Report Flag ─────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(post.authorName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                color = DeepBlack,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // Hashtag Category Badge
                            val hashtag = when (post.category.uppercase()) {
                                "GENERAL" -> "#General"
                                "PEST_ALERT" -> "#PestControl"
                                "FARMING_TIP" -> "#FarmingTips"
                                "EQUIPMENT" -> "#Equipment"
                                else -> if (post.category.startsWith("#")) post.category else "#${post.category.replace("_", "")}"
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Text(
                                    text = hashtag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LushGreen,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
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

                // Report Flag Icon Button
                IconButton(
                    onClick = { onReportClick(post) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Report post",
                        tint = MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ── CENTER: CONTENT + SEE MORE... TOGGLE + PRESERVED IMAGE ──────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (post.title.isNotBlank() && !post.content.trim().startsWith(post.title.trim()) && post.title != "Community Thought") {
                    Text(
                        text = post.title,
                        color = LushGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }

                // Post Content with "See more..." feature
                Text(
                    text = post.content,
                    color = DeepBlack,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis
                )

                if (isLongContent) {
                    Text(
                        text = if (isExpanded) "Show less" else "See more...",
                        color = LushGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 2.dp)
                    )
                }

                // Height-limited Image (Full size visible, not cut/cropped)
                if (!post.imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 260.dp)
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
                            contentDescription = "Post photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp),
                            contentScale = ContentScale.Fit // Full size image, not cut!
                        )
                    }
                }
            }

            // ── BOTTOM: REACT & COMMENT & VIEW ACTION ──────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // React (Helpful / Heart)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onLikeClick() }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Helpful reaction",
                            tint = if (post.isLikedByMe) Color(0xFFD32F2F) else MutedText,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "${post.likesCount} Helpful",
                            color = if (post.isLikedByMe) Color(0xFFD32F2F) else DeepBlack,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comments count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = LushGreen,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "${post.commentsCount} Comments",
                            color = LushGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // View Details cue
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "View discussion",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MutedText
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Forum Post Separator Divider
        HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f), thickness = 1.dp)
    }
}
