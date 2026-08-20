package com.maptanim.app.ui.components.guide

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Direction where the animated pointing hand should point.
 */
enum class PointingDirection {
    DOWN,
    UP,
    RIGHT,
    LEFT
}

@Composable
fun rememberAssetBitmap(assetPath: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(assetPath) {
        try {
            context.assets.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Energetic Zooming & Jumping Spotlight Ring Highlight Effect
 */
@Composable
fun SpotlightPulseRing(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_scale"
    )

    val jumpOffsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_jump"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_alpha"
    )

    Box(
        modifier = modifier
            .offset(y = jumpOffsetY.dp)
            .size(size * scale)
            .shadow(12.dp, CircleShape)
            .border(
                width = 4.dp,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEB3B).copy(alpha = alpha), Color(0xFFFF9800).copy(alpha = alpha))
                ),
                shape = CircleShape
            )
            .background(Color(0xFFFFEB3B).copy(alpha = 0.25f), CircleShape)
    )
}

/**
 * Animated Jumping Pointing Hand Indicator (CoC Tutorial Style)
 */
@Composable
fun PointingHandSprite(
    modifier: Modifier = Modifier,
    direction: PointingDirection = PointingDirection.DOWN,
    label: String? = "CLICK HERE!"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hand_jump")
    val jumpOffsetY by infiniteTransition.animateFloat(
        initialValue = -16f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hand_jump_offset"
    )

    val rotationAngle = when (direction) {
        PointingDirection.DOWN -> 180f
        PointingDirection.UP -> 0f
        PointingDirection.RIGHT -> 90f
        PointingDirection.LEFT -> 270f
    }

    Column(
        modifier = modifier.offset(y = jumpOffsetY.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!label.isNullOrEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFD54F),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF3E2723),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(12.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))
                    ),
                    shape = CircleShape
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = "Hand Pointing",
                tint = Color(0xFF3E2723),
                modifier = Modifier
                    .size(34.dp)
                    .rotate(rotationAngle)
            )
        }
    }
}

/**
 * Old Man Farmer Guide Dialog Box (Clash of Clans Style)
 */
@Composable
fun OldManFarmerGuideOverlay(
    dialogText: String,
    titleText: String = "Tatay Juan (Farm Guide)",
    showSkip: Boolean = true,
    nextButtonText: String? = null,
    secondaryButtonText: String? = null,
    dialogAlignment: Alignment = Alignment.TopCenter,
    compactMode: Boolean = false,
    scrimAlpha: Float = 0.0f,
    onSkip: () -> Unit = {},
    onNext: (() -> Unit)? = null,
    onSecondaryClick: (() -> Unit)? = null,
    pointingHandTarget: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val grandfatherBitmap = rememberAssetBitmap("Avatar/Grandfather_Avatar.png")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (scrimAlpha > 0f) {
                    Modifier.background(Color.Black.copy(alpha = scrimAlpha))
                } else Modifier
            )
    ) {
        // Pointing Hand Anchor Layer if provided
        pointingHandTarget?.let {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                it()
            }
        }

        // CoC-Style Speech Card Dialog
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            contentAlignment = dialogAlignment
        ) {
            Surface(
                modifier = modifier
                    .then(
                        if (compactMode) Modifier.widthIn(max = 350.dp) else Modifier.fillMaxWidth()
                    )
                    .shadow(12.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFF8E7), // Warm parchment yellow
                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF5D4037)) // Dark wood border
            ) {
                Row(
                    modifier = Modifier
                        .then(
                            if (compactMode) Modifier.wrapContentWidth() else Modifier.fillMaxWidth()
                        )
                        .padding(if (compactMode) 10.dp else 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Old Man Farmer Avatar Box
                    Box(
                        modifier = Modifier
                            .size(if (compactMode) 52.dp else 80.dp)
                            .shadow(6.dp, CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (grandfatherBitmap != null) {
                            Image(
                                bitmap = grandfatherBitmap,
                                contentDescription = "Old Man Farmer Guide",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = "👴",
                                fontSize = if (compactMode) 26.sp else 40.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Speech Bubble & Controls
                    Column(
                        modifier = Modifier.weight(1f, fill = !compactMode)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = titleText,
                                fontSize = if (compactMode) 13.sp else 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )

                            if (showSkip) {
                                TextButton(
                                    onClick = onSkip,
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Skip",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8D6E63)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = dialogText,
                            fontSize = if (compactMode) 12.sp else 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3E2723),
                            lineHeight = if (compactMode) 15.sp else 17.sp
                        )

                        if ((onNext != null && nextButtonText != null) || (onSecondaryClick != null && secondaryButtonText != null)) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (onSecondaryClick != null && secondaryButtonText != null) {
                                    OutlinedButton(
                                        onClick = onSecondaryClick,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF795548)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFF5D4037)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = secondaryButtonText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (onNext != null && nextButtonText != null) {
                                    Button(
                                        onClick = onNext,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50),
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = ButtonDefaults.buttonElevation(4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = nextButtonText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Next",
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
