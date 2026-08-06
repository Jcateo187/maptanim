package com.maptanim.app.ui.components.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maptanim.app.ui.screens.monitoring.*
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDashboardOverlay(
    onDismiss: () -> Unit,
    viewModel: MonitoringViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredCrops = viewModel.getFilteredCrops()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF10160F)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Dual Pane Layout (Full Vertical Left Nav Bar + Large Right Content Section)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ── 1. Left Side Nav Bar (Full Vertical Height) ───────────
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B2317),
                        modifier = Modifier
                            .width(170.dp)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Items (My Plants, Timeline, Companions, Growing Tips, Calendar)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "MONITORING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White.copy(alpha = 0.45f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )

                                val topSections = listOf(
                                    MonitoringNavSection.MY_PLANTS,
                                    MonitoringNavSection.TIMELINE,
                                    MonitoringNavSection.CALENDAR,
                                    MonitoringNavSection.COMPANIONS,
                                    MonitoringNavSection.GROWING_TIPS
                                )

                                topSections.forEach { section ->
                                    NavSectionItem(
                                        section = section,
                                        isSelected = uiState.selectedNavSection == section,
                                        onClick = { viewModel.selectNavSection(section) }
                                    )
                                }
                            }

                            // Bottom Item: PEST (Perfectly Aligned at Bottom)
                            NavSectionItem(
                                section = MonitoringNavSection.PEST,
                                isSelected = uiState.selectedNavSection == MonitoringNavSection.PEST,
                                onClick = { viewModel.selectNavSection(MonitoringNavSection.PEST) }
                            )
                        }
                    }

                    // ── 2. Right Content Section (Full Vertical Content Area) ──
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        // Compact Controls Bar (Search Bar, Category Dropdown, 4 Seasonality Tabs)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1B2317),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Search Bar & Category Dropdown (Compact Row)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom BasicTextField Search Bar (No text cutoff!)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF2A3424),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                tint = White.copy(alpha = 0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            BasicTextField(
                                                value = uiState.searchQuery,
                                                onValueChange = { viewModel.updateSearchQuery(it) },
                                                singleLine = true,
                                                textStyle = TextStyle(
                                                    color = White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                cursorBrush = SolidColor(ForestGreen),
                                                decorationBox = { innerTextField ->
                                                    if (uiState.searchQuery.isEmpty()) {
                                                        Text(
                                                            text = "Search planted crops...",
                                                            color = White.copy(alpha = 0.45f),
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                    innerTextField()
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // Compact Category Dropdown Box
                                    Box {
                                        Surface(
                                            onClick = { viewModel.toggleCategoryDropdown(true) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2A3424),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Category, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(uiState.selectedCategory.label, fontSize = 11.sp, color = White, fontWeight = FontWeight.SemiBold)
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = uiState.isCategoryDropdownExpanded,
                                            onDismissRequest = { viewModel.toggleCategoryDropdown(false) },
                                            modifier = Modifier.background(Color(0xFF1B2317))
                                        ) {
                                            CropCategoryFilter.values().forEach { category ->
                                                DropdownMenuItem(
                                                    text = { Text(category.label, color = White, fontSize = 12.sp) },
                                                    onClick = { viewModel.selectCategory(category) }
                                                )
                                            }
                                        }
                                    }

                                    // Compact Close Button
                                    IconButton(
                                        onClick = onDismiss,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White, modifier = Modifier.size(20.dp))
                                    }
                                }

                                // 4 Seasonality Tabs (Compact Pill Row)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SeasonalityFilter.values().forEach { season ->
                                        val isSelected = uiState.selectedSeasonality == season
                                        Surface(
                                            onClick = { viewModel.selectSeasonality(season) },
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) ForestGreen else Color(0xFF2A3424),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = season.label,
                                                    fontSize = 10.sp,
                                                    color = White,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Large Full Vertical Content Panel
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            if (filteredCrops.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No planted crops match the selected filters.", color = White.copy(alpha = 0.6f))
                                }
                            } else {
                                var selectedCropForVariety by remember { mutableStateOf<MonitoredPlant?>(null) }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredCrops) { crop ->
                                        when (uiState.selectedNavSection) {
                                            MonitoringNavSection.MY_PLANTS -> MyPlantsCard(crop, onStartMonitoring = { selectedCropForVariety = crop })
                                            MonitoringNavSection.TIMELINE -> TimelineCard(crop)
                                            MonitoringNavSection.COMPANIONS -> CompanionsCard(crop)
                                            MonitoringNavSection.GROWING_TIPS -> GrowingTipsCard(crop)
                                            MonitoringNavSection.CALENDAR -> CalendarCard(crop, onRescheduleClick = { selectedCropForVariety = crop })
                                            MonitoringNavSection.PEST -> PestCard(crop)
                                        }
                                    }
                                }

                                selectedCropForVariety?.let { targetCrop ->
                                    VarietySelectionModal(
                                        crop = targetCrop,
                                        onDismiss = { selectedCropForVariety = null },
                                        onConfirmSchedule = { varietyName, targetDate ->
                                            viewModel.startMonitoring(targetCrop.id, targetDate, varietyName)
                                            selectedCropForVariety = null
                                        }
                                    )
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
private fun NavSectionItem(
    section: MonitoringNavSection,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ForestGreen else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (section) {
                MonitoringNavSection.MY_PLANTS -> Icons.Default.Eco
                MonitoringNavSection.TIMELINE -> Icons.Default.Timeline
                MonitoringNavSection.COMPANIONS -> Icons.Default.Groups
                MonitoringNavSection.GROWING_TIPS -> Icons.Default.Lightbulb
                MonitoringNavSection.CALENDAR -> Icons.Default.CalendarMonth
                MonitoringNavSection.PEST -> Icons.Default.BugReport
            }
            val iconColor = if (section == MonitoringNavSection.PEST && !isSelected) Color(0xFFE53935) else White
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = section.title,
                fontSize = 12.sp,
                color = White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ── 1. MY PLANTS CARD ────────────────────────────────────────────────────────
@Composable
private fun MyPlantsCard(
    crop: MonitoredPlant,
    onStartMonitoring: (String) -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ForestGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(crop.cropName, fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("(${crop.localName})", fontSize = 12.sp, color = White.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Location: ${crop.plotLabel} | Category: ${crop.category.label}", fontSize = 11.sp, color = White.copy(alpha = 0.75f))
                Text("Seasonality: ${crop.seasonality.label}", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
            }

            if (!crop.isMonitoringStarted) {
                Button(
                    onClick = { onStartMonitoring(crop.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                        Text("Start", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ForestGreen.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen)
                ) {
                    Text(
                        text = crop.healthStatus,
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ── 2. TIMELINE CARD (4 Stages) ──────────────────────────────────────────────
@Composable
private fun TimelineCard(crop: MonitoredPlant) {
    val stages = listOf("Seedling", "Vegetative", "Flowering / Podding", "Harvest Ready")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${crop.cropName} (${crop.plotLabel})", fontWeight = FontWeight.Bold, color = White, fontSize = 14.sp)
                Text("Day ${crop.daysPlanted} of ${crop.daysToHarvest}", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
            }

            // 4 Stages Progress Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stages.forEachIndexed { idx, stageTitle ->
                    val isActive = idx <= crop.currentStageIndex
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isActive) ForestGreen else Color.Gray.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stageTitle,
                            fontSize = 9.sp,
                            color = if (isActive) White else White.copy(alpha = 0.4f),
                            fontWeight = if (idx == crop.currentStageIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ── 3. COMPANIONS CARD ───────────────────────────────────────────────────────
@Composable
private fun CompanionsCard(crop: MonitoredPlant) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${crop.cropName} + ${crop.companionCrop}", fontWeight = FontWeight.Bold, color = White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(crop.companionStatus, fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.SemiBold)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2A3424)
            ) {
                Text("Compatible", fontSize = 10.sp, color = White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

// ── 4. GROWING TIPS CARD ─────────────────────────────────────────────────────
@Composable
private fun GrowingTipsCard(crop: MonitoredPlant) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cultivation Tip for ${crop.cropName}", fontWeight = FontWeight.Bold, color = White, fontSize = 13.sp)
            }
            Text(crop.growingTip, fontSize = 11.sp, color = White.copy(alpha = 0.85f))
        }
    }
}

// ── 5. CALENDAR CARD & MONTHLY CALENDAR GRID ─────────────────────────────────
@Composable
private fun CalendarCard(crop: MonitoredPlant, onRescheduleClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${crop.cropName} Crop Calendar", fontWeight = FontWeight.Bold, color = White, fontSize = 14.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    modifier = Modifier.clickable { onRescheduleClick() }
                ) {
                    Text(
                        text = "📅 Schedule Date",
                        fontSize = 10.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Planted Date", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                    Text("${crop.daysPlanted} days ago", fontSize = 12.sp, color = White, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Expected Harvest", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                    Text("in ${(crop.daysToHarvest - crop.daysPlanted).coerceAtLeast(0)} days", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Variety", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                    Text(crop.cropVariety ?: "Standard", fontSize = 12.sp, color = Color(0xFF1E88E5), fontWeight = FontWeight.SemiBold)
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            // Interactive Monthly Calendar View
            MonthlyCalendarView(
                plantedDateStr = crop.rawPlantedDate,
                daysToHarvest = crop.daysToHarvest,
                onSelectPlantingDate = { dateStr ->
                    onRescheduleClick()
                }
            )
        }
    }
}

@Composable
private fun MonthlyCalendarView(
    plantedDateStr: String?,
    daysToHarvest: Int,
    onSelectPlantingDate: (String) -> Unit
) {
    val initialDate = try {
        if (!plantedDateStr.isNullOrBlank()) java.time.LocalDate.parse(plantedDateStr) else java.time.LocalDate.now()
    } catch (e: Exception) {
        java.time.LocalDate.now()
    }

    var currentYearMonth by remember { mutableStateOf(java.time.YearMonth.from(initialDate)) }

    val plantingDate = initialDate
    val harvestDate = initialDate.plusDays(daysToHarvest.toLong())
    val today = java.time.LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = White)
            }

            Text(
                text = "${currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = White
            )

            IconButton(
                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = White)
            }
        }

        // Days of Week Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White.copy(alpha = 0.5f))
            }
        }

        // Calendar Days Grid
        val firstDayOfMonth = currentYearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0=Sun, 1=Mon...
        val daysInMonth = currentYearMonth.lengthOfMonth()

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (c in 0..6) {
                        val dayNum = r * 7 + c - firstDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val dateObj = currentYearMonth.atDay(dayNum)
                            val isPlantingDay = dateObj == plantingDate
                            val isHarvestDay = dateObj == harvestDate
                            val isToday = dateObj == today

                            val bgColor = when {
                                isPlantingDay -> ForestGreen
                                isHarvestDay -> Color(0xFFD48806)
                                isToday -> Color(0xFF2A3424)
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isPlantingDay || isHarvestDay -> White
                                isToday -> ForestGreen
                                else -> White.copy(alpha = 0.85f)
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .border(
                                        width = if (isToday && !isPlantingDay && !isHarvestDay) 1.dp else 0.dp,
                                        color = if (isToday) ForestGreen else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectPlantingDate(dateObj.toString()) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNum",
                                    fontSize = 10.sp,
                                    fontWeight = if (isPlantingDay || isHarvestDay || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        // Legend Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ForestGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Planting", fontSize = 9.sp, color = White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFD48806)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Harvest", fontSize = 9.sp, color = White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).border(1.dp, ForestGreen, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today", fontSize = 9.sp, color = White.copy(alpha = 0.7f))
            }
        }
    }
}

// ── 6. PEST CARD ─────────────────────────────────────────────────────────────
@Composable
private fun PestCard(crop: MonitoredPlant) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pest Prevention: ${crop.cropName}", fontWeight = FontWeight.Bold, color = White, fontSize = 13.sp)
            }
            Text(crop.pestInfo, fontSize = 11.sp, color = White.copy(alpha = 0.85f))
        }
    }
}

// ── 7. VARIETY & TARGET PLANTING DATE SELECTION MODAL ─────────────────────────
@Composable
private fun VarietySelectionModal(
    crop: MonitoredPlant,
    onDismiss: () -> Unit,
    onConfirmSchedule: (varietyName: String, targetDate: String) -> Unit
) {
    val todayStr = java.time.LocalDate.now().toString()
    val tomorrowStr = java.time.LocalDate.now().plusDays(1).toString()
    val in3DaysStr = java.time.LocalDate.now().plusDays(3).toString()
    val nextWeekStr = java.time.LocalDate.now().plusDays(7).toString()

    val presets = when (crop.cropName.lowercase().replace(" ", "")) {
        "stringbeans", "sitaw", "beans" -> listOf("Sandigan F1", "Galante F1", "Bongga F1")
        "eggplant", "talong" -> listOf("Morena F1", "Dumaguete Long Purple", "Casino 217")
        "tomato", "kamatis" -> listOf("Diamante Max F1", "Apollo", "Cherry Tomato")
        "carrot", "karots" -> listOf("Terracotta F1", "Kuroda Improved", "Chantenay")
        "onion", "sibuyas" -> listOf("Red Pinoy F1", "Yellow Granex", "Super Rex")
        "pumpkin", "kalabasa" -> listOf("Suprema F1", "Horizon F1")
        "corn", "mais" -> listOf("Machismo F1 (Sweet)", "IPB Var 6 (White)", "Pioneer Hybrid")
        "cabbage", "repolyo" -> listOf("K-S Cross F1", "Kyross F1")
        "pechay" -> listOf("Pavon", "Black Beets")
        "ampalaya" -> listOf("Jade Star XL F1")
        "okra" -> listOf("Smooth Green")
        "sili", "chili" -> listOf("Django F1 (Siling Haba)", "Taiwan Hot")
        else -> listOf("Standard Hybrid", "Local Cultivar")
    }

    var selectedVariety by remember { mutableStateOf(presets.firstOrNull() ?: "Standard Variety") }
    var customVariety by remember { mutableStateOf("") }
    var selectedTargetDate by remember { mutableStateOf(todayStr) }
    var customTargetDate by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule ${crop.cropName} Planting",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                Text(
                    text = "Select cultivar variety and target planting date for ${crop.plotLabel}:",
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.7f)
                )

                // Variety Selector
                Text("Seed Variety / Cultivar:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    presets.forEach { preset ->
                        val isSelected = selectedVariety == preset && customVariety.isBlank()
                        Surface(
                            onClick = {
                                selectedVariety = preset
                                customVariety = ""
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ForestGreen else Color(0xFF2A3424),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(preset, fontSize = 12.sp, color = White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Target Planting Date Selector
                Text("Target Planting Date:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Today" to todayStr,
                        "Tomorrow" to tomorrowStr,
                        "3 Days" to in3DaysStr,
                        "Next Week" to nextWeekStr
                    ).forEach { (label, dateVal) ->
                        val isSelected = selectedTargetDate == dateVal && customTargetDate.isBlank()
                        Surface(
                            onClick = {
                                selectedTargetDate = dateVal
                                customTargetDate = ""
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) ForestGreen else Color(0xFF2A3424),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(label, fontSize = 10.sp, color = White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customTargetDate,
                    onValueChange = { customTargetDate = it },
                    label = { Text("Or custom date (YYYY-MM-DD)", fontSize = 10.sp, color = White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = White.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalVariety = customVariety.ifBlank { selectedVariety }
                            val finalDate = customTargetDate.ifBlank { selectedTargetDate }
                            onConfirmSchedule(finalVariety, finalDate)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save & Start Schedule", fontWeight = FontWeight.Bold, color = White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
