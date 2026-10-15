package com.maptanim.app.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.R
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.renderer.canvas.GardenSvgRenderer
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.schedule.resolveScheduleStatus
import java.time.LocalDate

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)
private val MutedText = Color(0xFF555555)

// VEGETABLES CHECK UP card: semi-transparent green with a mix of light and ocean blue and cloud white
private val CheckUpCardBgBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xBA2E7D32), // Semi-transparent Green (~73% opacity)
        Color(0xB000796B), // Semi-transparent Ocean Teal (~69% opacity)
        Color(0xA60277BD), // Semi-transparent Ocean Blue (~65% opacity)
        Color(0x9E4FC3F7), // Semi-transparent Light Blue (~62% opacity)
        Color(0x8CF0F8FF)  // Semi-transparent Cloud White (~55% opacity)
    ),
    start = Offset(0f, 0f),
    end = Offset(900f, 600f)
)

// Border: semi-transparent mix of green, sky blue, and dirty white
private val CheckUpCardBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xDD2E7D32), // Green (~87% opacity)
        Color(0xCC4FC3F7), // Sky Blue (~80% opacity)
        Color(0xCCE5E2D9)  // Dirty White (~80% opacity)
    ),
    start = Offset(0f, 0f),
    end = Offset(900f, 600f)
)

private val GardenImages = listOf(
    R.drawable.garden_card_1,
    R.drawable.garden_card_2,
    R.drawable.garden_card_3,
    R.drawable.garden_card_4,
    R.drawable.garden_card_5
)

data class GardenCardItem(
    val id: String,
    val name: String,
    val crops: List<PlantedVegetableData>,
    val startedTodayCount: Int = crops.size
)

data class PlantedVegetableData(
    val plotId: String,
    val cropName: String,
    val cropVariety: String?,
    val daysPlanted: Int,
    val growthStage: Int,
    val stage: ManagementStage
)

/**
 * VegetableCheckUpCard — Home screen module replacing the previous Quick Shortcuts.
 * Displays small cards showing each garden name.
 * Only shows in numbers those vegetables who have ALREADY started a schedule on this day.
 */
@Composable
fun VegetableCheckUpCard(
    plots: List<PlotRenderData>,
    farmName: String? = null,
    onStartCheckUp: (cropName: String, variety: String, gardenLabel: String, plotId: String, stage: ManagementStage) -> Unit,
    onOpenFarmHub: () -> Unit,
    onGardenClick: (gardenId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }

    // Group plots into garden items — only count vegetables whose schedule started on this day
    val gardens = remember(plots, today, farmName) {
        val activePlots = plots.filter { it.isActive }
        if (activePlots.isEmpty()) {
            emptyList()
        } else {
            val grouped = activePlots.groupBy { it.plotLabel.substringBefore(" (Z") }
            grouped.map { (gardenLabel, plotList) ->
                val startedTodayVegetables = plotList.mapNotNull { p ->
                    val name = p.cropName?.trim()
                    if (!name.isNullOrBlank() && !name.equals("Bed", ignoreCase = true)) {
                        val schedStatus = resolveScheduleStatus(
                            plantedDate = p.plantedDate,
                            cropName = name,
                            isMonitoringStarted = p.isMonitoringStarted,
                            today = today
                        )

                        if (schedStatus.canCheckUp) {
                            PlantedVegetableData(
                                plotId = p.id,
                                cropName = name,
                                cropVariety = p.cropVariety,
                                daysPlanted = schedStatus.dayNumberOrNull ?: p.daysPlanted,
                                growthStage = p.growthStage,
                                stage = schedStatus.stageOrNull ?: ManagementStage.fromIndex(p.growthStage - 1)
                            )
                        } else null
                    } else null
                }
                GardenCardItem(
                    id = plotList.first().id,
                    name = gardenLabel,
                    crops = startedTodayVegetables,
                    startedTodayCount = startedTodayVegetables.size
                )
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        shadowElevation = 3.dp,
        border = BorderStroke(1.5.dp, CheckUpCardBorderBrush)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CheckUpCardBgBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                // Header Area with Title on left and Mascot on right resting on the line
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Text(
                            text = "VEGETABLES CHECK UP",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            color = Color.White
                        )
                    }

                    // Top of checkup area image placed directly on top of line with ZERO gap and NO blend color
                    Image(
                        painter = painterResource(id = R.drawable.top_of_checkup_area),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp)
                            .height(48.dp)
                    )
                }

                // Line bottom of icon VEGETABLES CHECK UP (zero gap directly beneath image)
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.35f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cards for Garden Names or Empty State
                if (gardens.isEmpty()) {
                    Surface(
                        onClick = onOpenFarmHub,
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Walang Aktibong Hardin",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Gumawa ng hardin sa Garden tab upang masuri ang kalusugan ng iyong mga pananim.",
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    lineHeight = 13.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Hardin",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LushGreen
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(gardens, key = { _, garden -> garden.id }) { index, garden ->
                            val gardenImageRes = GardenImages[(index % GardenImages.size + GardenImages.size) % GardenImages.size]
                            Surface(
                                onClick = { onGardenClick(garden.id) },
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                modifier = Modifier
                                    .width(88.dp)
                                    .height(96.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    // 1. Full-Card Garden Photograph (5 distinct Philippine vegetable garden scenes with loop)
                                    Image(
                                        painter = painterResource(id = gardenImageRes),
                                        contentDescription = garden.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // 2. Number of vegetables in Top-Right Badge (Solid White background, same color as garden name)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.White,
                                        shadowElevation = 2.dp,
                                        border = BorderStroke(0.5.dp, Color(0xFFE0E0E0)),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = "${garden.startedTodayCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepBlack, // Same color as garden name
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }

                                    // 3. Garden Name ONLY at Bottom (Solid White Area)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .padding(horizontal = 4.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = garden.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepBlack,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
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
