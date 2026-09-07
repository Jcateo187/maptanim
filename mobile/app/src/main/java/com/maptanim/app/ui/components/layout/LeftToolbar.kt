package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.ui.screens.home.HomeUiState

/**
 * LeftToolbar — Clash of Clans inspired HUD buttons on the left side.
 *
 * Features:
 *   - 3 HUD Buttons:
 *       1. 📊 Monitoring (opens full screen monitoring dashboard overlay)
 *       2. 📝 Today's Tasks (opens today's tasks dialog/sheet)
 *       3. 🌾 Farm Summary (opens farm statistics sheet)
 *   - Zero white container box
 */
@Composable
fun LeftToolbar(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    onMonitoringClick: () -> Unit = {},
    onTasksClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── 1. Monitoring HUD Button ──────────────────────────────────────
        HudLeftButton(
            icon = Icons.Default.Sensors,
            iconBgColor = Color(0xFF1B5E20),
            title = "Monitoring",
            subtitle = "Full Screen",
            onClick = onMonitoringClick
        )

        // ── 2. Today's Tasks HUD Button ───────────────────────────────────
        val pendingTaskCount = uiState.todayTasks.count { !it.isCompleted }
        HudLeftButton(
            icon = Icons.AutoMirrored.Filled.Assignment,
            iconBgColor = Color(0xFF1E88E5),
            title = "Today's Tasks",
            subtitle = if (pendingTaskCount > 0) "$pendingTaskCount Pending" else "${uiState.todayTasks.size} Done",
            onClick = onTasksClick
        )
    }
}

@Composable
private fun HudLeftButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.65f),
        shadowElevation = 4.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
