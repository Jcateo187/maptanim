package com.maptanim.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF10160F)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // ── 1. Top Header Bar ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFF1B2317), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLANTING CALENDAR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = White
                    )
                }

                // Month Navigator Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.previousMonth() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = White
                        )
                    }

                    Text(
                        text = uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = White,
                        modifier = Modifier.widthIn(min = 120.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { viewModel.nextMonth() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── 2. Dual Pane Layout (Calendar Grid + Selected Day Details) ─────
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Pane: Monthly Calendar Grid
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B2317),
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        // Day of week headers (Sun .. Sat)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val daysOfWeek = listOf(
                                DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                            )
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Calendar Month Days
                        val firstDayOfMonth = uiState.currentMonth.atDay(1)
                        val daysInMonth = uiState.currentMonth.lengthOfMonth()
                        val startOffset = (firstDayOfMonth.dayOfWeek.value % 7) // Sunday = 0

                        val totalGridCells = startOffset + daysInMonth
                        val gridDays = (0 until totalGridCells).map { index ->
                            if (index < startOffset) null
                            else uiState.currentMonth.atDay(index - startOffset + 1)
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(gridDays) { date ->
                                if (date != null) {
                                    val isSelected = date == uiState.selectedDate
                                    val isToday = date == LocalDate.now()
                                    val tasksForDay = uiState.tasksByDate[date] ?: emptyList()
                                    val harvestsForDay = uiState.timelineEntries.filter { it.expectedHarvestDate == date }

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1.2f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isSelected -> ForestGreen
                                                    isToday -> Color(0xFF2D3828)
                                                    else -> Color(0xFF232C1E)
                                                }
                                            )
                                            .border(
                                                width = if (isToday) 1.dp else 0.dp,
                                                color = if (isToday) ForestGreen else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.selectDate(date) }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = date.dayOfMonth.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) White else White.copy(alpha = 0.9f)
                                            )

                                            // Task & Harvest Indicators
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (harvestsForDay.isNotEmpty()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFFF9800))
                                                    )
                                                }
                                                tasksForDay.take(3).forEach { task ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                when (task.taskType) {
                                                                    TaskType.WATER -> Color(0xFF2196F3)
                                                                    TaskType.FERTILIZE -> Color(0xFF4CAF50)
                                                                    TaskType.HARVEST -> Color(0xFFFF9800)
                                                                    TaskType.PEST_ALERT -> Color(0xFFF44336)
                                                                    TaskType.APPLY_PESTICIDE -> Color(0xFFFF5722)
                                                                }
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier.aspectRatio(1.2f))
                                }
                            }
                        }
                    }
                }

                // Right Pane: Selected Day Tasks & Harvest Timeline Breakdown
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B2317),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = White
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Section 1: Scheduled Harvests on this day
                            if (uiState.selectedDayHarvests.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "🚜 SCHEDULED HARVESTS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9800)
                                    )
                                }

                                items(uiState.selectedDayHarvests) { entry ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2415)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = entry.plot.cropName ?: "Crop",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = White
                                                )
                                                Text(
                                                    text = "${entry.plot.plotLabel} • Expected Harvest Date",
                                                    fontSize = 11.sp,
                                                    color = White.copy(alpha = 0.7f)
                                                )
                                            }

                                            Badge(containerColor = Color(0xFFFF9800), contentColor = White) {
                                                Text("READY", modifier = Modifier.padding(4.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            // Section 2: Tasks on this day
                            item {
                                Text(
                                    text = "📋 TODAY'S TASKS (${uiState.selectedDayTasks.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }

                            if (uiState.selectedDayTasks.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No tasks scheduled for this day.",
                                            fontSize = 12.sp,
                                            color = White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            } else {
                                items(uiState.selectedDayTasks) { task ->
                                    CalendarTaskRow(
                                        task = task,
                                        onComplete = { viewModel.completeTask(task.id) }
                                    )
                                }
                            }

                            // Section 3: Overall Active Crop Timelines
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🌱 ACTIVE CROP TIMELINES (${uiState.timelineEntries.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White.copy(alpha = 0.7f)
                                )
                            }

                            items(uiState.timelineEntries) { entry ->
                                TimelineRow(entry = entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarTaskRow(
    task: FarmTask,
    onComplete: () -> Unit
) {
    val taskColor = when (task.taskType) {
        TaskType.WATER -> Color(0xFF2196F3)
        TaskType.FERTILIZE -> Color(0xFF4CAF50)
        TaskType.HARVEST -> Color(0xFFFF9800)
        TaskType.PEST_ALERT -> Color(0xFFF44336)
        TaskType.APPLY_PESTICIDE -> Color(0xFFFF5722)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF232C1E)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(taskColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = White
                    )
                    Text(
                        text = "${task.plotLabel} • ${task.cropName ?: "General"}",
                        fontSize = 10.sp,
                        color = White.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(
                onClick = onComplete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Complete",
                    tint = if (task.isCompleted) ForestGreen else White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun TimelineRow(entry: CropTimelineEntry) {
    val progress = ((60 - entry.daysRemaining).toFloat() / 60f).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E261A)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${entry.plot.plotLabel}: ${entry.plot.cropName ?: "Crop"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = White
                )
                Text(
                    text = "${entry.daysRemaining} days to harvest",
                    fontSize = 10.sp,
                    color = ForestGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ForestGreen,
                trackColor = White.copy(alpha = 0.1f)
            )
        }
    }
}
