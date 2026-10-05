package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.community.model.ChatChannel
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
 * CommunityChatSection — Direct peer-to-peer farmer messaging and contact channels.
 * Adheres strictly to the Daylight High-Contrast Theme (no emojis, vector icons only).
 */
@Composable
fun CommunityChatSection(
    friends: List<CommunityMember>,
    currentUserName: String,
    onOpenAddFriend: () -> Unit,
    onReportUser: (ReportTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val chatChannels = remember(friends) {
        friends.map { m ->
            ChatChannel(id = m.id, name = m.name, statusText = m.statusText)
        }
    }
    var selectedChannelId by remember { mutableStateOf<String?>(null) }
    var friendSearchQuery by remember { mutableStateOf("") }
    var chatMessageInput by remember { mutableStateOf("") }
    var isChatInputFocused by remember { mutableStateOf(false) }

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

    val filteredChannels = remember(chatChannels, friendSearchQuery) {
        if (friendSearchQuery.isBlank()) {
            chatChannels
        } else {
            chatChannels.filter { it.name.contains(friendSearchQuery, ignoreCase = true) }
        }
    }

    val chatListState = rememberLazyListState()
    LaunchedEffect(rawActiveMessages.size) {
        if (rawActiveMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(rawActiveMessages.size - 1)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Side Nav for Chat (Channels & Contacts)
        ChatConversationsSidebar(
            channels = filteredChannels,
            selectedChannelId = selectedChannelId,
            searchQuery = friendSearchQuery,
            onSearchQueryChange = { friendSearchQuery = it },
            onSelectChannel = { selectedChannelId = it },
            onOpenAddFriend = onOpenAddFriend,
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
        )

        // Right: Active Conversation Area or Empty State
        if (selectedChannel == null) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (chatChannels.isEmpty()) "No Conversations Yet" else "No Conversation Selected",
                        color = DeepBlack,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (chatChannels.isEmpty())
                            "Add fellow farmers using the + button to start messaging!"
                        else
                            "Select a friend on the left to start chatting.",
                        color = MutedText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Conversation Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(getAvatarColor(selectedChannel.name)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedChannel.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = selectedChannel.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                                Text(
                                    text = selectedChannel.statusText,
                                    fontSize = 9.sp,
                                    color = LushGreen
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onReportUser(
                                    ReportTarget(
                                        type = "USER",
                                        id = selectedChannel.id,
                                        name = selectedChannel.name,
                                        content = "Chat Participant: ${selectedChannel.name}"
                                    )
                                )
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Report user",
                                tint = MutedText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = CardBorderColor)

                    // Messages Stream
                    if (rawActiveMessages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No messages yet. Say hello!",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            state = chatListState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(rawActiveMessages) { msg ->
                                val isMe = msg.sender == "me"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    if (!isMe) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(getAvatarColor(msg.senderName)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = msg.senderName.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isMe) LushGreen else LightSurface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isMe) LushGreen else CardBorderColor
                                        ),
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            Text(
                                                text = msg.text,
                                                color = if (isMe) Color.White else DeepBlack,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                            Text(
                                                text = msg.timestamp,
                                                color = if (isMe) Color.White.copy(alpha = 0.7f) else MutedText,
                                                fontSize = 8.sp,
                                                modifier = Modifier.align(Alignment.End)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Send Message Row
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
                                .height(38.dp),
                            shape = RoundedCornerShape(19.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, if (isChatInputFocused) LushGreen else CardBorderColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (chatMessageInput.isEmpty()) {
                                    Text(
                                        "Type a message...",
                                        color = MutedText,
                                        fontSize = 11.sp
                                    )
                                }
                                BasicTextField(
                                    value = chatMessageInput,
                                    onValueChange = { chatMessageInput = it },
                                    singleLine = true,
                                    textStyle = TextStyle(color = DeepBlack, fontSize = 11.sp),
                                    cursorBrush = SolidColor(LushGreen),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { isChatInputFocused = it.isFocused }
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (chatMessageInput.isNotBlank()) {
                                    rawActiveMessages.add(
                                        CommunityChatMessage(
                                            sender = "me",
                                            senderName = currentUserName,
                                            text = chatMessageInput.trim()
                                        )
                                    )
                                    chatMessageInput = ""
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
        }
    }
}
