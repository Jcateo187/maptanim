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
        Surface(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionChip(Icons.Default.ContentCopy, "Duplicate", onClick = onDuplicateClick)
                ActionChip(Icons.Default.AspectRatio, "Resize", onClick = onResizeClick)
                ActionChip(Icons.Default.Delete, "Delete", isDestructive = true, onClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isDestructive) Color(0xFFEF5350) else Color.White
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(14.dp))
        Text(label, fontWeight = FontWeight.Bold, color = contentColor, fontSize = 11.sp)
    }
}