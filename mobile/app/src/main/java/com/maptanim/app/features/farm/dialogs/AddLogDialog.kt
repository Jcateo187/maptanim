package com.maptanim.app.features.farm.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.*
import com.maptanim.app.dss.logflow.LogFlowDataProvider
import com.maptanim.app.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * AddLogDialog — Implements the stage-aware interactive crop logging flow:
 *
 * CURRENT STAGE → ALLOWED LOG CONTEXTS → QUESTION → A/B/C → CHECKBOXES → SUBMIT
 *
 * Follows the dark agronomic design system of MapTanim:
 * - Emerald & Forest Green accents
 * - Card surfaces with subtle borders
 * - Dynamic reactive options driven by LogFlowDataProvider
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLogDialog(
    cropPlantingId: String,
    bedId: String,
    cropId: String? = null,
    varietyId: String? = null,
    cropName: String,
    varietyName: String,
    currentStage: ManagementStage,
    plantingMethod: String = "Direct Seeding",
    onDismiss: () -> Unit,
    onSubmitLog: (CropLog) -> Unit
) {
    // Determine allowed contexts for the current stage
    val allowedContexts = remember(currentStage) {
        val list = LogContext.allowedForStage(currentStage)
        if (list.isEmpty()) listOf(LogContext.OBSERVE) else list
    }

    // Default to OBSERVE if present in allowedContexts
    var selectedContext by remember(currentStage) {
        mutableStateOf(if (allowedContexts.contains(LogContext.OBSERVE)) LogContext.OBSERVE else allowedContexts.first())
    }
    var selectedCareActivity by remember(currentStage) { mutableStateOf(CareActivity.WATERING) }

    // Applicable care activities for current stage
    val applicableCareActivities = remember(currentStage) {
        LogFlowDataProvider.getApplicableCareActivities(currentStage)
    }

    // Get the dynamic question and choices for selected context & stage
    val questionData = remember(selectedContext, currentStage, plantingMethod) {
        LogFlowDataProvider.getQuestion(selectedContext, currentStage, plantingMethod)
    }

    // Selected choice key: "A", "B", or "C" (default to first choice)
    var selectedChoiceKey by remember(selectedContext, currentStage) {
        mutableStateOf(questionData.choices.firstOrNull()?.key ?: "A")
    }

    // Selected checkboxes set
    val selectedCheckboxes = remember(selectedChoiceKey, selectedContext, selectedCareActivity) {
        mutableStateListOf<String>()
    }

    var notes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Active choice object
    val currentChoice = questionData.choices.find { it.key == selectedChoiceKey }
        ?: questionData.choices.firstOrNull()

    // Determine active checkboxes list based on context
    val activeCheckboxes = remember(selectedContext, currentChoice, selectedCareActivity) {
        if (selectedContext == LogContext.CARE_MAINTENANCE) {
            LogFlowDataProvider.getCheckboxesForCareActivity(selectedCareActivity)
        } else {
            currentChoice?.checkboxes ?: emptyList()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF10160F)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── HEADER ──────────────────────────────────────────────────
                Surface(
                    color = Color(0xFF161E14),
                    border = BorderStroke(1.dp, Color(0xFF2B3825)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Record Crop Observation",
                                    color = White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                // Current stage pill
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = currentStage.label,
                                        color = Color(0xFF81C784),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "$cropName • $varietyName • $bedId",
                                color = Color(0xFF8B9B85),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

                // ── SCROLLABLE FORM ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── 1. LOG CONTEXT SELECTOR ─────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "OBSERVATION CONTEXT",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF161E14), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF2B3825), RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allowedContexts.forEach { ctx ->
                                val isSelected = ctx == selectedContext
                                Surface(
                                    onClick = {
                                        if (selectedContext != ctx) {
                                            selectedContext = ctx
                                            selectedChoiceKey = "A"
                                            selectedCheckboxes.clear()
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF2E7D32) else Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF4CAF50) else Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ctx.label,
                                            color = if (isSelected) White else Color(0xFF8B9B85),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── 1.1 CARE ACTIVITY DROPDOWN/PILLS (If CARE_MAINTENANCE) ─
                    if (selectedContext == LogContext.CARE_MAINTENANCE) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "SELECT ACTIVITY",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                applicableCareActivities.forEach { act ->
                                    val isSelected = act == selectedCareActivity
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (selectedCareActivity != act) {
                                                selectedCareActivity = act
                                                selectedCheckboxes.clear()
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = act.label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = Color(0xFF161E14),
                                            labelColor = Color(0xFFC0D0BA),
                                            selectedContainerColor = Color(0xFF2E7D32),
                                            selectedLabelColor = White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = Color(0xFF2B3825),
                                            selectedBorderColor = Color(0xFF4CAF50)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── 2. THE QUESTION & ABC CHOICES CARD ────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF2B3825))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Question header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "THE QUESTION",
                                    color = Color(0xFF81C784),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Surface(
                                    color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "STAGE ${currentStage.stageNumber}: ${currentStage.label.uppercase()}",
                                        color = Color(0xFFA5D6A7),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // The actual question text
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f)),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("❓", fontSize = 14.sp)
                                    }
                                }
                                Text(
                                    text = questionData.question,
                                    color = White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 20.sp
                                )
                            }

                            if (questionData.choices.isNotEmpty()) {
                                HorizontalDivider(color = Color(0xFF243221), thickness = 0.5.dp)

                                Text(
                                    text = "CHOICE (SELECT A, B, OR C):",
                                    color = Color(0xFF81C784),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )

                                // Choices A / B / C
                                questionData.choices.forEach { choice ->
                                    val isSelected = choice.key == selectedChoiceKey
                                    Surface(
                                        onClick = {
                                            if (selectedChoiceKey != choice.key) {
                                                selectedChoiceKey = choice.key
                                                selectedCheckboxes.clear()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Color(0xFF1E3321) else Color(0xFF111910),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isSelected) Color(0xFF4CAF50) else Color(0xFF243221)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Choice Badge (A / B / C)
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .background(
                                                        if (isSelected) Color(0xFF2E7D32) else Color(0xFF243221),
                                                        CircleShape
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color(0xFF81C784) else Color(0xFF384A33),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = choice.key,
                                                    color = White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Choice ${choice.key}: ${choice.label}",
                                                    color = if (isSelected) White else Color(0xFFB0C8AA),
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                                if (choice.checkboxes.isNotEmpty()) {
                                                    Text(
                                                        text = "${choice.checkboxes.size} observable conditions",
                                                        color = Color(0xFF6B7B65),
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color(0xFF81C784),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── 3. SPECIFIC SELECTION FOR CHOSEN A/B/C ──────────────
                    if (activeCheckboxes.isNotEmpty()) {
                        val selectionTitle = if (selectedContext == LogContext.CARE_MAINTENANCE) {
                            "SELECTION (${selectedCareActivity.label.uppercase()} DETAILS)"
                        } else {
                            "SELECTION (CHOICE ${selectedChoiceKey}: ${currentChoice?.label?.uppercase() ?: ""})"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF2B3825))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectionTitle,
                                        color = Color(0xFF81C784),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = "${selectedCheckboxes.size}/${activeCheckboxes.size} selected",
                                        color = Color(0xFFA0B09A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = "Select all observed conditions that apply to this crop:",
                                    color = Color(0xFF8B9B85),
                                    fontSize = 11.sp
                                )

                                HorizontalDivider(color = Color(0xFF243221), thickness = 0.5.dp)

                                activeCheckboxes.forEach { item ->
                                    val isChecked = selectedCheckboxes.contains(item)
                                    Surface(
                                        onClick = {
                                            if (isChecked) {
                                                selectedCheckboxes.remove(item)
                                            } else {
                                                selectedCheckboxes.add(item)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isChecked) Color(0xFF1C2C1D) else Color(0xFF111910),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChecked) Color(0xFF4CAF50).copy(alpha = 0.5f) else Color(0xFF243221)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    if (checked) selectedCheckboxes.add(item)
                                                    else selectedCheckboxes.remove(item)
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = Color(0xFF2E7D32),
                                                    uncheckedColor = Color(0xFF556850),
                                                    checkmarkColor = White
                                                ),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = item,
                                                color = if (isChecked) White else Color(0xFFB0C8AA),
                                                fontSize = 12.sp,
                                                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── 4. OPTIONAL NOTES FIELD ─────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ADDITIONAL NOTES (OPTIONAL)",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Enter any extra observations, weather notes, or comments...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5A6B54)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF161E14),
                                unfocusedContainerColor = Color(0xFF161E14),
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFF2B3825),
                                focusedTextColor = White,
                                unfocusedTextColor = White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 3,
                            maxLines = 5
                        )
                    }

                    // Validation error notice if any
                    if (validationError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF331614),
                            border = BorderStroke(1.dp, Color(0xFF8B2C24)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = validationError ?: "",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── BOTTOM ACTION BAR: SUBMIT BUTTON ────────────────────────
                HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

                Surface(
                    color = Color(0xFF161E14),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF3B4D35))
                        ) {
                            Text(
                                "Cancel",
                                color = Color(0xFFB0C8AA),
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                // Validation: Check if at least one checkbox is selected if checkboxes exist
                                if (activeCheckboxes.isNotEmpty() && selectedCheckboxes.isEmpty()) {
                                    validationError = "Please select at least one condition from the selection list."
                                    return@Button
                                }
                                validationError = null

                                val log = CropLog(
                                    id = "log-${UUID.randomUUID().toString().take(8)}",
                                    cropPlantingId = cropPlantingId,
                                    bedId = bedId,
                                    cropId = cropId,
                                    varietyId = varietyId,
                                    cropName = cropName,
                                    varietyName = varietyName,
                                    currentStage = currentStage,
                                    logContext = selectedContext,
                                    careActivity = if (selectedContext == LogContext.CARE_MAINTENANCE) selectedCareActivity else null,
                                    selectedChoice = selectedChoiceKey,
                                    selectedCheckboxes = selectedCheckboxes.toList(),
                                    notes = notes.trim().ifBlank { null },
                                    date = LocalDate.now().toString(),
                                    createdAt = Instant.now().toString()
                                )

                                onSubmitLog(log)
                            },
                            modifier = Modifier.weight(2f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "💾 Submit Observation & Evaluate",
                                color = White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
