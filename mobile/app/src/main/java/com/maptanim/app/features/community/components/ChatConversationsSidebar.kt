package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.community.model.ChatChannel
import com.maptanim.app.features.community.model.getAvatarColor

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * ChatConversationsSidebar — Side navigation panel showing farmer contacts and conversation channels.
 */
@Composable
fun ChatConversationsSidebar(
    channels: List<ChatChannel>,
    selectedChannelId: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectChannel: (String) -> Unit,
    onOpenAddFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen
                )
                IconButton(
                    onClick = onOpenAddFriend,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Friend",
                        tint = LushGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Search bar for chats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(15.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor)
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
                        contentDescription = "Search",
                        tint = LushGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search chats...",
                                color = MutedText,
                                fontSize = 10.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(color = DeepBlack, fontSize = 10.sp),
                            cursorBrush = SolidColor(LushGreen),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (channels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "No friends yet",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Text(
                            text = "Tap + to add fellow farmers",
                            fontSize = 9.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(channels) { channel ->
                        val isSelected = channel.id == selectedChannelId
                        Surface(
                            onClick = { onSelectChannel(channel.id) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFE8F5E9) else LightSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) LushGreen else CardBorderColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(getAvatarColor(channel.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = channel.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = channel.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LushGreen else DeepBlack,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = channel.statusText,
                                        fontSize = 9.sp,
                                        color = MutedText,
                                        maxLines = 1
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
