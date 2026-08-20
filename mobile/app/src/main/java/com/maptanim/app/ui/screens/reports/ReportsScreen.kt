package com.maptanim.app.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel()
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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFF1B2317), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Reports",
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FARM ANALYTICS & REPORTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Key Performance Indicators Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportStatCard(
                            title = "Total Plots",
                            value = "${uiState.totalPlots}",
                            subtitle = "Active Crop Zones",
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatCard(
                            title = "Active Crops",
                            value = "${uiState.totalPlantedCrops}",
                            subtitle = "Planted Varieties",
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatCard(
                            title = "Task Success",
                            value = "${uiState.completionRate.toInt()}%",
                            subtitle = "${uiState.completedTasks} Completed",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Task Completion Rate Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Task Execution Rate",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = White
                                    )
                                }
                                Text(
                                    text = "${uiState.completedTasks} / ${uiState.completedTasks + uiState.pendingTasks} Tasks",
                                    fontSize = 12.sp,
                                    color = White.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (uiState.completionRate / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = ForestGreen,
                                trackColor = White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }

                // Category Distribution Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = ForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Crop Category Breakdown",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.categoryDistribution.isEmpty()) {
                                Text(
                                    text = "No active crops available to analyze.",
                                    fontSize = 12.sp,
                                    color = White.copy(alpha = 0.5f)
                                )
                            } else {
                                uiState.categoryDistribution.forEach { cat ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = cat.category, fontSize = 12.sp, color = White, fontWeight = FontWeight.SemiBold)
                                            Text(text = "${cat.count} (${cat.percentage.toInt()}%)", fontSize = 12.sp, color = ForestGreen)
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        LinearProgressIndicator(
                                            progress = { (cat.percentage / 100f).coerceIn(0f, 1f) },
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, fontSize = 11.sp, color = White.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = ForestGreen)
        }
    }
}
