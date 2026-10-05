package com.maptanim.app.features.farm.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.farm.viewmodel.EditUiState

/**
 * EditBottomLayout — Compact contextual toolbar for selected bed or crop plot.
 *
 * Core actions:
 *   - Duplicate: Spawns a duplicate crop/bed instance
 *   - Resize: Toggles 8-point bounding box handles
 *   - Rotate: Swaps width and height (90 degree rotation)
 *   - Delete: Removes selected plot from farm
 */
@Composable
fun EditBottomLayout(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    onDuplicateClick: () -> Unit = {},
    onResizeClick: () -> Unit = {},
    onRotateClick: () -> Unit = {},
    onChangeCropClick: () -> Unit = {},
    onChangeSoilClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    if (uiState.selectedZoneId != null || uiState.selectedPlotId != null) {
        val isCropSelected = uiState.selectedZoneId != null
        val selectedZone = uiState.cropZones.firstOrNull { it.id == uiState.selectedZoneId }
        val selectedPlot = uiState.plots.firstOrNull { it.id == (selectedZone?.plotId ?: uiState.selectedPlotId) }

        val cropTitle: String
        val dimensionsText: String
        val badgeColor: Color

        if (isCropSelected && selectedZone != null) {
            val cropName = selectedZone.cropName ?: "Crop"
            val parentLabel = selectedPlot?.plotLabel ?: "Bed"
            cropTitle = "$cropName (Crop Zone)"
            dimensionsText = "${selectedZone.widthM}m × ${selectedZone.heightM}m • in $parentLabel"
            badgeColor = Color(0xFFFFB300) // Amber for crop zone
        } else {
            val isBed = selectedPlot?.cropName.equals("Bed", ignoreCase = true) || selectedPlot?.cropId.equals("bed", ignoreCase = true)
            val bedCrops = uiState.cropZones.filter { it.plotId == uiState.selectedPlotId && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
            cropTitle = when {
                isBed && bedCrops.isNotEmpty() -> {
                    val summary = bedCrops.mapNotNull { it.cropName }.distinct().joinToString(", ")
                    "${selectedPlot?.plotLabel ?: "Bed"} • $summary"
                }
                isBed -> "${selectedPlot?.plotLabel ?: "Garden Bed"} • Empty"
                else -> selectedPlot?.cropName ?: selectedPlot?.plotLabel ?: "Garden Bed"
            }
            dimensionsText = "${selectedPlot?.widthM?.toInt() ?: 1}m × ${selectedPlot?.heightM?.toInt() ?: 1}m"
            badgeColor = Color(0xFF81C784) // Green for bed
        }

        Surface(
            modifier = modifier
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1C2418).copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D28))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Info Badge
                Column(modifier = Modifier.padding(end = 4.dp)) {
                    Text(
                        text = cropTitle,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Text(
                        text = dimensionsText,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .padding(horizontal = 1.dp)
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(Color(0xFF2E3D28), androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(0f, size.height), strokeWidth = 1f)
                    }
                }

                ActionChip(Icons.Default.ContentCopy, "Duplicate", onClick = onDuplicateClick)
                ActionChip(Icons.Default.AspectRatio, "Resize", isActive = uiState.isResizeMode, onClick = onResizeClick)
                if (!isCropSelected) {
                    ActionChip(Icons.AutoMirrored.Filled.RotateRight, "Rotate", onClick = onRotateClick)
                }
                ActionChip(Icons.Default.Delete, if (isCropSelected) "Remove" else "Delete", isDestructive = true, onClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = when {
        isDestructive -> Color(0xFFEF5350)
        isActive -> Color.White
        else -> Color(0xFFE0E0E0)
    }
    val containerColor = if (isActive) Color(0xFF2E7D32) else Color(0xFF253022)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(15.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = contentColor, fontSize = 11.sp)
        }
    }
}