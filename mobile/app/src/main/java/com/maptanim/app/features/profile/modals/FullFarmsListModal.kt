package com.maptanim.app.features.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.maptanim.app.domain.model.Farm

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * FullFarmsListModal — Paginated full modal of registered farm workspaces in Daylight theme.
 */
@Composable
fun FullFarmsListModal(
    farms: List<Farm>,
    activeFarmId: String? = null,
    onCreateFarmClick: () -> Unit = {},
    onRenameFarmClick: (Farm) -> Unit = {},
    onDeleteFarmClick: (Farm) -> Unit = {},
    onSelectActiveFarm: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    val filteredFarms = remember(farms, searchQuery) {
        if (searchQuery.isBlank()) farms
        else farms.filter {
            it.farmName.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalPages = (filteredFarms.size + itemsPerPage - 1).coerceAtLeast(1) / itemsPerPage
    val pageItems = remember(filteredFarms, currentPage) {
        val safePage = currentPage.coerceIn(1, totalPages.coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredFarms.drop(startIndex).take(itemsPerPage)
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
                            text = "My Registered Farms (${filteredFarms.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DeepBlack
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCreateFarmClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Farm",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = DeepBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
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
                            contentDescription = "Search",
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isBlank()) {
                                Text("Search farms by name...", color = MutedText, fontSize = 12.sp)
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
                    }
                }

                // List Items
                if (filteredFarms.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = LushGreen, modifier = Modifier.size(36.dp))
                            Text("No farms found", color = DeepBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your search criteria.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pageItems) { farm ->
                            val isActive = farm.id == activeFarmId
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightSurface,
                                border = BorderStroke(1.dp, if (isActive) LushGreen else CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = farm.farmName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = DeepBlack
                                        )
                                        Text(
                                            text = if (farm.createdAt.isNotBlank()) "Created: ${farm.createdAt}" else "Farm Workspace",
                                            fontSize = 11.sp,
                                            color = MutedText
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isActive) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFE8F5E9),
                                                border = BorderStroke(1.dp, LushGreen)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = LushGreen,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = { onSelectActiveFarm(farm.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                border = BorderStroke(1.dp, LushGreen),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(
                                                    text = "Select",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = LushGreen
                                                )
                                            }
                                        }

                                        // Rename Button
                                        IconButton(
                                            onClick = { onRenameFarmClick(farm) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Rename Farm",
                                                tint = DeepBlack,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Delete Button (only if more than 1 farm)
                                        if (farms.size > 1) {
                                            IconButton(
                                                onClick = { onDeleteFarmClick(farm) },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Farm",
                                                    tint = Color(0xFFC62828),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
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
}
