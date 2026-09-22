package com.maptanim.app.ui.screens.knowledgebase

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual Bed, Furrow, and Subsoil Depth Diagram with Centimeter/Meter (CM/M) Ruler Callouts.
 * Inspired by modern agricultural engineering & gardening visual guides.
 */
@Composable
fun FurrowBedVisualGuide(
    modifier: Modifier = Modifier,
    plantSpacingCm: String = "30–50 cm",
    deepDigCm: String = "20–30 cm",
    bedHeightCm: String = "15–20 cm",
    bedWidthM: String = "1.0 Metro"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF132319))
            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📐 Sukat ng Kama at Lalim ng Hukay (Bed Dimensions)",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "DA-BPI Gabay",
                    color = Color(0xFFA5D6A7),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Canvas drawing the cross-section with ruler dimensions
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val w = size.width
                val h = size.height

                val groundY = h * 0.46f
                val bedTopY = h * 0.22f
                val bedLeft = w * 0.18f
                val bedRight = w * 0.82f
                val subsoilBottomY = h * 0.88f

                // 1. Draw Subsoil Deep Dig Area (Hatched/Dotted background)
                val subsoilPath = Path().apply {
                    moveTo(bedLeft, groundY)
                    lineTo(bedRight, groundY)
                    lineTo(bedRight, subsoilBottomY)
                    lineTo(bedLeft, subsoilBottomY)
                    close()
                }
                drawPath(
                    path = subsoilPath,
                    color = Color(0xFF1C2C20)
                )

                // Subsoil dash outline
                drawPath(
                    path = subsoilPath,
                    color = Color(0xFF81C784).copy(alpha = 0.4f),
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )

                // 2. Draw Raised Bed Mound (Kama) with organic topsoil gradient
                val bedPath = Path().apply {
                    moveTo(bedLeft - 15f, groundY)
                    lineTo(bedLeft + 15f, bedTopY)
                    lineTo(bedRight - 15f, bedTopY)
                    lineTo(bedRight + 15f, groundY)
                    close()
                }
                drawPath(
                    path = bedPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E4D36), Color(0xFF1E3524)),
                        startY = bedTopY,
                        endY = groundY
                    )
                )
                drawPath(
                    path = bedPath,
                    color = Color(0xFF81C784),
                    style = Stroke(width = 2f)
                )

                // 3. Ground line (Surface Level)
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(0f, groundY),
                    end = Offset(w, groundY),
                    strokeWidth = 1.5f
                )

                // 4. Two seedlings on top of the bed
                val p1X = bedLeft + (bedRight - bedLeft) * 0.30f
                val p2X = bedLeft + (bedRight - bedLeft) * 0.70f
                drawSeedling(Offset(p1X, bedTopY))
                drawSeedling(Offset(p2X, bedTopY))

                // 5. Dimension Arrow: Plant Spacing (Top)
                drawDimensionLine(
                    start = Offset(p1X, bedTopY - 14f),
                    end = Offset(p2X, bedTopY - 14f),
                    color = Color(0xFFFFD54F)
                )

                // 6. Dimension Arrow: Bed Width (Middle)
                drawDimensionLine(
                    start = Offset(bedLeft, groundY - 6f),
                    end = Offset(bedRight, groundY - 6f),
                    color = Color(0xFF81C784)
                )

                // 7. Dimension Arrow: Bed Height (Right side)
                drawVerticalDimensionLine(
                    top = Offset(bedRight + 20f, bedTopY),
                    bottom = Offset(bedRight + 20f, groundY),
                    color = Color(0xFF4FC3F7)
                )

                // 8. Dimension Arrow: Deep Dig Subsoil (Left side)
                drawVerticalDimensionLine(
                    top = Offset(bedLeft - 22f, groundY),
                    bottom = Offset(bedLeft - 22f, subsoilBottomY),
                    color = Color(0xFFFFB74D)
                )
            }

            // Legend Callouts below canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalloutTag("↔️ Lapad ng Kama:", bedWidthM, Color(0xFF81C784))
                CalloutTag("↕️ Taas ng Kama:", bedHeightCm, Color(0xFF4FC3F7))
                CalloutTag("⬇️ Deep Dig:", deepDigCm, Color(0xFFFFB74D))
                CalloutTag("🌱 Pagitan:", plantSpacingCm, Color(0xFFFFD54F))
            }
        }
    }
}

/**
 * Visual Trellis Structures Diagram (Balag Guide).
 * Illustrates Tulos (Single Stake), A-Frame, and Overhead Pergola with meter height guides.
 */
@Composable
fun TrellisVisualGuide(
    modifier: Modifier = Modifier,
    activeTrellisType: String = "A_FRAME",
    cropName: String = "Pananim"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF132319))
            .border(1.dp, Color(0xFF795548).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🪵 Mga Uri ng Balag (Trellis Types & Height)",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Taas at Gamit",
                    color = Color(0xFFA5D6A7),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TrellisCard(
                    title = "1. TULOS (Stake)",
                    heightStr = "Taas: 1.5 Metro",
                    cropsStr = "Kamatis, Talong",
                    isSelected = activeTrellisType.contains("TULOS", ignoreCase = true),
                    modifier = Modifier.weight(1f)
                ) {
                    Canvas(modifier = Modifier.size(60.dp, 55.dp)) {
                        val midX = size.width / 2f
                        val bottomY = size.height * 0.90f
                        val topY = size.height * 0.10f
                        // Main bamboo stake
                        drawLine(Color(0xFF8D6E63), Offset(midX, bottomY), Offset(midX, topY), strokeWidth = 4f)
                        // Vine tying loops
                        drawCircle(Color(0xFF81C784), radius = 5f, center = Offset(midX, topY + 14f), style = Stroke(1.5f))
                        drawCircle(Color(0xFF81C784), radius = 6f, center = Offset(midX, topY + 28f), style = Stroke(1.5f))
                        // Ground line
                        drawLine(Color.White.copy(alpha = 0.3f), Offset(10f, bottomY), Offset(size.width - 10f, bottomY), strokeWidth = 2f)
                    }
                }

                TrellisCard(
                    title = "2. A-FRAME (Tatsulok)",
                    heightStr = "Taas: 1.8 Metro",
                    cropsStr = "Sitaw, Pipino",
                    isSelected = activeTrellisType.contains("A_FRAME", ignoreCase = true) || activeTrellisType.isEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Canvas(modifier = Modifier.size(60.dp, 55.dp)) {
                        val bottomY = size.height * 0.90f
                        val topY = size.height * 0.10f
                        val leftX = size.width * 0.20f
                        val rightX = size.width * 0.80f
                        val apexX = size.width / 2f

                        // A frame poles
                        drawLine(Color(0xFF8D6E63), Offset(leftX, bottomY), Offset(apexX, topY), strokeWidth = 3f)
                        drawLine(Color(0xFF8D6E63), Offset(rightX, bottomY), Offset(apexX, topY), strokeWidth = 3f)
                        // Crossbar
                        val crossY = bottomY * 0.60f
                        drawLine(Color(0xFFBCAAA4), Offset(leftX + 8f, crossY), Offset(rightX - 8f, crossY), strokeWidth = 2f)
                        // Netting grid lines
                        drawLine(Color(0xFF81C784).copy(alpha = 0.5f), Offset(apexX, topY + 12f), Offset(leftX + 15f, bottomY), strokeWidth = 1f)
                        drawLine(Color(0xFF81C784).copy(alpha = 0.5f), Offset(apexX, topY + 12f), Offset(rightX - 15f, bottomY), strokeWidth = 1f)
                        // Ground
                        drawLine(Color.White.copy(alpha = 0.3f), Offset(5f, bottomY), Offset(size.width - 5f, bottomY), strokeWidth = 2f)
                    }
                }

                TrellisCard(
                    title = "3. OVERHEAD (Balandra)",
                    heightStr = "Taas: 2.0 Metro",
                    cropsStr = "Ampalaya, Upo",
                    isSelected = activeTrellisType.contains("OVERHEAD", ignoreCase = true),
                    modifier = Modifier.weight(1f)
                ) {
                    Canvas(modifier = Modifier.size(60.dp, 55.dp)) {
                        val bottomY = size.height * 0.90f
                        val roofY = size.height * 0.20f
                        val leftX = size.width * 0.20f
                        val rightX = size.width * 0.80f

                        // Vertical posts
                        drawLine(Color(0xFF8D6E63), Offset(leftX, bottomY), Offset(leftX, roofY), strokeWidth = 3f)
                        drawLine(Color(0xFF8D6E63), Offset(rightX, bottomY), Offset(rightX, roofY), strokeWidth = 3f)
                        // Overhead canopy grid
                        drawLine(Color(0xFFBCAAA4), Offset(leftX - 6f, roofY), Offset(rightX + 6f, roofY), strokeWidth = 3f)
                        // Netting squares
                        drawLine(Color(0xFF81C784).copy(alpha = 0.6f), Offset(leftX + 10f, roofY - 4f), Offset(leftX + 10f, roofY + 4f), strokeWidth = 1.5f)
                        drawLine(Color(0xFF81C784).copy(alpha = 0.6f), Offset(rightX - 10f, roofY - 4f), Offset(rightX - 10f, roofY + 4f), strokeWidth = 1.5f)
                        // Ground
                        drawLine(Color.White.copy(alpha = 0.3f), Offset(5f, bottomY), Offset(size.width - 5f, bottomY), strokeWidth = 2f)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrellisCard(
    title: String,
    heightStr: String,
    cropsStr: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    drawing: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF233B2B) else Color(0xFF16251C))
            .border(
                1.dp,
                if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = if (isSelected) Color(0xFFFFD54F) else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            drawing()
            Text(
                text = heightStr,
                color = Color(0xFFA5D6A7),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = cropsStr,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun CalloutTag(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.65f), fontSize = 9.sp)
        Text(text = value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

private fun DrawScope.drawSeedling(pos: Offset) {
    // Stem
    drawLine(
        color = Color(0xFF81C784),
        start = pos,
        end = Offset(pos.x, pos.y - 12f),
        strokeWidth = 2f
    )
    // Left Leaf
    val leftLeaf = Path().apply {
        moveTo(pos.x, pos.y - 10f)
        cubicTo(pos.x - 6f, pos.y - 14f, pos.x - 8f, pos.y - 6f, pos.x, pos.y - 8f)
    }
    drawPath(leftLeaf, Color(0xFF4CAF50))
    // Right Leaf
    val rightLeaf = Path().apply {
        moveTo(pos.x, pos.y - 10f)
        cubicTo(pos.x + 6f, pos.y - 14f, pos.x + 8f, pos.y - 6f, pos.x, pos.y - 8f)
    }
    drawPath(rightLeaf, Color(0xFF66BB6A))
}

private fun DrawScope.drawDimensionLine(start: Offset, end: Offset, color: Color) {
    drawLine(color, start, end, strokeWidth = 1.5f)
    // Left Tick
    drawLine(color, Offset(start.x, start.y - 3f), Offset(start.x, start.y + 3f), strokeWidth = 1.5f)
    // Right Tick
    drawLine(color, Offset(end.x, end.y - 3f), Offset(end.x, end.y + 3f), strokeWidth = 1.5f)
}

private fun DrawScope.drawVerticalDimensionLine(top: Offset, bottom: Offset, color: Color) {
    drawLine(color, top, bottom, strokeWidth = 1.5f)
    // Top Tick
    drawLine(color, Offset(top.x - 3f, top.y), Offset(top.x + 3f, top.y), strokeWidth = 1.5f)
    // Bottom Tick
    drawLine(color, Offset(bottom.x - 3f, bottom.y), Offset(bottom.x + 3f, bottom.y), strokeWidth = 1.5f)
}
