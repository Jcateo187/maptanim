package com.maptanim.app.features.home.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.schedule.CropRotationEngine

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val LightGreenBg = Color(0xFFE8F5E9)
private val StarGold = Color(0xFFFFB300)
private val CardBorderColor = Color(0xFFD0D8CC)
private val Charcoal = Color(0xFF28302A)
private val MutedText = Color(0xFF5A665D)

/**
 * TaskCompletionCelebrationDialog — Displays a celebration overlay when all daily tasks are completed.
 * Features:
 * 1. Milestone celebration ("All chores completed for today!").
 * 2. Optional Play Store 5-star rating prompt with direct Play Store launch.
 * 3. Plot next lifecycle actions: Replant same crop, Compost & rest soil (fallow), or Smart Crop Rotation.
 */
@Composable
fun TaskCompletionCelebrationDialog(
    completedPlot: PlotRenderData? = null,
    onDismiss: () -> Unit,
    onReplant: (plotId: String, cropName: String) -> Unit,
    onCompostAndFallow: (plotId: String) -> Unit,
    onRotateCrop: (plotId: String, newCropName: String) -> Unit
) {
    val context = LocalContext.current
    var ratingStars by remember { mutableStateOf(5) }
    var ratingSubmitted by remember { mutableStateOf(false) }

    val cropName = completedPlot?.cropName?.takeIf { it.isNotBlank() } ?: "Pananim"
    val plotId = completedPlot?.id ?: "plot-1"

    val rotationRec = remember(cropName) {
        CropRotationEngine.getRecommendation(cropName)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White,
            border = BorderStroke(1.2.dp, CardBorderColor),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 1. CELEBRATION HEADER ──
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(LightGreenBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = LushGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }

                val titleText = if (completedPlot != null && !completedPlot.cropName.isNullOrBlank()) {
                    "🎉 $cropName Harvest Complete!"
                } else {
                    "🎉 All Tasks Completed!"
                }

                val subText = if (completedPlot != null && !completedPlot.cropName.isNullOrBlank()) {
                    "Mahusay! Matagumpay mong naani ang $cropName sa ${completedPlot.plotLabel}. Pumili ng susunod na hakbang para sa lupang ito!"
                } else {
                    "Mahusay! Natapos mo ang lahat ng gawaing pang-alaga para sa araw na ito. Malusog at masagana ang iyong taniman!"
                }

                Text(
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subText,
                    fontSize = 12.5.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f), thickness = 1.dp)

                // ── 2. GOOGLE PLAY STORE RATING SECTION ──
                if (!ratingSubmitted) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF9FAF8),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⭐ Naibigan mo ba ang MapTanim?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal
                            )
                            Text(
                                text = "Mag-iwan ng mabilis na review upang matulungan ang iba pang magsasaka!",
                                fontSize = 11.5.sp,
                                color = MutedText,
                                textAlign = TextAlign.Center
                            )

                            // Interactive 5-star row
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = if (star <= ratingStars) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Star $star",
                                        tint = if (star <= ratingStars) StarGold else Color.LightGray,
                                        modifier = Modifier
                                            .size(32.dp)
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
                                        ratingSubmitted = true
                                        val packageName = context.packageName
                                        try {
                                            val marketIntent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id=$packageName")
                                            ).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                            }
                                            context.startActivity(marketIntent)
                                        } catch (_: Exception) {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("I-rate sa Play Store", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { ratingSubmitted = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mamaya", fontSize = 11.sp, color = MutedText)
                                }
                            }
                        }
                    }
                }

                // ── 3. PLOT NEXT STEPS (REPLANT / COMPOST / ROTATE) ──
                if (completedPlot != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🌱 SUSUNOD NA HAKBANG SA ${completedPlot.plotLabel.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = ForestGreen
                        )

                        // Choice A: Replant Same Crop
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReplant(plotId, cropName) },
                            color = Color(0xFFF4F7F2),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = LushGreen)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Magtanim Muli ng $cropName",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Charcoal
                                    )
                                    Text(
                                        text = "Simulan ang bagong seedling cycle ng parehong gulay sa kamang ito.",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
                            }
                        }

                        // Choice B: Compost & Fallow (Soil Rest)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCompostAndFallow(plotId) },
                            color = Color(0xFFFBF8F2),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Park, contentDescription = null, tint = Color(0xFF8D6E63))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Mag-compost at Pahingahin ang Lupa (Fallow)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Charcoal
                                    )
                                    Text(
                                        text = "10 araw na pagpapataba gamit ang organic compost upang maibalik ang sustansya.",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
                            }
                        }

                        // Choice C: Rotate Crop (Smart Recommendation)
                        val topRotationCrop = rotationRec.recommendedNextCrops.firstOrNull() ?: "Sitaw (Legumes)"
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRotateCrop(plotId, topRotationCrop.substringBefore(" (")) },
                            color = LightGreenBg,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Autorenew, contentDescription = null, tint = LushGreen)
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "I-rotate sa $topRotationCrop",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = ForestGreen
                                        )
                                        Surface(
                                            color = LushGreen,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "REKOMENDADO",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = rotationRec.rotationReasonTagalog.take(90) + "...",
                                        fontSize = 10.5.sp,
                                        color = MutedText,
                                        lineHeight = 14.sp
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LushGreen)
                            }
                        }
                    }
                }

                // ── 4. CLOSE / DONE BUTTON ──
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "Tapos na para sa Ngayon",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen
                    )
                }
            }
        }
    }
}
