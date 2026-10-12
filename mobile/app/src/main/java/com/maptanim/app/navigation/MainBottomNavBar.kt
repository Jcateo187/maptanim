package com.maptanim.app.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val SubtleBorder = Color(0xFFE0E0E0)
private val UnselectedGray = Color(0xFF757575)

/**
 * MainBottomNavBar — Primary 5-tab navigation bar for MapTanim.
 * Follows clean aesthetic: no background on icons, no click/ripple animations,
 * only the icon and label show the green accent color when active on that screen.
 */
@Composable
fun MainBottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, SubtleBorder),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = when (item) {
                    BottomNavItem.Home -> selectedRoute == Routes.HOME
                    BottomNavItem.Farm -> selectedRoute == Routes.FARM
                    BottomNavItem.Community -> selectedRoute == Routes.COMMUNITY
                    BottomNavItem.Vegetables -> selectedRoute == Routes.LIBRARY
                    BottomNavItem.Profile -> selectedRoute == Routes.PROFILE || selectedRoute.startsWith("profile") || selectedRoute == Routes.SETTINGS
                }
                val itemColor = if (isSelected) LushGreen else UnselectedGray

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigate(item.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = itemColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = itemColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * LandscapeSideNavBar — Adaptive side navigation bar for tablet/landscape orientation.
 */
@Composable
fun LandscapeSideNavBar(
    isOpen: Boolean,
    onToggleOpen: () -> Unit,
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    val navWidth by animateDpAsState(
        targetValue = if (isOpen) 180.dp else 64.dp,
        label = "side_nav_width"
    )

    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, SubtleBorder),
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(navWidth)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Open / Close toggle button
                IconButton(
                    onClick = onToggleOpen,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F8E9))
                        .border(1.dp, LushGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = if (isOpen) "Close Side Navigation" else "Open Side Navigation",
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Navigation items
                BottomNavItem.items.forEach { item ->
                    val isSelected = when (item) {
                        BottomNavItem.Home -> selectedRoute == Routes.HOME
                        BottomNavItem.Farm -> selectedRoute == Routes.FARM
                        BottomNavItem.Community -> selectedRoute == Routes.COMMUNITY
                        BottomNavItem.Vegetables -> selectedRoute == Routes.LIBRARY
                        BottomNavItem.Profile -> selectedRoute == Routes.PROFILE || selectedRoute.startsWith("profile") || selectedRoute == Routes.SETTINGS
                    }
                    val itemColor = if (isSelected) LushGreen else UnselectedGray

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onNavigate(item.route)
                            }
                            .padding(horizontal = if (isOpen) 12.dp else 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isOpen) Arrangement.Start else Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = itemColor,
                            modifier = Modifier.size(22.dp)
                        )
                        if (isOpen) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = itemColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Bottom brand indicator when expanded
            if (isOpen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "MapTanim",
                        tint = LushGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MapTanim",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                }
            }
        }
    }
}
