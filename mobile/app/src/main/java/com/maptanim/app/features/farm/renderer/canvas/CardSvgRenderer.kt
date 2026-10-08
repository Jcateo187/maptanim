package com.maptanim.app.features.farm.renderer.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * CardSvgRenderer — High-performance vector/SVG renderer for Farm & Encyclopedia cards.
 * Provides custom, crisp, scalable vector illustrations for:
 * 1. Quick Info cards (spacing, depth, sun, water, season, flood protection, height, germ, temp, harvest, pH, seedling)
 * 2. Nutrition badges (Lycopene, Vit C, Vit K, Potassium, Folate B9, Vit A, Iron)
 * 3. Beneficial critters (Honeybee, Ladybug, Praying Mantis, Spider, Earthworm, Hoverfly)
 * 4. Pests & Plant Diseases (Fruit borers, Aphids, Blight, Mildew)
 * 5. Agronomic support badges (DSS recommendation, All-Clear protective shield)
 *
 * All drawings are rendered natively via DrawScope without raster pixelation or external SVG assets.
 */
object CardSvgRenderer {

    // ═══════════════════════════════════════════════════════════════════════════
    // 1. QUICK INFO CARD SVG DRAWINGS
    // ═══════════════════════════════════════════════════════════════════════════

    fun drawQuickInfoSvg(
        drawScope: DrawScope,
        key: String,
        center: Offset,
        sizePx: Float,
        isAlert: Boolean = false
    ) {
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        when (key.lowercase().trim()) {
            "spacing" -> drawSpacingGridSvg(drawScope, cx, cy, r)
            "depth" -> drawPlantingDepthSvg(drawScope, cx, cy, r)
            "sun" -> drawRadiantSunSvg(drawScope, cx, cy, r)
            "water" -> drawWaterDropletSvg(drawScope, cx, cy, r)
            "season" -> drawSeasonCycleSvg(drawScope, cx, cy, r)
            "frost" -> drawFloodProtectionSvg(drawScope, cx, cy, r, isAlert)
            "height" -> drawPlantHeightSvg(drawScope, cx, cy, r)
            "germ" -> drawGerminationSproutSvg(drawScope, cx, cy, r)
            "germ_temp" -> drawThermometerSvg(drawScope, cx, cy, r)
            "harvest" -> drawHarvestBasketSvg(drawScope, cx, cy, r)
            "ph" -> drawPhFlaskSvg(drawScope, cx, cy, r)
            "seedling" -> drawSeedlingPlugSvg(drawScope, cx, cy, r)
            else -> drawGenericInfoLeafSvg(drawScope, cx, cy, r)
        }
    }

    // 1. Spacing: Garden bed row with two plants and a double-headed measurement arrow
    private fun drawSpacingGridSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val soilColor = Color(0xFF795548)
        val plantGreen = Color(0xFF2E7D32)
        val arrowColor = Color(0xFFE65100)

        // Baseline soil line
        ds.drawLine(
            color = soilColor,
            start = Offset(cx - r * 0.85f, cy + r * 0.45f),
            end = Offset(cx + r * 0.85f, cy + r * 0.45f),
            strokeWidth = (r * 0.12f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )

        // Left plant sprout
        val leftX = cx - r * 0.48f
        val plantY = cy + r * 0.45f
        ds.drawCircle(plantGreen, radius = r * 0.22f, center = Offset(leftX, plantY - r * 0.28f))
        ds.drawLine(Color(0xFF1B5E20), Offset(leftX, plantY), Offset(leftX, plantY - r * 0.28f), strokeWidth = r * 0.08f)

        // Right plant sprout
        val rightX = cx + r * 0.48f
        ds.drawCircle(plantGreen, radius = r * 0.22f, center = Offset(rightX, plantY - r * 0.28f))
        ds.drawLine(Color(0xFF1B5E20), Offset(rightX, plantY), Offset(rightX, plantY - r * 0.28f), strokeWidth = r * 0.08f)

        // Dimension arrow line between plants (above soil)
        val arrowY = cy - r * 0.4f
        ds.drawLine(
            color = arrowColor,
            start = Offset(leftX, arrowY),
            end = Offset(rightX, arrowY),
            strokeWidth = (r * 0.1f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )

        // Left arrowhead
        val arrowHeadLeft = Path().apply {
            moveTo(leftX, arrowY)
            lineTo(leftX + r * 0.18f, arrowY - r * 0.14f)
            lineTo(leftX + r * 0.18f, arrowY + r * 0.14f)
            close()
        }
        ds.drawPath(arrowHeadLeft, arrowColor)

        // Right arrowhead
        val arrowHeadRight = Path().apply {
            moveTo(rightX, arrowY)
            lineTo(rightX - r * 0.18f, arrowY - r * 0.14f)
            lineTo(rightX - r * 0.18f, arrowY + r * 0.14f)
            close()
        }
        ds.drawPath(arrowHeadRight, arrowColor)

        // Tick marks at the plant positions
        ds.drawLine(arrowColor, Offset(leftX, arrowY - r * 0.2f), Offset(leftX, arrowY + r * 0.2f), strokeWidth = r * 0.08f)
        ds.drawLine(arrowColor, Offset(rightX, arrowY - r * 0.2f), Offset(rightX, arrowY + r * 0.2f), strokeWidth = r * 0.08f)
    }

    // 2. Depth: Soil cutaway with seed buried underground and downward depth caliper
    private fun drawPlantingDepthSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val groundY = cy - r * 0.2f

        // Underground soil block
        val soilPath = Path().apply {
            moveTo(cx - r * 0.85f, groundY)
            lineTo(cx + r * 0.85f, groundY)
            lineTo(cx + r * 0.85f, cy + r * 0.85f)
            lineTo(cx - r * 0.85f, cy + r * 0.85f)
            close()
        }
        ds.drawPath(soilPath, Color(0xFFD7CCC8))

        // Surface grass line
        ds.drawLine(
            color = Color(0xFF43A047),
            start = Offset(cx - r * 0.85f, groundY),
            end = Offset(cx + r * 0.85f, groundY),
            strokeWidth = (r * 0.14f).coerceAtLeast(2f),
            cap = StrokeCap.Round
        )

        // Buried seed
        val seedX = cx + r * 0.15f
        val seedY = cy + r * 0.45f
        ds.drawOval(
            color = Color(0xFF6D4C41),
            topLeft = Offset(seedX - r * 0.18f, seedY - r * 0.14f),
            size = Size(r * 0.36f, r * 0.28f)
        )
        ds.drawCircle(Color(0xFFFFB300), radius = r * 0.06f, center = Offset(seedX, seedY))

        // Downward depth arrow on left
        val arrowX = cx - r * 0.35f
        ds.drawLine(
            color = Color(0xFFE65100),
            start = Offset(arrowX, groundY),
            end = Offset(arrowX, seedY),
            strokeWidth = (r * 0.1f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )

        // Downward arrowhead pointing to seed depth
        val downArrow = Path().apply {
            moveTo(arrowX, seedY)
            lineTo(arrowX - r * 0.12f, seedY - r * 0.16f)
            lineTo(arrowX + r * 0.12f, seedY - r * 0.16f)
            close()
        }
        ds.drawPath(downArrow, Color(0xFFE65100))

        // Top horizontal depth marker
        ds.drawLine(Color(0xFFE65100), Offset(arrowX - r * 0.12f, groundY), Offset(arrowX + r * 0.12f, groundY), strokeWidth = r * 0.08f)
    }

    // 3. Sun: Radiant tropical sun with core disc and golden geometric rays
    private fun drawRadiantSunSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val sunCenter = Offset(cx, cy)
        val coreRadius = r * 0.42f

        // Outer glow corona
        ds.drawCircle(
            color = Color(0xFFFFF8E1),
            radius = r * 0.88f,
            center = sunCenter
        )

        // 8 Radiating triangular rays
        val rayColor = Color(0xFFFFA000)
        val rayLength = r * 0.82f
        val angles = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)

        for (angleDeg in angles) {
            val rad = Math.toRadians(angleDeg)
            val radLeft = Math.toRadians(angleDeg - 12.0)
            val radRight = Math.toRadians(angleDeg + 12.0)

            val tipX = cx + (rayLength * Math.cos(rad)).toFloat()
            val tipY = cy + (rayLength * Math.sin(rad)).toFloat()
            val base1X = cx + (coreRadius * Math.cos(radLeft)).toFloat()
            val base1Y = cy + (coreRadius * Math.sin(radLeft)).toFloat()
            val base2X = cx + (coreRadius * Math.cos(radRight)).toFloat()
            val base2Y = cy + (coreRadius * Math.sin(radRight)).toFloat()

            val rayPath = Path().apply {
                moveTo(base1X, base1Y)
                lineTo(tipX, tipY)
                lineTo(base2X, base2Y)
                close()
            }
            ds.drawPath(rayPath, rayColor)
        }

        // Inner glowing core
        ds.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEE58), Color(0xFFFF8F00)),
                center = sunCenter,
                radius = coreRadius
            ),
            radius = coreRadius,
            center = sunCenter
        )

        // Core shine highlight
        ds.drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = coreRadius * 0.35f,
            center = Offset(cx - coreRadius * 0.25f, cy - coreRadius * 0.25f)
        )
    }

    // 4. Water: Crystal blue water droplet with highlight shine and hydration ripples
    private fun drawWaterDropletSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val dropColor = Color(0xFF1E88E5)
        val rippleColor = Color(0xFF90CAF9)

        // Concentric hydration ripples at bottom
        ds.drawOval(
            color = rippleColor.copy(alpha = 0.5f),
            topLeft = Offset(cx - r * 0.75f, cy + r * 0.48f),
            size = Size(r * 1.5f, r * 0.32f),
            style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f))
        )
        ds.drawOval(
            color = rippleColor,
            topLeft = Offset(cx - r * 0.5f, cy + r * 0.54f),
            size = Size(r * 1.0f, r * 0.22f),
            style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f))
        )

        // Teardrop droplet path
        val dropPath = Path().apply {
            moveTo(cx, cy - r * 0.85f)
            cubicTo(
                cx + r * 0.55f, cy - r * 0.15f,
                cx + r * 0.55f, cy + r * 0.52f,
                cx, cy + r * 0.58f
            )
            cubicTo(
                cx - r * 0.55f, cy + r * 0.52f,
                cx - r * 0.55f, cy - r * 0.15f,
                cx, cy - r * 0.85f
            )
            close()
        }
        ds.drawPath(
            path = dropPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)),
                startY = cy - r * 0.85f,
                endY = cy + r * 0.58f
            )
        )

        // Internal glass specular shine highlight
        val shinePath = Path().apply {
            moveTo(cx - r * 0.22f, cy - r * 0.35f)
            cubicTo(
                cx - r * 0.38f, cy - r * 0.05f,
                cx - r * 0.38f, cy + r * 0.25f,
                cx - r * 0.22f, cy + r * 0.38f
            )
            cubicTo(
                cx - r * 0.3f, cy + r * 0.25f,
                cx - r * 0.3f, cy - r * 0.05f,
                cx - r * 0.22f, cy - r * 0.35f
            )
            close()
        }
        ds.drawPath(shinePath, Color.White.copy(alpha = 0.75f))
    }

    // 5. Season: Tropical calendar vector (Sun meets Monsoon rain-bands)
    private fun drawSeasonCycleSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Upper left: Golden sun arch (Dry season / Tag-araw)
        val sunCenter = Offset(cx - r * 0.25f, cy - r * 0.25f)
        ds.drawCircle(Color(0xFFFFA000), radius = r * 0.36f, center = sunCenter)

        // Sun rays
        ds.drawLine(Color(0xFFFFB300), Offset(sunCenter.x, sunCenter.y - r * 0.5f), Offset(sunCenter.x, sunCenter.y - r * 0.38f), strokeWidth = r * 0.09f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFFFB300), Offset(sunCenter.x - r * 0.5f, sunCenter.y), Offset(sunCenter.x - r * 0.38f, sunCenter.y), strokeWidth = r * 0.09f, cap = StrokeCap.Round)

        // Lower right: Monsoon Cloud (Wet season / Tag-ulan)
        val cloudColor = Color(0xFF64B5F6)
        val cloudBaseY = cy + r * 0.22f
        val cloudX = cx + r * 0.2f

        ds.drawCircle(cloudColor, radius = r * 0.28f, center = Offset(cloudX, cloudBaseY - r * 0.12f))
        ds.drawCircle(cloudColor, radius = r * 0.22f, center = Offset(cloudX - r * 0.25f, cloudBaseY))
        ds.drawCircle(cloudColor, radius = r * 0.2f, center = Offset(cloudX + r * 0.25f, cloudBaseY))

        // Raindrop streaks falling from cloud
        val rainColor = Color(0xFF1976D2)
        val rainStroke = (r * 0.08f).coerceAtLeast(1.2f)
        ds.drawLine(rainColor, Offset(cloudX - r * 0.2f, cloudBaseY + r * 0.18f), Offset(cloudX - r * 0.28f, cloudBaseY + r * 0.45f), strokeWidth = rainStroke, cap = StrokeCap.Round)
        ds.drawLine(rainColor, Offset(cloudX, cloudBaseY + r * 0.18f), Offset(cloudX - r * 0.08f, cloudBaseY + r * 0.45f), strokeWidth = rainStroke, cap = StrokeCap.Round)
        ds.drawLine(rainColor, Offset(cloudX + r * 0.2f, cloudBaseY + r * 0.18f), Offset(cloudX + r * 0.12f, cloudBaseY + r * 0.45f), strokeWidth = rainStroke, cap = StrokeCap.Round)
    }

    // 6. Flood Protection / Frost Advisory: Raised garden bed with water drainage channel (Modern agronomic vector instead of warning sign!)
    private fun drawFloodProtectionSvg(ds: DrawScope, cx: Float, cy: Float, r: Float, isAlert: Boolean) {
        val accentColor = if (isAlert) Color(0xFFE65100) else Color(0xFF1B5E20)

        // Protective shield outline in background
        val shieldPath = Path().apply {
            moveTo(cx, cy - r * 0.85f)
            lineTo(cx + r * 0.72f, cy - r * 0.5f)
            lineTo(cx + r * 0.72f, cy + r * 0.1f)
            cubicTo(
                cx + r * 0.72f, cy + r * 0.55f,
                cx + r * 0.35f, cy + r * 0.8f,
                cx, cy + r * 0.92f
            )
            cubicTo(
                cx - r * 0.35f, cy + r * 0.8f,
                cx - r * 0.72f, cy + r * 0.55f,
                cx - r * 0.72f, cy + r * 0.1f
            )
            lineTo(cx - r * 0.72f, cy - r * 0.5f)
            close()
        }
        ds.drawPath(shieldPath, if (isAlert) Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
        ds.drawPath(shieldPath, accentColor, style = Stroke(width = (r * 0.1f).coerceAtLeast(1.5f)))

        // Raised garden bed structure
        val bedY = cy + r * 0.05f
        val bedW = r * 0.88f
        val bedH = r * 0.3f
        ds.drawRoundRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(cx - bedW / 2f, bedY),
            size = Size(bedW, bedH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.08f, r * 0.08f)
        )

        // Thriving green seedling atop raised bed
        ds.drawCircle(Color(0xFF2E7D32), radius = r * 0.18f, center = Offset(cx - r * 0.12f, bedY - r * 0.18f))
        ds.drawCircle(Color(0xFF43A047), radius = r * 0.18f, center = Offset(cx + r * 0.12f, bedY - r * 0.22f))
        ds.drawLine(Color(0xFF1B5E20), Offset(cx, bedY), Offset(cx, bedY - r * 0.2f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)

        // Drainage water channel curving below bed
        val waterPath = Path().apply {
            moveTo(cx - r * 0.55f, bedY + bedH + r * 0.12f)
            quadraticTo(cx, bedY + bedH + r * 0.22f, cx + r * 0.55f, bedY + bedH + r * 0.12f)
        }
        ds.drawPath(waterPath, Color(0xFF0288D1), style = Stroke(width = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round))
    }

    // 7. Height: Bamboo tulos stake with climbing vine and vertical measurement caliper bracket
    private fun drawPlantHeightSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val stakeX = cx - r * 0.18f

        // Bamboo tulos stake
        ds.drawLine(
            color = Color(0xFF8D6E63),
            start = Offset(stakeX, cy - r * 0.85f),
            end = Offset(stakeX, cy + r * 0.85f),
            strokeWidth = (r * 0.14f).coerceAtLeast(2f),
            cap = StrokeCap.Round
        )

        // Bamboo joints (segments)
        ds.drawLine(Color(0xFF5D4037), Offset(stakeX - r * 0.1f, cy - r * 0.4f), Offset(stakeX + r * 0.1f, cy - r * 0.4f), strokeWidth = r * 0.08f)
        ds.drawLine(Color(0xFF5D4037), Offset(stakeX - r * 0.1f, cy + r * 0.1f), Offset(stakeX + r * 0.1f, cy + r * 0.1f), strokeWidth = r * 0.08f)

        // Lush green vine wrapped around stake
        val vinePath = Path().apply {
            moveTo(stakeX - r * 0.12f, cy + r * 0.7f)
            cubicTo(stakeX + r * 0.2f, cy + r * 0.4f, stakeX - r * 0.2f, cy - r * 0.1f, stakeX + r * 0.15f, cy - r * 0.5f)
        }
        ds.drawPath(vinePath, Color(0xFF2E7D32), style = Stroke(width = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round))

        // Vine leaves
        ds.drawCircle(Color(0xFF43A047), radius = r * 0.14f, center = Offset(stakeX + r * 0.22f, cy + r * 0.3f))
        ds.drawCircle(Color(0xFF43A047), radius = r * 0.14f, center = Offset(stakeX - r * 0.22f, cy - r * 0.2f))
        ds.drawCircle(Color(0xFF66BB6A), radius = r * 0.12f, center = Offset(stakeX + r * 0.18f, cy - r * 0.6f))

        // Vertical measurement bracket with tick marks on the right
        val rulerX = cx + r * 0.45f
        ds.drawLine(
            color = Color(0xFFE65100),
            start = Offset(rulerX, cy - r * 0.8f),
            end = Offset(rulerX, cy + r * 0.8f),
            strokeWidth = (r * 0.1f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )
        // Top and bottom bracket ticks
        ds.drawLine(Color(0xFFE65100), Offset(rulerX - r * 0.18f, cy - r * 0.8f), Offset(rulerX, cy - r * 0.8f), strokeWidth = r * 0.1f)
        ds.drawLine(Color(0xFFE65100), Offset(rulerX - r * 0.18f, cy + r * 0.8f), Offset(rulerX, cy + r * 0.8f), strokeWidth = r * 0.1f)
        ds.drawLine(Color(0xFFE65100), Offset(rulerX - r * 0.12f, cy), Offset(rulerX, cy), strokeWidth = r * 0.08f)
    }

    // 8. Germination: Split seed underground with anchor taproot and fresh emergent cotyledons
    private fun drawGerminationSproutSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val soilY = cy + r * 0.2f

        // Soil surface line
        ds.drawLine(
            color = Color(0xFF8D6E63),
            start = Offset(cx - r * 0.85f, soilY),
            end = Offset(cx + r * 0.85f, soilY),
            strokeWidth = (r * 0.12f).coerceAtLeast(1.8f),
            cap = StrokeCap.Round
        )

        // Seed hull below soil line
        val seedCenter = Offset(cx, soilY + r * 0.32f)
        ds.drawOval(
            color = Color(0xFF5D4037),
            topLeft = Offset(seedCenter.x - r * 0.22f, seedCenter.y - r * 0.15f),
            size = Size(r * 0.44f, r * 0.3f)
        )

        // Downward taproot
        val rootPath = Path().apply {
            moveTo(seedCenter.x, seedCenter.y + r * 0.15f)
            quadraticTo(seedCenter.x + r * 0.1f, seedCenter.y + r * 0.4f, seedCenter.x - r * 0.05f, seedCenter.y + r * 0.55f)
        }
        ds.drawPath(rootPath, Color(0xFFD7CCC8), style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f), cap = StrokeCap.Round))

        // Arching green hypocotyl stem emerging upward through soil
        val stemPath = Path().apply {
            moveTo(seedCenter.x, seedCenter.y - r * 0.15f)
            cubicTo(
                seedCenter.x - r * 0.2f, soilY - r * 0.1f,
                seedCenter.x - r * 0.15f, cy - r * 0.45f,
                cx, cy - r * 0.5f
            )
        }
        ds.drawPath(stemPath, Color(0xFF388E3C), style = Stroke(width = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round))

        // Left cotyledon leaf
        val leftLeaf = Path().apply {
            moveTo(cx, cy - r * 0.5f)
            quadraticTo(cx - r * 0.45f, cy - r * 0.7f, cx - r * 0.4f, cy - r * 0.45f)
            quadraticTo(cx - r * 0.2f, cy - r * 0.4f, cx, cy - r * 0.5f)
            close()
        }
        ds.drawPath(leftLeaf, Color(0xFF4CAF50))

        // Right cotyledon leaf
        val rightLeaf = Path().apply {
            moveTo(cx, cy - r * 0.5f)
            quadraticTo(cx + r * 0.45f, cy - r * 0.7f, cx + r * 0.4f, cy - r * 0.45f)
            quadraticTo(cx + r * 0.2f, cy - r * 0.4f, cx, cy - r * 0.5f)
            close()
        }
        ds.drawPath(rightLeaf, Color(0xFF66BB6A))
    }

    // 9. Thermometer: Laboratory glass thermometer with mercury bulb and thermal gradations
    private fun drawThermometerSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val bulbRadius = r * 0.32f
        val bulbCenter = Offset(cx, cy + r * 0.5f)
        val stemW = r * 0.28f
        val stemTop = cy - r * 0.85f

        // Glass tube background
        val glassPath = Path().apply {
            moveTo(cx - stemW / 2f, bulbCenter.y - bulbRadius * 0.7f)
            lineTo(cx - stemW / 2f, stemTop + stemW / 2f)
            arcTo(
                rect = Rect(cx - stemW / 2f, stemTop, cx + stemW / 2f, stemTop + stemW),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(cx + stemW / 2f, bulbCenter.y - bulbRadius * 0.7f)
        }
        ds.drawPath(glassPath, Color(0xFFECEFF1))
        ds.drawCircle(Color(0xFFECEFF1), radius = bulbRadius, center = bulbCenter)

        // Glass outline
        ds.drawPath(glassPath, Color(0xFF90A4AE), style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f)))
        ds.drawCircle(Color(0xFF90A4AE), radius = bulbRadius, center = bulbCenter, style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f)))

        // Red/Amber mercury column
        val mercuryTop = cy - r * 0.35f
        val mercuryW = stemW * 0.6f
        ds.drawRoundRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(cx - mercuryW / 2f, mercuryTop),
            size = Size(mercuryW, bulbCenter.y - mercuryTop),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(mercuryW / 2f, mercuryW / 2f)
        )
        ds.drawCircle(Color(0xFFD32F2F), radius = bulbRadius * 0.78f, center = bulbCenter)

        // Bulb shine reflection
        ds.drawCircle(Color.White.copy(alpha = 0.65f), radius = bulbRadius * 0.25f, center = Offset(bulbCenter.x - bulbRadius * 0.28f, bulbCenter.y - bulbRadius * 0.28f))

        // Temperature tick marks on right side of tube
        val tickX = cx + stemW / 2f + r * 0.05f
        ds.drawLine(Color(0xFFE65100), Offset(tickX, cy - r * 0.6f), Offset(tickX + r * 0.18f, cy - r * 0.6f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFE65100), Offset(tickX, cy - r * 0.35f), Offset(tickX + r * 0.24f, cy - r * 0.35f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFE65100), Offset(tickX, cy - r * 0.1f), Offset(tickX + r * 0.18f, cy - r * 0.1f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
    }

    // 10. Harvest: Philippine woven bilawo basket filled with golden ears of grain & vegetables
    private fun drawHarvestBasketSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val basketColor = Color(0xFF8D6E63)
        val weaveColor = Color(0xFF6D4C41)

        // Produce mound overflowing from basket top
        ds.drawCircle(Color(0xFFE53935), radius = r * 0.22f, center = Offset(cx - r * 0.25f, cy - r * 0.18f)) // Ripe tomato
        ds.drawCircle(Color(0xFFFFB300), radius = r * 0.25f, center = Offset(cx + r * 0.2f, cy - r * 0.2f))  // Golden corn
        ds.drawCircle(Color(0xFF43A047), radius = r * 0.2f, center = Offset(cx, cy - r * 0.3f))              // Fresh leaf

        // Woven bilawo / basket rim
        ds.drawOval(
            color = Color(0xFFA1887F),
            topLeft = Offset(cx - r * 0.8f, cy - r * 0.12f),
            size = Size(r * 1.6f, r * 0.35f)
        )

        // Basket body (tapered bowl)
        val basketBody = Path().apply {
            moveTo(cx - r * 0.8f, cy)
            lineTo(cx + r * 0.8f, cy)
            cubicTo(
                cx + r * 0.65f, cy + r * 0.6f,
                cx + r * 0.4f, cy + r * 0.78f,
                cx, cy + r * 0.82f
            )
            cubicTo(
                cx - r * 0.4f, cy + r * 0.78f,
                cx - r * 0.65f, cy + r * 0.6f,
                cx - r * 0.8f, cy
            )
            close()
        }
        ds.drawPath(basketBody, basketColor)

        // Basket weave lines
        ds.drawPath(basketBody, weaveColor, style = Stroke(width = (r * 0.08f).coerceAtLeast(1.2f)))
        ds.drawLine(weaveColor, Offset(cx - r * 0.5f, cy + r * 0.1f), Offset(cx - r * 0.25f, cy + r * 0.75f), strokeWidth = r * 0.08f)
        ds.drawLine(weaveColor, Offset(cx, cy + r * 0.1f), Offset(cx, cy + r * 0.8f), strokeWidth = r * 0.08f)
        ds.drawLine(weaveColor, Offset(cx + r * 0.5f, cy + r * 0.1f), Offset(cx + r * 0.25f, cy + r * 0.75f), strokeWidth = r * 0.08f)
        ds.drawLine(weaveColor, Offset(cx - r * 0.68f, cy + r * 0.4f), Offset(cx + r * 0.68f, cy + r * 0.4f), strokeWidth = r * 0.08f)
    }

    // 11. pH: Laboratory Erlenmeyer flask with gradient pH litmus fluid and bubbles
    private fun drawPhFlaskSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val neckW = r * 0.28f
        val neckTop = cy - r * 0.8f
        val neckBottom = cy - r * 0.2f
        val baseW = r * 1.3f
        val baseBottom = cy + r * 0.78f

        // Flask glass silhouette
        val flaskPath = Path().apply {
            moveTo(cx - neckW / 2f, neckTop)
            lineTo(cx - neckW / 2f, neckBottom)
            lineTo(cx - baseW / 2f, baseBottom)
            arcTo(
                rect = Rect(cx - baseW / 2f, baseBottom - r * 0.15f, cx - baseW / 2f + r * 0.3f, baseBottom + r * 0.15f),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(cx + baseW / 2f - r * 0.15f, baseBottom + r * 0.05f)
            arcTo(
                rect = Rect(cx + baseW / 2f - r * 0.3f, baseBottom - r * 0.15f, cx + baseW / 2f, baseBottom + r * 0.15f),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(cx + neckW / 2f, neckBottom)
            lineTo(cx + neckW / 2f, neckTop)
            close()
        }
        ds.drawPath(flaskPath, Color(0xFFF1F8E9))

        // Fluid level inside flask (Teal/Emerald optimal pH)
        val fluidLevelY = cy + r * 0.05f
        val fluidLeftX = cx - (baseW / 2f) * 0.75f
        val fluidRightX = cx + (baseW / 2f) * 0.75f

        val fluidPath = Path().apply {
            moveTo(fluidLeftX, fluidLevelY)
            lineTo(cx - baseW / 2f + r * 0.1f, baseBottom)
            lineTo(cx + baseW / 2f - r * 0.1f, baseBottom)
            lineTo(fluidRightX, fluidLevelY)
            close()
        }
        ds.drawPath(
            path = fluidPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF26A69A), Color(0xFF00796B)),
                startY = fluidLevelY,
                endY = baseBottom
            )
        )

        // Effervescent bubbles
        ds.drawCircle(Color.White.copy(alpha = 0.8f), radius = r * 0.08f, center = Offset(cx - r * 0.15f, cy + r * 0.45f))
        ds.drawCircle(Color.White.copy(alpha = 0.8f), radius = r * 0.06f, center = Offset(cx + r * 0.12f, cy + r * 0.32f))
        ds.drawCircle(Color.White.copy(alpha = 0.8f), radius = r * 0.05f, center = Offset(cx - r * 0.05f, cy + r * 0.2f))

        // Glass outline & rim
        ds.drawPath(flaskPath, Color(0xFF004D40), style = Stroke(width = (r * 0.1f).coerceAtLeast(1.5f)))
        ds.drawOval(Color(0xFF004D40), topLeft = Offset(cx - neckW * 0.7f, neckTop - r * 0.06f), size = Size(neckW * 1.4f, r * 0.12f))

        // Gradation volume tick marks on glass wall
        ds.drawLine(Color(0xFF004D40), Offset(cx - r * 0.25f, cy + r * 0.2f), Offset(cx - r * 0.15f, cy + r * 0.2f), strokeWidth = r * 0.08f)
        ds.drawLine(Color(0xFF004D40), Offset(cx - r * 0.35f, cy + r * 0.45f), Offset(cx - r * 0.2f, cy + r * 0.45f), strokeWidth = r * 0.08f)
    }

    // 12. Seedling: Nursery plug cell container with rich root plug and paired true leaves
    private fun drawSeedlingPlugSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val plugTop = cy + r * 0.05f
        val plugBottom = cy + r * 0.82f
        val topW = r * 0.9f
        val bottomW = r * 0.48f

        // Nursery plug root ball (tapered cell)
        val plugPath = Path().apply {
            moveTo(cx - topW / 2f, plugTop)
            lineTo(cx + topW / 2f, plugTop)
            lineTo(cx + bottomW / 2f, plugBottom)
            lineTo(cx - bottomW / 2f, plugBottom)
            close()
        }
        ds.drawPath(
            path = plugPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF5D4037), Color(0xFF3E2723)),
                startY = plugTop,
                endY = plugBottom
            )
        )

        // Fine feeder root hairs on cell surface
        ds.drawLine(Color(0xFFD7CCC8), Offset(cx - r * 0.2f, plugTop + r * 0.2f), Offset(cx - r * 0.35f, plugTop + r * 0.35f), strokeWidth = r * 0.06f)
        ds.drawLine(Color(0xFFD7CCC8), Offset(cx + r * 0.15f, plugTop + r * 0.35f), Offset(cx + r * 0.28f, plugTop + r * 0.5f), strokeWidth = r * 0.06f)

        // Seedling stem rising from cell
        ds.drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(cx, plugTop),
            end = Offset(cx, cy - r * 0.35f),
            strokeWidth = (r * 0.12f).coerceAtLeast(1.8f),
            cap = StrokeCap.Round
        )

        // Left true leaf
        val leftLeaf = Path().apply {
            moveTo(cx, cy - r * 0.35f)
            cubicTo(
                cx - r * 0.35f, cy - r * 0.6f,
                cx - r * 0.65f, cy - r * 0.3f,
                cx - r * 0.75f, cy - r * 0.55f
            )
            cubicTo(
                cx - r * 0.4f, cy - r * 0.8f,
                cx - r * 0.1f, cy - r * 0.55f,
                cx, cy - r * 0.35f
            )
            close()
        }
        ds.drawPath(leftLeaf, Color(0xFF43A047))

        // Right true leaf
        val rightLeaf = Path().apply {
            moveTo(cx, cy - r * 0.35f)
            cubicTo(
                cx + r * 0.35f, cy - r * 0.6f,
                cx + r * 0.65f, cy - r * 0.3f,
                cx + r * 0.75f, cy - r * 0.55f
            )
            cubicTo(
                cx + r * 0.4f, cy - r * 0.8f,
                cx + r * 0.1f, cy - r * 0.55f,
                cx, cy - r * 0.35f
            )
            close()
        }
        ds.drawPath(rightLeaf, Color(0xFF66BB6A))

        // Center budding shoot tip
        ds.drawCircle(Color(0xFF81C784), radius = r * 0.12f, center = Offset(cx, cy - r * 0.52f))
    }

    private fun drawGenericInfoLeafSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val leafPath = Path().apply {
            moveTo(cx, cy + r * 0.7f)
            cubicTo(cx - r * 0.7f, cy + r * 0.2f, cx - r * 0.7f, cy - r * 0.6f, cx, cy - r * 0.8f)
            cubicTo(cx + r * 0.7f, cy - r * 0.6f, cx + r * 0.7f, cy + r * 0.2f, cx, cy + r * 0.7f)
            close()
        }
        ds.drawPath(leafPath, Color(0xFF2E7D32))
        ds.drawLine(Color(0xFF1B5E20), Offset(cx, cy + r * 0.7f), Offset(cx, cy - r * 0.75f), strokeWidth = r * 0.08f)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 2. NUTRITION VECTOR BADGES
    // ═══════════════════════════════════════════════════════════════════════════

    fun drawNutritionSvg(
        drawScope: DrawScope,
        code: String,
        center: Offset,
        sizePx: Float
    ) {
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        when (code.uppercase().trim()) {
            "LYC" -> drawLycopeneBadge(drawScope, cx, cy, r)
            "C" -> drawVitaminCBadge(drawScope, cx, cy, r)
            "K1", "K" -> drawVitaminKBadge(drawScope, cx, cy, r)
            "B9" -> drawFolateB9Badge(drawScope, cx, cy, r)
            "A" -> drawVitaminABadge(drawScope, cx, cy, r)
            "FE" -> drawIronBadge(drawScope, cx, cy, r)
            else -> drawGenericNutrientBadge(drawScope, cx, cy, r)
        }
    }

    // Lycopene: Ruby antioxidant jewel with crystalline molecular ring
    private fun drawLycopeneBadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val rubyColor = Color(0xFFD32F2F)
        val deepRed = Color(0xFFB71C1C)

        // Outer faceted octagon ring
        val ringPath = Path().apply {
            val count = 8
            for (i in 0 until count) {
                val angle = Math.toRadians((i * 45.0) - 22.5)
                val x = cx + (r * 0.85f * Math.cos(angle)).toFloat()
                val y = cy + (r * 0.85f * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        ds.drawPath(ringPath, rubyColor)

        // Inner glowing core
        ds.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8A80), deepRed),
                center = Offset(cx, cy),
                radius = r * 0.55f
            ),
            radius = r * 0.55f,
            center = Offset(cx, cy)
        )

        // Diamond spark in center
        val sparkPath = Path().apply {
            moveTo(cx, cy - r * 0.35f)
            lineTo(cx + r * 0.25f, cy)
            lineTo(cx, cy + r * 0.35f)
            lineTo(cx - r * 0.25f, cy)
            close()
        }
        ds.drawPath(sparkPath, Color.White.copy(alpha = 0.9f))
    }

    // Vitamin C: Citrus sunshine segment geometry with ascorbic radiance
    private fun drawVitaminCBadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val orangeColor = Color(0xFFFF9800)
        val peelColor = Color(0xFFF57C00)

        // Outer citrus round
        ds.drawCircle(peelColor, radius = r * 0.85f, center = Offset(cx, cy))
        ds.drawCircle(Color(0xFFFFF3E0), radius = r * 0.75f, center = Offset(cx, cy))

        // Citrus segments (spokes)
        val segmentRadius = r * 0.65f
        for (i in 0 until 6) {
            val startAngle = (i * 60f) + 6f
            val sweep = 48f
            val segPath = Path().apply {
                moveTo(cx, cy)
                arcTo(
                    rect = Rect(cx - segmentRadius, cy - segmentRadius, cx + segmentRadius, cy + segmentRadius),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweep,
                    forceMoveTo = false
                )
                close()
            }
            ds.drawPath(segPath, orangeColor)
        }

        // Center core dot
        ds.drawCircle(Color.White, radius = r * 0.16f, center = Offset(cx, cy))
    }

    // Vitamin K / Potassium: Emerald mineral crystal leaf
    private fun drawVitaminKBadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val emerald = Color(0xFF2E7D32)
        val brightGreen = Color(0xFF66BB6A)

        // Diamond facet shield
        val shieldPath = Path().apply {
            moveTo(cx, cy - r * 0.85f)
            lineTo(cx + r * 0.75f, cy - r * 0.2f)
            lineTo(cx, cy + r * 0.85f)
            lineTo(cx - r * 0.75f, cy - r * 0.2f)
            close()
        }
        ds.drawPath(shieldPath, emerald)

        // Facet lines
        ds.drawLine(Color.White.copy(alpha = 0.6f), Offset(cx, cy - r * 0.85f), Offset(cx, cy + r * 0.85f), strokeWidth = r * 0.08f)
        ds.drawLine(Color.White.copy(alpha = 0.6f), Offset(cx - r * 0.75f, cy - r * 0.2f), Offset(cx + r * 0.75f, cy - r * 0.2f), strokeWidth = r * 0.08f)

        // Inner glowing spark
        ds.drawCircle(brightGreen, radius = r * 0.25f, center = Offset(cx, cy))
    }

    // Folate B9: Vital hexagonal molecular node with cell regeneration sprout
    private fun drawFolateB9Badge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val cyan = Color(0xFF00897B)

        // Hexagon ring
        val hexPath = Path().apply {
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60.0) - 30.0)
                val x = cx + (r * 0.8f * Math.cos(angle)).toFloat()
                val y = cy + (r * 0.8f * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        ds.drawPath(hexPath, cyan)

        // Inner nucleus sprout
        ds.drawCircle(Color(0xFF80CBC4), radius = r * 0.45f, center = Offset(cx, cy))
        ds.drawCircle(Color.White, radius = r * 0.2f, center = Offset(cx, cy))
    }

    // Vitamin A: Clear vision / beta-carotene radiant iris
    private fun drawVitaminABadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val gold = Color(0xFFFFB300)

        // Eye / lens almond shape
        val eyePath = Path().apply {
            moveTo(cx - r * 0.85f, cy)
            quadraticTo(cx, cy - r * 0.75f, cx + r * 0.85f, cy)
            quadraticTo(cx, cy + r * 0.75f, cx - r * 0.85f, cy)
            close()
        }
        ds.drawPath(eyePath, gold)

        // Pupil iris core
        ds.drawCircle(Color(0xFFE65100), radius = r * 0.4f, center = Offset(cx, cy))
        ds.drawCircle(Color.White, radius = r * 0.16f, center = Offset(cx - r * 0.1f, cy - r * 0.1f))
    }

    // Iron: Slate metallic shield
    private fun drawIronBadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val steel = Color(0xFF546E7A)
        val rustAccent = Color(0xFFBF360C)

        val shieldPath = Path().apply {
            moveTo(cx, cy - r * 0.8f)
            lineTo(cx + r * 0.75f, cy - r * 0.4f)
            lineTo(cx + r * 0.65f, cy + r * 0.4f)
            lineTo(cx, cy + r * 0.85f)
            lineTo(cx - r * 0.65f, cy + r * 0.4f)
            lineTo(cx - r * 0.75f, cy - r * 0.4f)
            close()
        }
        ds.drawPath(shieldPath, steel)
        ds.drawCircle(rustAccent, radius = r * 0.35f, center = Offset(cx, cy))
        ds.drawCircle(Color.White, radius = r * 0.12f, center = Offset(cx, cy))
    }

    private fun drawGenericNutrientBadge(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawCircle(Color(0xFF2E7D32), radius = r * 0.8f, center = Offset(cx, cy))
        ds.drawCircle(Color(0xFF81C784), radius = r * 0.5f, center = Offset(cx, cy))
        ds.drawCircle(Color.White, radius = r * 0.2f, center = Offset(cx, cy))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3. BENEFICIAL CRITTERS VECTOR DRAWINGS
    // ═══════════════════════════════════════════════════════════════════════════

    fun drawBeneficialCritterSvg(
        drawScope: DrawScope,
        name: String,
        center: Offset,
        sizePx: Float
    ) {
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        val clean = name.lowercase().replace(" ", "").replace("-", "")
        when {
            clean.contains("bee") || clean.contains("honeybee") || clean.contains("pukyutan") || clean.contains("bubuyog") -> {
                drawHoneybeeSvg(drawScope, cx, cy, r)
            }
            clean.contains("ladybug") || clean.contains("ladybird") || clean.contains("harabas") -> {
                drawLadybugSvg(drawScope, cx, cy, r)
            }
            clean.contains("mantis") || clean.contains("samba") -> {
                drawPrayingMantisSvg(drawScope, cx, cy, r)
            }
            clean.contains("spider") || clean.contains("gagamba") -> {
                drawSpiderSvg(drawScope, cx, cy, r)
            }
            clean.contains("worm") || clean.contains("earthworm") || clean.contains("bulate") -> {
                drawEarthwormSvg(drawScope, cx, cy, r)
            }
            else -> drawHoverflySvg(drawScope, cx, cy, r)
        }
    }

    // Honeybee / Pukyutan: Striped abdomen, translucent wings, pollen halo
    private fun drawHoneybeeSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Wings (translucent iridescent)
        val wingColor = Color(0xFFE1F5FE).copy(alpha = 0.85f)
        val leftWing = Path().apply {
            moveTo(cx, cy - r * 0.1f)
            cubicTo(cx - r * 0.7f, cy - r * 0.85f, cx - r * 0.85f, cy - r * 0.3f, cx, cy - r * 0.1f)
            close()
        }
        val rightWing = Path().apply {
            moveTo(cx, cy - r * 0.1f)
            cubicTo(cx + r * 0.7f, cy - r * 0.85f, cx + r * 0.85f, cy - r * 0.3f, cx, cy - r * 0.1f)
            close()
        }
        ds.drawPath(leftWing, wingColor)
        ds.drawPath(rightWing, wingColor)
        ds.drawPath(leftWing, Color(0xFF0288D1), style = Stroke(width = (r * 0.06f).coerceAtLeast(1f)))
        ds.drawPath(rightWing, Color(0xFF0288D1), style = Stroke(width = (r * 0.06f).coerceAtLeast(1f)))

        // Bee body (Abdomen)
        val bodyRect = Rect(cx - r * 0.35f, cy - r * 0.2f, cx + r * 0.35f, cy + r * 0.75f)
        ds.drawOval(Color(0xFFFFB300), topLeft = Offset(bodyRect.left, bodyRect.top), size = Size(bodyRect.width, bodyRect.height))

        // Black stripes
        ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.3f, cy + r * 0.1f), Offset(cx + r * 0.3f, cy + r * 0.1f), strokeWidth = r * 0.14f)
        ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.28f, cy + r * 0.4f), Offset(cx + r * 0.28f, cy + r * 0.4f), strokeWidth = r * 0.14f)

        // Head & Antennae
        ds.drawCircle(Color(0xFF212121), radius = r * 0.22f, center = Offset(cx, cy - r * 0.35f))
        ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.08f, cy - r * 0.5f), Offset(cx - r * 0.25f, cy - r * 0.75f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFF212121), Offset(cx + r * 0.08f, cy - r * 0.5f), Offset(cx + r * 0.25f, cy - r * 0.75f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
    }

    // Ladybug: Red domed shell with distinct black spots and antennae
    private fun drawLadybugSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val redShell = Color(0xFFE53935)
        val blackBody = Color(0xFF212121)

        // Legs (6 legs)
        val legStroke = (r * 0.08f).coerceAtLeast(1.2f)
        ds.drawLine(blackBody, Offset(cx - r * 0.3f, cy - r * 0.2f), Offset(cx - r * 0.75f, cy - r * 0.35f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(blackBody, Offset(cx + r * 0.3f, cy - r * 0.2f), Offset(cx + r * 0.75f, cy - r * 0.35f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(blackBody, Offset(cx - r * 0.35f, cy + r * 0.1f), Offset(cx - r * 0.8f, cy + r * 0.15f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(blackBody, Offset(cx + r * 0.35f, cy + r * 0.1f), Offset(cx + r * 0.8f, cy + r * 0.15f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(blackBody, Offset(cx - r * 0.3f, cy + r * 0.45f), Offset(cx - r * 0.7f, cy + r * 0.65f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(blackBody, Offset(cx + r * 0.3f, cy + r * 0.45f), Offset(cx + r * 0.7f, cy + r * 0.65f), strokeWidth = legStroke, cap = StrokeCap.Round)

        // Head
        ds.drawCircle(blackBody, radius = r * 0.25f, center = Offset(cx, cy - r * 0.45f))
        // White eyes
        ds.drawCircle(Color.White, radius = r * 0.06f, center = Offset(cx - r * 0.12f, cy - r * 0.5f))
        ds.drawCircle(Color.White, radius = r * 0.06f, center = Offset(cx + r * 0.12f, cy - r * 0.5f))

        // Red Elytra shell (domed body)
        ds.drawCircle(redShell, radius = r * 0.55f, center = Offset(cx, cy + r * 0.12f))

        // Center division line
        ds.drawLine(blackBody, Offset(cx, cy - r * 0.4f), Offset(cx, cy + r * 0.67f), strokeWidth = r * 0.09f)

        // Black spots
        val spotR = r * 0.11f
        ds.drawCircle(blackBody, radius = spotR, center = Offset(cx - r * 0.28f, cy - r * 0.05f))
        ds.drawCircle(blackBody, radius = spotR, center = Offset(cx + r * 0.28f, cy - r * 0.05f))
        ds.drawCircle(blackBody, radius = spotR, center = Offset(cx - r * 0.32f, cy + r * 0.28f))
        ds.drawCircle(blackBody, radius = spotR, center = Offset(cx + r * 0.32f, cy + r * 0.28f))
        ds.drawCircle(blackBody, radius = spotR * 0.8f, center = Offset(cx, cy + r * 0.48f))
    }

    // Praying Mantis / Samba-samba: Triangular head, raptorial folded forelegs, slender green thorax
    private fun drawPrayingMantisSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val mantisGreen = Color(0xFF2E7D32)
        val brightGreen = Color(0xFF43A047)

        // Slender abdomen
        val bodyPath = Path().apply {
            moveTo(cx - r * 0.15f, cy - r * 0.1f)
            lineTo(cx + r * 0.15f, cy - r * 0.1f)
            lineTo(cx + r * 0.1f, cy + r * 0.85f)
            lineTo(cx - r * 0.1f, cy + r * 0.85f)
            close()
        }
        ds.drawPath(bodyPath, mantisGreen)

        // Thorax
        ds.drawLine(mantisGreen, Offset(cx, cy - r * 0.1f), Offset(cx, cy - r * 0.45f), strokeWidth = r * 0.16f, cap = StrokeCap.Round)

        // Triangular head
        val headPath = Path().apply {
            moveTo(cx, cy - r * 0.72f)
            lineTo(cx + r * 0.32f, cy - r * 0.45f)
            lineTo(cx - r * 0.32f, cy - r * 0.45f)
            close()
        }
        ds.drawPath(headPath, brightGreen)

        // Large compound eyes
        ds.drawCircle(Color(0xFF7CB342), radius = r * 0.12f, center = Offset(cx - r * 0.25f, cy - r * 0.52f))
        ds.drawCircle(Color(0xFF7CB342), radius = r * 0.12f, center = Offset(cx + r * 0.25f, cy - r * 0.52f))

        // Raptorial praying forelegs folded in front
        val legStroke = (r * 0.1f).coerceAtLeast(1.5f)
        ds.drawLine(mantisGreen, Offset(cx - r * 0.08f, cy - r * 0.35f), Offset(cx - r * 0.45f, cy - r * 0.2f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(mantisGreen, Offset(cx - r * 0.45f, cy - r * 0.2f), Offset(cx - r * 0.2f, cy + r * 0.05f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(mantisGreen, Offset(cx + r * 0.08f, cy - r * 0.35f), Offset(cx + r * 0.45f, cy - r * 0.2f), strokeWidth = legStroke, cap = StrokeCap.Round)
        ds.drawLine(mantisGreen, Offset(cx + r * 0.45f, cy - r * 0.2f), Offset(cx + r * 0.2f, cy + r * 0.05f), strokeWidth = legStroke, cap = StrokeCap.Round)
    }

    // Spider / Gagamba: 8 arched legs with segmented cephalothorax
    private fun drawSpiderSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val spiderColor = Color(0xFF37474F)

        // 8 Arched legs
        val legStroke = (r * 0.07f).coerceAtLeast(1.2f)
        val legAngles = listOf(-70.0, -40.0, 40.0, 70.0, -110.0, -140.0, 110.0, 140.0)
        for (deg in legAngles) {
            val rad = Math.toRadians(deg)
            val kneeX = cx + (r * 0.65f * Math.cos(rad)).toFloat()
            val kneeY = cy + (r * 0.45f * Math.sin(rad)).toFloat()
            val footX = cx + (r * 0.88f * Math.cos(rad)).toFloat()
            val footY = cy + (r * 0.85f * Math.sin(rad)).toFloat()

            ds.drawLine(spiderColor, Offset(cx, cy), Offset(kneeX, kneeY), strokeWidth = legStroke, cap = StrokeCap.Round)
            ds.drawLine(spiderColor, Offset(kneeX, kneeY), Offset(footX, footY), strokeWidth = legStroke, cap = StrokeCap.Round)
        }

        // Abdomen
        ds.drawCircle(spiderColor, radius = r * 0.32f, center = Offset(cx, cy + r * 0.22f))
        // Cephalothorax
        ds.drawCircle(spiderColor, radius = r * 0.22f, center = Offset(cx, cy - r * 0.22f))
        // Eye cluster
        ds.drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx - r * 0.07f, cy - r * 0.28f))
        ds.drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx + r * 0.07f, cy - r * 0.28f))
    }

    // Earthworm / Bulate: Segmented curved burrower
    private fun drawEarthwormSvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val wormPink = Color(0xFFD81B60)
        val clitellum = Color(0xFFAD1457)

        val wormPath = Path().apply {
            moveTo(cx - r * 0.7f, cy + r * 0.5f)
            cubicTo(
                cx - r * 0.4f, cy - r * 0.6f,
                cx + r * 0.2f, cy + r * 0.7f,
                cx + r * 0.7f, cy - r * 0.4f
            )
        }
        ds.drawPath(wormPath, wormPink, style = Stroke(width = (r * 0.3f).coerceAtLeast(3f), cap = StrokeCap.Round))

        // Clitellum saddle band
        ds.drawCircle(clitellum, radius = r * 0.2f, center = Offset(cx - r * 0.1f, cy - r * 0.05f))
    }

    private fun drawHoverflySvg(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawCircle(Color(0xFF388E3C), radius = r * 0.4f, center = Offset(cx, cy))
        ds.drawCircle(Color.White, radius = r * 0.15f, center = Offset(cx, cy))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4. PESTS & DISEASES VECTOR DRAWINGS
    // ═══════════════════════════════════════════════════════════════════════════

    fun drawPestDiseaseSvg(
        drawScope: DrawScope,
        isPest: Boolean,
        severity: String,
        center: Offset,
        sizePx: Float
    ) {
        val ds = drawScope
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f
        val alertColor = if (severity == "Kritikal" || severity == "Nakamamatay") Color(0xFFC62828) else Color(0xFFE65100)

        if (isPest) {
            // Harmful borer / aphid vector illustration
            // Insect beetle body
            ds.drawOval(
                alertColor,
                topLeft = Offset(cx - r * 0.4f, cy - r * 0.2f),
                size = Size(r * 0.8f, r * 0.95f)
            )
            // Head
            ds.drawCircle(Color(0xFF212121), radius = r * 0.25f, center = Offset(cx, cy - r * 0.42f))

            // Antennae
            ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.1f, cy - r * 0.5f), Offset(cx - r * 0.4f, cy - r * 0.85f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
            ds.drawLine(Color(0xFF212121), Offset(cx + r * 0.1f, cy - r * 0.5f), Offset(cx + r * 0.4f, cy - r * 0.85f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)

            // Sharp mandibles
            ds.drawLine(Color(0xFFD32F2F), Offset(cx - r * 0.35f, cy - r * 0.85f), Offset(cx - r * 0.2f, cy - r * 0.92f), strokeWidth = r * 0.08f)
            ds.drawLine(Color(0xFFD32F2F), Offset(cx + r * 0.35f, cy - r * 0.85f), Offset(cx + r * 0.2f, cy - r * 0.92f), strokeWidth = r * 0.08f)

            // Cross-ribs on shell
            ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.35f, cy + r * 0.1f), Offset(cx + r * 0.35f, cy + r * 0.1f), strokeWidth = r * 0.08f)
            ds.drawLine(Color(0xFF212121), Offset(cx - r * 0.32f, cy + r * 0.4f), Offset(cx + r * 0.32f, cy + r * 0.4f), strokeWidth = r * 0.08f)
        } else {
            // Leaf blight / fungal pathogen vector illustration
            // Leaf profile
            val leafPath = Path().apply {
                moveTo(cx, cy + r * 0.85f)
                cubicTo(cx - r * 0.75f, cy + r * 0.3f, cx - r * 0.75f, cy - r * 0.5f, cx, cy - r * 0.85f)
                cubicTo(cx + r * 0.75f, cy - r * 0.5f, cx + r * 0.75f, cy + r * 0.3f, cx, cy + r * 0.85f)
                close()
            }
            ds.drawPath(leafPath, Color(0xFF7CB342))

            // Fungal necrotic halo spots (blight / mildew)
            ds.drawCircle(alertColor, radius = r * 0.22f, center = Offset(cx - r * 0.15f, cy - r * 0.1f))
            ds.drawCircle(Color(0xFF3E2723), radius = r * 0.12f, center = Offset(cx - r * 0.15f, cy - r * 0.1f))

            ds.drawCircle(alertColor, radius = r * 0.18f, center = Offset(cx + r * 0.2f, cy + r * 0.2f))
            ds.drawCircle(Color(0xFF3E2723), radius = r * 0.09f, center = Offset(cx + r * 0.2f, cy + r * 0.2f))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5. AGRONOMIC SUPPORT & SHIELD BADGES
    // ═══════════════════════════════════════════════════════════════════════════

    fun drawAgronomicRecommendationBadge(
        drawScope: DrawScope,
        center: Offset,
        sizePx: Float
    ) {
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        // Outer glow
        drawScope.drawCircle(Color(0xFFE8F5E9), radius = r * 0.95f, center = center)

        // Botanical sprout
        val leafLeft = Path().apply {
            moveTo(cx, cy + r * 0.45f)
            cubicTo(cx - r * 0.65f, cy + r * 0.2f, cx - r * 0.7f, cy - r * 0.45f, cx, cy - r * 0.65f)
            cubicTo(cx - r * 0.15f, cy - r * 0.25f, cx - r * 0.15f, cy + r * 0.2f, cx, cy + r * 0.45f)
            close()
        }
        drawScope.drawPath(leafLeft, Color(0xFF2E7D32))

        val leafRight = Path().apply {
            moveTo(cx, cy + r * 0.45f)
            cubicTo(cx + r * 0.65f, cy + r * 0.2f, cx + r * 0.7f, cy - r * 0.45f, cx, cy - r * 0.65f)
            cubicTo(cx + r * 0.15f, cy - r * 0.25f, cx + r * 0.15f, cy + r * 0.2f, cx, cy + r * 0.45f)
            close()
        }
        drawScope.drawPath(leafRight, Color(0xFF43A047))

        // Stem
        drawScope.drawLine(Color(0xFF1B5E20), Offset(cx, cy + r * 0.75f), Offset(cx, cy - r * 0.5f), strokeWidth = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
    }

    fun drawAllGoodShieldBadge(
        drawScope: DrawScope,
        center: Offset,
        sizePx: Float
    ) {
        val s = sizePx.coerceAtLeast(12f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        val shieldPath = Path().apply {
            moveTo(cx, cy - r * 0.85f)
            lineTo(cx + r * 0.72f, cy - r * 0.45f)
            lineTo(cx + r * 0.72f, cy + r * 0.15f)
            cubicTo(cx + r * 0.72f, cy + r * 0.6f, cx + r * 0.35f, cy + r * 0.85f, cx, cy + r * 0.95f)
            cubicTo(cx - r * 0.35f, cy + r * 0.85f, cx - r * 0.72f, cy + r * 0.6f, cx - r * 0.72f, cy + r * 0.15f)
            lineTo(cx - r * 0.72f, cy - r * 0.45f)
            close()
        }
        drawScope.drawPath(shieldPath, Color(0xFFE8F5E9))
        drawScope.drawPath(shieldPath, Color(0xFF2E7D32), style = Stroke(width = (r * 0.1f).coerceAtLeast(1.8f)))

        // Checkmark inside shield
        val checkPath = Path().apply {
            moveTo(cx - r * 0.35f, cy + r * 0.05f)
            lineTo(cx - r * 0.08f, cy + r * 0.35f)
            lineTo(cx + r * 0.4f, cy - r * 0.28f)
        }
        drawScope.drawPath(checkPath, Color(0xFF2E7D32), style = Stroke(width = (r * 0.14f).coerceAtLeast(2.2f), cap = StrokeCap.Round))
    }
}
