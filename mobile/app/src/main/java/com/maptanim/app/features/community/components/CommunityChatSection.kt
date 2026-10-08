package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import com.maptanim.app.features.community.model.CommunityChatMessage
import com.maptanim.app.features.community.model.ReportTarget
import com.maptanim.app.features.community.model.getAvatarColor
import com.maptanim.app.features.community.viewmodel.CommunityMember

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CommunityChatSection:
 * - Search bar and search button at top (bottom of header).
 * - Divider line.
 * - "Add friends": 1-row horizontal scroll with suggested farmers and "+ Add" action.
 * - Divider line.
 * - "Friend list": vertical list of friends/conversations.
 * - Dedicated 1-on-1 Chat Screen when tapping a friend, with back navigation, message stream, and reply box.
 * Adheres strictly to the Daylight High-Contrast Theme (wide edge-to-edge layout, no cards).
 */
@Composable
fun CommunityChatSection(
    friends: List<CommunityMember>,
    communityMembers: List<CommunityMember> = emptyList(),
    onAddFriend: (String) -> Unit = {},
    currentUserName: String = "You",
    onOpenAddFriend: () -> Unit,
    onReportUser: (ReportTarget) -> Unit,
    searchQuery: String = "",
    modifier: Modifier = Modifier
) {
    var chatSearchQuery by remember { mutableStateOf(searchQuery) }
    var isSearchFocused by remember { mutableStateOf(false) }
    var activeChatMember by remember { mutableStateOf<CommunityMember?>(null) }

    // Direct messages cache per member id
    val directChatMessages = remember {
        mutableStateMapOf<String, androidx.compose.runtime.snapshots.SnapshotStateList<CommunityChatMessage>>()
    }

    // If activeChatMember is selected, render the dedicated 1-on-1 Chat Screen!
    if (activeChatMember != null) {
        val member = activeChatMember!!
        val memberMessages = remember(member.id) {
            directChatMessages.getOrPut(member.id) {
                androidx.compose.runtime.mutableStateListOf(
                    CommunityChatMessage(
                        sender = member.id,
                        senderName = member.name,
                        text = "Kumusta! Welcome to the chat. How's your crop harvest?",
                        timestamp = "10:30 AM"
                    )
                )
            }
        }
        DedicatedChatScreen(
            member = member,
            messages = memberMessages,
            currentUserName = currentUserName,
            onBack = { activeChatMember = null },
            onSendMessage = { text ->
                memberMessages.add(
                    CommunityChatMessage(
                        sender = "me",
                        senderName = currentUserName,
                        text = text,
                        timestamp = "Just now"
                    )
                )
            },
            onReport = {
                onReportUser(
                    ReportTarget(
                        type = "USER",
                        id = member.id,
                        name = member.name,
                        content = "Chat conversation with ${member.name}"
                    )
                )
            },
            modifier = modifier
        )
        return
    }

    // ── MAIN FRIENDS / CHAT LIST VIEW ──
    val friendIds = remember(friends) { friends.map { it.id }.toSet() }
    val suggestedToAdd = remember(communityMembers, friendIds) {
        communityMembers.filter { it.id !in friendIds }
    }

    val filteredFriends = remember(friends, chatSearchQuery) {
        if (chatSearchQuery.isBlank()) {
            friends
        } else {
            friends.filter { friend ->
                friend.name.contains(chatSearchQuery, ignoreCase = true) ||
                friend.statusText.contains(chatSearchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── 1. SEARCH BAR & SEARCH BUTTON (Bottom of Header) ───────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, if (isSearchFocused) LushGreen else CardBorderColor)
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
                        tint = if (isSearchFocused) LushGreen else MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (chatSearchQuery.isEmpty()) {
                            Text(
                                text = "Search friends or farmers...",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                        }
                        BasicTextField(
                            value = chatSearchQuery,
                            onValueChange = { chatSearchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = DeepBlack,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(LushGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isSearchFocused = it.isFocused }
                        )
                    }
                    if (chatSearchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { chatSearchQuery = "" },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MutedText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Search Button
            Button(
                onClick = { /* Applied via query */ },
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "Search",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── 2. ADD FRIENDS (1 ROW HORIZONTAL SCROLL) ────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Friends",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Text(
                    text = "${suggestedToAdd.size} suggestions",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 1: "+ Add Friend" dialog launcher
                item(key = "add_friend_action") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenAddFriend() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(LightSurface)
                                .border(1.5.dp, LushGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add Friend",
                                tint = LushGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Find Farmer",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen
                        )
                    }
                }

                // Horizontal scrollable farmer cards to add
                items(suggestedToAdd, key = { it.id }) { member ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAddFriend(member.id) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(getAvatarColor(member.name)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = member.name.substringBefore(" ").take(10),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack,
                            maxLines = 1
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LushGreen.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.height(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+ Add",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── 3. FRIEND LIST (VERTICAL LIST) ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Friend List",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = "${filteredFriends.size} connected",
                fontSize = 11.sp,
                color = MutedText
            )
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        if (filteredFriends.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = if (friends.isEmpty()) "No Friends Added Yet" else "No friends match \"$chatSearchQuery\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Text(
                        text = if (friends.isEmpty())
                            "Tap on any farmer in the \"Add Friends\" row above or use \"Find Farmer\" to start connecting and chatting!"
                        else
                            "Try searching with a different farmer name.",
                        fontSize = 12.sp,
                        color = MutedText,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredFriends, key = { it.id }) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeChatMember = friend }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(getAvatarColor(friend.name)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = friend.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                                Text(
                                    text = friend.statusText.ifBlank { "Active farmer" },
                                    fontSize = 12.sp,
                                    color = MutedText
                                )
                            }
                        }

                        // Chat button cue
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    tint = LushGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Chat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f), thickness = 1.dp)
                }
            }
        }
    }
}

/**
 * Dedicated 1-on-1 Chat Screen:
 * Opens when a user taps a friend from the friend list.
 * Includes top bar with back navigation, real-time message stream, and message input field.
 */
@Composable
private fun DedicatedChatScreen(
    member: CommunityMember,
    messages: List<CommunityChatMessage>,
    currentUserName: String,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── TOP BAR: Back Navigation + Friend Info + Report ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Friend List",
                        tint = DeepBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(getAvatarColor(member.name)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = member.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(LushGreen)
                        )
                        Text(
                            text = "Active Now",
                            fontSize = 11.sp,
                            color = LushGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            IconButton(onClick = onReport, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Report User",
                    tint = MutedText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── MESSAGE HISTORY ──────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.sender == "me" || msg.senderName.equals(currentUserName, ignoreCase = true)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        color = if (isMe) LushGreen else LightSurface,
                        border = BorderStroke(1.dp, if (isMe) LushGreen else CardBorderColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (isMe) Color.White else DeepBlack,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    Text(
                        text = msg.timestamp,
                        fontSize = 10.sp,
                        color = MutedText,
                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── MESSAGE INPUT ROW ────────────────────────────────────────────────
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
                    .height(42.dp),
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
                    if (textInput.isEmpty()) {
                        Text(
                            text = "Type a message...",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                    BasicTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
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
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                enabled = textInput.isNotBlank(),
                modifier = Modifier
                    .size(42.dp)
                    .background(if (textInput.isNotBlank()) LushGreen else CardBorderColor, CircleShape)
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
