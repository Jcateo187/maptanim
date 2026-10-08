package com.maptanim.app.features.farm.renderer.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * GardenSvgRenderer — High-performance scalable vector SVG renderer for Garden cards on HomeScreen.
 * Provides 10 distinct, beautifully crafted Philippine garden landscape illustrations:
 * 1. Raised Bed Garden
 * 2. Hoophouse / Greenhouse Nursery
 * 3. Terraced Highland Slopes
 * 4. Backyard Container Garden
 * 5. Bamboo A-Frame Trellis Garden
 * 6. Orchard & Canopy Fruit Garden
 * 7. Hydroponic Vertical Tower Garden
 * 8. Traditional Furrow (Tudling) Garden
 * 9. Shaded Seedling Nursery
 * 10. Homestead Picket-Fenced Garden
 *
 * Automatically loops through the 10 SVGs (e.g., Garden #11 wraps back to SVG #1).
 */
object GardenSvgRenderer {

    fun drawGardenSvg(
        drawScope: DrawScope,
        gardenIndex: Int,
        size: Size
    ) {
        val w = size.width
        val h = size.height
        val idx = (gardenIndex % 10 + 10) % 10 // Safe 0..9 index

        when (idx) {
            0 -> drawRaisedBedGarden(drawScope, w, h)
            1 -> drawGreenhouseGarden(drawScope, w, h)
            2 -> drawTerraceGarden(drawScope, w, h)
            3 -> drawContainerGarden(drawScope, w, h)
            4 -> drawTrellisGarden(drawScope, w, h)
            5 -> drawOrchardGarden(drawScope, w, h)
            6 -> drawHydroponicGarden(drawScope, w, h)
            7 -> drawFurrowGarden(drawScope, w, h)
            8 -> drawNurseryGarden(drawScope, w, h)
            9 -> drawHomesteadGarden(drawScope, w, h)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 1. Raised Bed Garden (Wooden planter box with loamy soil & leafy greens)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawRaisedBedGarden(ds: DrawScope, w: Float, h: Float) {
        // Sky background
        ds.drawRect(Color(0xFFE8F5E9), Offset.Zero, Size(w, h))

        // Sun in upper right
        ds.drawCircle(Color(0xFFFFF59D), radius = w * 0.16f, center = Offset(w * 0.85f, h * 0.18f))
        ds.drawCircle(Color(0xFFFFEE58), radius = w * 0.10f, center = Offset(w * 0.85f, h * 0.18f))

        // Distant gentle rolling hill
        val hillPath = Path().apply {
            moveTo(0f, h * 0.55f)
            quadraticTo(w * 0.45f, h * 0.40f, w, h * 0.52f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        ds.drawPath(hillPath, Color(0xFFA5D6A7))

        // Raised wooden planter box
        val boxLeft = w * 0.12f
        val boxRight = w * 0.88f
        val boxTop = h * 0.52f
        val boxBottom = h * 0.82f

        // Planter soil surface
        ds.drawRoundRect(
            color = Color(0xFF4E342E),
            topLeft = Offset(boxLeft, boxTop - h * 0.04f),
            size = Size(boxRight - boxLeft, h * 0.10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
        )

        // Wood boards
        ds.drawRoundRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(boxLeft, boxTop),
            size = Size(boxRight - boxLeft, boxBottom - boxTop),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
        // Wood plank divider lines
        ds.drawLine(Color(0xFF5D4037), Offset(boxLeft, boxTop + (boxBottom - boxTop) * 0.5f), Offset(boxRight, boxTop + (boxBottom - boxTop) * 0.5f), strokeWidth = 2f)

        // Corner posts
        ds.drawRect(Color(0xFF6D4C41), Offset(boxLeft - 2f, boxTop - 4f), Size(w * 0.06f, boxBottom - boxTop + 8f))
        ds.drawRect(Color(0xFF6D4C41), Offset(boxRight - w * 0.06f + 2f, boxTop - 4f), Size(w * 0.06f, boxBottom - boxTop + 8f))

        // Vegetables growing out of box
        // Left: Pechay fan
        ds.drawCircle(Color(0xFF388E3C), radius = w * 0.10f, center = Offset(w * 0.28f, boxTop - h * 0.06f))
        ds.drawCircle(Color(0xFF4CAF50), radius = w * 0.07f, center = Offset(w * 0.28f, boxTop - h * 0.07f))

        // Center: Tomato bush with red fruits
        ds.drawCircle(Color(0xFF2E7D32), radius = w * 0.14f, center = Offset(w * 0.50f, boxTop - h * 0.12f))
        ds.drawCircle(Color(0xFFE53935), radius = w * 0.035f, center = Offset(w * 0.44f, boxTop - h * 0.11f))
        ds.drawCircle(Color(0xFFE53935), radius = w * 0.032f, center = Offset(w * 0.56f, boxTop - h * 0.09f))

        // Right: Carrot feathery greens
        ds.drawCircle(Color(0xFF43A047), radius = w * 0.09f, center = Offset(w * 0.72f, boxTop - h * 0.06f))
        ds.drawCircle(Color(0xFF66BB6A), radius = w * 0.06f, center = Offset(w * 0.72f, boxTop - h * 0.07f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2. Greenhouse / Hoophouse Garden (Tunnel structure with glass ribs & nursery)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawGreenhouseGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFE0F2F1), Offset.Zero, Size(w, h))

        // Ground gravel
        ds.drawRect(Color(0xFFB0BEC5), Offset(0f, h * 0.65f), Size(w, h * 0.35f))

        // Hoophouse Outer Arch
        val archPath = Path().apply {
            moveTo(w * 0.10f, h * 0.72f)
            cubicTo(w * 0.10f, h * 0.15f, w * 0.90f, h * 0.15f, w * 0.90f, h * 0.72f)
            close()
        }
        ds.drawPath(archPath, Color(0xFF80CBC4).copy(alpha = 0.35f))

        // Structural Steel Arches
        ds.drawPath(archPath, Color(0xFF00695C), style = Stroke(width = 3.5f))

        // Secondary inner arch rib
        val innerRib = Path().apply {
            moveTo(w * 0.25f, h * 0.72f)
            cubicTo(w * 0.25f, h * 0.28f, w * 0.75f, h * 0.28f, w * 0.75f, h * 0.72f)
        }
        ds.drawPath(innerRib, Color(0xFF00796B), style = Stroke(width = 2f))

        // Glass reflection diagonals
        ds.drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.22f, h * 0.40f), Offset(w * 0.45f, h * 0.22f), strokeWidth = 3f, cap = StrokeCap.Round)
        ds.drawLine(Color.White.copy(alpha = 0.35f), Offset(w * 0.26f, h * 0.48f), Offset(w * 0.50f, h * 0.30f), strokeWidth = 2f, cap = StrokeCap.Round)

        // Seedling benches inside
        ds.drawRect(Color(0xFF455A64), Offset(w * 0.15f, h * 0.60f), Size(w * 0.28f, h * 0.12f))
        ds.drawRect(Color(0xFF455A64), Offset(w * 0.57f, h * 0.60f), Size(w * 0.28f, h * 0.12f))

        // Green seedlings on benches
        for (i in 0..3) {
            ds.drawCircle(Color(0xFF2E7D32), radius = w * 0.03f, center = Offset(w * 0.18f + i * w * 0.07f, h * 0.58f))
            ds.drawCircle(Color(0xFF43A047), radius = w * 0.03f, center = Offset(w * 0.60f + i * w * 0.07f, h * 0.58f))
        }

        // Center walkway
        ds.drawRect(Color(0xFFCFD8DC), Offset(w * 0.45f, h * 0.60f), Size(w * 0.10f, h * 0.25f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 3. Terraced Highland Slopes (Cordillera/Benguet stepped agriculture tiers)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawTerraceGarden(ds: DrawScope, w: Float, h: Float) {
        // Morning Sky
        ds.drawRect(Color(0xFFFFF9C4), Offset.Zero, Size(w, h))

        // Distant Mountain Silhouette
        val mountain = Path().apply {
            moveTo(0f, h * 0.40f)
            lineTo(w * 0.35f, h * 0.20f)
            lineTo(w * 0.70f, h * 0.32f)
            lineTo(w, h * 0.15f)
            lineTo(w, h * 0.50f)
            lineTo(0f, h * 0.50f)
            close()
        }
        ds.drawPath(mountain, Color(0xFFC8E6C9))

        // Tier 1 (Top terrace)
        val tier1 = Path().apply {
            moveTo(0f, h * 0.36f)
            cubicTo(w * 0.5f, h * 0.32f, w * 0.7f, h * 0.40f, w, h * 0.35f)
            lineTo(w, h * 0.48f)
            lineTo(0f, h * 0.48f)
            close()
        }
        ds.drawPath(tier1, Color(0xFF689F38))
        // Stone retaining wall 1
        ds.drawRect(Color(0xFF78909C), Offset(0f, h * 0.46f), Size(w, h * 0.05f))

        // Tier 2 (Middle terrace)
        val tier2 = Path().apply {
            moveTo(0f, h * 0.51f)
            cubicTo(w * 0.3f, h * 0.48f, w * 0.8f, h * 0.55f, w, h * 0.50f)
            lineTo(w, h * 0.68f)
            lineTo(0f, h * 0.68f)
            close()
        }
        ds.drawPath(tier2, Color(0xFF388E3C))
        // Stone retaining wall 2
        ds.drawRect(Color(0xFF546E7A), Offset(0f, h * 0.66f), Size(w, h * 0.06f))

        // Tier 3 (Bottom terrace with crops)
        val tier3 = Path().apply {
            moveTo(0f, h * 0.72f)
            lineTo(w, h * 0.72f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        ds.drawPath(tier3, Color(0xFF2E7D32))

        // Cabbage dots across middle & bottom tiers
        for (i in 0..5) {
            ds.drawCircle(Color(0xFF81C784), radius = w * 0.035f, center = Offset(w * 0.12f + i * w * 0.16f, h * 0.58f))
            ds.drawCircle(Color(0xFFA5D6A7), radius = w * 0.040f, center = Offset(w * 0.08f + i * w * 0.18f, h * 0.76f))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4. Backyard Container Garden (Terracotta clay pots & watering can)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawContainerGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFF1F8E9), Offset.Zero, Size(w, h))

        // Paved patio ground
        ds.drawRect(Color(0xFFECEFF1), Offset(0f, h * 0.65f), Size(w, h * 0.35f))
        // Paver stone seam
        ds.drawLine(Color(0xFFCFD8DC), Offset(0f, h * 0.65f), Offset(w, h * 0.65f), strokeWidth = 2f)

        // Center large terracotta pot
        val potCenterX = w * 0.45f
        val potTop = h * 0.56f
        val potPath = Path().apply {
            moveTo(potCenterX - w * 0.18f, potTop)
            lineTo(potCenterX + w * 0.18f, potTop)
            lineTo(potCenterX + w * 0.13f, potTop + h * 0.26f)
            lineTo(potCenterX - w * 0.13f, potTop + h * 0.26f)
            close()
        }
        ds.drawPath(potPath, Color(0xFFD84315))
        // Pot rim
        ds.drawRoundRect(Color(0xFFBF360C), Offset(potCenterX - w * 0.20f, potTop - h * 0.04f), Size(w * 0.40f, h * 0.05f), androidx.compose.ui.geometry.CornerRadius(4f))

        // Plant in center pot (Staked bushy vine)
        ds.drawLine(Color(0xFF795548), Offset(potCenterX, potTop), Offset(potCenterX, potTop - h * 0.35f), strokeWidth = 3f)
        ds.drawCircle(Color(0xFF2E7D32), radius = w * 0.14f, center = Offset(potCenterX, potTop - h * 0.20f))
        ds.drawCircle(Color(0xFF43A047), radius = w * 0.10f, center = Offset(potCenterX, potTop - h * 0.22f))
        // Tomato dots
        ds.drawCircle(Color(0xFFE53935), radius = w * 0.035f, center = Offset(potCenterX - w * 0.06f, potTop - h * 0.18f))
        ds.drawCircle(Color(0xFFE53935), radius = w * 0.032f, center = Offset(potCenterX + w * 0.06f, potTop - h * 0.22f))

        // Left small pot with herbs
        val leftPotX = w * 0.18f
        val leftPotTop = h * 0.66f
        ds.drawRoundRect(Color(0xFFE64A19), Offset(leftPotX - w * 0.09f, leftPotTop), Size(w * 0.18f, h * 0.16f), androidx.compose.ui.geometry.CornerRadius(3f))
        ds.drawCircle(Color(0xFF66BB6A), radius = w * 0.07f, center = Offset(leftPotX, leftPotTop - h * 0.04f))

        // Right watering can
        val canX = w * 0.78f
        val canTop = h * 0.65f
        ds.drawRoundRect(Color(0xFF00897B), Offset(canX - w * 0.10f, canTop), Size(w * 0.20f, h * 0.17f), androidx.compose.ui.geometry.CornerRadius(6f))
        // Spout
        ds.drawLine(Color(0xFF00796B), Offset(canX - w * 0.08f, canTop + h * 0.06f), Offset(canX - w * 0.18f, canTop), strokeWidth = 4f, cap = StrokeCap.Round)
        // Handle
        ds.drawCircle(Color(0xFF00796B), radius = w * 0.06f, center = Offset(canX + w * 0.10f, canTop + h * 0.06f), style = Stroke(width = 3f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 5. Bamboo A-Frame Trellis Garden (Pergola balag with climbing vines)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawTrellisGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFF1F8E9), Offset.Zero, Size(w, h))

        // Ground soil
        ds.drawRect(Color(0xFF6D4C41), Offset(0f, h * 0.70f), Size(w, h * 0.30f))

        val apexX = w * 0.50f
        val apexY = h * 0.18f
        val leftFoot = w * 0.18f
        val rightFoot = w * 0.82f
        val groundY = h * 0.72f

        // Bamboo poles (Left leg & right leg)
        val bambooColor = Color(0xFFAFB42B)
        ds.drawLine(bambooColor, Offset(leftFoot, groundY), Offset(apexX + 6f, apexY - 8f), strokeWidth = 6f, cap = StrokeCap.Round)
        ds.drawLine(bambooColor, Offset(rightFoot, groundY), Offset(apexX - 6f, apexY - 8f), strokeWidth = 6f, cap = StrokeCap.Round)

        // Crossed pole lashings at apex
        ds.drawCircle(Color(0xFF5D4037), radius = 5f, center = Offset(apexX, apexY))

        // Horizontal crossbars
        ds.drawLine(bambooColor, Offset(w * 0.26f, h * 0.40f), Offset(w * 0.74f, h * 0.40f), strokeWidth = 4f)
        ds.drawLine(bambooColor, Offset(w * 0.22f, h * 0.56f), Offset(w * 0.78f, h * 0.56f), strokeWidth = 4f)

        // Trailing vines climbing poles
        val vineColor = Color(0xFF2E7D32)
        ds.drawCircle(vineColor, radius = w * 0.08f, center = Offset(w * 0.32f, h * 0.40f))
        ds.drawCircle(Color(0xFF43A047), radius = w * 0.07f, center = Offset(w * 0.68f, h * 0.42f))
        ds.drawCircle(vineColor, radius = w * 0.09f, center = Offset(apexX, apexY + h * 0.08f))

        // Hanging gourds / string beans
        ds.drawLine(Color(0xFF1B5E20), Offset(w * 0.38f, h * 0.42f), Offset(w * 0.38f, h * 0.58f), strokeWidth = 5f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFF388E3C), Offset(w * 0.62f, h * 0.44f), Offset(w * 0.62f, h * 0.62f), strokeWidth = 5f, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFF2E7D32), Offset(apexX, apexY + h * 0.16f), Offset(apexX, apexY + h * 0.32f), strokeWidth = 4f, cap = StrokeCap.Round)
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 6. Orchard & Canopy Fruit Garden (Tree with sun canopy & ground meadow)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawOrchardGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFE8F5E9), Offset.Zero, Size(w, h))

        // Ground meadow
        val meadow = Path().apply {
            moveTo(0f, h * 0.64f)
            quadraticTo(w * 0.5f, h * 0.58f, w, h * 0.64f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        ds.drawPath(meadow, Color(0xFFC8E6C9))

        // Tree trunk
        val trunkX = w * 0.50f
        val trunkPath = Path().apply {
            moveTo(trunkX - w * 0.06f, h * 0.72f)
            lineTo(trunkX - w * 0.04f, h * 0.40f)
            lineTo(trunkX - w * 0.12f, h * 0.30f)
            lineTo(trunkX, h * 0.38f)
            lineTo(trunkX + w * 0.12f, h * 0.30f)
            lineTo(trunkX + w * 0.04f, h * 0.40f)
            lineTo(trunkX + w * 0.06f, h * 0.72f)
            close()
        }
        ds.drawPath(trunkPath, Color(0xFF5D4037))

        // Canopy foliage clouds
        ds.drawCircle(Color(0xFF2E7D32), radius = w * 0.20f, center = Offset(trunkX - w * 0.12f, h * 0.28f))
        ds.drawCircle(Color(0xFF388E3C), radius = w * 0.22f, center = Offset(trunkX + w * 0.12f, h * 0.28f))
        ds.drawCircle(Color(0xFF43A047), radius = w * 0.24f, center = Offset(trunkX, h * 0.20f))

        // Golden fruits (Calamansi / Mango / Citrus)
        val fruitColor = Color(0xFFFFB300)
        ds.drawCircle(fruitColor, radius = w * 0.030f, center = Offset(trunkX - w * 0.16f, h * 0.24f))
        ds.drawCircle(fruitColor, radius = w * 0.032f, center = Offset(trunkX - w * 0.04f, h * 0.16f))
        ds.drawCircle(fruitColor, radius = w * 0.030f, center = Offset(trunkX + w * 0.14f, h * 0.22f))
        ds.drawCircle(fruitColor, radius = w * 0.028f, center = Offset(trunkX + w * 0.02f, h * 0.26f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 7. Hydroponic Vertical Tower Garden (White tower pipe with lush net cups)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawHydroponicGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFF1F8E9), Offset.Zero, Size(w, h))

        val towerX = w * 0.50f
        val towerWidth = w * 0.20f
        val towerTop = h * 0.16f
        val towerBottom = h * 0.68f

        // Base nutrient reservoir tank
        ds.drawRoundRect(Color(0xFF37474F), Offset(towerX - w * 0.22f, towerBottom), Size(w * 0.44f, h * 0.16f), androidx.compose.ui.geometry.CornerRadius(6f))
        // Blue water level window
        ds.drawRoundRect(Color(0xFF0288D1), Offset(towerX - w * 0.14f, towerBottom + h * 0.04f), Size(w * 0.28f, h * 0.05f), androidx.compose.ui.geometry.CornerRadius(4f))

        // Vertical PVC column
        ds.drawRoundRect(Color(0xFFECEFF1), Offset(towerX - towerWidth / 2f, towerTop), Size(towerWidth, towerBottom - towerTop), androidx.compose.ui.geometry.CornerRadius(6f))
        ds.drawRoundRect(Color(0xFFCFD8DC), Offset(towerX - towerWidth / 2f, towerTop), Size(towerWidth, towerBottom - towerTop), style = Stroke(width = 2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))

        // Lettuce cups bursting out at 3 levels
        val levels = listOf(0.26f, 0.40f, 0.54f)
        for (yLvl in levels) {
            val cupY = h * yLvl
            // Left foliage
            ds.drawCircle(Color(0xFF43A047), radius = w * 0.08f, center = Offset(towerX - towerWidth * 0.70f, cupY))
            ds.drawCircle(Color(0xFF66BB6A), radius = w * 0.05f, center = Offset(towerX - towerWidth * 0.70f, cupY))
            // Right foliage
            ds.drawCircle(Color(0xFF388E3C), radius = w * 0.08f, center = Offset(towerX + towerWidth * 0.70f, cupY))
            ds.drawCircle(Color(0xFF4CAF50), radius = w * 0.05f, center = Offset(towerX + towerWidth * 0.70f, cupY))
        }

        // Top cap
        ds.drawRoundRect(Color(0xFFB0BEC5), Offset(towerX - towerWidth * 0.55f, towerTop - h * 0.02f), Size(towerWidth * 1.1f, h * 0.04f), androidx.compose.ui.geometry.CornerRadius(3f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 8. Traditional Furrow (Tudling) Garden (Diagonal soil ridges with irrigation)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawFurrowGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFFFF3E0), Offset.Zero, Size(w, h))

        // 3 Plowed soil furrow ridges
        val furrows = listOf(
            Triple(h * 0.38f, Color(0xFF6D4C41), Color(0xFF5D4037)),
            Triple(h * 0.52f, Color(0xFF5D4037), Color(0xFF4E342E)),
            Triple(h * 0.68f, Color(0xFF4E342E), Color(0xFF3E2723))
        )

        for ((furrowY, ridgeColor, trenchColor) in furrows) {
            // Trench shadow / irrigation line
            ds.drawRect(trenchColor, Offset(0f, furrowY), Size(w, h * 0.04f))
            // Irrigation water sheen
            ds.drawRect(Color(0xFF81D4FA).copy(alpha = 0.5f), Offset(0f, furrowY + h * 0.01f), Size(w, h * 0.02f))
            // Soil mound ridge
            val ridge = Path().apply {
                moveTo(0f, furrowY + h * 0.04f)
                cubicTo(w * 0.5f, furrowY + h * 0.02f, w * 0.8f, furrowY + h * 0.06f, w, furrowY + h * 0.04f)
                lineTo(w, furrowY + h * 0.12f)
                lineTo(0f, furrowY + h * 0.12f)
                close()
            }
            ds.drawPath(ridge, ridgeColor)

            // Crops along the ridge
            for (i in 0..5) {
                val cropX = w * 0.10f + i * w * 0.16f
                ds.drawCircle(Color(0xFF43A047), radius = w * 0.038f, center = Offset(cropX, furrowY + h * 0.06f))
                ds.drawLine(Color(0xFF2E7D32), Offset(cropX, furrowY + h * 0.06f), Offset(cropX, furrowY + h * 0.09f), strokeWidth = 2f)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 9. Shaded Seedling Nursery (Overhead 30% mesh with seedling tray cell grid)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawNurseryGarden(ds: DrawScope, w: Float, h: Float) {
        ds.drawRect(Color(0xFFE8F5E9), Offset.Zero, Size(w, h))

        // Overhead Shade Netting Roof (30% black shade cloth)
        ds.drawRect(Color(0xFF212121).copy(alpha = 0.18f), Offset(0f, 0f), Size(w, h * 0.32f))
        // Net weave grid lines
        for (i in 0..6) {
            ds.drawLine(Color(0xFF1B5E20).copy(alpha = 0.35f), Offset(i * w * 0.16f, 0f), Offset(i * w * 0.16f, h * 0.32f), strokeWidth = 1.5f)
        }
        ds.drawLine(Color(0xFF5D4037), Offset(0f, h * 0.32f), Offset(w, h * 0.32f), strokeWidth = 3f)

        // Nursery Benches
        val tableTop = h * 0.52f
        ds.drawRect(Color(0xFF795548), Offset(w * 0.10f, tableTop), Size(w * 0.80f, h * 0.08f))
        // Table legs
        ds.drawLine(Color(0xFF5D4037), Offset(w * 0.16f, tableTop + h * 0.08f), Offset(w * 0.16f, h * 0.82f), strokeWidth = 4f)
        ds.drawLine(Color(0xFF5D4037), Offset(w * 0.84f, tableTop + h * 0.08f), Offset(w * 0.84f, h * 0.82f), strokeWidth = 4f)

        // Seedling Cell Trays on Table (Black 104-hole trays)
        ds.drawRoundRect(Color(0xFF263238), Offset(w * 0.14f, tableTop - h * 0.04f), Size(w * 0.34f, h * 0.06f), androidx.compose.ui.geometry.CornerRadius(3f))
        ds.drawRoundRect(Color(0xFF263238), Offset(w * 0.52f, tableTop - h * 0.04f), Size(w * 0.34f, h * 0.06f), androidx.compose.ui.geometry.CornerRadius(3f))

        // Tiny Cotyledon Sprouts (2 leaves each)
        for (i in 0..3) {
            val sproutX1 = w * 0.18f + i * w * 0.08f
            val sproutX2 = w * 0.56f + i * w * 0.08f
            ds.drawCircle(Color(0xFF81C784), radius = w * 0.024f, center = Offset(sproutX1, tableTop - h * 0.06f))
            ds.drawCircle(Color(0xFF4CAF50), radius = w * 0.024f, center = Offset(sproutX2, tableTop - h * 0.06f))
        }

        // Mist water droplets falling
        ds.drawCircle(Color(0xFF80DEEA), radius = 2.5f, center = Offset(w * 0.30f, h * 0.40f))
        ds.drawCircle(Color(0xFF80DEEA), radius = 2f, center = Offset(w * 0.50f, h * 0.36f))
        ds.drawCircle(Color(0xFF80DEEA), radius = 2.5f, center = Offset(w * 0.70f, h * 0.42f))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 10. Homestead Picket-Fenced Garden (White fence with arched arbor & sunflowers)
    // ─────────────────────────────────────────────────────────────────────────────
    private fun drawHomesteadGarden(ds: DrawScope, w: Float, h: Float) {
        // Soft daylight sky
        ds.drawRect(Color(0xFFE3F2FD), Offset.Zero, Size(w, h))

        // Ground lawn
        ds.drawRect(Color(0xFFA5D6A7), Offset(0f, h * 0.60f), Size(w, h * 0.40f))

        // Sunflowers / corn behind fence
        ds.drawCircle(Color(0xFFFDD835), radius = w * 0.08f, center = Offset(w * 0.20f, h * 0.38f))
        ds.drawCircle(Color(0xFF5D4037), radius = w * 0.035f, center = Offset(w * 0.20f, h * 0.38f))

        ds.drawCircle(Color(0xFFFBC02D), radius = w * 0.07f, center = Offset(w * 0.80f, h * 0.40f))
        ds.drawCircle(Color(0xFF4E342E), radius = w * 0.030f, center = Offset(w * 0.80f, h * 0.40f))

        // Stems
        ds.drawLine(Color(0xFF2E7D32), Offset(w * 0.20f, h * 0.38f), Offset(w * 0.20f, h * 0.65f), strokeWidth = 3f)
        ds.drawLine(Color(0xFF2E7D32), Offset(w * 0.80f, h * 0.40f), Offset(w * 0.80f, h * 0.65f), strokeWidth = 3f)

        // White picket fence across card
        val fenceY = h * 0.58f
        val picketCount = 9
        val picketSpacing = w / picketCount

        // Horizontal rails
        ds.drawLine(Color.White, Offset(0f, fenceY + h * 0.06f), Offset(w, fenceY + h * 0.06f), strokeWidth = 3f)
        ds.drawLine(Color.White, Offset(0f, fenceY + h * 0.16f), Offset(w, fenceY + h * 0.16f), strokeWidth = 3f)

        // Vertical pickets with pointed tops
        for (i in 0 until picketCount) {
            val px = i * picketSpacing + picketSpacing * 0.25f
            val pw = picketSpacing * 0.50f
            val pt = fenceY
            val pb = fenceY + h * 0.24f

            val picketPath = Path().apply {
                moveTo(px + pw / 2f, pt) // Point tip
                lineTo(px + pw, pt + h * 0.04f)
                lineTo(px + pw, pb)
                lineTo(px, pb)
                lineTo(px, pt + h * 0.04f)
                close()
            }
            ds.drawPath(picketPath, Color.White)
            ds.drawPath(picketPath, Color(0xFFCFD8DC), style = Stroke(width = 1f))
        }

        // Cobblestone path in center
        val pathP = Path().apply {
            moveTo(w * 0.42f, h * 0.68f)
            lineTo(w * 0.58f, h * 0.68f)
            lineTo(w * 0.66f, h)
            lineTo(w * 0.34f, h)
            close()
        }
        ds.drawPath(pathP, Color(0xFFB0BEC5))
    }
}
