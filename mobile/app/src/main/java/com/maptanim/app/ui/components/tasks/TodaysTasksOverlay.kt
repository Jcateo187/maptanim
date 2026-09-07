package com.maptanim.app.ui.components.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.ui.screens.home.HomeViewModel
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@Composable
fun TodaysTasksOverlay(
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val liveTasks = homeUiState.todayTasks

    var selectedStatusIndex by remember { mutableStateOf(0) } // 0: All, 1: Pending, 2: Completed
    var selectedCropFilter by remember { mutableStateOf<String?>(null) } // null = All crops

    // List of unique crops with tasks today
    val uniqueCrops = remember(liveTasks) {
        liveTasks.mapNotNull { it.cropName?.ifBlank { null } ?: it.subLabel?.ifBlank { null } }.distinct()
    }

    // Filter tasks by status and crop
    val filteredTasks = remember(liveTasks, selectedStatusIndex, selectedCropFilter) {
        liveTasks.filter { task ->
            val matchStatus = when (selectedStatusIndex) {
                1 -> !task.isCompleted
                2 -> task.isCompleted
                else -> true
            }
            val taskCropName = task.cropName ?: task.subLabel
            val matchCrop = selectedCropFilter == null || taskCropName.equals(selectedCropFilter, ignoreCase = true)
            matchStatus && matchCrop
        }
    }

    val pendingCount = liveTasks.count { !it.isCompleted }
    val completedCount = liveTasks.count { it.isCompleted }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1B2317),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
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
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E88E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Today's Tasks", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = White)
                            Text(
                                if (selectedCropFilter != null) "Tasks for $selectedCropFilter" else "All Farm Tasks (${liveTasks.size})",
                                fontSize = 11.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                // ── 1. Status Filter Tabs (All / Pending / Completed) ──────
                PrimaryTabRow(
                    selectedTabIndex = selectedStatusIndex,
                    containerColor = Color(0xFF2A3424),
                    contentColor = ForestGreen
                ) {
                    Tab(
                        selected = selectedStatusIndex == 0,
                        onClick = { selectedStatusIndex = 0 },
                        text = { Text("All (${liveTasks.size})", color = White, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedStatusIndex == 1,
                        onClick = { selectedStatusIndex = 1 },
                        text = { Text("Pending ($pendingCount)", color = White, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedStatusIndex == 2,
                        onClick = { selectedStatusIndex = 2 },
                        text = { Text("Completed ($completedCount)", color = White, fontSize = 12.sp) }
                    )
                }

                // ── 2. Crop Selector Strip (Direct Crop Filtering) ────────
                if (uniqueCrops.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FILTER BY CROP:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedCropFilter == null,
                                    onClick = { selectedCropFilter = null },
                                    label = { Text("All Crops (${liveTasks.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = White,
                                        containerColor = Color(0xFF243020),
                                        labelColor = White.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            items(uniqueCrops) { cropName ->
                                val countForCrop = liveTasks.count { (it.cropName ?: it.subLabel).equals(cropName, ignoreCase = true) }
                                FilterChip(
                                    selected = selectedCropFilter.equals(cropName, ignoreCase = true),
                                    onClick = {
                                        selectedCropFilter = if (selectedCropFilter.equals(cropName, ignoreCase = true)) null else cropName
                                    },
                                    label = { Text("$cropName ($countForCrop)", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = White,
                                        containerColor = Color(0xFF243020),
                                        labelColor = White.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }
                }

                // ── 3. Task List ──────────────────────────────────────────
                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedCropFilter != null) "No tasks found for $selectedCropFilter." else "No tasks found for today.",
                            color = White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (task.isCompleted) Color(0xFF161E14) else Color(0xFF243020),
                                border = if (task.isCompleted) null else BorderStroke(1.dp, Color(0xFF2E4D3E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = {
                                                homeViewModel.completeTask(task.id)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = task.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (task.isCompleted) White.copy(alpha = 0.5f) else White,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            val subtext = listOfNotNull(task.subLabel, task.plotLabel.ifBlank { null }, "Due: ${task.dueDate}").joinToString(" • ")
                                            Text(
                                                text = subtext,
                                                fontSize = 11.sp,
                                                color = White.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Task Type Badge
                                    val badgeColor = when (task.taskType) {
                                        TaskType.WATER -> Color(0xFF1E88E5)
                                        TaskType.FERTILIZE -> Color(0xFF43A047)
                                        TaskType.HARVEST -> Color(0xFFFFA000)
                                        TaskType.PEST_ALERT -> Color(0xFFE53935)
                                        else -> Color(0xFF8E24AA)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeColor.copy(alpha = 0.25f),
                                        border = BorderStroke(1.dp, badgeColor)
                                    ) {
                                        Text(
                                            text = task.taskType.name.replace("_", " "),
                                            color = White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
