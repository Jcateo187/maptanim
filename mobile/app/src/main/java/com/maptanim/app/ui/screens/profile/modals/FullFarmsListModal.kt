package com.maptanim.app.ui.screens.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import com.maptanim.app.ui.theme.Danger
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

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
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
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
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, ForestGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
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
                            Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🚜 My Registered Farms (${filteredFarms.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = White
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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = White
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create Farm",
                                modifier = Modifier.size(16.dp),
                                tint = White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New Farm",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Modal",
                                tint = White
                            )
                        }
                    }
                }

                // Search Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = SolidColor(ForestGreen),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "Search farms by name...",
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

                // Farm Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No matching farms found.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(pageItems) { farm ->
                            val isActive = farm.id == activeFarmId
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(
                                    1.dp,
                                    if (isActive) ForestGreen else ForestGreen.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            farm.farmName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = White
                                        )
                                        Text(
                                            text = if (farm.createdAt.isNotBlank()) "Created: ${farm.createdAt}" else "Farm Workspace",
                                            fontSize = 10.sp,
                                            color = White.copy(alpha = 0.5f)
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isActive) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = ForestGreen.copy(alpha = 0.25f),
                                                border = BorderStroke(1.dp, ForestGreen)
                                            ) {
                                                Text(
                                                    "ACTIVE",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = { onSelectActiveFarm(farm.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(
                                                    "Select",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = ForestGreen
                                                )
                                            }
                                        }

                                        // Rename Button
                                        IconButton(
                                            onClick = { onRenameFarmClick(farm) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Rename Farm",
                                                tint = White.copy(alpha = 0.85f),
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
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Farm",
                                                    tint = Danger.copy(alpha = 0.85f),
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text(
                            "Page $currentPage of $totalPages",
                            fontSize = 12.sp,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
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
}
