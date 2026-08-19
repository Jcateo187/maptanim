package com.maptanim.app.ui.screens.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
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
import com.maptanim.app.ui.screens.profile.utils.formatActivityTime
import com.maptanim.app.ui.screens.profile.utils.getCropEmoji
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

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

    val totalPages = (filteredRecords.size + itemsPerPage - 1) / itemsPerPage
    val pageItems = remember(filteredRecords, currentPage) {
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
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
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFD48806).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFFD48806), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "🌾 Harvests on $selectedDateFilter (${filteredRecords.size})" else "🌾 Complete Harvest History (${filteredRecords.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = White)
                    }
                }

                // Search Bar with Date Selection Icon Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, Color(0xFFD48806).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = SolidColor(Color(0xFFD48806)),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search harvest history by crop, variety, plot...", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Activity Date",
                                tint = if (selectedDateFilter != null) Color(0xFFD48806) else White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Date Filter Badge if Active
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD48806).copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, Color(0xFFD48806))
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { selectedDateFilter = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 Activity Date: $selectedDateFilter ✖",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }
                        Text(
                            text = "Showing all harvest activity on $selectedDateFilter",
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.6f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (selectedDateFilter != null) "No harvest activities found on $selectedDateFilter." else "No harvest records match your search filter.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(pageItems) { record ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, Color(0xFF2A3828)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(getCropEmoji(record.cropName), fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = record.cropName.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = White
                                            )
                                            if (!record.cropVariety.isNullOrBlank()) {
                                                Text(
                                                    text = " (${record.cropVariety})",
                                                    fontSize = 12.sp,
                                                    color = ForestGreen
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFD48806).copy(alpha = 0.25f),
                                            border = BorderStroke(1.dp, Color(0xFFD48806))
                                        ) {
                                            Text(
                                                text = record.farmName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("📍 Plot/Zone: ${record.plotLabel}", fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                        Text("⚖️ Yield: ${if (record.yieldKg > 0f) "${record.yieldKg} kg" else "N/A"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD48806))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("🌱 Planted: ${record.plantedDate?.take(10) ?: "N/A"}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                        Text("🌾 Harvested: ${record.harvestedAt.take(10)}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                        Text("⏱️ ${record.growingDurationDays} ${if (record.cropName.lowercase().contains("ampalaya") || record.cropVariety?.contains("10s", ignoreCase = true) == true) "Secs" else "Days"}", fontSize = 10.sp, color = ForestGreen)
                                    }

                                    Text(
                                        text = "🕒 Activity Time: ${formatActivityTime(record.harvestedAt)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD48806)
                                    )

                                    if (!record.notes.isNullOrBlank()) {
                                        Text("📝 Notes: ${record.notes}", fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text("Page $currentPage of $totalPages", fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next ►", fontSize = 11.sp, color = White)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        DatePickerSelectionDialog(
            selectedDate = selectedDateFilter,
            onDateSelected = { selectedDateFilter = it; currentPage = 1 },
            onDismiss = { showDatePickerModal = false }
        )
    }
}
