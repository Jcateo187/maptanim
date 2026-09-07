package com.maptanim.app.ui.screens.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.ui.screens.profile.utils.formatActivityTime
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

enum class CommunityActivityFilter {
    ALL,
    MY_POSTS,
    REACTED
}

@Composable
fun FullCommunityActivityModal(
    posts: List<CommunityPost>,
    currentUserNickname: String = "",
    currentUserId: String? = null,
    onDismiss: () -> Unit
) {
    var activityFilter by remember { mutableStateOf(CommunityActivityFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }
    var showDatePickerModal by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    fun isPostAuthoredByMe(post: CommunityPost): Boolean {
        return (currentUserId != null && post.authorId != null && post.authorId == currentUserId) ||
                (currentUserNickname.isNotBlank() && post.authorName.equals(currentUserNickname, ignoreCase = true)) ||
                post.authorName.equals("You", ignoreCase = true)
    }

    val myPostsCount = remember(posts, currentUserNickname, currentUserId) {
        posts.count { isPostAuthoredByMe(it) }
    }
    val reactedCount = remember(posts, currentUserNickname, currentUserId) {
        posts.count { it.isLikedByMe && !isPostAuthoredByMe(it) }
    }

    val filteredPosts = remember(posts, searchQuery, selectedDateFilter, activityFilter, currentUserNickname, currentUserId) {
        posts.filter { post ->
            val isAuthored = isPostAuthoredByMe(post)
            val isReacted = post.isLikedByMe

            val matchesFilter = when (activityFilter) {
                CommunityActivityFilter.ALL -> true
                CommunityActivityFilter.MY_POSTS -> isAuthored
                CommunityActivityFilter.REACTED -> isReacted && !isAuthored
            }

            val matchesSearch = searchQuery.isBlank() || (
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.content.contains(searchQuery, ignoreCase = true) ||
                post.category.contains(searchQuery, ignoreCase = true) ||
                post.authorName.contains(searchQuery, ignoreCase = true)
            )
            val matchesDate = selectedDateFilter.isNullOrBlank() || (
                post.timestamp.contains(selectedDateFilter!!)
            )
            matchesFilter && matchesSearch && matchesDate
        }
    }

    val totalPages = (filteredPosts.size + itemsPerPage - 1) / itemsPerPage
    val pageItems = remember(filteredPosts, currentPage) {
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredPosts.drop(startIndex).take(itemsPerPage)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, ForestGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "💬 Forum Activity on $selectedDateFilter (${filteredPosts.size})" else "💬 Community Forum Activity (${filteredPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = White)
                    }
                }

                // Filter Pills: All Activity vs My Posts vs Reacted
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B2418))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = { activityFilter = CommunityActivityFilter.ALL; currentPage = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activityFilter == CommunityActivityFilter.ALL) ForestGreen else Color.Transparent
                    ) {
                        Text(
                            text = "All (${posts.size})",
                            fontSize = 11.sp,
                            fontWeight = if (activityFilter == CommunityActivityFilter.ALL) FontWeight.Bold else FontWeight.Normal,
                            color = if (activityFilter == CommunityActivityFilter.ALL) White else White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        onClick = { activityFilter = CommunityActivityFilter.MY_POSTS; currentPage = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activityFilter == CommunityActivityFilter.MY_POSTS) ForestGreen else Color.Transparent
                    ) {
                        Text(
                            text = "✍️ My Posts ($myPostsCount)",
                            fontSize = 11.sp,
                            fontWeight = if (activityFilter == CommunityActivityFilter.MY_POSTS) FontWeight.Bold else FontWeight.Normal,
                            color = if (activityFilter == CommunityActivityFilter.MY_POSTS) White else White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        onClick = { activityFilter = CommunityActivityFilter.REACTED; currentPage = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activityFilter == CommunityActivityFilter.REACTED) ForestGreen else Color.Transparent
                    ) {
                        Text(
                            text = "❤️ Reacted ($reactedCount)",
                            fontSize = 11.sp,
                            fontWeight = if (activityFilter == CommunityActivityFilter.REACTED) FontWeight.Bold else FontWeight.Normal,
                            color = if (activityFilter == CommunityActivityFilter.REACTED) White else White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Search Bar with Date Selection Icon Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = SolidColor(ForestGreen),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search forum activity by title, content, category...", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Activity Date",
                                tint = if (selectedDateFilter != null) ForestGreen else White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Date Filter Badge if Active
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreen.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, ForestGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { selectedDateFilter = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 Activity Date: $selectedDateFilter ✖",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }
                        Text(
                            text = "Showing forum activity on $selectedDateFilter",
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.6f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (selectedDateFilter != null) "No forum activity found on $selectedDateFilter." else "No forum activity matches your current filter.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(pageItems) { post ->
                            val isAuthored = isPostAuthoredByMe(post)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                            color = White,
                                            modifier = Modifier.weight(1f, fill = false),
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isAuthored) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = ForestGreen.copy(alpha = 0.22f)
                                                ) {
                                                    Text(
                                                        text = "✍️ Your Post",
                                                        fontSize = 9.sp,
                                                        color = ForestGreen,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFFF5252).copy(alpha = 0.18f)
                                                ) {
                                                    Text(
                                                        text = "❤️ Reacted",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFFF7272),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = post.category,
                                                fontSize = 10.sp,
                                                color = ForestGreen.copy(alpha = 0.85f),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Text(post.content, fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text("❤️ ${post.likesCount}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                            Text("💬 ${post.commentsCount} comments", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                            if (!isAuthored && post.authorName.isNotBlank()) {
                                                Text("• by ${post.authorName}", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                                            }
                                        }
                                        Text("🕒 ${formatActivityTime(post.timestamp)}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ForestGreen)
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text("Page $currentPage of $totalPages", fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next ►", fontSize = 11.sp, color = White)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        DatePickerSelectionDialog(
            selectedDate = selectedDateFilter,
            onDateSelected = { selectedDateFilter = it; currentPage = 1 },
            onDismiss = { showDatePickerModal = false }
        )
    }
}
