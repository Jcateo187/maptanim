package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.maptanim.app.ui.screens.edit.EditUiState

/**
 * EditBottomLayout — Contextual floating bottom bar for selected crop.
 *
 * Direct Crop Planting Tools per System Architecture Diagram:
 *   - Duplicate: Spawns 1 duplicate crop instance on farm area
 *   - Resize: Shows 8 bounding box edges for precision resizing
 *   - Delete: Removes selected crop from farm area
 */
@Composable
fun EditBottomLayout(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    onDuplicateClick: () -> Unit = {},
    onResizeClick: () -> Unit = {},
    onChangeCropClick: () -> Unit = {},
    onChangeSoilClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    if (uiState.selectedPlotId != null) {
        val selectedPlot = uiState.plots.firstOrNull { it.id == uiState.selectedPlotId }
        val cropTitle = selectedPlot?.cropName ?: "Planted Crop"
        val dimensionsText = "${selectedPlot?.widthM?.toInt() ?: 1}m × ${selectedPlot?.heightM?.toInt() ?: 1}m"

        Surface(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.Black.copy(alpha = 0.85f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.padding(end = 4.dp)) {
                    Text(
                        text = cropTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C784),
                        fontSize = 12.sp
                    )
                    Text(
                        text = dimensionsText,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                ActionChip(Icons.Default.ContentCopy, "Duplicate", onClick = onDuplicateClick)
                ActionChip(Icons.Default.AspectRatio, "Resize", isActive = uiState.isResizeMode, onClick = onResizeClick)
                ActionChip(Icons.Default.Delete, "Delete", isDestructive = true, onClick = onDeleteClick)
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
        else -> Color.White
    }
    val containerColor = if (isActive) Color(0xFF2E7D32) else Color.Transparent

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = containerColor,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(14.dp))
            Text(label, fontWeight = FontWeight.Bold, color = contentColor, fontSize = 11.sp)
        }
    }
}