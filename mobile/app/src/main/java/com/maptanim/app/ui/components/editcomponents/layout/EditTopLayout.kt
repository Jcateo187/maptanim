package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.ui.screens.edit.EditUiState

/**
 * EditTopLayout — Top bar matching PNG 2 pixel-for-pixel.
 *
 * Contains:
 *   - Logo: MAPTANIM (green square leaf icon + text)
 *   - Farm selector: "Murcia Farm ▾" + "📍 Murcia, Negros Occidental"
 *   - EDIT MODE badge: "✏ EDIT MODE" + "Tap a bed or item to edit"
 *   - Weather: ☀️ 28°C Partly Cloudy
 *   - Notification: 🔔 with red count badge
 *   - User profile: Avatar circle + "James"
 */
@Composable
fun EditTopLayout(
    modifier: Modifier = Modifier,
    uiState: EditUiState? = null,
    farmName: String = "Murcia Farm",
    location: String = "Murcia, Negros Occidental",
    notificationCount: Int = 3,
    userName: String = "James",
    onFarmSelectClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── 1. LOGO & FARM SELECTOR ───────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "MapTanim Logo",
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "MAPTANIM",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color(0xFF1B5E20),
                        letterSpacing = 0.5.sp
                    )
                }

                // Farm Selector
                Column(
                    modifier = Modifier.clickable { onFarmSelectClick() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = farmName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select farm",
                            tint = Color.DarkGray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = location,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ── 2. EDIT MODE BADGE (CENTER) ───────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "EDIT MODE",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            fontSize = 12.sp
                        )
                    }
                }
                Text(
                    text = "Tap a plot or item to edit",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // ── 3. WEATHER, NOTIFICATIONS & USER AVATAR ──────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Weather
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "28°C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Partly Cloudy",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Notification Bell
                Box(
                    modifier = Modifier.clickable { onNotificationClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                    if (notificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$notificationCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // User Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF43A047)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = userName,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = userName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.DarkGray
                    )
                }
            }
        }
    }
}