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
                                Text(
                                    text = "${task.cropName} (${task.plotLabel}): ${task.title}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = DeepBlack
                                )
                                Text(
                                    text = task.taskType.name,
                                    fontSize = 11.sp,
                                    color = LushGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onCompleteTask(task.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = "Complete task",
                                    tint = LushGreen,
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
