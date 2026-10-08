package com.maptanim.app.features.community.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.core.preferences.CommunityPreferencesManager
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.features.community.components.*
import com.maptanim.app.features.community.dialogs.AddFriendDialog
import com.maptanim.app.features.community.dialogs.CommunityReportDialog
import com.maptanim.app.features.community.model.*
import com.maptanim.app.features.community.viewmodel.CommunityViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes
import kotlinx.coroutines.delay

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * CommunityScreen — Edge-to-edge community forum and direct messaging hub.
 * Structured cleanly:
 * 1. CommunityHeaderBar (Feed/Chat tabs, Search Bar + Search Button, All discussion / My Post filters, Create Post button)
 * 2. Edge-to-edge full width forum feed (PostCard with dividers, not cards)
 * 3. CreatePostSheet (Streamlined thoughts editor with 2-row hashtags)
 * 4. PostDetailView (Edge-to-edge discussion thread with comments)
 * 5. CommunityChatSection (Search bar + button, Add Friends horizontal row, Friend list, Dedicated Chat screen)
 *
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CommunityScreen(
    navController: NavHostController,
    viewModel: CommunityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeMode by remember { mutableStateOf(CommunityViewMode.FEED) }
    var feedSubMode by remember { mutableStateOf(FeedSubMode.FEED_LIST) }
    var showOnlyMyPosts by remember { mutableStateOf(false) }
    var activeReportTarget by remember { mutableStateOf<ReportTarget?>(null) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.reportNotice) {
        if (uiState.reportNotice != null) {
            delay(4500)
            viewModel.clearReportNotice()
        }
    }

    LaunchedEffect(uiState.postNotice) {
        if (uiState.postNotice != null) {
            delay(4500)
            viewModel.clearPostNotice()
        }
    }

    val displayPosts = remember(uiState.posts, showOnlyMyPosts, uiState.currentUserName) {
        if (showOnlyMyPosts) {
            uiState.posts.filter {
                (uiState.currentUserName.isNotBlank() && it.authorName.equals(uiState.currentUserName, ignoreCase = true)) ||
                it.authorName.contains("You", ignoreCase = true) ||
                CommunityPreferencesManager.getInstance().isMyPost(null, it.id)
            }
        } else {
            uiState.posts
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (feedSubMode == FeedSubMode.FEED_LIST) {
                MainBottomNavBar(
                    selectedRoute = Routes.COMMUNITY,
                    onNavigate = { route ->
                        if (route != Routes.COMMUNITY) {
                            navController.navigate(route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // ── Status Toast / Notice Banner ────────────────────────────
            val activeNotice = uiState.reportNotice ?: uiState.postNotice
            val isErrorNotice = uiState.reportNotice == null && uiState.postNoticeIsError
            activeNotice?.let { notice ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isErrorNotice) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, if (isErrorNotice) Color(0xFFEF9A9A) else LushGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isErrorNotice) Icons.Default.ReportProblem else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isErrorNotice) Color(0xFFC62828) else LushGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = notice,
                                color = if (isErrorNotice) Color(0xFFC62828) else LushGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (uiState.reportNotice != null) viewModel.clearReportNotice()
                                    if (uiState.postNotice != null) viewModel.clearPostNotice()
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = DeepBlack,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Top Header Navigation Bar ───────────────────────────────
            if (feedSubMode == FeedSubMode.FEED_LIST) {
                CommunityHeaderBar(
                    activeMode = activeMode,
                    onModeChange = { newMode ->
                        activeMode = newMode
                        if (newMode == CommunityViewMode.FEED) {
                            feedSubMode = FeedSubMode.FEED_LIST
                        }
                    },
                    showOnlyMyPosts = showOnlyMyPosts,
                    onToggleShowOnlyMyPosts = { showOnlyMyPosts = it },
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onCreatePostClick = { feedSubMode = FeedSubMode.CREATE_POST }
                )
            }

            // ── Main Content Area (Wide, Edge-to-Edge) ──────────────────
            if (activeMode == CommunityViewMode.FEED) {
                when (feedSubMode) {
                    FeedSubMode.FEED_LIST -> {
                        if (displayPosts.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.searchQuery.isNotBlank()) "No discussions match \"${uiState.searchQuery}\"" else "No community posts yet. Be the first to start a conversation!",
                                    color = MutedText,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color(0xFFF3F5F3)),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(displayPosts, key = { it.id }) { post ->
                                    PostCard(
                                        post = post,
                                        onClick = {
                                            viewModel.selectPost(post)
                                            feedSubMode = FeedSubMode.POST_DETAIL
                                        },
                                        onLikeClick = { viewModel.toggleLikePost(post.id) },
                                        onReportClick = {
                                            activeReportTarget = ReportTarget(
                                                type = "POST",
                                                id = post.id,
                                                name = post.authorName,
                                                content = post.title
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    FeedSubMode.CREATE_POST -> {
                        CreatePostSheet(
                            currentUserName = uiState.currentUserName,
                            isPublishing = uiState.isPublishingPost,
                            onCancel = { feedSubMode = FeedSubMode.FEED_LIST },
                            onSubmit = { title, category, content, authorName, imagePath ->
                                viewModel.createPost(title, category, content, authorName, imagePath) { success ->
                                    if (success) {
                                        feedSubMode = FeedSubMode.FEED_LIST
                                    }
                                }
                            }
                        )
                    }

                    FeedSubMode.POST_DETAIL -> {
                        uiState.selectedPost?.let { post ->
                            val relatedPosts = remember(post, displayPosts) {
                                displayPosts.filter { it.id != post.id }
                            }
                            PostDetailView(
                                post = post,
                                comments = uiState.selectedPostComments,
                                relatedPosts = relatedPosts,
                                currentUserName = uiState.currentUserName,
                                onBack = {
                                    viewModel.selectPost(null)
                                    feedSubMode = FeedSubMode.FEED_LIST
                                },
                                onLikeToggle = { viewModel.toggleLikePost(it) },
                                onAddComment = { postId, content, authorName ->
                                    viewModel.addComment(postId, content, authorName)
                                },
                                onReportPost = {
                                    activeReportTarget = ReportTarget(
                                        type = "POST",
                                        id = post.id,
                                        name = post.authorName,
                                        content = post.title
                                    )
                                },
                                onReportComment = { comment ->
                                    activeReportTarget = ReportTarget(
                                        type = "COMMENT",
                                        id = comment.id,
                                        name = comment.authorName,
                                        content = comment.content
                                    )
                                },
                                onSelectRelatedPost = { related ->
                                    viewModel.selectPost(related)
                                }
                            )
                        } ?: run {
                            feedSubMode = FeedSubMode.FEED_LIST
                        }
                    }
                }
            } else {
                // ── CHAT MODE ───────────────────────────────────────────
                CommunityChatSection(
                    friends = uiState.friends,
                    communityMembers = uiState.communityMembers,
                    onAddFriend = { memberId -> viewModel.addFriend(memberId) },
                    currentUserName = uiState.currentUserName,
                    onOpenAddFriend = { showAddFriendDialog = true },
                    onReportUser = { target -> activeReportTarget = target },
                    searchQuery = uiState.searchQuery,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Report Dialog
    activeReportTarget?.let { target ->
        CommunityReportDialog(
            target = target,
            onDismiss = { activeReportTarget = null },
            onSubmit = { reason, details ->
                viewModel.submitReport(
                    targetType = target.type,
                    targetId = target.id,
                    targetName = target.name,
                    targetContent = target.content,
                    reason = reason,
                    details = details.ifBlank { null }
                )
                activeReportTarget = null
            }
        )
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        AddFriendDialog(
            members = uiState.communityMembers,
            friends = uiState.friends,
            currentUserId = uiState.currentUserId,
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { memberId -> viewModel.addFriend(memberId) },
            onSelectFriend = { /* Handled in Chat */ }
        )
    }
}
