package com.maptanim.app.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.FarmTask

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * TodayTasksCard — Dashboard widget displaying pending farm care tasks for today.
 * Shows up to 2 task rows with click-to-calendar redirection, and an underlined "view more..." text link.
 */
@Composable
fun TodayTasksCard(
    tasks: List<FarmTask>,
    onCompleteTask: (String) -> Unit,
    onTaskClick: (FarmTask) -> Unit = {},
    onViewMore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section heading outside card at top
        Text(
            text = "TODAY'S CARE TASKS",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (tasks.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "All today's tasks completed! Your garden is thriving.",
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                    }
                } else {
                    // Maximum 2 task rows visible on HomeScreen as requested
                    tasks.take(2).forEach { task ->
                        val isOverdue = try {
                            java.time.LocalDate.parse(task.dueDate).isBefore(java.time.LocalDate.now()) && !task.isCompleted
                        } catch (_: Exception) { false }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTaskClick(task) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                val formattedTitle = buildString {
                                    val crop = task.cropName?.trim()?.takeIf { it.isNotBlank() }
                                    val plot = task.plotLabel.trim().takeIf { it.isNotBlank() && !it.equals("PLOT", ignoreCase = true) }
                                    val title = task.title.trim()

                                    val prefix = when {
                                        crop != null && plot != null -> "$crop ($plot)"
                                        crop != null -> crop
                                        plot != null -> plot
                                        else -> null
                                    }

                                    if (prefix != null) {
                                        if (title.startsWith(prefix, ignoreCase = true)) {
                                            append(title)
                                        } else if (crop != null && title.contains(crop, ignoreCase = true) && plot != null) {
                                            append("$plot: $title")
                                        } else {
                                            append("$prefix: $title")
                                        }
                                    } else {
                                        append(title)
                                    }
                                }

                                val taskTypeLabel = when (task.taskType) {
                                    com.maptanim.app.domain.model.TaskType.WATER -> "Pagdidilig (Watering)"
                                    com.maptanim.app.domain.model.TaskType.FERTILIZE -> "Pagpapataba (Fertilizing)"
                                    com.maptanim.app.domain.model.TaskType.HARVEST -> "Pag-aani (Harvesting)"
                                    com.maptanim.app.domain.model.TaskType.PEST_ALERT -> "Babala sa Peste (Pest Alert)"
                                    com.maptanim.app.domain.model.TaskType.APPLY_PESTICIDE -> "Peste at Gamot (Pest Care)"
                                    com.maptanim.app.domain.model.TaskType.WEED -> "Pag-aalis ng Damo (Weeding)"
                                    com.maptanim.app.domain.model.TaskType.TRELLIS -> "Paglalagay ng Balag (Trellising)"
                                    com.maptanim.app.domain.model.TaskType.NUTRITION -> "Sustansya ng Halaman (Nutrition)"
                                    com.maptanim.app.domain.model.TaskType.ROTATION_ALERT -> "Palit-tanim (Crop Rotation)"
                                    com.maptanim.app.domain.model.TaskType.SOIL_AMENDMENT -> "Pangangalaga ng Lupa (Soil Care)"
                                    com.maptanim.app.domain.model.TaskType.PRUNING -> "Pagpupungos (Pruning)"
                                    com.maptanim.app.domain.model.TaskType.OBSERVATION -> "Pagsusuri (Observation / Check-up)"
                                }

                                val titleColor = when {
                                    task.isCompleted -> LushGreen
                                    isOverdue -> Color(0xFFD32F2F)
                                    else -> DeepBlack
                                }

                                val titleDecoration = when {
                                    task.isCompleted || isOverdue -> TextDecoration.LineThrough
                                    else -> TextDecoration.None
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isOverdue) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFEBEE)
                                        ) {
                                            Text(
                                                text = "Hindi Nagawa",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC62828),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    } else if (task.isCompleted) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "Tapos Na",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LushGreen,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = formattedTitle,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = titleColor,
                                        textDecoration = titleDecoration
                                    )
                                }

                                Text(
                                    text = taskTypeLabel,
                                    fontSize = 11.sp,
                                    color = if (task.isCompleted) LushGreen.copy(alpha = 0.7f) else if (isOverdue) Color(0xFFD32F2F).copy(alpha = 0.7f) else LushGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onCompleteTask(task.id) }
                            ) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                    contentDescription = if (task.isCompleted) "Tapos na" else "Complete task",
                                    tint = if (task.isCompleted) LushGreen else if (isOverdue) Color(0xFFE53935) else LushGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // "view more..." text only with line at the bottom (underlined)
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.8.dp)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "view more...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable { onViewMore() }
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
