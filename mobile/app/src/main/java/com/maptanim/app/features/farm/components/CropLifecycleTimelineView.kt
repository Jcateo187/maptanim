package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val LushGreen = Color(0xFF2E7D32)
private val LightGreenSurface = Color(0xFFE8F5E9)
private val DeepBlack = Color(0xFF111813)
private val MutedGray = Color(0xFF757575)
private val InactiveNodeColor = Color(0xFFE0E0E0)
private val ActiveGold = Color(0xFFFFB300)

enum class PhenologicalStage(val displayName: String, val approxDayPct: Float) {
    SEED("Seed", 0.00f),
    GERMINATION("Sprout", 0.08f),
    SEEDLING("Seedling", 0.18f),
    VEGETATIVE("Vegetative", 0.35f),
    FLOWERING("Flowering", 0.60f),
    FRUITING("Fruiting", 0.75f),
    MATURITY("Maturity", 0.90f),
    HARVEST("Harvest", 1.00f)
}

data class CalendarMilestone(
    val stage: PhenologicalStage,
    val dayNumber: Int,
    val calendarDateStr: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

@Composable
fun CropLifecycleTimelineView(
    cropName: String,
    daysPlanted: Int,
    totalMaturityDays: Int = 75,
    plantedDateStr: String? = null,
    modifier: Modifier = Modifier
) {
    val currentRatio = (daysPlanted.toFloat() / totalMaturityDays.toFloat()).coerceIn(0f, 1f)

    val currentStage = remember(currentRatio) {
        when {
            currentRatio < 0.06f -> PhenologicalStage.SEED
            currentRatio < 0.15f -> PhenologicalStage.GERMINATION
            currentRatio < 0.25f -> PhenologicalStage.SEEDLING
            currentRatio < 0.50f -> PhenologicalStage.VEGETATIVE
            currentRatio < 0.70f -> PhenologicalStage.FLOWERING
            currentRatio < 0.85f -> PhenologicalStage.FRUITING
            currentRatio < 0.95f -> PhenologicalStage.MATURITY
            else -> PhenologicalStage.HARVEST
        }
    }

    val milestones = remember(plantedDateStr, totalMaturityDays, daysPlanted) {
        val baseDate = try {
            if (!plantedDateStr.isNullOrBlank()) {
                LocalDate.parse(plantedDateStr.take(10))
            } else {
                LocalDate.now().minusDays(daysPlanted.toLong())
            }
        } catch (_: Exception) {
            LocalDate.now().minusDays(daysPlanted.toLong())
        }

        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        PhenologicalStage.entries.map { stage ->
            val targetDay = (stage.approxDayPct * totalMaturityDays).toInt()
            val targetDate = baseDate.plusDays(targetDay.toLong())
            CalendarMilestone(
                stage = stage,
                dayNumber = targetDay,
                calendarDateStr = targetDate.format(formatter),
                isCompleted = daysPlanted >= targetDay,
                isCurrent = stage == currentStage
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAFAFA),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Calendar Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "8-STAGE PHENOLOGICAL LIFECYCLE",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LightGreenSurface,
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Day $daysPlanted of $totalMaturityDays (${(currentRatio * 100).toInt()}%)",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                }
            }

            // Continuous Horizontal Stepper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                milestones.forEach { m ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(62.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = when {
                                        m.isCurrent -> ActiveGold
                                        m.isCompleted -> LushGreen
                                        else -> InactiveNodeColor
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (m.isCompleted && !m.isCurrent) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${m.stage.ordinal + 1}",
                                    color = if (m.isCurrent) DeepBlack else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = m.stage.displayName,
                            fontSize = 10.sp,
                            fontWeight = if (m.isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (m.isCurrent) LushGreen else if (m.isCompleted) DeepBlack else MutedGray,
                            maxLines = 1
                        )

                        Text(
                            text = m.calendarDateStr,
                            fontSize = 8.5.sp,
                            color = if (m.isCurrent) LushGreen else MutedGray,
                            fontWeight = if (m.isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Current Active Stage Description Callout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ActiveGold, CircleShape)
                    )
                    Text(
                        text = "Current Stage: ${currentStage.displayName} • Focus: Vegetative leaf vigor & bamboo staking.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack
                    )
                }
            }
        }
    }
}
