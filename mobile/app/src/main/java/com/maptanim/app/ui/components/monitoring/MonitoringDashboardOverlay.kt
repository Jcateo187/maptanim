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
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredCrops) { crop ->
                                        when (uiState.selectedNavSection) {
                                            MonitoringNavSection.MY_PLANTS -> MyPlantsCard(crop, onStartMonitoring = { viewModel.startMonitoring(it) })
                                            MonitoringNavSection.TIMELINE -> TimelineCard(crop)
                                            MonitoringNavSection.COMPANIONS -> CompanionsCard(crop)
                                            MonitoringNavSection.GROWING_TIPS -> GrowingTipsCard(crop)
                                            MonitoringNavSection.CALENDAR -> CalendarCard(crop)
                                            MonitoringNavSection.PEST -> PestCard(crop)
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

// ── 5. CALENDAR CARD ─────────────────────────────────────────────────────────
@Composable
private fun CalendarCard(crop: MonitoredPlant) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${crop.cropName} Calendar Schedule", fontWeight = FontWeight.Bold, color = White, fontSize = 14.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424)
                ) {
                    Text(crop.plotLabel, fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
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
                    Text("in ${crop.daysToHarvest - crop.daysPlanted} days", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Next Task", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                    Text("Water Tomorrow", fontSize = 12.sp, color = Color(0xFF1E88E5), fontWeight = FontWeight.SemiBold)
                }
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
