package com.maptanim.app.ui.screens.knowledgebase

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.data.datasource.CropVarietyInfo
import com.maptanim.app.data.datasource.WhyDetailInfo
import com.maptanim.app.domain.model.Crop

private enum class CropGuideTab(val title: String, val icon: String) {
    LUPA("Lupa", "🌍"),
    TANIM("Tanim", "🌱"),
    BALAG("Balag", "🪵"),
    ALAGA("Alaga", "💧"),
    PESTE("Peste", "🐛"),
    ANI("Ani", "🌾")
}

@Composable
private fun ModernTabIcon(tab: CropGuideTab, isSelected: Boolean) {
    val iconColor = if (isSelected) Color(0xFF0E2316) else Color(0xFFA5D6A7)
    Canvas(modifier = Modifier.size(13.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.4.dp.toPx()

        when (tab) {
            CropGuideTab.LUPA -> {
                // Soil / Furrow layered lines
                drawLine(iconColor, Offset(1f, cy - 3.5f), Offset(w - 1f, cy - 3.5f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(iconColor, Offset(2.5f, cy + 1f), Offset(w - 2.5f, cy + 1f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(iconColor, Offset(4.5f, cy + 5f), Offset(w - 4.5f, cy + 5f), strokeWidth = strokeW, cap = StrokeCap.Round)
            }
            CropGuideTab.TANIM -> {
                // Sprouting plant
                drawLine(iconColor, Offset(cx, h - 1f), Offset(cx, cy - 2f), strokeWidth = strokeW, cap = StrokeCap.Round)
                val rightLeaf = Path().apply {
                    moveTo(cx, cy)
                    quadraticBezierTo(w - 1f, cy - 1f, w - 2f, cy - 5f)
                    quadraticBezierTo(cx + 2f, cy - 4f, cx, cy)
                    close()
                }
                drawPath(rightLeaf, iconColor)
                val leftLeaf = Path().apply {
                    moveTo(cx, cy + 2f)
                    quadraticBezierTo(1f, cy + 1f, 2f, cy - 3f)
                    quadraticBezierTo(cx - 2f, cy - 2f, cx, cy + 2f)
                    close()
                }
                drawPath(leftLeaf, iconColor)
            }
            CropGuideTab.BALAG -> {
                // A-Frame Trellis
                drawLine(iconColor, Offset(cx, 1f), Offset(2f, h - 1f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(iconColor, Offset(cx, 1f), Offset(w - 2f, h - 1f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(iconColor, Offset(cx - 3.5f, cy + 1.5f), Offset(cx + 3.5f, cy + 1.5f), strokeWidth = strokeW, cap = StrokeCap.Round)
            }
            CropGuideTab.ALAGA -> {
                // Water droplet
                val drop = Path().apply {
                    moveTo(cx, 1.5f)
                    cubicTo(w - 1.5f, cy, w - 1.5f, h - 2f, cx, h - 0.5f)
                    cubicTo(1.5f, h - 2f, 1.5f, cy, cx, 1.5f)
                    close()
                }
                drawPath(drop, iconColor)
            }
            CropGuideTab.PESTE -> {
                // Shield / Bug Alert
                val shield = Path().apply {
                    moveTo(2f, 2f)
                    lineTo(w - 2f, 2f)
                    quadraticBezierTo(w - 2f, cy + 2f, cx, h - 1f)
                    quadraticBezierTo(2f, cy + 2f, 2f, 2f)
                    close()
                }
                drawPath(shield, iconColor, style = Stroke(width = strokeW))
                drawCircle(iconColor, radius = 1.dp.toPx(), center = Offset(cx, cy))
            }
            CropGuideTab.ANI -> {
                // Sickle / Grain
                val sickle = Path().apply {
                    moveTo(3f, cy + 3f)
                    cubicTo(1f, cy - 2f, cx, 1f, w - 2f, 3f)
                    cubicTo(cx + 1f, 3f, 4f, cy - 1f, 4f, cy + 3f)
                    close()
                }
                drawPath(sickle, iconColor)
                drawLine(iconColor, Offset(3f, cy + 2f), Offset(cx + 2f, h - 1f), strokeWidth = strokeW + 0.3f, cap = StrokeCap.Round)
            }
        }
    }
}

private enum class WhyTopic {
    CATEGORY,
    HARVEST,
    WATERING,
    SOIL
}

@Composable
fun CropDetailDialog(
    crop: Crop,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val metadataInfo = remember(crop.name) {
        CropMetadataAssetDataSource.getCropMetadataByName(context, crop.name)
    }
    val varietiesList = remember(crop.name) {
        CropMetadataAssetDataSource.getVarietiesForCrop(context, crop.name)
    }
    val whyReasoning = remember(crop.name) {
        CropMetadataAssetDataSource.getWhyReasoningForCrop(context, crop)
    }

    var selectedVarietyId by remember(crop.name) {
        mutableStateOf(varietiesList.firstOrNull()?.varietyId)
    }
    val activeVariety: CropVarietyInfo? = varietiesList.firstOrNull { it.varietyId == selectedVarietyId } ?: varietiesList.firstOrNull()

    var selectedTab by remember { mutableStateOf(CropGuideTab.TANIM) }
    var activeWhyTopic by remember { mutableStateOf<WhyTopic?>(null) }

    val cropNameLower = crop.name.lowercase()
    val needsTrellis = cropNameLower.contains("ampalaya") ||
            cropNameLower.contains("bitter gourd") ||
            cropNameLower.contains("sitaw") ||
            cropNameLower.contains("string bean") ||
            cropNameLower.contains("pipino") ||
            cropNameLower.contains("cucumber") ||
            cropNameLower.contains("kamatis") ||
            cropNameLower.contains("tomato") ||
            cropNameLower.contains("upo") ||
            cropNameLower.contains("patola")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
            color = Color(0xFF111E16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ─── COMPACT LANDSCAPE HEADER (80dp) ─────────────────────────
                // Avoids eating half the screen with tall banners on landscape
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF16281E))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 72dp Rounded Crop Thumbnail
                        val heroImage = CropMetadataAssetDataSource.resolveCropImage(crop.id, crop.name, crop.imageUrl)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0D1711))
                                .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = heroImage,
                                contentDescription = crop.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Title, Local Name, and Scientific/Family
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = crop.name,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                crop.localName?.let { local ->
                                    if (local.isNotBlank() && !local.equals(crop.name, ignoreCase = true)) {
                                        Text(
                                            text = "($local)",
                                            color = Color(0xFFA5D6A7),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                metadataInfo?.scientificName?.let { sci ->
                                    Text(
                                        text = sci,
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                                metadataInfo?.taxonomicFamily?.let { fam ->
                                    if (fam.isNotBlank()) {
                                        Text(
                                            text = "• Pamilya: $fam",
                                            color = Color(0xFF81C784),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // ─── VARIETY SELECTOR ROW ─────────────────────────────────────
                if (varietiesList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF14241B))
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🌾 BARIYEDAD:",
                            color = Color(0xFFFFD54F),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        varietiesList.forEach { vInfo ->
                            val isSelected = vInfo.varietyId == activeVariety?.varietyId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF2E7D32) else Color(0xFF1E3526))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedVarietyId = vInfo.varietyId }
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = vInfo.varietyName,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // ─── 6-STEP HAKBANG TAB ROW ───────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF182D20))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CropGuideTab.values().forEach { tab ->
                        val isSelected = tab == selectedTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF4CAF50) else Color(0xFF223E2D))
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                ModernTabIcon(tab, isSelected)
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) Color(0xFF0E2316) else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // ─── TAB CONTENT (VERTICAL SCROLL) ────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        // ─────────────────────────────────────────────────────────
                        // 🌍 TAB 1: LUPA (Soil & Land Preparation)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.LUPA -> {
                            SectionCard(title = "🌍 Hakbang 1: Paghahanda ng Lupa") {
                                Text(
                                    text = "Tamang Uri ng Lupa:",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Buhaghag (Loam) o mabuhangin na may maayos na daluyan ng tubig. Hindi binabaha at hindi sobrang malagkit.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SpecItem("Lalim ng Hukay (Deep Dig)", "20–30 cm (1 dipa)")
                                    SpecItem("Halong Compost", "2–3 kilo / m²")
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                // Visual Bed & Furrow Diagram with CM/M ruler guidelines
                                FurrowBedVisualGuide(
                                    plantSpacingCm = "30–50 cm",
                                    deepDigCm = "20–30 cm",
                                    bedHeightCm = "15–20 cm",
                                    bedWidthM = "1.0 Metro"
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🌾 Mga Paraan ng Pagtatanim:",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                MethodGuideCard(
                                    name = "1. Diretsong Tanim (Direct Seeding)",
                                    desc = "Ibaon ang 2-3 buto sa mababaw na tudling (2-3 cm lalim). Diligan bago tabunan nang banayad."
                                )

                                MethodGuideCard(
                                    name = "2. Kama (Raised Bed - 15-20 cm taas)",
                                    desc = "Inirerekomenda kung tag-ulan o malagkit ang lupa upang maiwasan ang pagkabulok ng ugat."
                                )

                                MethodGuideCard(
                                    name = "3. Paso o Lalagyan (Container Gardening)",
                                    desc = "Gamitin ang 5-10 litrong paso na may butas sa ilalim para sa bakuran o limitadong espasyo."
                                )
                            }
                        }

                        // ─────────────────────────────────────────────────────────
                        // 🌱 TAB 2: TANIM (Variety Timeline, Spacing & Traits)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.TANIM -> {
                            val activeHarvestDays = activeVariety?.growthDurationDays ?: crop.daysToHarvest.takeIf { it > 0 } ?: 60

                            SectionCard(title = "🌱 Hakbang 2: Pagpupunla at Bariyedad") {
                                // Variety header in card
                                activeVariety?.let { vInfo ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = vInfo.varietyName,
                                                color = Color(0xFFA5D6A7),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (vInfo.localNamePh.isNotBlank() && vInfo.localNamePh != vInfo.varietyName) {
                                                Text(
                                                    text = vInfo.localNamePh,
                                                    color = Color.White.copy(alpha = 0.6f),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        BadgePill(
                                            text = "$activeHarvestDays Araw Hanggang Ani",
                                            color = Color(0xFF2E7D32)
                                        )
                                    }

                                    Text(
                                        text = vInfo.description,
                                        color = Color.White.copy(alpha = 0.88f),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )

                                    // Trait chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        vInfo.fruitLengthCm?.let { length ->
                                            TraitChip("Haba", length, Color(0xFF26A69A))
                                        }
                                        vInfo.bitternessLevel?.let { bit ->
                                            TraitChip("Pait", bit, Color(0xFFFFA726))
                                        }
                                        vInfo.diseaseResistance?.let { res ->
                                            TraitChip("Laban sa", res, Color(0xFF66BB6A))
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                // 5-Stage Agronomic Growth Schedule
                                Text(
                                    text = "📊 5-Stage Agronomic Growth Schedule (Araw bawat Yugto):",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                val s1 = activeVariety?.stageDays?.stage1Sprout ?: (activeHarvestDays * 0.10f).toInt().coerceAtLeast(3)
                                val s2 = activeVariety?.stageDays?.stage2Seedling ?: (activeHarvestDays * 0.20f).toInt().coerceAtLeast(7)
                                val s3 = activeVariety?.stageDays?.stage3Vegetative ?: (activeHarvestDays * 0.35f).toInt().coerceAtLeast(15)
                                val s4 = activeVariety?.stageDays?.stage4Flowering ?: (activeHarvestDays * 0.20f).toInt().coerceAtLeast(10)
                                val s5 = activeVariety?.stageDays?.stage5Harvest ?: (activeHarvestDays * 0.15f).toInt().coerceAtLeast(5)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StageStep("1. Sprout", "${s1}d", Color(0xFF81C784))
                                    StageStep("2. Seedling", "${s2}d", Color(0xFF81C784))
                                    StageStep("3. Veg", "${s3}d", Color(0xFF81C784))
                                    StageStep("4. Bloom", "${s4}d", Color(0xFFFFD54F))
                                    StageStep("5. Harvest", "${s5}d+", Color(0xFF4CAF50), isHighlight = true)
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SpecItem("Distansya ng Puno", "30–50 cm")
                                    SpecItem("Tamang Panahon", activeVariety?.optimalSeasons?.joinToString(", ") ?: crop.seasonality.joinToString(", "))
                                }
                            }
                        }

                        // ─────────────────────────────────────────────────────────
                        // 🪵 TAB 3: BALAG (Trellis & Structural Support)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.BALAG -> {
                            SectionCard(title = "🪵 Hakbang 3: Balag at Suporta (Trellis)") {
                                if (needsTrellis) {
                                    Text(
                                        text = "⚠️ KAILANGAN NG BALAG:",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ang ${crop.name} ay gumagapang o may mabigat na bunga. Ilagay ang balag bago matapos ang Seedling stage (Day 15-20) upang hindi masira ang mga ugat sa pagtulos.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                    MethodGuideCard(
                                        name = "Inirerekomendang Balag: Tulos o A-Frame (1.5 - 2m)",
                                        desc = "Gamit ang kawayan at nylon trellis net para malayang makakapit ang baging."
                                    )

                                    MethodGuideCard(
                                        name = "Bakit Kailangan?",
                                        desc = "Iniiwasan ang pagkabulok ng bunga sa basang lupa at pinapataas ang ani."
                                    )

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                    // Visual Trellis Diagrams with meter height guides
                                    TrellisVisualGuide(
                                        activeTrellisType = "A_FRAME",
                                        cropName = crop.name
                                    )
                                } else {
                                    Text(
                                        text = "✅ HINDI KAILANGAN NG BALAG",
                                        color = Color(0xFF81C784),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ang ${crop.name} ay may sariling matatag na tangkay o mababang gulay. Hindi ito nangangailangan ng trellis o balag.",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                    // Educational reference showing what trellises look like if intercropping
                                    TrellisVisualGuide(
                                        activeTrellisType = "NONE",
                                        cropName = crop.name
                                    )
                                }
                            }
                        }

                        // ─────────────────────────────────────────────────────────
                        // 💧 TAB 4: ALAGA (Water, Fertilizer & Weeding)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.ALAGA -> {
                            val waterInterval = activeVariety?.wateringIntervalDays ?: crop.wateringIntervalDays.takeIf { it > 0 } ?: 2
                            val fertInterval = activeVariety?.fertilizeIntervalDays ?: 14

                            SectionCard(title = "💧 Hakbang 4: Pag-aalaga, Pataba at Damo") {
                                Text(
                                    text = "💧 Iskedyul ng Pagdidilig:",
                                    color = Color(0xFF4FC3F7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Diligan tuwing $waterInterval araw sa malamig na oras ng umaga (6:00 AM - 8:00 AM). Iwasan ang pagdilig sa tanghali.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🧪 Pataba at Sustansya (Tuwing $fertInterval araw):",
                                    color = Color(0xFF81C784),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "• Organiko: Vermicast o dumi ng baka sa lupa; FPJ (Fermented Plant Juice) tuwing 2 linggo.\n• Sintetiko: Complete 14-14-14 sa paglilipat; Urea (46-0-0) sa dahon; Potash (0-0-60) sa pamumunga.",
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🌿 Pag-aalis ng Damo (Weeding):",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Linisin ang damo sa paligid tuwing 7 araw upang hindi maagawan ng sustansya at tubig ang pananim.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // ─────────────────────────────────────────────────────────
                        // 🐛 TAB 5: PESTE (Pests & Companion Matrix)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.PESTE -> {
                            SectionCard(title = "🐛 Hakbang 5: Peste at Kaibigang Pananim") {
                                if (crop.commonPests.isNotEmpty()) {
                                    Text(
                                        text = "Mga Karaniwang Peste:",
                                        color = Color(0xFFFF8A80),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        crop.commonPests.forEach { pest ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFD32F2F).copy(alpha = 0.25f))
                                                    .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(pest, color = Color(0xFFFFCDD2), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🌿 Likas na Pangontra (Organic Remedy):",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "1 litrong tubig + 1 kutsaritang sabong panlaba (walang bleach) + 1 kutsaritang mantika o sili. I-spray sa ilalim ng dahon sa hapon.",
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E3A2B))
                                            .padding(8.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text("🤝 Kasama (Good)", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            if (crop.companionPlants.isNotEmpty()) {
                                                crop.companionPlants.take(3).forEach {
                                                    Text("• $it", color = Color.White, fontSize = 10.sp)
                                                }
                                            } else {
                                                Text("Walang limitasyon", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF3A1E1E))
                                            .padding(8.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text("⚠️ Iwasan (Avoid)", color = Color(0xFFFF8A80), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            if (crop.avoidPlants.isNotEmpty()) {
                                                crop.avoidPlants.take(3).forEach {
                                                    Text("• $it", color = Color.White, fontSize = 10.sp)
                                                }
                                            } else {
                                                Text("Walang kalaban", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ─────────────────────────────────────────────────────────
                        // 🌾 TAB 6: ANI (Harvest & Rotation)
                        // ─────────────────────────────────────────────────────────
                        CropGuideTab.ANI -> {
                            SectionCard(title = "🌾 Hakbang 6: Pag-aani at Crop Rotation") {
                                Text(
                                    text = "🔍 Palatandaan na Pwede nang Anihin:",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val indicators = crop.harvestIndicators ?: "Pansinin ang kulay, sukat at katigasan ng bunga batay sa tamang gulang ng bariyedad."
                                Text(
                                    text = indicators,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "⏰ Oras ng Pag-aani:",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Mag-ani sa maagang umaga bago uminit ang araw upang mapanatili ang tamis at sariwang timbang ng ani.",
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 12.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🔄 Crop Rotation para sa Susunod na Siklo:",
                                    color = Color(0xFF00E676),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Iwasan ang pagtatanim ng parehong pamilya sa parehong plot. Magtanim ng Sitaw o munggo pagkatapos nito upang maibalik ang Nitrogen sa lupa.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // ─── BOTTOM "BAKIT?" QUICK EXPLAINER PILLS ─────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Bakit?",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        InteractiveBadgePill(
                            label = "Kategorya",
                            tag = "Bakit? 💡",
                            containerColor = Color(0xFF1E5E38),
                            onClick = { activeWhyTopic = WhyTopic.CATEGORY }
                        )
                        InteractiveBadgePill(
                            label = "Araw ng Ani",
                            tag = "Bakit? 💡",
                            containerColor = Color(0xFF8D6E63),
                            onClick = { activeWhyTopic = WhyTopic.HARVEST }
                        )
                        InteractiveBadgePill(
                            label = "Dilig",
                            tag = "Bakit? 💡",
                            containerColor = Color(0xFF0277BD),
                            onClick = { activeWhyTopic = WhyTopic.WATERING }
                        )
                        InteractiveBadgePill(
                            label = "Tamang Lupa",
                            tag = "Bakit? 💡",
                            containerColor = Color(0xFF6A1B9A),
                            onClick = { activeWhyTopic = WhyTopic.SOIL }
                        )
                    }
                }
            }
        }
    }

    // ─── SECOND-SCREEN OVERLAY: "WHY?" SCIENCE MODAL ─────────────────────────
    activeWhyTopic?.let { topic ->
        val whyInfo: WhyDetailInfo = when (topic) {
            WhyTopic.CATEGORY -> whyReasoning.categoryWhy
            WhyTopic.HARVEST -> whyReasoning.harvestWhy
            WhyTopic.WATERING -> whyReasoning.wateringWhy
            WhyTopic.SOIL -> whyReasoning.soilWhy
        }

        val topicIcon = when (topic) {
            WhyTopic.CATEGORY -> "🏷️"
            WhyTopic.HARVEST -> "⏱️"
            WhyTopic.WATERING -> "💧"
            WhyTopic.SOIL -> "🪴"
        }

        Dialog(
            onDismissRequest = { activeWhyTopic = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                color = Color(0xFF14241B)
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(topicIcon, fontSize = 18.sp)
                            Text(
                                text = "Bakit Ito ang Gabay?",
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { activeWhyTopic = null },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close overlay",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = whyInfo.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 19.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1D3526))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = whyInfo.summary,
                            color = Color(0xFFA5D6A7),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        whyInfo.points.forEach { point ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = point,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { activeWhyTopic = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Naintindihan Ko", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MethodGuideCard(name: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B3224))
            .border(1.dp, Color(0xFF81C784).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(name, color = Color(0xFFA5D6A7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun InteractiveBadgePill(
    label: String,
    tag: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = tag,
                    color = Color(0xFFFFD54F),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TraitChip(label: String, value: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("$label:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            Text(value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StageStep(
    title: String,
    days: String,
    textColor: Color,
    isHighlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(if (isHighlight) Color(0xFF2E7D32) else Color(0xFF1E3A2B))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = days,
                color = if (isHighlight) Color.White else textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F382A).copy(alpha = 0.75f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                color = Color(0xFF81C784),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
fun BadgePill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
