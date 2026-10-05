package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.features.farm.viewmodel.TopTab

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

enum class WorkflowStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val tab: TopTab?
) {
    FARM_SETUP(1, "Farm Setup & Scale", "Agro-zone, base soil, Basketball Court scale", null),
    BED_PLANNING(2, "Bed Planning & Capacity", "Bed sizing (sp × rg), microclimate fit", TopTab.PLAN),
    DAILY_GUIDE(3, "Daily Care & Dosing", "Phase 1 Soil Prep, Phase 2 Sowing, Phase 3 Care", TopTab.GUIDE),
    CHECKUP(4, "Field Health Check-Up", "Pest risks, symptoms & organic remedies", TopTab.CHECKUP),
    HARVEST(5, "Harvest & Succession", "Yield kg log & legume crop rotation", TopTab.HARVEST)
}

/**
 * InterconnectedWorkflowHeader — Dual-dropdown anchor providing:
 * 1. Active Bed Dropdown Selector: Allows switching beds across the entire app with one tap.
 * 2. Interconnected Step Dropdown: Explicitly shows the 5 interconnected DSS phases and where the user is.
 * 3. Beginner "Start Here" Card: Orients new farmers when no beds exist.
 */
@Composable
fun InterconnectedWorkflowHeader(
    activePlot: CropPlot?,
    allPlots: List<CropPlot>,
    currentTab: TopTab,
    environment: FarmEnvironment,
    onSelectPlot: (String) -> Unit,
    onAddNewBed: () -> Unit,
    onSelectStep: (WorkflowStep) -> Unit,
    onOpenSetupDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBedMenuExpanded by remember { mutableStateOf(false) }
    var isStepMenuExpanded by remember { mutableStateOf(false) }

    val currentStep = when (currentTab) {
        TopTab.PLAN -> WorkflowStep.BED_PLANNING
        TopTab.GUIDE -> WorkflowStep.DAILY_GUIDE
        TopTab.CHECKUP -> WorkflowStep.CHECKUP
        TopTab.HARVEST -> WorkflowStep.HARVEST
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Dual Dropdown Selector Bar ──────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 1: Active Bed Selector
            Box(modifier = Modifier.weight(1.1f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isBedMenuExpanded = true },
                    shape = RoundedCornerShape(10.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, if (activePlot != null) LushGreen.copy(alpha = 0.6f) else CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE BED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LushGreen,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = activePlot?.let { "${it.plotLabel}: ${it.cropName ?: "Unplanted"}" }
                                    ?: if (allPlots.isEmpty()) "Tap to Add Bed" else "Select Bed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack,
                                maxLines = 1
                            )
                            Text(
                                text = activePlot?.let {
                                    val cap = calculatePlantCapacity(it.widthM, it.heightM, it.cropName)
                                    "${String.format("%.1f", it.widthM)}m × ${String.format("%.1f", it.heightM)}m" +
                                            if (cap > 0) " • $cap Plants" else ""
                                } ?: "${allPlots.size} beds created",
                                fontSize = 10.sp,
                                color = Color(0xFF666666),
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Bed",
                            tint = LushGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isBedMenuExpanded,
                    onDismissRequest = { isBedMenuExpanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .widthIn(min = 240.dp)
                ) {
                    Text(
                        text = "SELECT ACTIVE GARDEN BED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    HorizontalDivider(color = CardBorderColor)

                    if (allPlots.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text("No garden beds yet. Tap + to add one.", fontSize = 12.sp, color = Color(0xFF666666))
                            },
                            onClick = {
                                isBedMenuExpanded = false
                                onAddNewBed()
                            }
                        )
                    } else {
                        allPlots.forEach { plot ->
                            val isSelected = activePlot?.id == plot.id
                            val cap = calculatePlantCapacity(plot.widthM, plot.heightM, plot.cropName)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${plot.plotLabel}: ${plot.cropName ?: "Unplanted"}",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.sp,
                                                color = if (isSelected) LushGreen else DeepBlack
                                            )
                                            Text(
                                                text = "${String.format("%.1f", plot.widthM)}m × ${String.format("%.1f", plot.heightM)}m" +
                                                        if (cap > 0) " • $cap Plants" else " • ${plot.soilType.name}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF757575)
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = LushGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    isBedMenuExpanded = false
                                    onSelectPlot(plot.id)
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorderColor)
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null, tint = LushGreen, modifier = Modifier.size(18.dp))
                        },
                        text = {
                            Text("＋ Create New Garden Bed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        },
                        onClick = {
                            isBedMenuExpanded = false
                            onAddNewBed()
                        }
                    )
                }
            }

            // Dropdown 2: Interconnected Step Selector
            Box(modifier = Modifier.weight(0.9f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isStepMenuExpanded = true },
                    shape = RoundedCornerShape(10.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, Color(0xFF90CAF9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STEP ${currentStep.stepNumber} OF 5",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = currentStep.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack,
                                maxLines = 1
                            )
                            Text(
                                text = "Interconnected DSS",
                                fontSize = 10.sp,
                                color = Color(0xFF666666),
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.UnfoldMore,
                            contentDescription = "Select Step",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isStepMenuExpanded,
                    onDismissRequest = { isStepMenuExpanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .widthIn(min = 270.dp)
                ) {
                    Text(
                        text = "INTERCONNECTED FARMING FLOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    HorizontalDivider(color = CardBorderColor)

                    WorkflowStep.values().forEach { step ->
                        val isCurrent = step == currentStep
                        DropdownMenuItem(
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isCurrent) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${step.stepNumber}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else DeepBlack
                                        )
                                    }
                                }
                            },
                            text = {
                                Column {
                                    Text(
                                        text = step.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) Color(0xFF1565C0) else DeepBlack
                                    )
                                    Text(
                                        text = step.subtitle,
                                        fontSize = 10.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            },
                            onClick = {
                                isStepMenuExpanded = false
                                if (step == WorkflowStep.FARM_SETUP) {
                                    onOpenSetupDialog()
                                } else {
                                    onSelectStep(step)
                                }
                            }
                        )
                    }
                }
            }
        }

        // ── 0-Beds Beginner Onboarding Card ("Where To Start") ──────────────
        if (allPlots.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = LushGreen, modifier = Modifier.size(16.dp))
                        Text(
                            text = "START HERE: Welcome to MapTanim!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LushGreen
                        )
                    }
                    Text(
                        text = "Your farm is calibrated for ${environment.zone.label} with ${environment.defaultSoil.name} soil. " +
                                "Follow Step 2 to add your first bed and calculate its plant capacity.",
                        fontSize = 11.sp,
                        color = DeepBlack,
                        lineHeight = 15.sp
                    )
                    Button(
                        onClick = onAddNewBed,
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("＋ Create Bed #1 Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
