package com.maptanim.app.features.farm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val AlertAmber = Color(0xFFFFB300)
private val WaterBlue = Color(0xFF42A5F5)

/**
 * CanvasSmartGuidanceHud — Interactive, floating agricultural guidance HUD.
 * Directly guides the farmer's next step on the canvas in real-time:
 * 1. Initial State: Guides placing the first garden bed.
 * 2. Bed Planning: Guides adding crops to empty beds.
 * 3. Dragging Crops: Displays instant companion planting rules and compatibility tips.
 * 4. Active Farm: Highlights today's urgent care tasks (watering, scouting).
 */
@Composable
fun CanvasSmartGuidanceHud(
    plots: List<PlotRenderData>,
    cropZones: List<CropZoneRenderData>,
    selectedPlotId: String?,
    isDraggingCrop: Boolean,
    dragCropName: String,
    onOpenAddBed: () -> Unit,
    onOpenCropTray: () -> Unit,
    onOpenGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeZones = remember(cropZones) {
        cropZones.filter { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
    }

    // Determine current agricultural guidance state
    val (icon, iconTint, title, subtitle, onClick) = when {
        // State 1: When dragging a crop -> Live Companion Guidance!
        isDraggingCrop -> {
            val beneficial = try { CompanionDataProvider.getBeneficialCompanions(dragCropName).take(2).joinToString(", ") } catch (_: Exception) { "" }
            val antagonist = try { CompanionDataProvider.getAntagonistCrops(dragCropName).take(1).joinToString(", ") } catch (_: Exception) { "" }
            val tipText = when {
                beneficial.isNotBlank() && antagonist.isNotBlank() ->
                    "Likes $beneficial • Avoid planting beside $antagonist"
                beneficial.isNotBlank() ->
                    "Best companions: $beneficial (boosts growth & yield)"
                else ->
                    "Drop inside any green garden bed to plant"
            }
            GuidanceInfo(
                icon = Icons.Default.Lightbulb,
                iconTint = AlertAmber,
                title = "Companion Tip for $dragCropName",
                subtitle = tipText,
                onClick = {}
            )
        }

        // State 2: No garden beds on the farm yet
        plots.isEmpty() -> {
            GuidanceInfo(
                icon = Icons.Default.AddCircle,
                iconTint = LushGreen,
                title = "Start Your Garden Layout",
                subtitle = "Tap here or '+ Bed' above to place your first raised bed",
                onClick = onOpenAddBed
            )
        }

        // State 3: Beds exist, but some have zero crops
        plots.any { p -> activeZones.none { z -> z.plotId == p.id } } -> {
            val emptyBed = plots.firstOrNull { p -> activeZones.none { z -> z.plotId == p.id } }
            val label = emptyBed?.plotLabel ?: "Bed"
            GuidanceInfo(
                icon = Icons.Default.Spa,
                iconTint = LushGreen,
                title = "$label is Ready for Planting",
                subtitle = "Tap here to open Crop Tray or drag vegetables into the bed",
                onClick = onOpenCropTray
            )
        }

        // State 4: Crops are planted -> Daily care guidance!
        else -> {
            val totalCrops = activeZones.size
            GuidanceInfo(
                icon = Icons.Default.WaterDrop,
                iconTint = WaterBlue,
                title = "Today's Care: $totalCrops Plants Growing",
                subtitle = "Water early morning (2.5L/m²) • Tap to view step-by-step guide",
                onClick = onOpenGuide
            )
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            color = Color(0xF2141D12), // Canva/Miro dark glassmorphic HUD
            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.65f)),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = iconTint.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFFC8E6C9),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 11.sp,
                        maxLines = 1
                    )
                }

                if (!isDraggingCrop) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Action",
                        tint = Color(0xFFA5D6A7),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

private data class GuidanceInfo(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)
