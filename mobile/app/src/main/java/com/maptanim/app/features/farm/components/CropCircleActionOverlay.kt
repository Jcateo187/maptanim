package com.maptanim.app.features.farm.components

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.R
import com.maptanim.app.features.farm.renderer.model.cropSinglePlantSpacingM
import java.util.Locale
import kotlin.math.roundToInt

private val LushGreen = Color(0xFF2E7D32)
private val EmeraldBorder = Color(0xFF81C784)
private val LimeRing = Color(0xFFCCFF90)
private val DarkCircleBg = Color(0xFF142917)
private val DeepBlack = Color(0xFF111813)
private val AlertRed = Color(0xFFE53935)
private const val INCH_IN_METERS = 0.0254f

/**
 * Returns high-resolution hero drawable for each of the 13 canonical vegetables.
 */
fun getCropHeroDrawable(cropName: String): Int {
    val lower = cropName.lowercase()
    return when {
        lower.contains("bell pepper") || lower.contains("pepper") || lower.contains("atsal") -> R.drawable.ph_bell_pepper_hero
        lower.contains("ampalaya") || lower.contains("bitter") || lower.contains("gourd") -> R.drawable.ph_ampalaya_hero
        lower.contains("cabbage") || lower.contains("repolyo") -> R.drawable.ph_cabbage_hero
        lower.contains("carrot") || lower.contains("karot") -> R.drawable.ph_carrot_hero
        lower.contains("cucumber") || lower.contains("pipino") -> R.drawable.ph_cucumber_hero
        lower.contains("eggplant") || lower.contains("talong") -> R.drawable.ph_eggplant_hero
        lower.contains("onion") || lower.contains("scallion") || lower.contains("sibuyas") -> R.drawable.ph_green_onion_hero
        lower.contains("lettuce") || lower.contains("litsugas") -> R.drawable.ph_lettuce_hero
        lower.contains("pechay") || lower.contains("petsay") || lower.contains("bok choy") || lower.contains("pak choi") -> R.drawable.ph_pechay_hero
        lower.contains("potato") || lower.contains("patatas") -> R.drawable.ph_potato_hero
        lower.contains("squash") || lower.contains("kalabasa") || lower.contains("pumpkin") -> R.drawable.ph_squash_hero
        lower.contains("string") || lower.contains("sitaw") || lower.contains("beans") || lower.contains("yardlong") -> R.drawable.ph_sitaw_hero
        lower.contains("tomato") || lower.contains("kamatis") -> R.drawable.ph_tomato_hero
        else -> R.drawable.ph_tomato_hero
    }
}

/**
 * CropCircleActionOverlay — 6 floating overlay user actions positioned symmetrically at clock hours:
 * - 12:00: See Info (Icons.Default.Info)
 * - 2:00:  Check Up (Icons.Default.MedicalServices)
 * - 4:00:  Calendar (Icons.Default.CalendarMonth)
 * - 6:00:  Resize (Icons.Default.AspectRatio)
 * - 8:00:  Copy (Icons.Default.ContentCopy)
 * - 10:00: Delete (Icons.Default.DeleteOutline)
 * - Center: Vegetable avatar with inner/outer circular layer and circular name display.
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

    // Spring bounce animation on tap
    var isSpawned by remember { mutableStateOf(false) }
    LaunchedEffect(cropName) {
        isSpawned = false
        isSpawned = true
    }

    val bounceScale by animateFloatAsState(
        targetValue = if (isSpawned) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = 0.52f, // Tactile bouncy spring!
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CropOverlayBounce"
    )

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
                        .padding(bottom = 12.dp)
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

            // ── The 5 Circular Action Buttons & Center Vegetable Display ─────
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .graphicsLayer {
                        scaleX = bounceScale
                        scaleY = bounceScale
                    },
                contentAlignment = Alignment.Center
            ) {
                // ── CENTER: VEGETABLE AVATAR + CIRCULAR NAME + INFO BADGE (Clickable) ─
                CenterVegetableCircleDisplay(
                    cropName = cropName,
                    varietyName = varietyName,
                    onClick = onOpenInfo
                )

                // ── 1. 10:00: CHECK UP (Top-Left) ──────────────────────────────
                FloatingCropActionButton(
                    icon = Icons.Default.MedicalServices,
                    label = "Check Up",
                    contentDescription = "Check Up",
                    ringColor = LimeRing,
                    bgColor = LushGreen,
                    iconTint = Color.White,
                    onClick = onOpenCheckUp,
                    modifier = Modifier.offset(x = (-75).dp, y = (-43).dp)
                )

                // ── 2. 11:45: CALENDAR (Top / 11:45) ───────────────────────────
                FloatingCropActionButton(
                    icon = Icons.Default.CalendarMonth,
                    label = "Calendar",
                    contentDescription = "Calendar",
                    ringColor = LushGreen,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onOpenCalendar,
                    modifier = Modifier.offset(x = (-11).dp, y = (-85).dp)
                )

                // ── 3. 1:30: RESIZE (Top-Right) ────────────────────────────────
                FloatingCropActionButton(
                    icon = Icons.Default.AspectRatio,
                    label = "Resize",
                    contentDescription = "Resize",
                    ringColor = if (isResizeActive) LimeRing else LushGreen,
                    bgColor = if (isResizeActive) LushGreen else DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onResizeCrop,
                    modifier = Modifier.offset(x = 61.dp, y = (-61).dp)
                )

                // ── 4. 3:15: COPY (Middle-Right) ───────────────────────────────
                FloatingCropActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy",
                    contentDescription = "Copy",
                    ringColor = LushGreen,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = onCopyCrop,
                    modifier = Modifier.offset(x = 85.dp, y = 11.dp)
                )

                // ── 5. 5:00: DELETE (Bottom-Right) ─────────────────────────────
                FloatingCropActionButton(
                    icon = Icons.Default.DeleteOutline,
                    label = "Delete",
                    contentDescription = "Delete",
                    ringColor = AlertRed,
                    bgColor = DarkCircleBg,
                    iconTint = Color.White,
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.offset(x = 43.dp, y = 75.dp)
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

/**
 * Center Vegetable Display with inner/outer circular layer and circular name display.
 * The entire center circle is clickable and acts as the "Info" entrypoint.
 */
@Composable
private fun CenterVegetableCircleDisplay(
    cropName: String,
    varietyName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroRes = remember(cropName) { getCropHeroDrawable(cropName) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(80.dp)
            .clickable(onClick = onClick)
    ) {
        // Outer glowing circular track (Green ring layer)
        Surface(
            shape = CircleShape,
            color = Color(0xF2102413),
            border = BorderStroke(2.dp, EmeraldBorder),
            shadowElevation = 10.dp,
            modifier = Modifier.size(80.dp)
        ) {}

        // Circular Name Track drawn in the annular layer between inner white border and outer green border
        Canvas(modifier = Modifier.size(80.dp)) {
            val r = size.minDimension / 2f
            val textR = r * (33f / 40f) // Centered between inner white radius (26dp) and outer green (40dp)
            val path = Path().apply {
                addArc(
                    RectF(r - textR, r - textR, r + textR, r + textR),
                    180f,
                    180f
                )
            }
            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 17f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                letterSpacing = 0.08f
            }
            drawIntoCanvas { canvas ->
                val displayLabel = cropName.uppercase(Locale.US)
                canvas.nativeCanvas.drawTextOnPath(displayLabel, path, 0f, 0f, textPaint)
            }
        }

        // Inner circular layer: High-res vegetable image with white circular border
        Surface(
            shape = CircleShape,
            color = DarkCircleBg,
            border = BorderStroke(1.8.dp, Color.White),
            shadowElevation = 4.dp,
            modifier = Modifier.size(52.dp)
        ) {
            Image(
                painter = painterResource(id = heroRes),
                contentDescription = cropName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        // Center bottom "INFO" badge pill
        Surface(
            shape = CircleShape,
            color = Color(0xEE142917),
            border = BorderStroke(0.8.dp, LimeRing),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = LimeRing,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "INFO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun FloatingCropActionButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    ringColor: Color,
    bgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 44.dp,
    iconSizeDp: Dp = 20.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = bgColor,
            border = BorderStroke(2.dp, ringColor),
            shadowElevation = 8.dp,
            modifier = Modifier.size(sizeDp)
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

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xD90D1C10),
            border = BorderStroke(0.6.dp, ringColor.copy(alpha = 0.75f)),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = label,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}
