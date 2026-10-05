package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val BasketballOrange = Color(0xFFE65100)

/**
 * CanvasTopToolbar — Sleek, responsive floating top toolbar for the farm canvas.
 * Designed to prevent horizontal overcrowding across mobile screen widths:
 * - Left Cluster: Garden Construction ([+ Bed], [Crops], Micro Auto-Save)
 * - Right Cluster: Viewport Tools (Compact Zoom Pill, Basketball Scale, Fullscreen)
 */
@Composable
fun CanvasTopToolbar(
    isCropTrayVisible: Boolean,
    onToggleCropTray: () -> Unit,
    onRequestAddBed: () -> Unit,
    isSaving: Boolean,
    showYardRulers: Boolean,
    onOpenYardGuide: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Left Cluster: Construction Actions ──────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Add Bed Button
            Surface(
                onClick = onRequestAddBed,
                shape = RoundedCornerShape(8.dp),
                color = LushGreen,
                border = BorderStroke(1.dp, Color(0xFF4CAF50))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bed",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Bed",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Crops Tray Toggle
            Surface(
                onClick = onToggleCropTray,
                shape = RoundedCornerShape(8.dp),
                color = if (isCropTrayVisible) Color(0xFF1B5E20) else Color(0xD9141D12),
                border = BorderStroke(1.dp, if (isCropTrayVisible) Color(0xFF81C784) else Color(0xFF385532))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Crops Tray",
                        tint = if (isCropTrayVisible) Color(0xFF81C784) else Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Crops",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Micro Auto-Save Status (Streamlined to prevent horizontal crowding)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x33000000),
                border = BorderStroke(1.dp, if (isSaving) Color(0xFF81C784) else Color(0x26FFFFFF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            color = Color(0xFF81C784),
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Auto-Saved",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // ── Right Cluster: Viewport Tools ───────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Yard Measurement Guide & Rulers Toggle
            Surface(
                onClick = onOpenYardGuide,
                shape = RoundedCornerShape(7.dp),
                color = if (showYardRulers) Color(0xFF2E7D32) else Color(0xD91E281C),
                border = BorderStroke(1.dp, if (showYardRulers) Color(0xFF81C784) else Color(0xFF385532))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = "Yard Rulers & Guide",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Yard",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Compact Zoom Control Capsule
            Surface(
                shape = RoundedCornerShape(7.dp),
                color = Color(0xD91E281C),
                border = BorderStroke(1.dp, Color(0xFF385532))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onZoomOut,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(14.dp)
                    )
                    IconButton(
                        onClick = onZoomIn,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Maximize / Restore Canvas Button
            Surface(
                onClick = onToggleExpand,
                shape = RoundedCornerShape(7.dp),
                color = if (isExpanded) LushGreen else Color(0xD91E281C),
                border = BorderStroke(1.dp, if (isExpanded) LushGreen else Color(0xFF385532))
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                    contentDescription = if (isExpanded) "Minimize Canvas" else "Expand Canvas",
                    tint = Color.White,
                    modifier = Modifier.padding(5.dp).size(13.dp)
                )
            }
        }
    }
}
