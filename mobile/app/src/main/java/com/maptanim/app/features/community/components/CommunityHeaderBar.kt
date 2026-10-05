package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Search
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
import com.maptanim.app.features.community.model.CommunityViewMode

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CommunityHeaderBar — Top navigation bar with Feed/Chat mode switcher, All/Mine filter, search input, and create post action.
 */
@Composable
fun CommunityHeaderBar(
    activeMode: CommunityViewMode,
    onModeChange: (CommunityViewMode) -> Unit,
    showOnlyMyPosts: Boolean,
    onToggleShowOnlyMyPosts: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Feed vs Chat Mode Switcher
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(LightSurface)
                .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Surface(
                onClick = { onModeChange(CommunityViewMode.FEED) },
                shape = RoundedCornerShape(6.dp),
                color = if (activeMode == CommunityViewMode.FEED) LushGreen else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DynamicFeed,
                        contentDescription = null,
                        tint = if (activeMode == CommunityViewMode.FEED) Color.White else MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Feed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeMode == CommunityViewMode.FEED) Color.White else DeepBlack
                    )
                }
            }

            Surface(
                onClick = { onModeChange(CommunityViewMode.CHAT) },
                shape = RoundedCornerShape(6.dp),
                color = if (activeMode == CommunityViewMode.CHAT) LushGreen else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = if (activeMode == CommunityViewMode.CHAT) Color.White else MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Chat",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeMode == CommunityViewMode.CHAT) Color.White else DeepBlack
                    )
                }
            }
        }

        // Feed Mode Controls: Filter + Search + Create
        if (activeMode == CommunityViewMode.FEED) {
            // All vs My Posts filter
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightSurface)
                    .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Surface(
                    onClick = { onToggleShowOnlyMyPosts(false) },
                    shape = RoundedCornerShape(6.dp),
                    color = if (!showOnlyMyPosts) LushGreen else Color.Transparent
                ) {
                    Text(
                        text = "All",
                        fontSize = 11.sp,
                        fontWeight = if (!showOnlyMyPosts) FontWeight.Bold else FontWeight.Normal,
                        color = if (!showOnlyMyPosts) Color.White else DeepBlack,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    onClick = { onToggleShowOnlyMyPosts(true) },
                    shape = RoundedCornerShape(6.dp),
                    color = if (showOnlyMyPosts) LushGreen else Color.Transparent
                ) {
                    Text(
                        text = "Mine",
                        fontSize = 11.sp,
                        fontWeight = if (showOnlyMyPosts) FontWeight.Bold else FontWeight.Normal,
                        color = if (showOnlyMyPosts) Color.White else DeepBlack,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Search Bar
            var isSearchFocused by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp),
                shape = RoundedCornerShape(8.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, if (isSearchFocused) LushGreen else CardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = LushGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search topics...",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(color = DeepBlack, fontSize = 11.sp),
                            cursorBrush = SolidColor(LushGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isSearchFocused = it.isFocused }
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MutedText,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // + Create Post Button
            Button(
                onClick = onCreatePostClick,
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
