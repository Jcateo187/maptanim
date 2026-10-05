package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * Multi-tier height expansion state for the Farm Hub bottom workspace drawer.
 * Supports complete hiding (HIDDEN) so the 2D Canvas has 100% full-screen view.
 */
enum class SheetExpandState {
    HIDDEN,
    PEEK,
    HALF,
    FULL
}

/**
 * FarmDrawerDragBar — Tactile draggable handle and quick action toggles
 * allowing the user to expand or collapse the bottom workspace drawer.
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White, Lush Green, Deep Black).
 */
@Composable
fun FarmDrawerDragBar(
    sheetState: SheetExpandState,
    onSetState: (SheetExpandState) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sheetState == SheetExpandState.HIDDEN) {
        // Subtle floating pill when hidden to restore drawer
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = { onSetState(SheetExpandState.HALF) },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Show Workspace",
                        tint = LushGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Show Guide & Plan",
                        color = DeepBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    if (delta < -12f) {
                        // Dragging UP -> expand towards FULL
                        when (sheetState) {
                            SheetExpandState.HIDDEN -> onSetState(SheetExpandState.PEEK)
                            SheetExpandState.PEEK -> onSetState(SheetExpandState.HALF)
                            SheetExpandState.HALF -> onSetState(SheetExpandState.FULL)
                            SheetExpandState.FULL -> {}
                        }
                    } else if (delta > 12f) {
                        // Dragging DOWN -> collapse towards HIDDEN
                        when (sheetState) {
                            SheetExpandState.FULL -> onSetState(SheetExpandState.HALF)
                            SheetExpandState.HALF -> onSetState(SheetExpandState.PEEK)
                            SheetExpandState.PEEK -> onSetState(SheetExpandState.HIDDEN)
                            SheetExpandState.HIDDEN -> {}
                        }
                    }
                }
            )
            .clickable {
                when (sheetState) {
                    SheetExpandState.HIDDEN -> onSetState(SheetExpandState.HALF)
                    SheetExpandState.PEEK -> onSetState(SheetExpandState.HALF)
                    SheetExpandState.HALF -> onSetState(SheetExpandState.FULL)
                    SheetExpandState.FULL -> onSetState(SheetExpandState.HALF)
                }
            },
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tactile Drag Indicator Pill & State Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .background(Color(0xFF9E9E9E), RoundedCornerShape(2.dp))
                )
                Text(
                    text = when (sheetState) {
                        SheetExpandState.HIDDEN -> "Canvas 100%"
                        SheetExpandState.PEEK -> "Peek Mode"
                        SheetExpandState.HALF -> "Split View"
                        SheetExpandState.FULL -> "Full Form"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
            }

            // Quick State Toggle Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (sheetState != SheetExpandState.FULL) {
                    Surface(
                        onClick = { onSetState(SheetExpandState.FULL) },
                        shape = RoundedCornerShape(6.dp),
                        color = LushGreen.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = LushGreen, modifier = Modifier.size(16.dp))
                            Text("Full Guide", color = LushGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Surface(
                    onClick = {
                        when (sheetState) {
                            SheetExpandState.FULL -> onSetState(SheetExpandState.HALF)
                            SheetExpandState.HALF -> onSetState(SheetExpandState.HIDDEN)
                            SheetExpandState.PEEK -> onSetState(SheetExpandState.HIDDEN)
                            SheetExpandState.HIDDEN -> onSetState(SheetExpandState.HALF)
                        }
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = DeepBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (sheetState == SheetExpandState.FULL) "Half View" else "Hide (100% Map)",
                            color = DeepBlack,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
