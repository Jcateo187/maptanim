package com.maptanim.app.features.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
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
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.features.profile.utils.formatActivityTime

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * FullHarvestHistoryModal — Complete paginated harvest yield history in Daylight theme.
 */
@Composable
fun FullHarvestHistoryModal(
    harvestHistory: List<HarvestRecord>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }
    var showDatePickerModal by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    val filteredRecords = remember(harvestHistory, searchQuery, selectedDateFilter) {
        harvestHistory.filter { record ->
            val matchesSearch = searchQuery.isBlank() || (
                record.cropName.contains(searchQuery, ignoreCase = true) ||
                (record.cropVariety?.contains(searchQuery, ignoreCase = true) == true) ||
                record.farmName.contains(searchQuery, ignoreCase = true) ||
                record.plotLabel.contains(searchQuery, ignoreCase = true) ||
                (record.notes?.contains(searchQuery, ignoreCase = true) == true)
            )
            val matchesDate = selectedDateFilter.isNullOrBlank() || (
                record.harvestedAt.take(10) == selectedDateFilter ||
                (record.plantedDate?.take(10) == selectedDateFilter)
            )
            matchesSearch && matchesDate
        }
    }

    val totalPages = (filteredRecords.size + itemsPerPage - 1).coerceAtLeast(1) / itemsPerPage
    val pageItems = remember(filteredRecords, currentPage) {
        val safePage = currentPage.coerceIn(1, totalPages.coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredRecords.drop(startIndex).take(itemsPerPage)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "Harvests on $selectedDateFilter (${filteredRecords.size})" else "Complete Harvest History (${filteredRecords.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = DeepBlack)
                    }
                }

                // Search Bar with Date Filter Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isBlank()) {
                                Text("Search crop, farm, plot...", color = MutedText, fontSize = 12.sp)
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    currentPage = 1
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = DeepBlack, fontSize = 12.sp),
                                cursorBrush = SolidColor(LushGreen),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = MutedText, modifier = Modifier.size(14.dp))
                            }
                        }

                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Filter by Date",
                                tint = if (selectedDateFilter != null) LushGreen else MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Active Date Filter Badge
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, LushGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Date: $selectedDateFilter",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                selectedDateFilter = null
                                currentPage = 1
                            }
                        ) {
                            Text("Clear Date Filter", fontSize = 11.sp, color = LushGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // List Items
                if (filteredRecords.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = LushGreen, modifier = Modifier.size(36.dp))
                            Text("No harvest records match", color = DeepBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your search or date filter.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pageItems) { record ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
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
                                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = LushGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = record.cropName.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DeepBlack
                                            )
                                            if (!record.cropVariety.isNullOrBlank()) {
                                                Text(
                                                    text = " (${record.cropVariety})",
                                                    fontSize = 12.sp,
                                                    color = LushGreen,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFE8F5E9),
                                            border = BorderStroke(1.dp, LushGreen)
                                        ) {
                                            Text(
                                                text = record.farmName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LushGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = LushGreen, modifier = Modifier.size(13.dp))
                                            Text("Plot: ${record.plotLabel}", fontSize = 11.sp, color = DeepBlack)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Scale, contentDescription = null, tint = LushGreen, modifier = Modifier.size(13.dp))
                                            Text("Yield: ${if (record.yieldKg > 0f) "${record.yieldKg} kg" else "N/A"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MutedText, modifier = Modifier.size(11.dp))
                                            Text("Planted: ${record.plantedDate?.take(10) ?: "N/A"}", fontSize = 10.sp, color = MutedText)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = MutedText, modifier = Modifier.size(11.dp))
                                            Text("Harvested: ${record.harvestedAt.take(10)}", fontSize = 10.sp, color = MutedText)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Timer, contentDescription = null, tint = LushGreen, modifier = Modifier.size(11.dp))
                                            Text("${record.growingDurationDays} ${if (record.cropName.lowercase().contains("ampalaya") || record.cropVariety?.contains("10s", ignoreCase = true) == true) "Secs" else "Days"}", fontSize = 10.sp, color = LushGreen)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MutedText, modifier = Modifier.size(11.dp))
                                        Text("Activity: ${formatActivityTime(record.harvestedAt)}", fontSize = 10.sp, color = MutedText)
                                    }

                                    if (!record.notes.isNullOrBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = MutedText, modifier = Modifier.size(12.dp))
                                            Text("Notes: ${record.notes}", fontSize = 11.sp, color = DeepBlack)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Pagination Controls
                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous", fontSize = 11.sp, color = DeepBlack)
                        }
                        Text(
                            text = "Page $currentPage of $totalPages",
                            fontSize = 12.sp,
                            color = DeepBlack,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text("Next", fontSize = 11.sp, color = DeepBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepBlack)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        val availableDates = remember(harvestHistory) {
            harvestHistory.map { it.harvestedAt.take(10) }.distinct().sortedDescending()
        }
        DatePickerSelectionDialog(
            availableDates = availableDates,
            selectedDate = selectedDateFilter,
            onSelectDate = {
                selectedDateFilter = it
                currentPage = 1
                showDatePickerModal = false
            },
            onDismiss = { showDatePickerModal = false }
        )
    }
}
