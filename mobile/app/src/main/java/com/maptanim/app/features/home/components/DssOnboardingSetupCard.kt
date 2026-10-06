package com.maptanim.app.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightGreenSurface = Color(0xFFF1F8F2)

/**
 * DssOnboardingSetupCard — Prompts newly registered farmers to define their
 * site observations and available zero-budget materials to personalize DSS guidance.
 * Adheres strictly to Daylight High-Contrast Theme (Pure White, Lush Green, Deep Black, ZERO emojis).
 */
@Composable
fun DssOnboardingSetupCard(
    onStartCalibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, LushGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Badge & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LightGreenSurface,
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "NEW ACCOUNT SETUP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = LushGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Calibrate Your Backyard DSS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepBlack
                )
                Text(
                    text = "MapTanim needs to know your location, current weather (e.g. rainy season), soil appearance, and available zero-budget materials to customize your daily planting and care plan.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF555555)
                )
            }

            // Quick Parameters Summary Pills
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "Agro-Zone & Elevation (Highland / Lowland)",
                    "Weather / Season (Rainy / Dry)",
                    "Ground Soil Appearance & Slope",
                    "Backyard Scale & Yard Dimensions (Step Pacing)",
                    "Available ₱0 Materials (Wood Ash, Eggshells, Mulch)"
                ).forEach { param ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = LushGreen,
                            modifier = Modifier.size(4.dp)
                        ) {}
                        Text(
                            text = param,
                            fontSize = 10.sp,
                            color = Color(0xFF444444)
                        )
                    }
                }
            }

            // Primary Call to Action Button
            Button(
                onClick = onStartCalibration,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Set Up Observations & Availability",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
