package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.schedule.CropRotationEngine
import com.maptanim.app.features.farm.schedule.CropRotationRecommendation
import kotlinx.coroutines.launch

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val LightGreenBg = Color(0xFFE8F5E9)
private val DeepBlack = Color(0xFF141915)
private val Charcoal = Color(0xFF28302A)
private val MutedText = Color(0xFF4C564D)
private val CardBorderColor = Color(0xFFD0D8CC)
private val CardBg = Color(0xFFFFFFFF)
private val SurfaceBg = Color(0xFFF2F5F0)
private val AmberAlert = Color(0xFFE65100)
private val BlueAccent = Color(0xFF1565C0)

/**
 * PostHarvestRotationDialog — Interactive, agricultural flow dialog
 * for recording crop harvest, executing post-harvest soil rehabilitation,
 * and guiding intelligent Crop Rotation.
 */
@Composable
fun PostHarvestRotationDialog(
    cropName: String,
    varietyName: String? = null,
    gardenLabel: String,
    plotId: String,
    daysPlanted: Int,
    onDismiss: () -> Unit,
    onHarvestRecorded: (isFinalHarvest: Boolean) -> Unit,
    onNavigateToCanvas: ((plotId: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isFinalHarvest by remember { mutableStateOf(true) }
    var yieldInput by remember { mutableStateOf("") }
    var harvestNotes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var ratingStars by remember { mutableStateOf(5) }
    var hasRated by remember { mutableStateOf(false) }

    val rotationData: CropRotationRecommendation = remember(cropName) {
        CropRotationEngine.getRecommendation(cropName)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp)),
            color = SurfaceBg,
            border = BorderStroke(1.2.dp, CardBorderColor),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── 1. DIALOG HEADER ──
                Surface(
                    color = CardBg,
                    border = BorderStroke(0.dp, Color.Transparent),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(30.dp)) {
                                        CropSvgRenderer.drawCropSvg(
                                            drawScope = this,
                                            cropName = cropName,
                                            center = Offset(size.width / 2f, size.height / 2f),
                                            sizePx = size.width * 0.95f
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = "Pag-aani at Siklo ng Lupa",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBlack
                                )
                                Text(
                                    text = "$cropName • $gardenLabel • $daysPlanted araw na itinanim",
                                    fontSize = 11.5.sp,
                                    color = MutedText
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Isara", tint = Charcoal)
                        }
                    }
                }

                // ── 2. SCROLLABLE BODY ──
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ── HARVEST TYPE SELECTOR CARD ──
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "URI NG PAG-AANI (HARVEST MODE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = MutedText
                            )

                            // Option 1: Final Harvest & Clear Bed
                            Surface(
                                onClick = { isFinalHarvest = true },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isFinalHarvest) LightGreenBg else SurfaceBg,
                                border = BorderStroke(
                                    if (isFinalHarvest) 1.5.dp else 1.dp,
                                    if (isFinalHarvest) LushGreen else CardBorderColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isFinalHarvest,
                                        onClick = { isFinalHarvest = true },
                                        colors = RadioButtonDefaults.colors(selectedColor = LushGreen)
                                    )
                                    Column {
                                        Text(
                                            text = "Huling Pag-aani at I-clear ang Kama",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isFinalHarvest) ForestGreen else DeepBlack
                                        )
                                        Text(
                                            text = "Tapos na ang bunga. Bubunutin ang puno, ihahanda muli ang lupa, at magsasagawa ng Crop Rotation.",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            // Option 2: Continuous Multi-Pick Flush
                            Surface(
                                onClick = { isFinalHarvest = false },
                                shape = RoundedCornerShape(10.dp),
                                color = if (!isFinalHarvest) LightGreenBg else SurfaceBg,
                                border = BorderStroke(
                                    if (!isFinalHarvest) 1.5.dp else 1.dp,
                                    if (!isFinalHarvest) LushGreen else CardBorderColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = !isFinalHarvest,
                                        onClick = { isFinalHarvest = false },
                                        colors = RadioButtonDefaults.colors(selectedColor = LushGreen)
                                    )
                                    Column {
                                        Text(
                                            text = "Karaniwang Pagpitas Lamang (Multi-Pick Flush)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (!isFinalHarvest) ForestGreen else DeepBlack
                                        )
                                        Text(
                                            text = "Mamimitas muli pagkalipas ng 2–4 na araw. Panatilihing nakatanim ang puno para sa susunod na ani.",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── YIELD INPUT CARD ──
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "TINATAYANG TIMBANG NG ANI (YIELD)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = MutedText
                            )

                            OutlinedTextField(
                                value = yieldInput,
                                onValueChange = { yieldInput = it },
                                label = { Text("Timbang (hal. 2.5 kg o bilang ng piraso)") },
                                placeholder = { Text("hal. 3.0 kg") },
                                trailingIcon = {
                                    Text(
                                        text = "kg",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MutedText,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // ── IF FINAL HARVEST: BED REHAB & CROP ROTATION PROTOCOL ──
                    if (isFinalHarvest) {
                        // Soil Rehabilitation Protocol
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CardBg,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "PAGHAHANDA AT PAGPAPAHINGA NG LUPA",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = ForestGreen
                                    )
                                }

                                rotationData.bedRehabSteps.forEachIndexed { idx, step ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = LightGreenBg,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ForestGreen
                                                )
                                            }
                                        }
                                        Text(
                                            text = step,
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.sp,
                                            color = Charcoal,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Smart Crop Rotation Advisory
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CardBg,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Autorenew, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "REKOMENDASYON SA PAGPAPALIT-TANIM (CROP ROTATION)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = BlueAccent
                                    )
                                }

                                Text(
                                    text = rotationData.botanicalFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MutedText
                                )

                                // Recommended next crops
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Pinakamainam Itanim Susunod:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        rotationData.recommendedNextCrops.take(3).forEach { nextCrop ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = LightGreenBg,
                                                border = BorderStroke(0.6.dp, LushGreen.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = nextCrop,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ForestGreen,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Avoid crops
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Iwasang Itanim Agad (Same Family):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberAlert
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        rotationData.avoidNextCrops.take(3).forEach { avoidCrop ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = AmberAlert.copy(alpha = 0.10f),
                                                border = BorderStroke(0.6.dp, AmberAlert.copy(alpha = 0.35f))
                                            ) {
                                                Text(
                                                    text = avoidCrop,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AmberAlert,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Agronomic Reason Box
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceBg,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = rotationData.rotationReasonTagalog,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = Charcoal,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }

                        // ── Optional Google Play Rating Section on Harvest ──
                        if (!hasRated) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightGreenBg.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "⭐ Natuwa ka ba sa pag-ani ng $cropName?",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = if (star <= ratingStars) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Star $star",
                                                tint = if (star <= ratingStars) Color(0xFFFFB300) else Color.LightGray,
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clickable { ratingStars = star }
                                                    .padding(2.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                hasRated = true
                                                val packageName = context.packageName
                                                try {
                                                    val intent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("market://details?id=$packageName")
                                                    ).apply {
                                                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {
                                                    context.startActivity(
                                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                        ) {
                                            Text("I-rate sa Google Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { hasRated = true },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Mamaya", fontSize = 11.sp, color = MutedText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── 3. DIALOG ACTION FOOTER ──
                Surface(
                    color = CardBg,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isSubmitting) return@Button
                                isSubmitting = true
                                val parsedYield = yieldInput.toFloatOrNull() ?: 0f

                                coroutineScope.launch {
                                    try {
                                        RepositoryProvider.cropPlotRepository.recordHarvest(
                                            plotId = plotId,
                                            yieldKg = parsedYield,
                                            notes = harvestNotes.ifBlank { "Harvest recorded via MapTanim Cycle" },
                                            isFinalHarvest = isFinalHarvest
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isSubmitting = false
                                        onHarvestRecorded(isFinalHarvest)
                                        if (isFinalHarvest && onNavigateToCanvas != null) {
                                            onNavigateToCanvas(plotId)
                                        } else {
                                            onDismiss()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFinalHarvest) Icons.Default.Agriculture else Icons.Default.Eco,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isFinalHarvest)
                                            "I-clear ang Kama at Pumili ng Bagong Tanim"
                                        else
                                            "I-record ang Pitas (Panatilihin ang Tanim)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Text("Kanselahin", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
