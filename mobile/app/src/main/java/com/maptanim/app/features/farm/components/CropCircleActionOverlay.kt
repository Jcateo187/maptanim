package com.maptanim.app.features.farm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt
import com.maptanim.app.features.farm.renderer.model.cropSinglePlantSpacingM

private val LushGreen = Color(0xFF2E7D32)
private val LimeRing = Color(0xFFCCFF90)
private val DarkCircleBg = Color(0xFF142917)
private val DeepBlack = Color(0xFF111813)
private val AlertRed = Color(0xFFE53935)
private const val INCH_IN_METERS = 0.0254f

/**
 * CropCircleActionOverlay — 6 floating overlay user actions for each crop area:
 * 1. Center (0:00 Center): Info (white circular button with lush green ring)
 * 2. 9:00 (Straight Left): Check Up (lime green ring)
 * 3. ~10:45 (Top-Left): Calendar (lush green ring)
 * 4. ~12:30 (Top): Resize (lush green / lime ring, with interactive size bar)
 * 5. ~2:15 (Top-Right): Copy (lush green ring)
 * 6. 4:00 (Bottom-Right): Delete (alert red ring)
 */
@Composable
fun CropCircleActionOverlay(
    cropName: String,
    varietyName: String? = null,
    isResizeActive: Boolean = false,
    cropWidthM: Float = 0.3048f,
    onOpenInfo: () -> Unit,
    onOpenCheckUp: () -> Unit,
    onOpenCalendar: () -> Unit,
    onResizeCrop: () -> Unit = {},
    onUpdateSize: (newDiameterM: Float) -> Unit = {},
    onCopyCrop: () -> Unit,
    onDeleteCrop: () -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = cropName.isNotBlank(),
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            // ── Interactive Quick Resize Bar (When Resize is active) ─────────
            AnimatedVisibility(
                visible = isResizeActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xF2142917),
                    border = BorderStroke(1.5.dp, LimeRing),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .wrapContentWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // [-] 2 inches
                            IconButton(
                                onClick = {
                                    val newW = (cropWidthM - (2f * INCH_IN_METERS)).coerceAtLeast(0.127f)
                                    onUpdateSize(newW)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease size",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            val inches = (cropWidthM / INCH_IN_METERS).roundToInt()
                            val spacing = cropSinglePlantSpacingM(cropName).coerceAtLeast(0.06f)
                            val plantCount = maxOf(1, kotlin.math.round(cropWidthM / spacing).toInt()).let { it * it }
                            val plantLabel = if (plantCount > 1) " • $plantCount plants" else " • 1 plant"
                            Text(
                                text = "Size: ${inches}\"$plantLabel (${String.format(Locale.US, "%.2fm", cropWidthM)})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                            // [+] 2 inches
                            IconButton(
                                onClick = {
                                    val newW = (cropWidthM + (2f * INCH_IN_METERS)).coerceAtMost(3.0f)
                                    onUpdateSize(newW)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase size",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Preset Size Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf(6, 12, 18, 24, 36).forEach { sizeInches ->
                                val targetM = sizeInches * INCH_IN_METERS
                                val isCur = (cropWidthM / INCH_IN_METERS).roundToInt() == sizeInches
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCur) LushGreen else Color(0xFF1E3A22),
                                    border = BorderStroke(0.8.dp, if (isCur) LimeRing else Color(0xFF2E7D32)),
                                    modifier = Modifier
                                        .height(24.dp)
                                        .clickable { onUpdateSize(targetM) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 7.dp)
                                    ) {
                                        Text(
                                            text = "${sizeInches}\"",
                                            fontSize = 10.sp,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── The 6 Circular Action Buttons (9 to 4 o'clock radial arc) ─────
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. CENTER (0:00 Center): INFO
                FloatingCropActionButton(
                    icon = Icons.Default.Info,
                    contentDescription = "Info",
                    ringColor = LushGreen,
                    bgColor = Color.White,
                    iconTint = LushGreen,
                    sizeDp = 48.dp,
                    iconSizeDp = 24.dp,
                    onClick = onOpenInfo,
                    modifier = Modifier.offset(x = 0.dp, y = 0.dp)
                )

                // 2. 9 O'CLOCK: CHECK UP (Straight Left)
                FloatingCropActionButton(
                    icon = Icons.Default.MedicalServices,
                    contentDescription = "Check Up",
                    ringColor = LimeRing,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onOpenCheckUp,
                    modifier = Modifier.offset(x = (-76).dp, y = 0.dp)
                )

                // 3. ~10:45: CALENDAR (Top-Left)
                FloatingCropActionButton(
                    icon = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    ringColor = LushGreen,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onOpenCalendar,
                    modifier = Modifier.offset(x = (-46).dp, y = (-60).dp)
                )

                // 4. ~12:30: RESIZE (Top)
                FloatingCropActionButton(
                    icon = Icons.Default.AspectRatio,
                    contentDescription = "Resize",
                    ringColor = if (isResizeActive) LimeRing else LushGreen,
                    bgColor = if (isResizeActive) LushGreen else DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onResizeCrop,
                    modifier = Modifier.offset(x = 20.dp, y = (-73).dp)
                )

                // 5. ~2:15: COPY (Top-Right)
                FloatingCropActionButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    ringColor = LushGreen,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onCopyCrop,
                    modifier = Modifier.offset(x = 70.dp, y = (-29).dp)
                )

                // 6. 4 O'CLOCK: DELETE (Bottom-Right)
                FloatingCropActionButton(
                    icon = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    ringColor = AlertRed,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.offset(x = 66.dp, y = 38.dp)
                )
            }
        }
    }

    // ── "Are you sure?" Confirmation Dialog for DELETE ───────────────────────
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Are you sure?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepBlack
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this $cropName from the garden?",
                    fontSize = 13.sp,
                    color = Color(0xFF424242)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteCrop()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Keep",
                        color = DeepBlack,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun FloatingCropActionButton(
    icon: ImageVector,
    contentDescription: String,
    ringColor: Color,
    bgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 46.dp,
    iconSizeDp: Dp = 22.dp
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bgColor,
        border = BorderStroke(2.5.dp, ringColor),
        shadowElevation = 8.dp,
        modifier = modifier.size(sizeDp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(iconSizeDp)
            )
        }
    }
}
