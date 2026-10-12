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
 * CommunityHeaderBar — Forum-styled header:
 * - Row 1: Feed and Chat tabs on the left, Search bar and Search button on the right.
 * - Divider line separating the header.
 * - Row 2 (Bottom of header in Feed mode): "All discussion" and "My Post" on left, "Create Post" on right.
 * - Divider line separating header from the feed.
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
    var isSearchFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // ── TOP ROW: Feed, Chat mode switcher (left) + Search Bar & Search Button (right) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mode Switcher: Feed / Chat
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurface)
                    .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { onModeChange(CommunityViewMode.FEED) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeMode == CommunityViewMode.FEED) LushGreen else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DynamicFeed,
                            contentDescription = "Feed",
                            tint = if (activeMode == CommunityViewMode.FEED) Color.White else MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Feed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeMode == CommunityViewMode.FEED) Color.White else DeepBlack
                        )
                    }
                }

                Surface(
                    onClick = { onModeChange(CommunityViewMode.CHAT) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeMode == CommunityViewMode.CHAT) LushGreen else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = if (activeMode == CommunityViewMode.CHAT) Color.White else MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Chat",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeMode == CommunityViewMode.CHAT) Color.White else DeepBlack
                        )
                    }
                }
            }

            // In Feed mode: Search Bar and Search Button on right
            if (activeMode == CommunityViewMode.FEED) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, if (isSearchFocused) LushGreen else CardBorderColor)
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
                                tint = if (isSearchFocused) LushGreen else MutedText,
                                modifier = Modifier.size(15.dp)
                            )
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search discussions...",
                                        color = MutedText,
                                        fontSize = 11.sp
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = DeepBlack,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(LushGreen),
                                    modifier = Modifier
                                        .fillMaxWidth()
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

                    // Search Button
                    Button(
                        onClick = { /* Search applied reactively */ },
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Search",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Header Divider Line
        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── BOTTOM OF HEADER (FEED MODE): All discussion, My Post (left) + Create Post (right) ──
        if (activeMode == CommunityViewMode.FEED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Filter Tabs: "All discussion" & "My Post"
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightSurface)
                        .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { onToggleShowOnlyMyPosts(false) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (!showOnlyMyPosts) LushGreen else Color.Transparent
                    ) {
                        Text(
                            text = "All discussion",
                            fontSize = 11.sp,
                            fontWeight = if (!showOnlyMyPosts) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (!showOnlyMyPosts) Color.White else DeepBlack,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        onClick = { onToggleShowOnlyMyPosts(true) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (showOnlyMyPosts) LushGreen else Color.Transparent
                    ) {
                        Text(
                            text = "My Post",
                            fontSize = 11.sp,
                            fontWeight = if (showOnlyMyPosts) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (showOnlyMyPosts) Color.White else DeepBlack,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Create Post Button
                Button(
                    onClick = onCreatePostClick,
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Create Post",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Divider Line below bottom of header
            HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
        }
    }
}
