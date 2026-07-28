package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState
import com.maptanim.app.ui.screens.edit.EditUiState

/**
 * EditLeftToolbar — Direct Planting Slide-out Edit Tools panel.
 */
@Composable
fun EditLeftToolbar(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    farmEditState: FarmEditState? = null,
    onToolSelected: (EditTool) -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Slide-Out Toggle Button ─────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shadowElevation = 6.dp,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                    contentDescription = "Toggle Tools",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isExpanded) "Hide Tools" else "Edit Tools",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        // ── Slide-Out Panel ─────────────────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                modifier = Modifier.width(200.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "EDIT TOOLS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )

                    // 1. Add Plant / Crops
                    ToolRow(
                        icon = Icons.Default.LocalFlorist,
                        title = "Add Plant/Crops",
                        isSelected = uiState.activeTool == EditTool.ADD_PLANT,
                        onClick = { onToolSelected(EditTool.ADD_PLANT) }
                    )

                    // 2. Select / Move
                    ToolRow(
                        icon = Icons.Default.CropFree,
                        title = "Select / Move",
                        isSelected = uiState.activeTool == EditTool.SELECT_MOVE,
                        onClick = { onToolSelected(EditTool.SELECT_MOVE) }
                    )

                    // 3. Delete
                    ToolRow(
                        icon = Icons.Default.Delete,
                        title = "Delete",
                        isDestructive = true,
                        isSelected = uiState.activeTool == EditTool.DELETE,
                        onClick = {
                            onToolSelected(EditTool.DELETE)
                            onDeleteClick()
                        }
                    )

                    // ── Divider Line ─────────────────────────────────────
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.6f)
                    )

                    // 4. Save Button
                    ActionRow(
                        icon = Icons.Default.Save,
                        title = if (uiState.isSaving) "Saving..." else "Save",
                        contentColor = Color(0xFF2E7D32),
                        bgColor = Color(0xFFE8F5E9),
                        onClick = onSaveClick
                    )

                    // 5. Exit Button
                    ActionRow(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Exit",
                        contentColor = Color(0xFFC62828),
                        bgColor = Color(0xFFFFEBEE),
                        onClick = onExitClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isDestructive: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent
    val contentColor = when {
        isDestructive -> Color(0xFFC62828)
        isSelected -> Color(0xFF1B5E20)
        else -> Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(18.dp))
        Text(title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp, color = contentColor)
    }
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    contentColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(18.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = contentColor)
    }
}