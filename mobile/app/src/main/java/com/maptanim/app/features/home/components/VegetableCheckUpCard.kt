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

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)
private val MutedText = Color(0xFF555555)

// VEGETABLES CHECK UP card: solid green with a mix of light and ocean blue and cloud white
private val CheckUpCardBgBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2E7D32), // Solid Green
        Color(0xFF00796B), // Deep Ocean Teal
        Color(0xFF0277BD), // Ocean Blue
        Color(0xFF4FC3F7), // Light Blue
        Color(0xFFF0F8FF)  // Cloud White
    ),
    start = Offset(0f, 0f),
    end = Offset(900f, 600f)
)

// Border: mix of green and sky blue and dirty white
private val CheckUpCardBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2E7D32), // Green
        Color(0xFF4FC3F7), // Sky Blue
        Color(0xFFE5E2D9)  // Dirty White (warm off-white)
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
    val crops: List<PlantedVegetableData>
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
 * When the user taps a garden name card, a modal shows the list of vegetables planted in that garden,
 * allowing the user to initiate a direct health and agronomic check-up for that vegetable.
 */
@Composable
fun VegetableCheckUpCard(
    plots: List<PlotRenderData>,
    farmName: String,
    onStartCheckUp: (cropName: String, variety: String, gardenLabel: String, plotId: String, stage: ManagementStage) -> Unit,
    onOpenFarmHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGardenForInspection by remember { mutableStateOf<GardenCardItem?>(null) }

    // Group plots into garden items
    val gardens = remember(plots, farmName) {
        if (plots.isEmpty()) {
            listOf(
                GardenCardItem(
                    id = "garden_default",
                    name = farmName.ifBlank { "My Vegetable Garden" },
                    crops = emptyList()
                )
            )
        } else {
            val grouped = plots.groupBy { it.plotLabel.substringBefore(" (Z") }
            grouped.map { (gardenLabel, plotList) ->
                val vegetables = plotList.mapNotNull { p ->
                    val name = p.cropName?.trim()
                    if (!name.isNullOrBlank() && !name.equals("Bed", ignoreCase = true)) {
                        PlantedVegetableData(
                            plotId = p.id,
                            cropName = name,
                            cropVariety = p.cropVariety,
                            daysPlanted = p.daysPlanted,
                            growthStage = p.growthStage,
                            stage = ManagementStage.fromIndex(p.growthStage - 1)
                        )
                    } else null
                }
                GardenCardItem(
                    id = plotList.first().id,
                    name = gardenLabel,
                    crops = vegetables
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

                // Cards for Garden Names (Compact size: 3 cards fully visible, 4th peeking in 1 row)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(gardens, key = { _, garden -> garden.id }) { index, garden ->
                        val gardenImageRes = GardenImages[(index % GardenImages.size + GardenImages.size) % GardenImages.size]
                        Surface(
                            onClick = { selectedGardenForInspection = garden },
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
                                        text = "${garden.crops.size}",
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

    // Modal Dialog showing the List of Vegetables when user taps a Garden Name
    selectedGardenForInspection?.let { garden ->
        Dialog(
            onDismissRequest = { selectedGardenForInspection = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Yard,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = garden.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DeepBlack
                                )
                                Text(
                                    text = "Vegetables in this garden",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }

                        IconButton(
                            onClick = { selectedGardenForInspection = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = DeepBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                    // List of Vegetables inside the garden
                    if (garden.crops.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "No vegetables planted in ${garden.name} yet.",
                                fontSize = 13.sp,
                                color = MutedText
                            )
                            Button(
                                onClick = {
                                    selectedGardenForInspection = null
                                    onOpenFarmHub()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Plant Vegetables in Farm Hub",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Quick test check-up option for common vegetables
                            Text(
                                text = "Or run a quick check up on a standard vegetable:",
                                fontSize = 11.sp,
                                color = MutedText,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Eggplant", "Tomato", "Pechay").forEach { defaultCrop ->
                                    OutlinedButton(
                                        onClick = {
                                            selectedGardenForInspection = null
                                            onStartCheckUp(
                                                defaultCrop,
                                                "",
                                                garden.name,
                                                garden.id,
                                                ManagementStage.VEGETATIVE_GROWTH
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, CardBorderColor)
                                    ) {
                                        Text(
                                            text = defaultCrop,
                                            fontSize = 11.sp,
                                            color = DeepBlack,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(garden.crops) { cropItem ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = LightSurface,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE8F5E9)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocalFlorist,
                                                    contentDescription = null,
                                                    tint = LushGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = cropItem.cropName,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DeepBlack
                                                )
                                                Text(
                                                    text = if (cropItem.daysPlanted > 0)
                                                        "${cropItem.daysPlanted} days planted • Stage ${cropItem.growthStage}"
                                                    else
                                                        "Stage ${cropItem.growthStage} • ${cropItem.cropVariety ?: "Vegetable"}",
                                                    fontSize = 11.sp,
                                                    color = MutedText
                                                )
                                            }
                                        }

                                        // Check Up Button
                                        Button(
                                            onClick = {
                                                selectedGardenForInspection = null
                                                onStartCheckUp(
                                                    cropItem.cropName,
                                                    cropItem.cropVariety ?: "",
                                                    garden.name,
                                                    cropItem.plotId,
                                                    cropItem.stage
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MedicalServices,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Check Up",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
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
}
