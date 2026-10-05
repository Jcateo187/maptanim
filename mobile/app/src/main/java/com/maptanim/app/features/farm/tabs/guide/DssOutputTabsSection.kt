package com.maptanim.app.features.farm.tabs.guide

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.features.farm.viewmodel.DssTab
import com.maptanim.app.features.farm.viewmodel.TopTab
import com.maptanim.app.ui.theme.White

@Composable
fun DssOutputTabsSection(
    currentStage: ManagementStage,
    selectedDssTab: DssTab,
    dynamicTasks: List<DssLogEvaluator.GeneratedLogTask>,
    observedLogs: List<CropLog>,
    onSelectDssTab: (DssTab) -> Unit,
    onCheckDynamicTask: (taskId: String) -> Unit,
    onSwitchToRecommendations: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
        border = BorderStroke(1.dp, Color(0xFF2B3825)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Sub-tabs: TASKS | LOGS
            val totalTaskCount = dynamicTasks.size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF192217), RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    DssTab.TASKS to "TODAY'S TASKS ($totalTaskCount)",
                    DssTab.LOGS to "LOGS (${observedLogs.size})"
                ).forEach { (tab, label) ->
                    val isSelected = tab == selectedDssTab
                    Surface(
                        onClick = { onSelectDssTab(tab) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) Color(0xFF2E7D32) else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) White else Color(0xFFA0B09A),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            var showAllTasks by remember { mutableStateOf(false) }
            var showAllLogs by remember { mutableStateOf(false) }

            when (selectedDssTab) {
                DssTab.TASKS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S TASKS (${dynamicTasks.size})",
                                color = Color(0xFFA0B09A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (dynamicTasks.size > 5) {
                                Text(
                                    text = if (showAllTasks) "Show 5" else "See All (${dynamicTasks.size})",
                                    color = Color(0xFF81C784),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { showAllTasks = !showAllTasks }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (dynamicTasks.isEmpty()) {
                            Text(
                                text = "No tasks pending for today. Record an observation to evaluate and generate actions for today.",
                                color = Color(0xFF81C784),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val visibleTasks = if (showAllTasks || dynamicTasks.size <= 5) dynamicTasks else dynamicTasks.take(5)
                        visibleTasks.forEach { task ->
                            TaskRowItem(
                                title = task.title,
                                description = task.description,
                                isChecked = false,
                                onCheckedChange = { onCheckDynamicTask(task.id) }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "View More Recommendations →",
                            color = Color(0xFF81C784),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onSwitchToRecommendations() }
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                DssTab.LOGS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECORDED LOG HISTORY (${observedLogs.size})",
                                color = Color(0xFFA0B09A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (observedLogs.size > 3) {
                                Text(
                                    text = if (showAllLogs) "Show 3" else "See All (${observedLogs.size})",
                                    color = Color(0xFF81C784),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { showAllLogs = !showAllLogs }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (observedLogs.isEmpty()) {
                            Text(
                                text = "No logs recorded for this planting yet. Tap 'Record Observation' to begin.",
                                color = Color(0xFF8B9B85),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            val visibleLogs = if (showAllLogs || observedLogs.size <= 3) observedLogs else observedLogs.take(3)
                            visibleLogs.forEach { log ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF192217),
                                    border = BorderStroke(1.dp, Color(0xFF283624)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${log.logContext.name} • Choice ${log.selectedChoice}",
                                                color = Color(0xFF81C784),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = log.date, color = Color(0xFF8B9B85), fontSize = 11.sp)
                                        }
                                        if (!log.notes.isNullOrBlank()) {
                                            Text(text = log.notes, color = White, fontSize = 12.sp)
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

@Composable
private fun TaskRowItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF192217),
        border = BorderStroke(1.dp, Color(0xFF283624)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2E7D32),
                    checkmarkColor = White,
                    uncheckedColor = Color(0xFF4C5D47)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = description, color = Color(0xFFC0CDC0), fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}
