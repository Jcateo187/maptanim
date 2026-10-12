package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * FarmEditTopHeader — Dedicated top bar for the 2D Farm Edit Screen.
 * Contains:
 * - Back button (returns to Bed List)
 * - Farm Name
 * - Undo button
 * - Redo button
 * - More vertical dots menu:
 *   1. Adjust garden size
 *   2. Rotate garden
 *   3. Garden summary
 *   4. Show companion (checkbox)
 *   5. Show variety (checkbox)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmEditTopHeader(
    gardenName: String,
    canUndo: Boolean,
    canRedo: Boolean,
    showCompanion: Boolean,
    showVariety: Boolean,
    showMeasurement: Boolean = true,
    showCropName: Boolean = true,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onAdjustSize: () -> Unit,
    onRotateGarden: () -> Unit,
    onGardenSummary: () -> Unit,
    onToggleCompanion: (Boolean) -> Unit,
    onToggleVariety: (Boolean) -> Unit,
    onToggleMeasurement: (Boolean) -> Unit = {},
    onToggleCropName: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF2F5F0),
        shadowElevation = 4.dp,
        border = BorderStroke(1.2.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Group: Back Button + Garden Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(38.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Garden List",
                        tint = DeepBlack
                    )
                }

                Text(
                    text = gardenName.ifBlank { "Garden" },
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
            }

            // Right Group: Undo, Redo, More Vert Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Undo Button
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) DeepBlack else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Redo Button
                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) DeepBlack else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // More Vertical Dots Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = DeepBlack,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        val itemColors = MenuDefaults.itemColors(
                            textColor = DeepBlack,
                            leadingIconColor = LushGreen
                        )

                        // 1. Adjust Garden Size
                        DropdownMenuItem(
                            text = { Text("Adjust garden size", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack) },
                            leadingIcon = {
                                Icon(Icons.Default.Straighten, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                menuExpanded = false
                                onAdjustSize()
                            }
                        )

                        // 2. Rotate Garden
                        DropdownMenuItem(
                            text = { Text("Rotate garden", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack) },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                menuExpanded = false
                                onRotateGarden()
                            }
                        )

                        // 3. Garden Summary
                        DropdownMenuItem(
                            text = { Text("Garden summary", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack) },
                            leadingIcon = {
                                Icon(Icons.Default.Assessment, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                menuExpanded = false
                                onGardenSummary()
                            }
                        )

                        HorizontalDivider(color = CardBorderColor)

                        // 4. Show Companion Checkbox
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Show companion", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                                    Checkbox(
                                        checked = showCompanion,
                                        onCheckedChange = { onToggleCompanion(it) },
                                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                onToggleCompanion(!showCompanion)
                            }
                        )

                        // 5. Show Variety Checkbox
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Show variety", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                                    Checkbox(
                                        checked = showVariety,
                                        onCheckedChange = { onToggleVariety(it) },
                                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                onToggleVariety(!showVariety)
                            }
                        )

                        // 6. Show Measurement Checkbox
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Show measurement", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                                    Checkbox(
                                        checked = showMeasurement,
                                        onCheckedChange = { onToggleMeasurement(it) },
                                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Straighten, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                onToggleMeasurement(!showMeasurement)
                            }
                        )

                        // 7. Show Crop Name Checkbox
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Show crop name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                                    Checkbox(
                                        checked = showCropName,
                                        onCheckedChange = { onToggleCropName(it) },
                                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = LushGreen, modifier = Modifier.size(20.dp))
                            },
                            colors = itemColors,
                            onClick = {
                                onToggleCropName(!showCropName)
                            }
                        )
                    }
                }
            }
        }
    }
}
