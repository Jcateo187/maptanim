package com.maptanim.app.features.profile.modals

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Schedule
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
import com.maptanim.app.features.profile.utils.formatActivityTime

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

enum class CommunityActivityFilter {
    ALL,
    MY_POSTS,
    REACTED
}

/**
 * FullCommunityActivityModal — Complete paginated forum activity history in Daylight theme.
 */
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

    val totalPages = (filteredPosts.size + itemsPerPage - 1).coerceAtLeast(1) / itemsPerPage
    val pageItems = remember(filteredPosts, currentPage) {
        val safePage = currentPage.coerceIn(1, totalPages.coerceAtLeast(1))
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
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "Forum Activity on $selectedDateFilter (${filteredPosts.size})" else "Community Forum Activity (${filteredPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = DeepBlack)
                    }
                }

                // Filter Chips (All, My Posts, Reacted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipButton(
                        label = "All (${posts.size})",
                        isSelected = activityFilter == CommunityActivityFilter.ALL,
                        onClick = {
                            activityFilter = CommunityActivityFilter.ALL
                            currentPage = 1
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChipButton(
                        label = "My Posts ($myPostsCount)",
                        isSelected = activityFilter == CommunityActivityFilter.MY_POSTS,
                        onClick = {
                            activityFilter = CommunityActivityFilter.MY_POSTS
                            currentPage = 1
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChipButton(
                        label = "Reacted ($reactedCount)",
                        isSelected = activityFilter == CommunityActivityFilter.REACTED,
                        onClick = {
                            activityFilter = CommunityActivityFilter.REACTED
                            currentPage = 1
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Search Bar with Date Filter Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isBlank()) {
                                Text("Search topic, category, author...", color = MutedText, fontSize = 12.sp)
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    currentPage = 1
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = DeepBlack, fontSize = 12.sp),
                                cursorBrush = SolidColor(LushGreen),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = MutedText, modifier = Modifier.size(14.dp))
                            }
                        }

                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Filter by Date",
                                tint = if (selectedDateFilter != null) LushGreen else MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Active Date Filter Badge
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, LushGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Date: $selectedDateFilter",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                selectedDateFilter = null
                                currentPage = 1
                            }
                        ) {
                            Text("Clear Date Filter", fontSize = 11.sp, color = LushGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // List Items
                if (filteredPosts.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = LushGreen, modifier = Modifier.size(36.dp))
                            Text("No forum discussions found", color = DeepBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your filter or search query.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pageItems) { post ->
                            val isAuthored = isPostAuthoredByMe(post)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (isAuthored) Color(0xFFE8F5E9) else Color(0xFFFBE9E7),
                                                border = BorderStroke(1.dp, if (isAuthored) LushGreen else Color(0xFFD32F2F))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isAuthored) Icons.Default.Edit else Icons.Default.Favorite,
                                                        contentDescription = null,
                                                        tint = if (isAuthored) LushGreen else Color(0xFFD32F2F),
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = if (isAuthored) "Your Post" else "Reacted",
                                                        fontSize = 9.sp,
                                                        color = if (isAuthored) LushGreen else Color(0xFFD32F2F),
                                                        fontWeight = FontWeight.Bold
                                                    )
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
                                        fontSize = 11.sp,
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
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(11.dp))
                                                Text("${post.likesCount}", fontSize = 10.sp, color = MutedText)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null, tint = LushGreen, modifier = Modifier.size(11.dp))
                                                Text("${post.commentsCount}", fontSize = 10.sp, color = MutedText)
                                            }
                                            if (!isAuthored && post.authorName.isNotBlank()) {
                                                Text("by ${post.authorName}", fontSize = 10.sp, color = MutedText)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MutedText, modifier = Modifier.size(10.dp))
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
                        }
                    }
                }

                // Pagination Controls
                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous", fontSize = 11.sp, color = DeepBlack)
                        }
                        Text(
                            text = "Page $currentPage of $totalPages",
                            fontSize = 12.sp,
                            color = DeepBlack,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text("Next", fontSize = 11.sp, color = DeepBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepBlack)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        val availableDates = remember(posts) {
            posts.map { it.timestamp.take(10) }.distinct().sortedDescending()
        }
        DatePickerSelectionDialog(
            availableDates = availableDates,
            selectedDate = selectedDateFilter,
            onSelectDate = {
                selectedDateFilter = it
                currentPage = 1
                showDatePickerModal = false
            },
            onDismiss = { showDatePickerModal = false }
        )
    }
}

@Composable
private fun FilterChipButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) LushGreen else LightSurface,
        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color.White else DeepBlack)
        }
    }
}
