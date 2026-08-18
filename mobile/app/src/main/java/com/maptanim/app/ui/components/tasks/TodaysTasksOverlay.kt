package com.maptanim.app.ui.components.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

data class TaskItem(
    val id: String,
    val title: String,
    val subtext: String,
    val taskType: TaskType,
    val isCompleted: Boolean = false
)

@Composable
fun TodaysTasksOverlay(
    onDismiss: () -> Unit
) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                TaskItem("t1", "Water String Beans", "String Beans Zone", TaskType.WATER),
                TaskItem("t2", "Fertilize Eggplant", "Eggplant Zone", TaskType.FERTILIZE),
                TaskItem("t3", "Harvest Carrot", "Carrot Zone - Ready for harvest", TaskType.HARVEST),
                TaskItem("t4", "Check Pest Alert", "Tomato Zone - Inspect for Hornworms", TaskType.PEST_ALERT)
            )
        )
    }

    var selectedFilterIndex by remember { mutableStateOf(0) } // 0: All, 1: Pending, 2: Completed

    val filteredTasks = when (selectedFilterIndex) {
        1 -> tasks.filter { !it.isCompleted }
        2 -> tasks.filter { it.isCompleted }
        else -> tasks
    }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Today's Tasks", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = White)
                            Text("DSS Data Algorithm Output", fontSize = 11.sp, color = White.copy(alpha = 0.6f))
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                // Filter Tabs (All / Pending / Completed)
                TabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = Color(0xFF2A3424),
                    contentColor = ForestGreen
                ) {
                    Tab(
                        selected = selectedFilterIndex == 0,
                        onClick = { selectedFilterIndex = 0 },
                        text = { Text("All (${tasks.size})", color = White, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedFilterIndex == 1,
                        onClick = { selectedFilterIndex = 1 },
                        text = { Text("Pending (${tasks.count { !it.isCompleted }})", color = White, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedFilterIndex == 2,
                        onClick = { selectedFilterIndex = 2 },
                        text = { Text("Completed (${tasks.count { it.isCompleted }})", color = White, fontSize = 12.sp) }
                    )
                }

                // Task List
                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tasks found.", color = White.copy(alpha = 0.6f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTasks) { task ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (task.isCompleted) Color(0xFF161E14) else Color(0xFF243020),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { checked ->
                                                tasks = tasks.map {
                                                    if (it.id == task.id) it.copy(isCompleted = checked) else it
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = task.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (task.isCompleted) White.copy(alpha = 0.5f) else White,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Text(
                                                text = task.subtext,
                                                fontSize = 11.sp,
                                                color = White.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Type Badge
                                    val badgeColor = when (task.taskType) {
                                        TaskType.WATER -> Color(0xFF1E88E5)
                                        TaskType.FERTILIZE -> Color(0xFF43A047)
                                        TaskType.HARVEST -> Color(0xFFFFA000)
                                        TaskType.PEST_ALERT -> Color(0xFFE53935)
                                        else -> Color.Gray
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeColor.copy(alpha = 0.25f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor)
                                    ) {
                                        Text(
                                            text = task.taskType.name,
                                            color = White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
