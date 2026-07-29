package com.maptanim.app.ui.components.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.maptanim.app.ui.theme.Charcoal
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.NightBlue
import com.maptanim.app.ui.theme.SlateBlue
import com.maptanim.app.ui.theme.Sunlight

/**
 * HomeBackground — Ambient gradient background for 2D Isometric Map canvas.
 */
@Composable
fun HomeBackground() {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NightBlue,
                        SlateBlue,
                        ForestGreen,
                        Charcoal
                    )
                )
            )
    ) {

        // Sunlight glow (top-right)
        drawCircle(
            color = Sunlight.copy(alpha = 0.18f),
            radius = size.minDimension * 0.32f,
            center = Offset(
                x = size.width * 0.92f,
                y = size.height * 0.08f
            )
        )

        // Green ambient glow (bottom-left)
        drawCircle(
            color = ForestGreen.copy(alpha = 0.15f),
            radius = size.minDimension * 0.42f,
            center = Offset(
                x = size.width * 0.10f,
                y = size.height * 0.85f
            )
        )

        // Soft blue glow (center)
        drawCircle(
            color = SlateBlue.copy(alpha = 0.10f),
            radius = size.minDimension * 0.28f,
            center = Offset(
                x = size.width * 0.50f,
                y = size.height * 0.40f
            )
        )
    }
}