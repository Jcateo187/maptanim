package com.maptanim.app.features.farm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.dss.evaluator.PlaceBasedCropEvaluator
import com.maptanim.app.dss.evaluator.PlaceCropSuitability
import com.maptanim.app.dss.evaluator.SuitabilityTier

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CropPlaceSuitabilityCard — Displays place-based agro-ecological suitability
 * for the selected crop or bed, along with actionable compensatory alternative methods.
 * Daylight High-Contrast Theme (Pure White, Lush Green, Deep Black, ZERO emojis).
 */
@Composable
fun CropPlaceSuitabilityCard(
    cropName: String?,
    environment: FarmEnvironment,
    onOpenSetupDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val evaluator = remember { PlaceBasedCropEvaluator() }
    val suitability = remember(cropName, environment) {
        if (!cropName.isNullOrBlank()) {
            evaluator.evaluate(cropName, environment)
        } else null
    }

    var isMethodsExpanded by remember { mutableStateOf(true) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── 1. Farm Environment Place Banner ──────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = environment.zone.label.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = LushGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "•",
                            color = Color(0xFF888888),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${environment.defaultSoil.name} SOIL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DeepBlack
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", environment.areaSqM)} m² (${String.format("%.1f", environment.basketballCourtPct)}% Basketball Court)",
                        fontSize = 10.sp,
                        color = Color(0xFF666666)
                    )
                }

                OutlinedButton(
                    onClick = onOpenSetupDialog,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, CardBorderColor),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Edit Setup",
                        tint = LushGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit Setup",
                        fontSize = 10.sp,
                        color = DeepBlack,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Site constraints tags if any
            if (environment.constraints.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    environment.constraints.forEach { constraint ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(
                                text = constraint.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = CardBorderColor)

            // ── 2. Crop Suitability Evaluation ────────────────────────────────
            if (suitability != null) {
                // Tier Badge & Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PLACE SUITABILITY: ${suitability.cropName.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = suitability.summaryReason,
                            fontSize = 11.sp,
                            color = DeepBlack,
                            lineHeight = 14.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(suitability.tier.colorHex).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(suitability.tier.colorHex))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = suitability.tier.label.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(suitability.tier.colorHex)
                            )
                            Text(
                                text = "${suitability.scorePct}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(suitability.tier.colorHex)
                            )
                        }
                    }
                }

                // Positives & Challenges Bullets
                if (suitability.positiveFactors.isNotEmpty() || suitability.challengeFactors.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightSurface, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        suitability.positiveFactors.forEach { factor ->
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", color = LushGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(factor, fontSize = 10.sp, color = DeepBlack, lineHeight = 13.sp)
                            }
                        }
                        suitability.challengeFactors.forEach { factor ->
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", color = Color(0xFFD32F2F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(factor, fontSize = 10.sp, color = DeepBlack, lineHeight = 13.sp)
                            }
                        }
                    }
                }

                // ── 3. Compensatory Alternative Methods Section ──────────────
                if (suitability.alternativeMethods.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F8E9), // Light green tint
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isMethodsExpanded = !isMethodsExpanded },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Engineering,
                                        contentDescription = "Alternative Method",
                                        tint = LushGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "RECOMMENDED ALTERNATIVE METHOD",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = LushGreen
                                    )
                                }
                                Icon(
                                    imageVector = if (isMethodsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle",
                                    tint = LushGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = isMethodsExpanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    suitability.alternativeMethods.forEach { method ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White, RoundedCornerShape(6.dp))
                                                .border(1.dp, CardBorderColor, RoundedCornerShape(6.dp))
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = method.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = DeepBlack
                                            )
                                            Text(
                                                text = method.challengeDescription,
                                                fontSize = 10.sp,
                                                color = Color(0xFF555555)
                                            )

                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "STEP-BY-STEP REMEDIATION:",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LushGreen
                                            )
                                            method.stepByStepRemediation.forEachIndexed { idx, step ->
                                                Row(
                                                    verticalAlignment = Alignment.Top,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "${idx + 1}.",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DeepBlack
                                                    )
                                                    Text(
                                                        text = step,
                                                        fontSize = 10.sp,
                                                        color = DeepBlack,
                                                        lineHeight = 13.sp
                                                    )
                                                }
                                            }

                                            if (method.materialList.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "MATERIALS NEEDED: ${method.materialList.joinToString(", ")}",
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF666666),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "RATIONALE: ${method.agronomicRationale}",
                                                fontSize = 9.sp,
                                                color = Color(0xFF777777),
                                                lineHeight = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Select a bed or crop to view place suitability and alternative methods.",
                    fontSize = 11.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}
