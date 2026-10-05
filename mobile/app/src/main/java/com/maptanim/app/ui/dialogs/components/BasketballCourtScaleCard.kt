package com.maptanim.app.ui.dialogs.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.ui.theme.White

/**
 * BasketballCourtScaleCard — Real-world physical scale benchmark for Philippine growers.
 * Uses a standard FIBA/Barangay basketball court (28m x 15m = 420 sqm) as a familiar
 * visual reference so beginners immediately understand how large their garden area is.
 */
@Composable
fun BasketballCourtScaleCard(
    widthM: Float,
    heightM: Float,
    cropName: String,
    plotLabel: String,
    modifier: Modifier = Modifier
) {
    val plotAreaSqm = (widthM * heightM).coerceAtLeast(0.1f)
    val courtWidthM = 28f
    val courtHeightM = 15f
    val courtAreaSqm = courtWidthM * courtHeightM // 420 sqm
    val courtPercentage = (plotAreaSqm / courtAreaSqm * 100f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141E16)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E4032))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🏀",
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Real-World Size Benchmark",
                        color = Color(0xFFA5D6A7),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF223627)
                ) {
                    Text(
                        text = "${String.format("%.1f", plotAreaSqm)} m² (${String.format("%.1f", widthM)}m × ${String.format("%.1f", heightM)}m)",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual Basketball Court Comparison Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(Color(0xFF0F1711), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF1F2E22), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasW = size.width
                    val canvasH = size.height

                    // Draw basketball court boundary (FIBA aspect ratio 28:15)
                    val courtAspect = 28f / 15f
                    val drawCourtW: Float
                    val drawCourtH: Float
                    if (canvasW / canvasH > courtAspect) {
                        drawCourtH = canvasH
                        drawCourtW = canvasH * courtAspect
                    } else {
                        drawCourtW = canvasW
                        drawCourtH = canvasW / courtAspect
                    }

                    val courtLeft = (canvasW - drawCourtW) / 2f
                    val courtTop = (canvasH - drawCourtH) / 2f

                    // Court floor
                    drawRoundRect(
                        color = Color(0xFF1E2F23),
                        topLeft = Offset(courtLeft, courtTop),
                        size = Size(drawCourtW, drawCourtH),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Court markings (lines)
                    val lineStroke = Stroke(width = 1.5f)
                    drawRoundRect(
                        color = Color(0xFF3B5640),
                        topLeft = Offset(courtLeft, courtTop),
                        size = Size(drawCourtW, drawCourtH),
                        cornerRadius = CornerRadius(4f, 4f),
                        style = lineStroke
                    )

                    // Half-court line
                    val midX = courtLeft + drawCourtW / 2f
                    drawLine(
                        color = Color(0xFF3B5640),
                        start = Offset(midX, courtTop),
                        end = Offset(midX, courtTop + drawCourtH),
                        strokeWidth = 1.5f
                    )

                    // Center circle
                    drawCircle(
                        color = Color(0xFF3B5640),
                        radius = drawCourtH * 0.18f,
                        center = Offset(midX, courtTop + drawCourtH / 2f),
                        style = lineStroke
                    )

                    // Draw user's bed/plot scaled proportionally to court
                    val scaleX = drawCourtW / courtWidthM
                    val scaleY = drawCourtH / courtHeightM

                    val plotW = (widthM * scaleX).coerceIn(4f, drawCourtW)
                    val plotH = (heightM * scaleY).coerceIn(4f, drawCourtH)

                    val plotX = courtLeft + 6f
                    val plotY = courtTop + (drawCourtH - plotH) / 2f

                    // Highlight plot in vibrant green
                    drawRoundRect(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(plotX, plotY),
                        size = Size(plotW, plotH),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Dotted outline for user's plot
                    drawRoundRect(
                        color = Color(0xFFC8E6C9),
                        topLeft = Offset(plotX, plotY),
                        size = Size(plotW, plotH),
                        cornerRadius = CornerRadius(3f, 3f),
                        style = Stroke(
                            width = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Explanation text
            val scaleComparison = when {
                courtPercentage < 1f -> "About ${String.format("%.2f", courtPercentage)}% of a full barangay basketball court."
                courtPercentage < 10f -> "Takes up about ${String.format("%.1f", courtPercentage)}% of a standard basketball court."
                courtPercentage < 50f -> "About ${String.format("%.1f", courtPercentage)}% (roughly ${if (courtPercentage < 30f) "a quarter" else "half"} of a court)."
                courtPercentage <= 100f -> "Equal to about ${String.format("%.1f", courtPercentage)}% of a full basketball court!"
                else -> "Larger than a full basketball court (${String.format("%.1f", courtPercentage / 100f)}× courts)!"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Visual Scale: $scaleComparison",
                    color = Color(0xFFB0C4B1),
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
