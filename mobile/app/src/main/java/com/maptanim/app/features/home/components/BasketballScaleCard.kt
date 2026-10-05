package com.maptanim.app.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.farm.renderer.model.PlotRenderData

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * BasketballScaleCard — Dashboard benchmark card contextualizing farm size against
 * the standard Barangay Basketball Court (28m × 15m = 420 sqm).
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun BasketballScaleCard(
    plots: List<PlotRenderData>,
    modifier: Modifier = Modifier
) {
    val totalAreaSqm = plots.sumOf { (it.widthM * it.heightM).toDouble() }.toFloat()
    val courtAreaSqm = 28f * 15f // 420 sqm
    val percentage = ((totalAreaSqm / courtAreaSqm) * 100f).coerceIn(0f, 100f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsBasketball,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "BARANGAY BASKETBALL BENCHMARK",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF555555)
                    )
                }

                Text(
                    text = String.format("%.1f sqm", totalAreaSqm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LushGreen
                )
            }

            Text(
                text = "Your ${plots.size} garden bed(s) cover ${String.format("%.1f%%", percentage)} of a standard 28m × 15m (420 sqm) basketball court.",
                fontSize = 12.sp,
                color = DeepBlack,
                lineHeight = 16.sp
            )

            LinearProgressIndicator(
                progress = { (percentage / 100f).coerceIn(0.02f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = LushGreen,
                trackColor = Color(0xFFE0E0E0)
            )
        }
    }
}
