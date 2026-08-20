package com.maptanim.app.ui.screens.knowledgebase

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.data.datasource.CropVarietyInfo
import com.maptanim.app.data.datasource.WhyDetailInfo
import com.maptanim.app.domain.model.Crop

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
    val refSource = remember(crop.name) {
        CropMetadataAssetDataSource.getReferenceSourceForCrop(context, crop.name)
    }

    var selectedVarietyId by remember(crop.name) {
        mutableStateOf(varietiesList.firstOrNull()?.varietyId)
    }
    val activeVariety: CropVarietyInfo? = varietiesList.firstOrNull { it.varietyId == selectedVarietyId } ?: varietiesList.firstOrNull()

    var activeWhyTopic by remember { mutableStateOf<WhyTopic?>(null) }

    fun openWebLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(30.dp))
                .border(1.5.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(30.dp)),
            color = Color(0xFF162A1E)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ─── HERO PHOTO BANNER ─────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color(0xFF101E15)),
                        contentAlignment = Alignment.Center
                    ) {
                        val heroImage = CropMetadataAssetDataSource.getCropAssetImagePath(crop.id, crop.name)
                        AsyncImage(
                            model = heroImage,
                            contentDescription = crop.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay for smooth transition to content
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent,
                                            Color(0xFF162A1E)
                                        ),
                                        startY = 0f
                                    )
                                )
                        )

                        // Top Badges (Taxonomic Family & Photo License Tag)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            metadataInfo?.taxonomicFamily?.let { family ->
                                if (family.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF2E7D32).copy(alpha = 0.9f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Family: $family",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ─── CONTENT BODY ──────────────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title & Scientific Name
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = crop.name,
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                crop.localName?.let { local ->
                                    Text(
                                        text = " ($local)",
                                        color = Color(0xFFA5D6A7),
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            crop.botanicalName?.let { botanical ->
                                Text(
                                    text = botanical,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        // ─── INTERACTIVE "WHY?" PARAMETER PILLS ROW ─────────────────────
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡 Interactive Parameters (Tap to view science & why):",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InteractiveBadgePill(
                                    label = "Category: ${metadataInfo?.cropType ?: crop.category}",
                                    tag = "Why? 💡",
                                    containerColor = Color(0xFF1E5E38),
                                    onClick = { activeWhyTopic = WhyTopic.CATEGORY }
                                )

                                val activeHarvestDays = activeVariety?.growthDurationDays ?: crop.daysToHarvest
                                InteractiveBadgePill(
                                    label = "Harvest: $activeHarvestDays days",
                                    tag = "Why? 💡",
                                    containerColor = Color(0xFF8D6E63),
                                    onClick = { activeWhyTopic = WhyTopic.HARVEST }
                                )

                                val activeWaterDays = activeVariety?.wateringIntervalDays ?: crop.wateringIntervalDays
                                InteractiveBadgePill(
                                    label = "Water: Every ${activeWaterDays}d",
                                    tag = "Why? 💡",
                                    containerColor = Color(0xFF0277BD),
                                    onClick = { activeWhyTopic = WhyTopic.WATERING }
                                )

                                InteractiveBadgePill(
                                    label = "Soil pH: ${crop.optimalPhMin}–${crop.optimalPhMax}",
                                    tag = "Why? 💡",
                                    containerColor = Color(0xFF6A1B9A),
                                    onClick = { activeWhyTopic = WhyTopic.SOIL }
                                )
                            }
                        }

                        // ─── CROP OVERVIEW DESCRIPTION ──────────────────────────────────
                        val overviewText = metadataInfo?.description?.ifBlank { null } ?: crop.description
                        overviewText?.let { desc ->
                            SectionCard(title = "📝 Crop Profile & Purpose") {
                                Text(
                                    text = desc,
                                    color = Color.White.copy(alpha = 0.92f),
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp
                                )
                            }
                        }

                        // ─── VARIETY SELECTOR & 5-STAGE TIMELINE ────────────────────────
                        if (varietiesList.isNotEmpty()) {
                            SectionCard(title = "🌾 Priority Cultivars & 5-Stage Growth Breakdown") {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "Select a variety to inspect tailored growth days, bitterness level, and plant traits:",
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 12.sp
                                    )

                                    // Variety Tabs Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        varietiesList.forEach { vInfo ->
                                            val isSelected = vInfo.varietyId == activeVariety?.varietyId
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isSelected) Color(0xFF4CAF50)
                                                        else Color(0xFF1E3A2B)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color.White else Color(0xFF4CAF50).copy(alpha = 0.4f),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { selectedVarietyId = vInfo.varietyId }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = vInfo.varietyName,
                                                    color = if (isSelected) Color(0xFF0E2316) else Color(0xFFA5D6A7),
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    // Active Variety Detail Card
                                    activeVariety?.let { vInfo ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFF15261C))
                                                .border(1.dp, Color(0xFF81C784).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                                .padding(14.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = vInfo.varietyName,
                                                            color = Color(0xFFA5D6A7),
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        if (vInfo.localNamePh.isNotBlank() && vInfo.localNamePh != vInfo.varietyName) {
                                                            Text(
                                                                text = vInfo.localNamePh,
                                                                color = Color.White.copy(alpha = 0.7f),
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                    }
                                                    BadgePill(
                                                        text = "${vInfo.growthDurationDays} Days Total",
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                }

                                                Text(
                                                    text = vInfo.description,
                                                    color = Color.White.copy(alpha = 0.88f),
                                                    fontSize = 13.sp,
                                                    lineHeight = 19.sp
                                                )

                                                // Variety specific traits row
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    vInfo.fruitLengthCm?.let { length ->
                                                        TraitChip("Length", length, Color(0xFF26A69A))
                                                    }
                                                    vInfo.bitternessLevel?.let { bit ->
                                                        TraitChip("Bitterness", bit, Color(0xFFFFA726))
                                                    }
                                                }

                                                vInfo.diseaseResistance?.let { res ->
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFF2E7D32).copy(alpha = 0.2f))
                                                            .border(1.dp, Color(0xFF81C784).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "🛡️ Resistance: $res",
                                                            color = Color(0xFFA5D6A7),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }

                                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                                // 5-Stage Growth Breakdown
                                                Text(
                                                    text = "📊 5-Stage Agronomic Growth Schedule:",
                                                    color = Color(0xFFFFD54F),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    StageStep("1. Sprout", "${vInfo.stageDays.stage1Sprout}d", Color(0xFF81C784))
                                                    StageStep("2. Seedling", "${vInfo.stageDays.stage2Seedling}d", Color(0xFF81C784))
                                                    StageStep("3. Veg", "${vInfo.stageDays.stage3Vegetative}d", Color(0xFF81C784))
                                                    StageStep("4. Bloom", "${vInfo.stageDays.stage4Flowering}d", Color(0xFFFFD54F))
                                                    StageStep("5. Harvest", "${vInfo.stageDays.stage5Harvest}d+", Color(0xFF4CAF50), isHighlight = true)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ─── SOIL & AGRONOMIC SPECIFICATIONS ───────────────────────────
                        SectionCard(title = "🌱 Soil, pH & Nutritional Specifications") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SpecItem("Optimal pH Range", "${crop.optimalPhMin} – ${crop.optimalPhMax}")
                                    SpecItem("NPK Fertilizer Target", "${crop.nRatio} : ${crop.pRatio} : ${crop.kRatio}")
                                    SpecItem("Seasonality", crop.seasonality.joinToString(", "))
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "Soil Texture Suitability:",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "Ideal: ${crop.idealSoils.joinToString { it.name }}",
                                        color = Color(0xFF81C784),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Suitable: ${crop.suitableSoils.joinToString { it.name }}",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // ─── COMPANION PLANTS & ANTAGONISTS ────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SectionCard(
                                title = "🤝 Companion Plants",
                                modifier = Modifier.weight(1f)
                            ) {
                                if (crop.companionPlants.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        crop.companionPlants.forEach { plant ->
                                            Text("• $plant", color = Color(0xFFA5D6A7), fontSize = 13.sp)
                                        }
                                    }
                                } else {
                                    Text("No specific companion restrictions", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                                }
                            }

                            SectionCard(
                                title = "⚠️ Avoid Planting With",
                                modifier = Modifier.weight(1f)
                            ) {
                                if (crop.avoidPlants.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        crop.avoidPlants.forEach { plant ->
                                            Text("• $plant", color = Color(0xFFFF8A80), fontSize = 13.sp)
                                        }
                                    }
                                } else {
                                    Text("No known antagonistic crops", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                                }
                            }
                        }

                        // ─── COMMON PESTS & DISEASES ────────────────────────────────────
                        if (crop.commonPests.isNotEmpty()) {
                            SectionCard(title = "🐛 Common Pests & Diseases") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    crop.commonPests.forEach { pest ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFD32F2F).copy(alpha = 0.3f))
                                                .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(pest, color = Color(0xFFFFCDD2), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }

                        // ─── HARVEST READINESS & POST-HARVEST ───────────────────────────
                        crop.harvestIndicators?.let { indicators ->
                            SectionCard(title = "🌾 Harvest Readiness & Field Handling") {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "• Maturity Indicators: $indicators",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 19.sp
                                    )
                                    Text(
                                        text = "• Field Timing: Harvest during cool early morning hours to minimize field heat respiration.",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "• Storage Conditions: Store at 12°C–15°C with 90% relative humidity. Keeps crisp for 7–10 days.",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // ─── OFFICIAL DATA SOURCE, OWNER & PURPOSE ───────────────────────
                        SectionCard(
                            title = "🏛️ Verified Data Source & Official References",
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Authority & Research Body:",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = refSource.organization,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Standard Document:",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = refSource.publicationTitle,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                Text(
                                    text = "🎯 Purpose of Dataset:",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = refSource.purposeStatement,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Clickable Official Links
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { openWebLink(refSource.sourceUrl) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInBrowser,
                                            contentDescription = "Open Source",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("DA-BPI Website", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }

                                    refSource.secondaryUrl?.let { secUrl ->
                                        OutlinedButton(
                                            onClick = { openWebLink(secUrl) },
                                            modifier = Modifier.weight(1f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Science,
                                                contentDescription = "Seed Guide",
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFF81C784)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Seed Catalog", fontSize = 12.sp, color = Color(0xFF81C784), fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Close Button Pinned at Top-Right
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
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
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(30.dp))
                    .border(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.6f), RoundedCornerShape(30.dp)),
                color = Color(0xFF14241B)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(topicIcon, fontSize = 22.sp)
                            Text(
                                text = "Agronomic Science & Rationale",
                                color = Color(0xFFFFD54F),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { activeWhyTopic = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close overlay",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Text(
                        text = whyInfo.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1D3526))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = whyInfo.summary,
                            color = Color(0xFFA5D6A7),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Key Botanical & Agricultural Insights:",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        whyInfo.points.forEach { point ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("•", color = Color(0xFF81C784), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = point,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { activeWhyTopic = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Understood", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
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
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = tag,
                    color = Color(0xFFFFD54F),
                    fontSize = 10.sp,
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
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("$label:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
            fontSize = 10.sp
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isHighlight) Color(0xFF2E7D32) else Color(0xFF1E3A2B))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                text = days,
                color = if (isHighlight) Color.White else textColor,
                fontSize = 11.sp,
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
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF234431).copy(alpha = 0.75f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                color = Color(0xFF81C784),
                fontSize = 15.sp,
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
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
