package com.maptanim.app.ui.screens.community

import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.core.preferences.CommunityPreferencesManager
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import kotlin.math.abs

private enum class CommunityViewMode {
    FEED,
    CHAT
}

private enum class FeedSubMode {
    FEED_LIST,
    CREATE_POST,
    POST_DETAIL
}

private data class CommunityChatMessage(
    val sender: String,
    val senderName: String,
    val text: String,
    val timestamp: String = "Just now"
)

private data class ReportTarget(
    val type: String, // "POST", "USER", "COMMENT"
    val id: String,
    val name: String,
    val content: String? = null
)

private data class ChatChannel(
    val id: String,
    val name: String,
    val statusText: String,
    val iconEmoji: String = "🌾",
    val unreadCount: Int = 0
)

private fun getAvatarColor(name: String): Color {
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

    // Chat State
    var chatMessageInput by remember { mutableStateOf("") }
    var isChatInputFocused by remember { mutableStateOf(false) }
    var convSearchQuery by remember { mutableStateOf("") }
    var friendConvSearchQuery by remember { mutableStateOf("") }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    val chatChannels = remember(uiState.friends) {
        uiState.friends.map { m ->
            ChatChannel(m.id, m.name, m.statusText, iconEmoji = "👨‍🌾")
        }
    }
    var selectedChannelId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(chatChannels) {
        if (chatChannels.isNotEmpty()) {
            if (selectedChannelId == null || chatChannels.none { it.id == selectedChannelId }) {
                selectedChannelId = chatChannels.first().id
            }
        } else {
            selectedChannelId = null
        }
    }
    val selectedChannel = chatChannels.firstOrNull { it.id == selectedChannelId }

    val directChatMessages = remember {
        mutableStateMapOf<String, androidx.compose.runtime.snapshots.SnapshotStateList<CommunityChatMessage>>()
    }

    val rawActiveMessages = remember(selectedChannelId) {
        if (selectedChannelId != null) {
            directChatMessages.getOrPut(selectedChannelId!!) {
                mutableStateListOf()
            }
        } else {
            mutableStateListOf()
        }
    }

    val activeMessages = remember(rawActiveMessages.size, convSearchQuery, rawActiveMessages) {
        if (convSearchQuery.isBlank()) {
            rawActiveMessages
        } else {
            rawActiveMessages.filter {
                it.text.contains(convSearchQuery, ignoreCase = true) ||
                it.senderName.contains(convSearchQuery, ignoreCase = true)
            }
        }
    }

    val filteredChannels = remember(chatChannels, friendConvSearchQuery, directChatMessages) {
        if (friendConvSearchQuery.isBlank()) {
            chatChannels
        } else {
            chatChannels.filter { channel ->
                val nameMatch = channel.name.contains(friendConvSearchQuery, ignoreCase = true)
                val messageMatch = directChatMessages[channel.id]?.any { msg ->
                    msg.text.contains(friendConvSearchQuery, ignoreCase = true)
                } == true
                nameMatch || messageMatch
            }
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

    val chatListState = rememberLazyListState()
    LaunchedEffect(rawActiveMessages.size) {
        if (rawActiveMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(rawActiveMessages.size - 1)
        }
    }

    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
            ?: (view.context as? Activity)?.window
        window?.let { win ->
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            WindowInsetsControllerCompat(win, win.decorView).apply {
                hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        onDispose {}
    }

    // Fullscreen edge-to-edge transparent scrim (homescreen visible in background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                navController.popBackStack()
            },
        contentAlignment = Alignment.Center
    ) {
        // Landscape Overlay Frame (With imePadding so keyboard never covers input)
        Card(
            modifier = Modifier
                .widthIn(min = 520.dp, max = 740.dp)
                .fillMaxWidth(0.80f)
                .fillMaxHeight(0.92f)
                .imePadding()
                .padding(vertical = 6.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* block background dismiss */ },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF102014).copy(alpha = 0.96f)),
            border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ── Status Toast / Notice Banner ────────────────────────────
                val activeNotice = uiState.reportNotice ?: uiState.postNotice
                val isErrorNotice = uiState.reportNotice == null && uiState.postNoticeIsError
                activeNotice?.let { notice ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isErrorNotice) Color(0xFFB71C1C).copy(alpha = 0.95f) else Color(0xFF1B5E20).copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, if (isErrorNotice) Color(0xFFFF8A80) else Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isErrorNotice) Icons.Default.ReportProblem else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isErrorNotice) Color(0xFFFF8A80) else Color(0xFF81C784),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = notice,
                                color = White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (uiState.reportNotice != null) viewModel.clearReportNotice()
                                    if (uiState.postNotice != null) viewModel.clearPostNotice()
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // ── 1. COMPACT TOP HEADER BAR ────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Feed vs Chat Switcher (Proper Icons: 📰 Feed & 💬 Chat)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E2F23))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Surface(
                            onClick = {
                                activeMode = CommunityViewMode.FEED
                                feedSubMode = FeedSubMode.FEED_LIST
                            },
                            shape = RoundedCornerShape(13.dp),
                            color = if (activeMode == CommunityViewMode.FEED) ForestGreen else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("📰", fontSize = 11.sp)
                                Text(
                                    text = "Feed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeMode == CommunityViewMode.FEED) White else White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Surface(
                            onClick = { activeMode = CommunityViewMode.CHAT },
                            shape = RoundedCornerShape(13.dp),
                            color = if (activeMode == CommunityViewMode.CHAT) ForestGreen else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("💬", fontSize = 11.sp)
                                Text(
                                    text = "Chat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeMode == CommunityViewMode.CHAT) White else White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // In Feed Mode (Feed List Sub-view): Center Search Bar + Filter + Create Post
                    if (activeMode == CommunityViewMode.FEED && feedSubMode == FeedSubMode.FEED_LIST) {
                        // Filter Pills: All vs My Posts
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF16251A))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Surface(
                                onClick = { showOnlyMyPosts = false },
                                shape = RoundedCornerShape(12.dp),
                                color = if (!showOnlyMyPosts) Color(0xFF264430) else Color.Transparent
                            ) {
                                Text(
                                    text = "All",
                                    fontSize = 10.sp,
                                    fontWeight = if (!showOnlyMyPosts) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!showOnlyMyPosts) White else White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                onClick = { showOnlyMyPosts = true },
                                shape = RoundedCornerShape(12.dp),
                                color = if (showOnlyMyPosts) Color(0xFF264430) else Color.Transparent
                            ) {
                                Text(
                                    text = "My Posts",
                                    fontSize = 10.sp,
                                    fontWeight = if (showOnlyMyPosts) FontWeight.Bold else FontWeight.Normal,
                                    color = if (showOnlyMyPosts) White else White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Search Bar (Placed in center of top header)
                        var isSearchFocused by remember { mutableStateOf(false) }
                        val searchScale by animateFloatAsState(if (isSearchFocused) 1.03f else 1.0f, label = "searchScale")

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .graphicsLayer {
                                    scaleX = searchScale
                                    scaleY = searchScale
                                },
                            shape = RoundedCornerShape(17.dp),
                            color = if (isSearchFocused) Color(0xFF223B2A) else Color(0xFF1B2E21),
                            border = BorderStroke(1.dp, if (isSearchFocused) Color(0xFF81C784) else White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(14.dp)
                                )
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (uiState.searchQuery.isEmpty()) {
                                        Text(
                                            "Search discussions, topics...",
                                            color = White.copy(alpha = 0.45f),
                                            fontSize = 11.sp
                                        )
                                    }
                                    BasicTextField(
                                        value = uiState.searchQuery,
                                        onValueChange = { viewModel.updateSearchQuery(it) },
                                        singleLine = true,
                                        textStyle = TextStyle(color = White, fontSize = 11.sp),
                                        cursorBrush = SolidColor(Color(0xFF81C784)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { isSearchFocused = it.isFocused }
                                    )
                                }
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.updateSearchQuery("") },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // + Create Post Button (Transitions to inline create view)
                        Button(
                            onClick = { feedSubMode = FeedSubMode.CREATE_POST },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(15.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Create Post",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Clean Close (X) Button (No Circle background)
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = White.copy(alpha = 0.08f))

                // ── 2. MAIN CONTENT AREA (Inline Feed / Create / Detail OR Chat) ─
                if (activeMode == CommunityViewMode.FEED) {
                    when (feedSubMode) {
                        FeedSubMode.FEED_LIST -> {
                            // Single Column Full-Height Feed
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                if (displayPosts.isEmpty()) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🌱", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (showOnlyMyPosts) "You haven't created any posts yet." else "No community discussions found.",
                                            color = White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(displayPosts) { post ->
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
                        }

                        FeedSubMode.CREATE_POST -> {
                            // Inline Create Post View in same screen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                InlineCreatePostView(
                                    currentUserName = uiState.currentUserName,
                                    isPublishing = uiState.isPublishingPost,
                                    onCancel = { feedSubMode = FeedSubMode.FEED_LIST },
                                    onSubmit = { title, category, content, authorName ->
                                        viewModel.createPost(title, category, content, authorName) { success ->
                                            if (success) {
                                                feedSubMode = FeedSubMode.FEED_LIST
                                            }
                                        }
                                    }
                                )

                            }
                        }

                        FeedSubMode.POST_DETAIL -> {
                            // Inline Post Detail & Comments in same screen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                uiState.selectedPost?.let { post ->
                                    InlinePostDetailView(
                                        post = post,
                                        comments = uiState.selectedPostComments,
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
                                        }
                                    )
                                } ?: run {
                                    feedSubMode = FeedSubMode.FEED_LIST
                                }
                            }
                        }
                    }
                } else {
                    // ── CHAT MODE WITH SIDE NAV & CONVERSATION SEARCH ────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Side Nav for Chat (Channels & Contacts with Avatars)
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF16251A),
                            border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Header: CONVERSATIONS + Add Friend Button
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "CONVERSATIONS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81C784)
                                    )
                                    IconButton(
                                        onClick = { showAddFriendDialog = true },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PersonAdd,
                                            contentDescription = "Add Friend",
                                            tint = Color(0xFF81C784),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                // Search bar for friend and conversation
                                var isFriendSearchFocused by remember { mutableStateOf(false) }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isFriendSearchFocused) Color(0xFF223B2A) else Color(0xFF1B2E21),
                                    border = BorderStroke(0.8.dp, if (isFriendSearchFocused) Color(0xFF81C784) else White.copy(alpha = 0.12f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color(0xFF81C784),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (friendConvSearchQuery.isEmpty()) {
                                                Text(
                                                    "Search chats...",
                                                    color = White.copy(alpha = 0.45f),
                                                    fontSize = 9.sp
                                                )
                                            }
                                            BasicTextField(
                                                value = friendConvSearchQuery,
                                                onValueChange = { friendConvSearchQuery = it },
                                                singleLine = true,
                                                textStyle = TextStyle(color = White, fontSize = 9.5.sp),
                                                cursorBrush = SolidColor(Color(0xFF81C784)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .onFocusChanged { isFriendSearchFocused = it.isFocused }
                                            )
                                        }
                                        if (friendConvSearchQuery.isNotEmpty()) {
                                            IconButton(
                                                onClick = { friendConvSearchQuery = "" },
                                                modifier = Modifier.size(14.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear",
                                                    tint = White,
                                                    modifier = Modifier.size(9.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (chatChannels.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 20.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("👥", fontSize = 20.sp)
                                            Text(
                                                text = "No friends yet",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = "Tap + to add friends",
                                                fontSize = 8.5.sp,
                                                color = Color(0xFF81C784),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else if (filteredChannels.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 20.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Text(
                                            text = "No chats found",
                                            fontSize = 9.5.sp,
                                            color = White.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(filteredChannels) { channel ->
                                            val isSelected = channel.id == selectedChannelId
                                            Surface(
                                                onClick = { selectedChannelId = channel.id },
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSelected) ForestGreen else Color(0xFF1D2F22),
                                                border = BorderStroke(
                                                    0.8.dp,
                                                    if (isSelected) Color(0xFF81C784) else White.copy(alpha = 0.05f)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    // Channel Avatar
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) Color(0xFF1B5E20) else getAvatarColor(channel.name)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(channel.iconEmoji, fontSize = 11.sp)
                                                    }

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = channel.name,
                                                            fontSize = 11.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            color = White,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = channel.statusText,
                                                            fontSize = 9.sp,
                                                            color = if (isSelected) White.copy(alpha = 0.85f) else Color(0xFF81C784),
                                                            maxLines = 1
                                                        )
                                                    }
                                                    if (channel.unreadCount > 0 && !isSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(16.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFFE53935)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "${channel.unreadCount}",
                                                                color = White,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Right: Active Conversation Area or Empty State
                        if (selectedChannel == null) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF142117),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("💬", fontSize = 34.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (chatChannels.isEmpty()) "No Conversations Yet" else "No Conversation Selected",
                                        color = White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (chatChannels.isEmpty())
                                            "Add friends using the + button to start messaging!"
                                        else
                                            "Select a friend on the left to view messages.",
                                        color = White.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    if (chatChannels.isEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { showAddFriendDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                            shape = RoundedCornerShape(14.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PersonAdd,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add Friend", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                                        }
                                    }
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF142117),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Conversation Header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Left: Avatar & User / Channel Name
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(getAvatarColor(selectedChannel.name)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(selectedChannel.iconEmoji, fontSize = 12.sp)
                                            }
                                            Text(
                                                text = selectedChannel.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                maxLines = 1
                                            )
                                        }

                                        // Center: Active Status
                                        Text(
                                            text = selectedChannel.statusText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF81C784)
                                        )

                                        // Right: Conversation Search Input & Report User
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            var isConvSearchFocused by remember { mutableStateOf(false) }
                                            val convSearchScale by animateFloatAsState(if (isConvSearchFocused) 1.03f else 1.0f, label = "convSearchScale")

                                            Surface(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .height(30.dp)
                                                    .graphicsLayer {
                                                        scaleX = convSearchScale
                                                        scaleY = convSearchScale
                                                    },
                                                shape = RoundedCornerShape(15.dp),
                                                color = if (isConvSearchFocused) Color(0xFF223B2A) else Color(0xFF1B2E21),
                                                border = BorderStroke(1.dp, if (isConvSearchFocused) Color(0xFF81C784) else White.copy(alpha = 0.12f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = "Search messages",
                                                        tint = Color(0xFF81C784),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Box(
                                                        modifier = Modifier.weight(1f),
                                                        contentAlignment = Alignment.CenterStart
                                                    ) {
                                                        if (convSearchQuery.isEmpty()) {
                                                            Text(
                                                                "Search chat...",
                                                                color = White.copy(alpha = 0.45f),
                                                                fontSize = 10.sp
                                                            )
                                                        }
                                                        BasicTextField(
                                                            value = convSearchQuery,
                                                            onValueChange = { convSearchQuery = it },
                                                            singleLine = true,
                                                            textStyle = TextStyle(color = White, fontSize = 10.sp),
                                                            cursorBrush = SolidColor(Color(0xFF81C784)),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .onFocusChanged { isConvSearchFocused = it.isFocused }
                                                        )
                                                    }
                                                    if (convSearchQuery.isNotEmpty()) {
                                                        IconButton(onClick = { convSearchQuery = "" }, modifier = Modifier.size(16.dp)) {
                                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = White, modifier = Modifier.size(11.dp))
                                                        }
                                                    }
                                                }
                                            }

                                            // Report User Button
                                            IconButton(
                                                onClick = {
                                                    activeReportTarget = ReportTarget(
                                                        type = "USER",
                                                        id = selectedChannel.id,
                                                        name = selectedChannel.name,
                                                        content = "Chat Participant: ${selectedChannel.name}"
                                                    )
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Flag,
                                                    contentDescription = "Report user",
                                                    tint = Color(0xFFFF8A80).copy(alpha = 0.85f),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = White.copy(alpha = 0.08f)
                                    )

                                    // Messages Stream
                                    if (activeMessages.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No messages yet",
                                                color = White.copy(alpha = 0.4f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            state = chatListState,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            items(activeMessages) { msg ->
                                                val isMe = msg.sender == "me"
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                                                    verticalAlignment = Alignment.Bottom
                                                ) {
                                                    if (!isMe) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(22.dp)
                                                                .clip(CircleShape)
                                                                .background(getAvatarColor(msg.senderName)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = msg.senderName.take(1).uppercase(),
                                                                color = White,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = if (isMe) ForestGreen else Color(0xFF1E2F23),
                                                        border = BorderStroke(
                                                            0.8.dp,
                                                            if (isMe) Color(0xFF81C784).copy(alpha = 0.5f) else White.copy(alpha = 0.08f)
                                                        ),
                                                        modifier = Modifier.widthIn(max = 280.dp)
                                                    ) {
                                                        Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)) {
                                                            if (!isMe) {
                                                                Text(
                                                                    text = msg.senderName,
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF81C784)
                                                                )
                                                            }
                                                            Text(
                                                                text = msg.text,
                                                                color = White,
                                                                fontSize = 11.sp,
                                                                lineHeight = 14.sp
                                                            )
                                                            Text(
                                                                text = msg.timestamp,
                                                                color = if (isMe) White.copy(alpha = 0.7f) else White.copy(alpha = 0.4f),
                                                                fontSize = 8.sp,
                                                                modifier = Modifier.align(Alignment.End)
                                                            )
                                                        }
                                                    }

                                                    if (isMe) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFF2E7D32)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = uiState.currentUserName.take(1).uppercase().ifBlank { "Y" },
                                                                color = White,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ── Message Input Row with Zoom-in & Focus Pop ──
                                    val chatInputScale by animateFloatAsState(
                                        targetValue = if (isChatInputFocused) 1.02f else 1.0f,
                                        label = "chatInputScale"
                                    )
                                    val chatInputHeight by animateDpAsState(
                                        targetValue = if (isChatInputFocused) 44.dp else 36.dp,
                                        label = "chatInputHeight"
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(chatInputHeight)
                                                .graphicsLayer {
                                                    scaleX = chatInputScale
                                                    scaleY = chatInputScale
                                                },
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isChatInputFocused) Color(0xFF223B2A) else Color(0xFF1B2E21),
                                            border = BorderStroke(1.dp, if (isChatInputFocused) Color(0xFF81C784) else White.copy(alpha = 0.15f))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 14.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                if (chatMessageInput.isEmpty()) {
                                                    Text(
                                                        "Message ${selectedChannel.name}...",
                                                        color = White.copy(alpha = 0.45f),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                BasicTextField(
                                                    value = chatMessageInput,
                                                    onValueChange = { chatMessageInput = it },
                                                    singleLine = true,
                                                    textStyle = TextStyle(
                                                        color = White,
                                                        fontSize = if (isChatInputFocused) 12.sp else 11.sp
                                                    ),
                                                    cursorBrush = SolidColor(Color(0xFF81C784)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .onFocusChanged { isChatInputFocused = it.isFocused }
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                if (chatMessageInput.isNotBlank() && selectedChannelId != null) {
                                                    val list = directChatMessages.getOrPut(selectedChannelId!!) { mutableStateListOf() }
                                                    list.add(
                                                        CommunityChatMessage("me", uiState.currentUserName.ifBlank { "You" }, chatMessageInput.trim())
                                                    )
                                                    chatMessageInput = ""
                                                }
                                            },
                                            modifier = Modifier
                                                .size(if (isChatInputFocused) 40.dp else 34.dp)
                                                .clip(CircleShape)
                                                .background(ForestGreen)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = "Send",
                                                tint = White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Active Report Dialog ─────────────────────────────────────────────
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

        // ── Add Friend Dialog ────────────────────────────────────────────────
        if (showAddFriendDialog) {
            AddFriendDialog(
                members = uiState.communityMembers,
                friends = uiState.friends,
                currentUserId = uiState.currentUserId,
                onDismiss = { showAddFriendDialog = false },
                onAddFriend = { memberId ->
                    viewModel.addFriend(memberId)
                },
                onSelectFriend = { memberId ->
                    selectedChannelId = memberId
                }
            )
        }
    }
}

// ─── Inline Create Post View ─────────────────────────────────────────────────

@Composable
private fun InlineCreatePostView(
    currentUserName: String = "You",
    isPublishing: Boolean = false,
    onCancel: () -> Unit,
    onSubmit: (title: String, category: String, content: String, authorName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isTitleFocused by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onCancel, enabled = !isPublishing, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (isPublishing) White.copy(alpha = 0.3f) else White, modifier = Modifier.size(18.dp))
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentUserName.take(1).uppercase().ifBlank { "Y" }, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text("Create Community Post", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = White)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !isPublishing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Cancel", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank() && !isPublishing) {
                            onSubmit(title, "GENERAL", content, currentUserName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    enabled = title.isNotBlank() && content.isNotBlank() && !isPublishing,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(13.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Publishing...", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White)
                    } else {
                        Text("Publish Post", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        }

        // Title Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2E21),
            border = BorderStroke(
                1.dp,
                if (isTitleFocused) Color(0xFF81C784) else White.copy(alpha = 0.15f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "Post title or summary (e.g. Flea Beetle remedy for Eggplant)...",
                        color = White.copy(alpha = 0.45f),
                        fontSize = 11.sp
                    )
                }
                BasicTextField(
                    value = title,
                    onValueChange = { if (!isPublishing) title = it },
                    readOnly = isPublishing,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(Color(0xFF81C784)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isTitleFocused = it.isFocused }
                )
            }
        }

        // Content Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2E21),
            border = BorderStroke(
                1.dp,
                if (isContentFocused) Color(0xFF81C784) else White.copy(alpha = 0.15f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (content.isEmpty()) {
                    Text(
                        text = "Write your advice, questions, or details here...",
                        color = White.copy(alpha = 0.45f),
                        fontSize = 11.sp
                    )
                }
                BasicTextField(
                    value = content,
                    onValueChange = { if (!isPublishing) content = it },
                    readOnly = isPublishing,
                    textStyle = TextStyle(
                        color = White,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(Color(0xFF81C784)),
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { isContentFocused = it.isFocused }
                )
            }
        }
    }
}

// ─── Inline Post Detail View ─────────────────────────────────────────────────

@Composable
private fun InlinePostDetailView(
    post: CommunityPost,
    comments: List<CommunityComment>,
    currentUserName: String = "You",
    onBack: () -> Unit,
    onLikeToggle: (String) -> Unit,
    onAddComment: (postId: String, content: String, authorName: String) -> Unit,
    onReportPost: (CommunityPost) -> Unit,
    onReportComment: (CommunityComment) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var isCommentFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Row with Back Button, Avatar, Username, Timestamp, Like, Reply and Report
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White, modifier = Modifier.size(18.dp))
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(getAvatarColor(post.authorName)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.authorName.take(1).uppercase(), color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                // Username & Timestamp SIDE BY SIDE (Aside)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(post.authorName, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("• ${post.timestamp}", color = Color(0xFFA5D6A7), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Likes & Reply summary & Report Button
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.clickable { onLikeToggle(post.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) Color(0xFFFF5252) else White.copy(alpha = 0.6f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${post.likesCount}",
                        color = if (post.isLikedByMe) Color(0xFFFF5252) else White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text("💬 ${comments.size}", color = Color(0xFFA5D6A7), fontSize = 11.sp)

                // Report Post Flag Button
                Surface(
                    onClick = { onReportPost(post) },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFB71C1C).copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, Color(0xFFFF8A80).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report post",
                            tint = Color(0xFFFF8A80),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Report",
                            color = Color(0xFFFF8A80),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = White.copy(alpha = 0.08f))

        // Scrollable Body: Post Content + Comments Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Post Content Card
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1A2B1E),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = post.title, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = post.content,
                            color = White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Comments Header
            item {
                Text(
                    text = "Discussion & Replies (${comments.size})",
                    color = Color(0xFF81C784),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "No comments yet. Be the first to share your thoughts!",
                        color = White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF142117),
                        border = BorderStroke(0.8.dp, White.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(getAvatarColor(comment.authorName)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(comment.authorName.take(1).uppercase(), color = White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(comment.authorName, color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("• ${comment.timestamp}", color = White.copy(alpha = 0.45f), fontSize = 9.sp)
                                }

                                // Flag icon for reporting comment
                                IconButton(
                                    onClick = { onReportComment(comment) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Report comment",
                                        tint = White.copy(alpha = 0.35f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Text(comment.content, color = White, fontSize = 11.sp, lineHeight = 14.sp, modifier = Modifier.padding(start = 26.dp))
                        }
                    }
                }
            }
        }

        // Add Comment Input Row at Bottom (Pixel-perfect vertically centered)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Text("Y", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1B2E21),
                border = BorderStroke(1.dp, if (isCommentFocused) Color(0xFF81C784) else White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newCommentText.isEmpty()) {
                        Text("Write a comment / reply...", color = White.copy(alpha = 0.45f), fontSize = 11.sp)
                    }
                    BasicTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = White, fontSize = 11.sp),
                        cursorBrush = SolidColor(Color(0xFF81C784)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isCommentFocused = it.isFocused }
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
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ForestGreen)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

// ─── Single Post Card Component ──────────────────────────────────────────────

@Composable
private fun PostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onReportClick: (CommunityPost) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ForestGreen.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16251A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Author info with Avatar & Timestamp SIDE BY SIDE (Aside) + Report Flag Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(post.authorName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    // Author Name and Timestamp placed horizontally aside
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = post.authorName,
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• ${post.timestamp}",
                            color = Color(0xFFA5D6A7),
                            fontSize = 10.sp
                        )
                    }
                }

                // Flag / Report Icon Button in Post Header
                IconButton(
                    onClick = { onReportClick(post) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Report post",
                        tint = White.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Title & Excerpt
            Text(
                text = post.title,
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = post.content,
                color = White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(6.dp))

            // Social Action Bar (Like & Reply)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onLikeClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) Color(0xFFFF5252) else White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likesCount} Helpful",
                        color = if (post.isLikedByMe) Color(0xFFFF5252) else White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "💬 ${post.commentsCount} Replies",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─── Community Report Dialog ─────────────────────────────────────────────────

@Composable
private fun CommunityReportDialog(
    target: ReportTarget,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, details: String) -> Unit
) {
    val reasons = remember(target.type) {
        if (target.type == "USER") {
            listOf(
                "Abusive / Harassing Behavior",
                "Spamming Unsolicited Messages",
                "Impersonation / Fake Account",
                "Prohibited Agricultural Selling",
                "Other Violations"
            )
        } else {
            listOf(
                "Inappropriate / Offensive Content",
                "Spam / Misleading Farming Advice",
                "Harassment / Abusive Remarks",
                "Fake Seeds / Counterfeit Product Scam",
                "Other Community Guideline Violation"
            )
        }
    }

    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var detailsText by remember { mutableStateOf("") }
    var isDetailsFocused by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(min = 320.dp, max = 460.dp)
                .fillMaxWidth(0.92f)
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF142417)),
            border = BorderStroke(1.2.dp, Color(0xFFE57373).copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dialog Title Header
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
                                .background(Color(0xFFD32F2F).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Report ${target.type.lowercase().replaceFirstChar { it.uppercase() }}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                text = "Notify admin moderation team",
                                fontSize = 9.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = White.copy(alpha = 0.08f))

                // Target Context Summary Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1B2F20),
                    border = BorderStroke(0.8.dp, White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (target.type) {
                                    "USER" -> "Reported User:"
                                    "COMMENT" -> "Reported Comment by:"
                                    else -> "Reported Post by:"
                                },
                                fontSize = 10.sp,
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = target.name,
                                fontSize = 10.sp,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (!target.content.isNullOrBlank()) {
                            Text(
                                text = "\"${target.content}\"",
                                fontSize = 10.sp,
                                color = White.copy(alpha = 0.75f),
                                maxLines = 2
                            )
                        }
                    }
                }

                // Reason Selection
                Text(
                    text = "Select Violation Reason:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA5D6A7)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reasons.forEach { reason ->
                        val isSelected = reason == selectedReason
                        Surface(
                            onClick = { selectedReason = reason },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFFB71C1C).copy(alpha = 0.35f) else Color(0xFF1A2B1E),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFFF8A80) else White.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFFF8A80) else Color.Transparent)
                                        .border(1.2.dp, if (isSelected) Color(0xFFFF8A80) else White.copy(alpha = 0.4f), CircleShape)
                                )
                                Text(
                                    text = reason,
                                    fontSize = 10.sp,
                                    color = if (isSelected) White else White.copy(alpha = 0.85f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Optional Details / Remarks Field
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1A2B1E),
                    border = BorderStroke(
                        1.dp,
                        if (isDetailsFocused) Color(0xFF81C784) else White.copy(alpha = 0.12f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (detailsText.isEmpty()) {
                            Text(
                                text = "Additional context or remarks for admin (optional)...",
                                color = White.copy(alpha = 0.4f),
                                fontSize = 10.sp
                            )
                        }
                        BasicTextField(
                            value = detailsText,
                            onValueChange = { detailsText = it },
                            textStyle = TextStyle(color = White, fontSize = 10.sp),
                            cursorBrush = SolidColor(Color(0xFF81C784)),
                            modifier = Modifier
                                .fillMaxSize()
                                .onFocusChanged { isDetailsFocused = it.isFocused }
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Cancel", fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSubmit(selectedReason, detailsText.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Submit Report",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

// ─── Add Friend Dialog ───────────────────────────────────────────────────────

@Composable
private fun AddFriendDialog(
    members: List<CommunityMember>,
    friends: List<CommunityMember>,
    currentUserId: String?,
    onDismiss: () -> Unit,
    onAddFriend: (String) -> Unit,
    onSelectFriend: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val friendIds = remember(friends) { friends.map { it.id }.toSet() }

    val filteredMembers = remember(members, searchQuery, currentUserId) {
        val nonSelf = if (currentUserId.isNullOrBlank()) members else members.filter { it.id != currentUserId }
        if (searchQuery.isBlank()) {
            nonSelf
        } else {
            nonSelf.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(min = 340.dp, max = 480.dp)
                .fillMaxWidth(0.9f)
                .heightIn(max = 420.dp)
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF142417)),
            border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add Friends",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Search Bar
                var isSearchFocused by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    color = if (isSearchFocused) Color(0xFF223B2A) else Color(0xFF1B2E21),
                    border = BorderStroke(1.dp, if (isSearchFocused) Color(0xFF81C784) else White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Search by name or handle...",
                                    color = White.copy(alpha = 0.45f),
                                    fontSize = 11.sp
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(color = White, fontSize = 11.sp),
                                cursorBrush = SolidColor(Color(0xFF81C784)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isSearchFocused = it.isFocused }
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Member List
                if (filteredMembers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No members match \"$searchQuery\"" else "No community members found.",
                            color = White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredMembers) { member ->
                            val isAlreadyFriend = friendIds.contains(member.id)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1B2E21),
                                border = BorderStroke(0.8.dp, White.copy(alpha = 0.08f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(getAvatarColor(member.name)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = member.name.take(1).uppercase(),
                                                color = White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = member.name,
                                                color = White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = member.statusText,
                                                color = Color(0xFF81C784),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }

                                    if (isAlreadyFriend) {
                                        Surface(
                                            onClick = {
                                                onSelectFriend(member.id)
                                                onDismiss()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF2E7D32).copy(alpha = 0.3f),
                                            border = BorderStroke(1.dp, Color(0xFF81C784))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF81C784),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "Chat",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF81C784)
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                onAddFriend(member.id)
                                                onSelectFriend(member.id)
                                                onDismiss()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Add",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

