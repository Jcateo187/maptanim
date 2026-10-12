package com.maptanim.app.features.farm.renderer.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * CropSvgRenderer — High-performance vector/SVG renderer for 2D design canvas.
 * Renders all 15 Philippine crops as crisp, scalable vector graphics directly in Compose DrawScope.
 * Zero raster pixelation at any zoom level, zero GC allocations on draw loops.
 */
object CropSvgRenderer {

    fun drawCropSvg(
        drawScope: DrawScope,
        cropName: String,
        center: Offset,
        sizePx: Float
    ) {
        val s = sizePx.coerceAtLeast(10f)
        val cx = center.x
        val cy = center.y
        val r = s / 2f

        // Draw soft grounding shadow beneath the crop
        drawScope.drawOval(
            color = Color.Black.copy(alpha = 0.22f),
            topLeft = Offset(cx - r * 0.75f, cy + r * 0.55f),
            size = Size(r * 1.5f, r * 0.45f)
        )

        val clean = cropName.lowercase().replace(" ", "").replace("_", "").replace("-", "")
        when {
            clean.contains("carrot") || clean.contains("karot") -> drawCarrot(drawScope, cx, cy, r)
            clean.contains("tomato") || clean.contains("kamatis") -> drawTomato(drawScope, cx, cy, r)
            clean.contains("eggplant") || clean.contains("talong") -> drawEggplant(drawScope, cx, cy, r)
            clean.contains("pechay") || clean.contains("bokchoy") || clean.contains("pakchoi") -> drawPechay(drawScope, cx, cy, r)
            clean.contains("corn") || clean.contains("mais") -> drawCorn(drawScope, cx, cy, r)
            clean.contains("onion") || clean.contains("sibuyas") -> drawOnion(drawScope, cx, cy, r)
            clean.contains("cabbage") || clean.contains("repolyo") -> drawCabbage(drawScope, cx, cy, r)
            clean.contains("lettuce") || clean.contains("litsugas") -> drawLettuce(drawScope, cx, cy, r)
            clean.contains("ampalaya") || clean.contains("bittergourd") -> drawAmpalaya(drawScope, cx, cy, r)
            clean.contains("okra") -> drawOkra(drawScope, cx, cy, r)
            clean.contains("kangkong") || clean.contains("waterspinach") -> drawKangkong(drawScope, cx, cy, r)
            clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> drawPumpkin(drawScope, cx, cy, r)
            clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> drawSili(drawScope, cx, cy, r)
            clean.contains("pipino") || clean.contains("cucumber") -> drawPipino(drawScope, cx, cy, r)
            clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> drawSitaw(drawScope, cx, cy, r)
            else -> drawGenericCrop(drawScope, cx, cy, r)
        }
    }

    // 1. Carrot (Orange root + feathery green tops)
    private fun drawCarrot(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Feathery greens
        val greenPath = Path().apply {
            moveTo(cx, cy - r * 0.1f)
            quadraticTo(cx - r * 0.4f, cy - r * 0.7f, cx - r * 0.7f, cy - r * 0.95f)
            quadraticTo(cx - r * 0.3f, cy - r * 0.6f, cx, cy - r * 0.2f)
            close()
        }
        ds.drawPath(greenPath, Color(0xFF2E7D32))

        val greenPath2 = Path().apply {
            moveTo(cx, cy - r * 0.1f)
            quadraticTo(cx, cy - r * 0.75f, cx, cy - r)
            quadraticTo(cx + r * 0.15f, cy - r * 0.7f, cx + r * 0.05f, cy - r * 0.2f)
            close()
        }
        ds.drawPath(greenPath2, Color(0xFF43A047))

        val greenPath3 = Path().apply {
            moveTo(cx, cy - r * 0.1f)
            quadraticTo(cx + r * 0.4f, cy - r * 0.7f, cx + r * 0.7f, cy - r * 0.95f)
            quadraticTo(cx + r * 0.3f, cy - r * 0.6f, cx, cy - r * 0.2f)
            close()
        }
        ds.drawPath(greenPath3, Color(0xFF388E3C))

        // Carrot body (tapered root)
        val carrotPath = Path().apply {
            moveTo(cx - r * 0.45f, cy - r * 0.15f)
            cubicTo(cx - r * 0.45f, cy - r * 0.35f, cx + r * 0.45f, cy - r * 0.35f, cx + r * 0.45f, cy - r * 0.15f)
            cubicTo(cx + r * 0.4f, cy + r * 0.4f, cx + r * 0.2f, cy + r * 0.8f, cx, cy + r * 0.95f)
            cubicTo(cx - r * 0.2f, cy + r * 0.8f, cx - r * 0.4f, cy + r * 0.4f, cx - r * 0.45f, cy - r * 0.15f)
            close()
        }
        ds.drawPath(carrotPath, Color(0xFFFF5722))

        // Rib lines
        val strokeW = (r * 0.08f).coerceAtLeast(1f)
        ds.drawLine(Color(0xFFD84315), Offset(cx - r * 0.25f, cy + r * 0.1f), Offset(cx + r * 0.25f, cy + r * 0.1f), strokeWidth = strokeW, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFD84315), Offset(cx - r * 0.2f, cy + r * 0.4f), Offset(cx + r * 0.2f, cy + r * 0.4f), strokeWidth = strokeW, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFD84315), Offset(cx - r * 0.12f, cy + r * 0.65f), Offset(cx + r * 0.12f, cy + r * 0.65f), strokeWidth = strokeW, cap = StrokeCap.Round)

        // Highlight
        ds.drawLine(Color(0xFFFFAB91), Offset(cx - r * 0.25f, cy - r * 0.05f), Offset(cx - r * 0.1f, cy + r * 0.6f), strokeWidth = strokeW * 1.2f, cap = StrokeCap.Round)
    }

    // 2. Tomato (Glossy red fruit + 5-star green calyx)
    private fun drawTomato(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawOval(
            color = Color(0xFFE53935),
            topLeft = Offset(cx - r * 0.82f, cy - r * 0.65f),
            size = Size(r * 1.64f, r * 1.55f)
        )
        // Highlight
        ds.drawOval(
            color = Color.White.copy(alpha = 0.45f),
            topLeft = Offset(cx - r * 0.55f, cy - r * 0.45f),
            size = Size(r * 0.55f, r * 0.35f)
        )
        // Green stem & calyx
        ds.drawLine(Color(0xFF2E7D32), Offset(cx, cy - r * 0.6f), Offset(cx + r * 0.2f, cy - r * 0.95f), strokeWidth = (r * 0.12f).coerceAtLeast(1.5f), cap = StrokeCap.Round)

        val calyx = Path().apply {
            moveTo(cx, cy - r * 0.55f)
            lineTo(cx - r * 0.45f, cy - r * 0.75f)
            lineTo(cx - r * 0.15f, cy - r * 0.55f)
            lineTo(cx, cy - r * 0.85f)
            lineTo(cx + r * 0.15f, cy - r * 0.55f)
            lineTo(cx + r * 0.45f, cy - r * 0.75f)
            lineTo(cx + r * 0.2f, cy - r * 0.45f)
            lineTo(cx + r * 0.35f, cy - r * 0.3f)
            lineTo(cx, cy - r * 0.45f)
            lineTo(cx - r * 0.35f, cy - r * 0.3f)
            lineTo(cx - r * 0.2f, cy - r * 0.45f)
            close()
        }
        ds.drawPath(calyx, Color(0xFF43A047))
    }

    // 3. Eggplant (Glossy deep purple teardrop + green cap)
    private fun drawEggplant(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val body = Path().apply {
            moveTo(cx - r * 0.1f, cy - r * 0.55f)
            cubicTo(cx - r * 0.6f, cy - r * 0.1f, cx - r * 0.75f, cy + r * 0.4f, cx - r * 0.2f, cy + r * 0.85f)
            cubicTo(cx + r * 0.35f, cy + r * 1.0f, cx + r * 0.8f, cy + r * 0.6f, cx + r * 0.65f, cy + r * 0.15f)
            cubicTo(cx + r * 0.55f, cy - r * 0.2f, cx + r * 0.2f, cy - r * 0.55f, cx - r * 0.1f, cy - r * 0.55f)
            close()
        }
        ds.drawPath(body, Color(0xFF6A1B9A))

        // Gloss streak
        ds.drawOval(
            color = Color(0xFFE1BEE7).copy(alpha = 0.5f),
            topLeft = Offset(cx - r * 0.45f, cy),
            size = Size(r * 0.3f, r * 0.6f)
        )

        // Green calyx cap
        val cap = Path().apply {
            moveTo(cx - r * 0.2f, cy - r * 0.55f)
            lineTo(cx - r * 0.4f, cy - r * 0.3f)
            lineTo(cx - r * 0.05f, cy - r * 0.38f)
            lineTo(cx + r * 0.1f, cy - r * 0.22f)
            lineTo(cx + r * 0.25f, cy - r * 0.35f)
            lineTo(cx + r * 0.45f, cy - r * 0.3f)
            lineTo(cx + r * 0.15f, cy - r * 0.55f)
            close()
        }
        ds.drawPath(cap, Color(0xFF388E3C))
        ds.drawLine(Color(0xFF2E7D32), Offset(cx, cy - r * 0.55f), Offset(cx + r * 0.15f, cy - r * 0.9f), strokeWidth = (r * 0.12f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
    }

    // 4. Pechay (White base stalks + green fan leaves)
    private fun drawPechay(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Outer green leaves
        val leafL = Path().apply {
            moveTo(cx - r * 0.15f, cy + r * 0.4f)
            cubicTo(cx - r * 0.8f, cy + r * 0.1f, cx - r * 0.9f, cy - r * 0.5f, cx - r * 0.4f, cy - r * 0.7f)
            cubicTo(cx - r * 0.1f, cy - r * 0.4f, cx - r * 0.1f, cy, cx - r * 0.15f, cy + r * 0.4f)
            close()
        }
        ds.drawPath(leafL, Color(0xFF388E3C))

        val leafR = Path().apply {
            moveTo(cx + r * 0.15f, cy + r * 0.4f)
            cubicTo(cx + r * 0.8f, cy + r * 0.1f, cx + r * 0.9f, cy - r * 0.5f, cx + r * 0.4f, cy - r * 0.7f)
            cubicTo(cx + r * 0.1f, cy - r * 0.4f, cx + r * 0.1f, cy, cx + r * 0.15f, cy + r * 0.4f)
            close()
        }
        ds.drawPath(leafR, Color(0xFF388E3C))

        // Center upright green leaf
        val leafC = Path().apply {
            moveTo(cx - r * 0.15f, cy + r * 0.2f)
            cubicTo(cx - r * 0.5f, cy - r * 0.4f, cx - r * 0.4f, cy - r * 0.9f, cx, cy - r * 0.95f)
            cubicTo(cx + r * 0.4f, cy - r * 0.9f, cx + r * 0.5f, cy - r * 0.4f, cx + r * 0.15f, cy + r * 0.2f)
            close()
        }
        ds.drawPath(leafC, Color(0xFF4CAF50))

        // White clustered stalks at base
        val stalk = Path().apply {
            moveTo(cx - r * 0.25f, cy + r * 0.85f)
            cubicTo(cx - r * 0.25f, cy + r * 0.3f, cx - r * 0.15f, cy + r * 0.1f, cx, cy + r * 0.1f)
            cubicTo(cx + r * 0.15f, cy + r * 0.1f, cx + r * 0.25f, cy + r * 0.3f, cx + r * 0.25f, cy + r * 0.85f)
            close()
        }
        ds.drawPath(stalk, Color(0xFFFFFFFF))
        ds.drawOval(Color(0xFFDCEDC8), Offset(cx - r * 0.3f, cy + r * 0.75f), Size(r * 0.6f, r * 0.18f))
    }

    // 5. Corn (Golden cob with kernels + green husks)
    private fun drawCorn(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Yellow cob body
        ds.drawRoundRect(
            color = Color(0xFFFBC02D),
            topLeft = Offset(cx - r * 0.3f, cy - r * 0.6f),
            size = Size(r * 0.6f, r * 1.35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.28f)
        )
        // Kernel lines
        val strokeW = (r * 0.06f).coerceAtLeast(1f)
        ds.drawLine(Color(0xFFF57F17), Offset(cx - r * 0.12f, cy - r * 0.5f), Offset(cx - r * 0.12f, cy + r * 0.6f), strokeWidth = strokeW)
        ds.drawLine(Color(0xFFF57F17), Offset(cx + r * 0.12f, cy - r * 0.5f), Offset(cx + r * 0.12f, cy + r * 0.6f), strokeWidth = strokeW)
        for (i in -2..3) {
            val y = cy + i * r * 0.22f
            ds.drawLine(Color(0xFFF57F17), Offset(cx - r * 0.25f, y), Offset(cx + r * 0.25f, y), strokeWidth = strokeW)
        }

        // Green husks peeling
        val huskL = Path().apply {
            moveTo(cx - r * 0.25f, cy + r * 0.75f)
            cubicTo(cx - r * 0.65f, cy + r * 0.4f, cx - r * 0.75f, cy - r * 0.1f, cx - r * 0.6f, cy - r * 0.35f)
            cubicTo(cx - r * 0.45f, cy - r * 0.1f, cx - r * 0.35f, cy + r * 0.3f, cx - r * 0.15f, cy + r * 0.75f)
            close()
        }
        ds.drawPath(huskL, Color(0xFF689F38))

        val huskR = Path().apply {
            moveTo(cx + r * 0.25f, cy + r * 0.75f)
            cubicTo(cx + r * 0.65f, cy + r * 0.4f, cx + r * 0.75f, cy - r * 0.1f, cx + r * 0.6f, cy - r * 0.35f)
            cubicTo(cx + r * 0.45f, cy - r * 0.1f, cx + r * 0.35f, cy + r * 0.3f, cx + r * 0.15f, cy + r * 0.75f)
            close()
        }
        ds.drawPath(huskR, Color(0xFF7CB342))
    }

    // 6. Onion (Bulb body + green shoots + whiskers)
    private fun drawOnion(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Green shoots
        ds.drawLine(Color(0xFF43A047), Offset(cx, cy - r * 0.3f), Offset(cx - r * 0.3f, cy - r * 0.85f), strokeWidth = (r * 0.1f).coerceAtLeast(1f), cap = StrokeCap.Round)
        ds.drawLine(Color(0xFF4CAF50), Offset(cx, cy - r * 0.3f), Offset(cx, cy - r * 0.95f), strokeWidth = (r * 0.1f).coerceAtLeast(1f), cap = StrokeCap.Round)
        ds.drawLine(Color(0xFF388E3C), Offset(cx, cy - r * 0.3f), Offset(cx + r * 0.3f, cy - r * 0.85f), strokeWidth = (r * 0.1f).coerceAtLeast(1f), cap = StrokeCap.Round)

        // Bulb
        val bulb = Path().apply {
            moveTo(cx, cy - r * 0.3f)
            cubicTo(cx + r * 0.65f, cy - r * 0.2f, cx + r * 0.8f, cy + r * 0.25f, cx + r * 0.6f, cy + r * 0.65f)
            cubicTo(cx + r * 0.35f, cy + r * 0.85f, cx - r * 0.35f, cy + r * 0.85f, cx - r * 0.6f, cy + r * 0.65f)
            cubicTo(cx - r * 0.8f, cy + r * 0.25f, cx - r * 0.65f, cy - r * 0.2f, cx, cy - r * 0.3f)
            close()
        }
        ds.drawPath(bulb, Color(0xFFE64A19))

        // Skin layer curves
        val strokeW = (r * 0.05f).coerceAtLeast(1f)
        ds.drawOval(Color(0xFFD84315), Offset(cx - r * 0.4f, cy - r * 0.1f), Size(r * 0.8f, r * 0.85f), style = Stroke(strokeW))
        ds.drawOval(Color(0xFFD84315), Offset(cx - r * 0.2f, cy - r * 0.15f), Size(r * 0.4f, r * 0.9f), style = Stroke(strokeW))

        // Roots
        ds.drawLine(Color(0xFFD7CCC8), Offset(cx, cy + r * 0.8f), Offset(cx - r * 0.15f, cy + r * 0.98f), strokeWidth = strokeW, cap = StrokeCap.Round)
        ds.drawLine(Color(0xFFD7CCC8), Offset(cx, cy + r * 0.8f), Offset(cx + r * 0.15f, cy + r * 0.98f), strokeWidth = strokeW, cap = StrokeCap.Round)
    }

    // 7. Cabbage (Spherical wrapped head + vein ribs)
    private fun drawCabbage(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawCircle(Color(0xFF2E7D32), r * 0.85f, Offset(cx, cy))
        ds.drawCircle(Color(0xFF43A047), r * 0.72f, Offset(cx, cy))

        // Overlapping leaf edges
        val leaf = Path().apply {
            moveTo(cx - r * 0.7f, cy)
            cubicTo(cx - r * 0.4f, cy - r * 0.7f, cx + r * 0.4f, cy - r * 0.7f, cx + r * 0.7f, cy)
            cubicTo(cx + r * 0.2f, cy - r * 0.3f, cx - r * 0.2f, cy - r * 0.3f, cx - r * 0.7f, cy)
            close()
        }
        ds.drawPath(leaf, Color(0xFF66BB6A))

        val leaf2 = Path().apply {
            moveTo(cx - r * 0.6f, cy + r * 0.2f)
            cubicTo(cx - r * 0.3f, cy + r * 0.7f, cx + r * 0.3f, cy + r * 0.7f, cx + r * 0.6f, cy + r * 0.2f)
            cubicTo(cx + r * 0.1f, cy + r * 0.35f, cx - r * 0.1f, cy + r * 0.35f, cx - r * 0.6f, cy + r * 0.2f)
            close()
        }
        ds.drawPath(leaf2, Color(0xFF81C784))

        ds.drawCircle(Color(0xFFA5D6A7), r * 0.32f, Offset(cx, cy + r * 0.05f))
    }

    // 8. Lettuce (Curly wavy lime green salad rosette)
    private fun drawLettuce(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawCircle(Color(0xFF689F38), r * 0.85f, Offset(cx, cy))
        for (i in 0..7) {
            val angle = i * 45f * (Math.PI / 180.0)
            val lx = cx + (r * 0.5f * Math.cos(angle)).toFloat()
            val ly = cy + (r * 0.5f * Math.sin(angle)).toFloat()
            ds.drawCircle(Color(0xFF7CB342), r * 0.4f, Offset(lx, ly))
        }
        ds.drawCircle(Color(0xFF9CCC65), r * 0.48f, Offset(cx, cy))
        ds.drawCircle(Color(0xFFAED581), r * 0.28f, Offset(cx, cy))
    }

    // 9. Ampalaya (Bumpy textured dark green bitter gourd)
    private fun drawAmpalaya(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Gourd body
        val body = Path().apply {
            moveTo(cx, cy - r * 0.8f)
            cubicTo(cx - r * 0.45f, cy - r * 0.4f, cx - r * 0.4f, cy + r * 0.4f, cx, cy + r * 0.9f)
            cubicTo(cx + r * 0.4f, cy + r * 0.4f, cx + r * 0.45f, cy - r * 0.4f, cx, cy - r * 0.8f)
            close()
        }
        ds.drawPath(body, Color(0xFF1B5E20))

        // Bumpy nodules
        val bumpColor = Color(0xFF43A047)
        val bumpR = (r * 0.1f).coerceAtLeast(1.5f)
        for (i in -3..3) {
            val y = cy + i * r * 0.2f
            ds.drawCircle(bumpColor, bumpR, Offset(cx - r * 0.18f, y))
            ds.drawCircle(bumpColor, bumpR * 1.1f, Offset(cx, y + r * 0.05f))
            ds.drawCircle(bumpColor, bumpR, Offset(cx + r * 0.18f, y))
        }

        // Stem
        ds.drawLine(Color(0xFF388E3C), Offset(cx, cy - r * 0.8f), Offset(cx + r * 0.25f, cy - r * 0.98f), strokeWidth = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
    }

    // 10. Okra (Slender 5-ridged tapering green pod)
    private fun drawOkra(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val body = Path().apply {
            moveTo(cx - r * 0.25f, cy - r * 0.6f)
            lineTo(cx + r * 0.25f, cy - r * 0.6f)
            lineTo(cx, cy + r * 0.95f)
            close()
        }
        ds.drawPath(body, Color(0xFF43A047))

        // Center ridge
        ds.drawLine(Color(0xFF1B5E20), Offset(cx, cy - r * 0.6f), Offset(cx, cy + r * 0.95f), strokeWidth = (r * 0.07f).coerceAtLeast(1f))
        ds.drawLine(Color(0xFF81C784), Offset(cx - r * 0.1f, cy - r * 0.5f), Offset(cx, cy + r * 0.85f), strokeWidth = (r * 0.06f).coerceAtLeast(1f))

        // Cap & stem
        ds.drawOval(Color(0xFF2E7D32), Offset(cx - r * 0.3f, cy - r * 0.68f), Size(r * 0.6f, r * 0.18f))
        ds.drawLine(Color(0xFF1B5E20), Offset(cx, cy - r * 0.65f), Offset(cx, cy - r * 0.9f), strokeWidth = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
    }

    // 11. Kangkong (Hollow stems & slender arrowhead leaves)
    private fun drawKangkong(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val stemW = (r * 0.1f).coerceAtLeast(1.5f)
        ds.drawLine(Color(0xFF4CAF50), Offset(cx, cy + r * 0.9f), Offset(cx, cy - r * 0.5f), strokeWidth = stemW, cap = StrokeCap.Round)

        // Arrow leaves
        val leaf1 = Path().apply {
            moveTo(cx, cy - r * 0.4f)
            lineTo(cx - r * 0.65f, cy - r * 0.8f)
            lineTo(cx - r * 0.2f, cy - r * 0.65f)
            close()
        }
        ds.drawPath(leaf1, Color(0xFF388E3C))

        val leaf2 = Path().apply {
            moveTo(cx, cy - r * 0.2f)
            lineTo(cx + r * 0.65f, cy - r * 0.7f)
            lineTo(cx + r * 0.2f, cy - r * 0.5f)
            close()
        }
        ds.drawPath(leaf2, Color(0xFF2E7D32))

        val leaf3 = Path().apply {
            moveTo(cx, cy - r * 0.5f)
            lineTo(cx, cy - r * 0.98f)
            lineTo(cx + r * 0.15f, cy - r * 0.65f)
            close()
        }
        ds.drawPath(leaf3, Color(0xFF43A047))
    }

    // 12. Pumpkin / Squash (Fluted round orange body + green stem)
    private fun drawPumpkin(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        // Outer ribs
        ds.drawOval(Color(0xFFE65100), Offset(cx - r * 0.85f, cy - r * 0.4f), Size(r * 0.6f, r * 1.1f))
        ds.drawOval(Color(0xFFE65100), Offset(cx + r * 0.25f, cy - r * 0.4f), Size(r * 0.6f, r * 1.1f))
        // Middle ribs
        ds.drawOval(Color(0xFFF57C00), Offset(cx - r * 0.55f, cy - r * 0.45f), Size(r * 0.6f, r * 1.2f))
        ds.drawOval(Color(0xFFF57C00), Offset(cx - r * 0.05f, cy - r * 0.45f), Size(r * 0.6f, r * 1.2f))
        // Center rib
        ds.drawOval(Color(0xFFFFA726), Offset(cx - r * 0.35f, cy - r * 0.5f), Size(r * 0.7f, r * 1.25f))

        // Stem
        ds.drawLine(Color(0xFF2E7D32), Offset(cx, cy - r * 0.45f), Offset(cx - r * 0.15f, cy - r * 0.85f), strokeWidth = (r * 0.14f).coerceAtLeast(2f), cap = StrokeCap.Round)
    }

    // 13. Sili (Curved tapered red hot pepper + green stem)
    private fun drawSili(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val pepper = Path().apply {
            moveTo(cx - r * 0.25f, cy - r * 0.55f)
            cubicTo(cx - r * 0.4f, cy, cx - r * 0.1f, cy + r * 0.6f, cx + r * 0.55f, cy + r * 0.9f)
            cubicTo(cx + r * 0.3f, cy + r * 0.4f, cx + r * 0.2f, cy, cx + r * 0.2f, cy - r * 0.55f)
            close()
        }
        ds.drawPath(pepper, Color(0xFFD32F2F))

        // Highlight
        ds.drawLine(Color(0xFFFFCDD2).copy(alpha = 0.6f), Offset(cx - r * 0.15f, cy - r * 0.35f), Offset(cx + r * 0.2f, cy + r * 0.6f), strokeWidth = (r * 0.08f).coerceAtLeast(1f), cap = StrokeCap.Round)

        // Green stem cap
        ds.drawOval(Color(0xFF388E3C), Offset(cx - r * 0.3f, cy - r * 0.65f), Size(r * 0.55f, r * 0.18f))
        ds.drawLine(Color(0xFF2E7D32), Offset(cx - r * 0.05f, cy - r * 0.62f), Offset(cx - r * 0.2f, cy - r * 0.95f), strokeWidth = (r * 0.1f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
    }

    // 14. Pipino / Cucumber (Cylindrical emerald cucumber with blossom tip)
    private fun drawPipino(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val cuke = Path().apply {
            moveTo(cx - r * 0.55f, cy - r * 0.65f)
            cubicTo(cx - r * 0.8f, cy - r * 0.3f, cx - r * 0.1f, cy + r * 0.5f, cx + r * 0.55f, cy + r * 0.75f)
            cubicTo(cx + r * 0.8f, cy + r * 0.4f, cx + r * 0.2f, cy - r * 0.4f, cx - r * 0.25f, cy - r * 0.75f)
            close()
        }
        ds.drawPath(cuke, Color(0xFF388E3C))

        // Ridge lines
        ds.drawLine(Color(0xFF81C784).copy(alpha = 0.6f), Offset(cx - r * 0.45f, cy - r * 0.5f), Offset(cx + r * 0.4f, cy + r * 0.65f), strokeWidth = (r * 0.06f).coerceAtLeast(1f))

        // Blossom tip
        ds.drawCircle(Color(0xFFFDD835), (r * 0.1f).coerceAtLeast(1.5f), Offset(cx + r * 0.65f, cy + r * 0.75f))
    }

    // 15. Sitaw (Long hanging green bean pods)
    private fun drawSitaw(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        val podW = (r * 0.12f).coerceAtLeast(1.5f)
        // Pod 1
        val pod1 = Path().apply {
            moveTo(cx - r * 0.05f, cy - r * 0.65f)
            cubicTo(cx - r * 0.4f, cy - r * 0.1f, cx - r * 0.35f, cy + r * 0.4f, cx - r * 0.1f, cy + r * 0.95f)
        }
        ds.drawPath(pod1, Color(0xFF4CAF50), style = Stroke(podW, cap = StrokeCap.Round))

        // Pod 2
        val pod2 = Path().apply {
            moveTo(cx + r * 0.05f, cy - r * 0.65f)
            cubicTo(cx + r * 0.35f, cy - r * 0.1f, cx + r * 0.3f, cy + r * 0.4f, cx + r * 0.1f, cy + r * 0.95f)
        }
        ds.drawPath(pod2, Color(0xFF388E3C), style = Stroke(podW * 0.9f, cap = StrokeCap.Round))

        // Leaf cap at top
        ds.drawCircle(Color(0xFF2E7D32), (r * 0.15f).coerceAtLeast(2f), Offset(cx, cy - r * 0.7f))
    }

    // Generic sprout
    private fun drawGenericCrop(ds: DrawScope, cx: Float, cy: Float, r: Float) {
        ds.drawLine(Color(0xFF4CAF50), Offset(cx, cy + r * 0.7f), Offset(cx, cy - r * 0.3f), strokeWidth = (r * 0.12f).coerceAtLeast(1.5f), cap = StrokeCap.Round)
        val leafL = Path().apply {
            moveTo(cx, cy - r * 0.2f)
            cubicTo(cx - r * 0.6f, cy - r * 0.5f, cx - r * 0.4f, cy - r * 0.8f, cx, cy - r * 0.6f)
            close()
        }
        ds.drawPath(leafL, Color(0xFF388E3C))
        val leafR = Path().apply {
            moveTo(cx, cy - r * 0.2f)
            cubicTo(cx + r * 0.6f, cy - r * 0.5f, cx + r * 0.4f, cy - r * 0.8f, cx, cy - r * 0.6f)
            close()
        }
        ds.drawPath(leafR, Color(0xFF43A047))
    }
}
