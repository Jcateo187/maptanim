package com.maptanim.app.features.farm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val ActionBlue = Color(0xFF1976D2)
private val AlertRed = Color(0xFFD32F2F)

/**
 * BedFloatingActionRing — Planter-style circular contextual action overlay.
 * Appears anchored above the active bed on the 2D canvas:
 * 1. [ ℹ️ Info / Dossier ] -> Opens complete GrowIt-level agronomic intelligence
 * 2. [ 🌱 Plant / Crop ] -> Opens CropTray for planting / intercropping
 * 3. [ 📐 Size / Capacity ] -> Opens Directional Bed Sizing Dialog with ergonomic reach warning
 * 4. [ 🗑️ Delete ] -> Deletes bed
 */
@Composable
fun BedFloatingActionRing(
    selectedPlot: PlotRenderData?,
    primaryCropName: String?,
    onOpenDossier: () -> Unit,
    onOpenCropTray: () -> Unit,
    onOpenSizingDialog: () -> Unit,
    onDeletePlot: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = selectedPlot != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        if (selectedPlot != null) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite.copy(alpha = 0.95f),
                border = BorderStroke(1.5.dp, LushGreen.copy(alpha = 0.6f)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bed Indicator Pill
                    Column(
                        modifier = Modifier
                            .clickable { onOpenDossier() }
                            .padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = selectedPlot.plotLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = if (!primaryCropName.isNullOrBlank()) primaryCropName else "Empty Bed",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (!primaryCropName.isNullOrBlank()) LushGreen else Color.Gray
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(26.dp), color = Color(0xFFE0E0E0))

                    // 1. [ ℹ️ Info ] -> Opens GrowIt Dossier
                    FloatingCircleButton(
                        icon = Icons.Default.Info,
                        label = "Info",
                        backgroundColor = LushGreen,
                        iconTint = Color.White,
                        onClick = onOpenDossier
                    )

                    // 2. [ 🌱 Plant ] -> Opens Crop Tray
                    FloatingCircleButton(
                        icon = Icons.Default.Eco,
                        label = "Plant",
                        backgroundColor = Color(0xFFE8F5E9),
                        iconTint = LushGreen,
                        onClick = onOpenCropTray
                    )

                    // 3. [ 📐 Size ] -> Opens Directional Sizing
                    FloatingCircleButton(
                        icon = Icons.Default.Straighten,
                        label = "Size",
                        backgroundColor = Color(0xFFE3F2FD),
                        iconTint = ActionBlue,
                        onClick = onOpenSizingDialog
                    )

                    // 4. [ 🗑️ Del ] -> Delete Bed
                    FloatingCircleButton(
                        icon = Icons.Default.DeleteOutline,
                        label = "Del",
                        backgroundColor = Color(0xFFFFEBEE),
                        iconTint = AlertRed,
                        onClick = onDeletePlot
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingCircleButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .shadow(2.dp, CircleShape)
            .background(backgroundColor, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
    }
}
