package com.maptanim.app.features.farm.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.LogContext
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.renderer.canvas.CardSvgRenderer
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ═══════════════════════════════════════════════════════════════════════════════
// Eye-Friendly, Glare-Free, High-Contrast Color Palette
// ═══════════════════════════════════════════════════════════════════════════════
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
private val RedAlert = Color(0xFFC62828)
private val BlueAccent = Color(0xFF1565C0)

// ═══════════════════════════════════════════════════════════════════════════════
// Data Models for Direct Agronomic Decision Engine
// ═══════════════════════════════════════════════════════════════════════════════
data class CheckUpActivity(
    val id: String,
    val title: String,
    val tagalogTitle: String,
    val icon: ImageVector,
    val description: String,
    val options: List<CheckUpOption>
)

data class CheckUpOption(
    val id: String,
    val label: String,
    val description: String
)

data class DirectCropSolution(
    val title: String,
    val tagalogTitle: String,
    val severity: String,
    val badgeColor: Color,
    val directAnswer: String,
    val actionSteps: List<String>,
    val materialsNeeded: List<String>,
    val organicRemedy: String? = null,
    val criticalRule: String? = null
)

/**
 * CropCareDssDialog — State-of-the-Art Full-Screen Direct Agronomic Check-Up Screen.
 * Triggered directly from the Canvas floating "Check Up" button.
 * Automatically detects the tapped crop, lets the user select an activity and checkboxes,
 * and provides immediate, prescriptive solutions without reading long encyclopedia text.
 */
@Composable
fun CropCareDssDialog(
    cropName: String,
    varietyName: String = "",
    gardenLabel: String = "Garden",
    plotId: String = "",
    currentStage: ManagementStage = ManagementStage.VEGETATIVE_GROWTH,
    onDismiss: () -> Unit
) {
    // 1. Detect activities tailored for this specific crop
    val activities = remember(cropName) { getCheckUpActivitiesForCrop(cropName) }
    var selectedActivityId by remember(cropName) { mutableStateOf(activities.firstOrNull()?.id ?: "watering") }
    val currentActivity = activities.firstOrNull { it.id == selectedActivityId } ?: activities.first()

    // 2. Selected condition checkboxes
    val selectedOptionIds = remember { mutableStateMapOf<String, Boolean>() }

    // Clear selections when switching activity
    fun switchActivity(newActivityId: String) {
        selectedActivityId = newActivityId
        selectedOptionIds.clear()
    }

    // 3. Solution state (evaluated on submit)
    var evaluatedSolution by remember { mutableStateOf<DirectCropSolution?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var isSavedToLog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Top App Bar (Back Arrow & Screen Title) ───────────────────
                Surface(
                    color = CardBg,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(onClick = onDismiss, modifier = Modifier.size(38.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Bumalik sa Canvas",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Crop Check-Up",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBlack,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    text = "Mabilisang Solusyon at Pagsusuri",
                                    fontSize = 12.5.sp,
                                    color = MutedText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Charcoal,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // ── Scrollable Body ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── Detected Crop Identity Card ───────────────────────────
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardBg,
                        border = BorderStroke(1.2.dp, CardBorderColor),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Native Crop Vector Avatar
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f)),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(42.dp)) {
                                        CropSvgRenderer.drawCropSvg(
                                            drawScope = this,
                                            cropName = cropName,
                                            center = Offset(size.width / 2f, size.height / 2f),
                                            sizePx = size.width
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = LightGreenBg
                                    ) {
                                        Text(
                                            text = "NAKITA SA CANVAS",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ForestGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = gardenLabel,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MutedText
                                    )
                                }

                                Text(
                                    text = cropName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBlack
                                )

                                Text(
                                    text = if (varietyName.isNotBlank()) "Barayti: $varietyName • ${currentStage.label}" else "Yugto: ${currentStage.label}",
                                    fontSize = 12.5.sp,
                                    color = Charcoal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ── Step 1: Select Activity Using Buttons / Chips ─────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. PUMILI NG GAWAIN O KATEGORYA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen,
                                letterSpacing = 0.4.sp
                            )
                            Text(
                                text = "Piliin ang sitwasyon",
                                fontSize = 11.5.sp,
                                color = MutedText
                            )
                        }

                        // Horizontal Activity Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activities.forEach { act ->
                                val isSelected = act.id == selectedActivityId
                                Surface(
                                    onClick = {
                                        switchActivity(act.id)
                                        hasSubmitted = false
                                        evaluatedSolution = null
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) ForestGreen else CardBg,
                                    border = BorderStroke(
                                        1.2.dp,
                                        if (isSelected) ForestGreen else CardBorderColor
                                    ),
                                    shadowElevation = if (isSelected) 2.dp else 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                                    ) {
                                        Icon(
                                            imageVector = act.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else ForestGreen,
                                            modifier = Modifier.size(17.dp)
                                        )
                                        Text(
                                            text = act.title,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isSelected) Color.White else DeepBlack
                                        )
                                    }
                                }
                            }
                        }

                        // Activity description banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF9FAF8),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(
                                text = currentActivity.description,
                                fontSize = 12.5.sp,
                                color = Charcoal,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // ── Step 2: Checkboxes for Identified Symptoms/Needs ──────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. LAGYAN NG TSEK ANG MGA NA-OBSERBAHAN",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen,
                                letterSpacing = 0.4.sp
                            )
                            val count = selectedOptionIds.values.count { it }
                            if (count > 0) {
                                Surface(shape = RoundedCornerShape(6.dp), color = LightGreenBg) {
                                    Text(
                                        text = "$count ang may tsek",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentActivity.options.forEach { opt ->
                                val isChecked = selectedOptionIds[opt.id] == true
                                Surface(
                                    onClick = {
                                        selectedOptionIds[opt.id] = !isChecked
                                        hasSubmitted = false
                                        evaluatedSolution = null
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isChecked) Color(0xFFF1F8E9) else CardBg,
                                    border = BorderStroke(
                                        1.2.dp,
                                        if (isChecked) ForestGreen else CardBorderColor
                                    ),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                selectedOptionIds[opt.id] = checked
                                                hasSubmitted = false
                                                evaluatedSolution = null
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = ForestGreen,
                                                uncheckedColor = Color.Gray
                                            ),
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = opt.label,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChecked) ForestGreen else DeepBlack
                                            )
                                            Text(
                                                text = opt.description,
                                                fontSize = 12.5.sp,
                                                color = Charcoal,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Step 3: Submit Button ─────────────────────────────────
                    Button(
                        onClick = {
                            hasSubmitted = true
                            isSavedToLog = false
                            val activeChecked = selectedOptionIds.filterValues { it }.keys.toSet()
                            evaluatedSolution = solveCropProblem(
                                cropName = cropName,
                                activityId = selectedActivityId,
                                selectedOptions = activeChecked
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SURIIN AT KUMUHA NG DIREKTANG SOLUSYON",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }

                    // ── Step 4: Direct Answer Card at the Bottom ──────────────
                    if (hasSubmitted) {
                        if (evaluatedSolution != null) {
                            val sol = evaluatedSolution!!
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardBg,
                                border = BorderStroke(1.5.dp, sol.badgeColor),
                                shadowElevation = 3.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Severity & Type Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = LightGreenBg,
                                            border = BorderStroke(0.8.dp, ForestGreen.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = ForestGreen,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = "DIREKTANG SOLUSYON",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = ForestGreen
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = sol.badgeColor.copy(alpha = 0.12f),
                                            border = BorderStroke(0.8.dp, sol.badgeColor.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = sol.severity,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = sol.badgeColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    // Main Title
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = sol.title,
                                            fontSize = 17.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepBlack
                                        )
                                        Text(
                                            text = sol.tagalogTitle,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForestGreen
                                        )
                                    }

                                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.7f))

                                    // Direct Prescriptive Explanation
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF1F8E9),
                                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "DIREKTANG SAGOT SA IYONG PROBLEMA:",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ForestGreen
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = sol.directAnswer,
                                                fontSize = 14.5.sp,
                                                color = DeepBlack,
                                                lineHeight = 21.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Action Steps (Numbered)
                                    if (sol.actionSteps.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "MGA HAKBANG NA DAPAT GAWIN (ACTION STEPS):",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Charcoal
                                            )
                                            sol.actionSteps.forEachIndexed { idx, step ->
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = ForestGreen,
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "${idx + 1}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = step,
                                                        fontSize = 13.5.sp,
                                                        color = Charcoal,
                                                        lineHeight = 19.sp,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Materials Needed
                                    if (sol.materialsNeeded.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, CardBorderColor),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "MGA KAILANGANG GAMIT AT MATERYALES:",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Charcoal
                                                )
                                                sol.materialsNeeded.forEach { mat ->
                                                    Text(
                                                        text = "• $mat",
                                                        fontSize = 13.sp,
                                                        color = DeepBlack
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Critical Rule / Warning
                                    sol.criticalRule?.let { rule ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFFFF3E0),
                                            border = BorderStroke(1.dp, AmberAlert.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = null,
                                                    tint = AmberAlert,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = "Paunawa / Mahalagang Tuntunin: $rule",
                                                    fontSize = 13.sp,
                                                    color = AmberAlert,
                                                    fontWeight = FontWeight.SemiBold,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }

                                    // Save Observation to Bed Log
                                    Button(
                                        onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val activeLabels = currentActivity.options
                                                        .filter { selectedOptionIds[it.id] == true }
                                                        .map { it.label }

                                                    val log = CropLog(
                                                        id = "log-${UUID.randomUUID().toString().take(8)}",
                                                        cropPlantingId = plotId.ifBlank { "plot-1" },
                                                        bedId = gardenLabel,
                                                        cropName = cropName,
                                                        varietyName = varietyName,
                                                        currentStage = currentStage,
                                                        logContext = LogContext.OBSERVE,
                                                        selectedChoice = currentActivity.title,
                                                        selectedCheckboxes = activeLabels,
                                                        notes = "${sol.title}: ${sol.directAnswer.take(160)}",
                                                        date = LocalDate.now().toString(),
                                                        createdAt = Instant.now().toString()
                                                    )
                                                    RepositoryProvider.cropLogRepository.insertLog(log)
                                                } catch (_: Exception) {}
                                            }
                                            isSavedToLog = true
                                        },
                                        enabled = !isSavedToLog,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSavedToLog) Color(0xFF2E7D32) else Color(0xFF1B5E20)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BookmarkBorder,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isSavedToLog) "Nai-save na sa Kasaysayan ng Bed!" else "I-save ang Solusyon sa Kasaysayan ng Bed",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            // ── If No Data Compatible State ──────────────────
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFFFBEB),
                                border = BorderStroke(1.2.dp, Color(0xFFFDE68A)),
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AmberAlert,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Walang Tugmang Solusyon sa Kasalukuyang Pinili",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Pakiusap pumili ng aktibidad at lagyan ng tsek ang kahit isang naobserbahang problema o sitwasyon sa itaas upang maipakita ang direktang solusyon para sa iyong $cropName.",
                                        fontSize = 13.sp,
                                        color = Charcoal,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Philippine Crop Activities & Options Generator
// ═══════════════════════════════════════════════════════════════════════════════
private fun getCheckUpActivitiesForCrop(cropName: String): List<CheckUpActivity> {
    val clean = cropName.lowercase().trim()
    val isStakingCrop = clean.contains("tomato") || clean.contains("kamatis") ||
            clean.contains("eggplant") || clean.contains("talong") ||
            clean.contains("ampalaya") || clean.contains("bitter") ||
            clean.contains("sitaw") || clean.contains("bean") ||
            clean.contains("pipino") || clean.contains("cucum") ||
            clean.contains("sili") || clean.contains("pepper") || clean.contains("chili") ||
            clean.contains("kalabasa") || clean.contains("squash") || clean.contains("pump")

    val list = mutableListOf<CheckUpActivity>()

    // 1. Staking Activity (prioritized for vine & heavy fruit crops)
    if (isStakingCrop) {
        list.add(
            CheckUpActivity(
                id = "staking",
                title = "🎋 Staking (Pagtutulos / Balag)",
                tagalogTitle = "Pagtutulos at Balag",
                icon = Icons.Default.Height,
                description = "Pangangalaga sa tangkay, suporta laban sa pagbagsak sa basang lupa, at gabay sa pagtatali ng tulos o balag para sa $cropName.",
                options = listOf(
                    CheckUpOption(
                        id = "falling_ground",
                        label = "Bumabagsak sa lupa ang sanga o baging",
                        description = "Nanganganib mabulok o magkaroon ng mantsa ang dahon at bunga dahil sumasayad sa basang lupa."
                    ),
                    CheckUpOption(
                        id = "heavy_fruit",
                        label = "Mabigat ang bunga at nababali ang tangkay",
                        description = "Sobrang bigat ng bunga at nabibiyak o nababali ang sumusuportang sanga."
                    ),
                    CheckUpOption(
                        id = "fruit_rot_soil",
                        label = "Nangingitim o may amag ang bunga malapit sa lupa",
                        description = "Soil rot at splash blight dulot ng pagkakadikit ng bunga sa lupang basa."
                    ),
                    CheckUpOption(
                        id = "wind_storm",
                        label = "Madaling mabuwal tuwing mahangin o maulan",
                        description = "Nabuwal ang buong puno o lumuluwag ang kapit ng ugat sa malakas na hangin."
                    ),
                    CheckUpOption(
                        id = "staking_guide",
                        label = "Kailangan ng gabay sa tamang taas at paraan ng pagtali",
                        description = "Paghahanda ng kawayan (1.5–2m) at tamang hugis '8' na tali upang hindi masakal ang tangkay."
                    )
                )
            )
        )
    }

    // 2. Watering & Soil Moisture
    list.add(
        CheckUpActivity(
            id = "watering",
            title = "💧 Pagdidilig (Watering)",
            tagalogTitle = "Pagdidilig at Kondisyon ng Lupa",
            icon = Icons.Default.WaterDrop,
            description = "Pagsusuri sa moisture ng lupa, pagkalanta sa tanghali, o labis na pagkababad sa baha.",
            options = listOf(
                CheckUpOption(
                    id = "cracked_dry",
                    label = "Tuyot at bitak-bitak ang ibabaw ng lupa",
                    description = "Mabilis mawalan ng halumigmig ang lupa sa ilalim ng matinding init."
                ),
                CheckUpOption(
                    id = "midday_wilt",
                    label = "Lanta ang dahon sa tanghali (Heat Stress)",
                    description = "Yumuyukod ang dahon tuwing 11 AM–2 PM ngunit bumabalik sa gabi."
                ),
                CheckUpOption(
                    id = "waterlogged",
                    label = "Nabababad sa baha ang ugat pagkatapos umulan",
                    description = "Hindi umaagos ang tubig at nananatiling matubig ang root zone."
                ),
                CheckUpOption(
                    id = "yellow_overwater",
                    label = "Naninilaw ang ibabang dahon kahit basa ang lupa",
                    description = "Kakulangan ng hangin sa ugat (root suffocation) dahil sa labis na pagdidilig."
                )
            )
        )
    )

    // 3. Fertilization & Nutrition
    list.add(
        CheckUpActivity(
            id = "fertilizer",
            title = "🌿 Pataba at Sustansya (Nutrition)",
            tagalogTitle = "Pagpapataba at Kakulangan sa Sustansya",
            icon = Icons.Default.Spa,
            description = "Pagsusuri sa kakulangan sa Nitrogen, Phosphorus, Potassium, at Calcium.",
            options = listOf(
                CheckUpOption(
                    id = "pale_nitrogen",
                    label = "Maputla at naninilaw ang buong halaman (Kulang sa Nitrogen)",
                    description = "Mabagal ang paglaki at mapusyaw ang berdeng kulay ng mga dahon."
                ),
                CheckUpOption(
                    id = "excess_nitrogen",
                    label = "Masyadong malago ang dahon pero walang bulaklak (Sobra sa N)",
                    description = "Napakakapal ng berdeng dahon ngunit hindi namumulaklak o nagbubunga."
                ),
                CheckUpOption(
                    id = "blossom_drop",
                    label = "Nalalaglag ang mga bulaklak (Kulang sa Phosphorus/CalPhos)",
                    description = "Nalulagas ang bulaklak bago pa makabuo ng munting bunga."
                ),
                CheckUpOption(
                    id = "blossom_end_rot",
                    label = "Maitim at lubog ang ilalim ng bunga (Blossom End Rot)",
                    description = "Kakulangan sa Calcium na karaniwan sa kamatis at sili tuwing tag-init."
                ),
                CheckUpOption(
                    id = "potassium_slow",
                    label = "Mabagal mamula o matamis ang bunga (Kulang sa Potassium)",
                    description = "Hindi pantay ang kulay ng paghinog at matamlay ang pamumunga."
                )
            )
        )
    )

    // 4. Pests & Insects
    list.add(
        CheckUpActivity(
            id = "pests",
            title = "🐛 Peste at Insekto (Pest Control)",
            tagalogTitle = "Pamamahala sa mga Uod at Peste",
            icon = Icons.Default.BugReport,
            description = "Pagkontrol sa uod sa bunga, aphids, leaf miner, at iba pang insekto gamit ang organikong pamamaraan.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_borer",
                    label = "May uod sa loob ng bunga o talbos (Fruit & Shoot Borer)",
                    description = "May butas sa bunga na may dumi ng uod at nabubulok ang loob."
                ),
                CheckUpOption(
                    id = "aphids_whitefly",
                    label = "May maliliit na insekto sa ilalim ng dahon (Aphids / Whiteflies)",
                    description = "Sumisipsip ng katas ng dahon at nagdudulot ng pamumutla o amag."
                ),
                CheckUpOption(
                    id = "leaf_miner",
                    label = "May mapuputing parang kalyeng linya sa dahon (Leaf Miner)",
                    description = "Uod na kumakain sa loob ng epidermis ng dahon."
                ),
                CheckUpOption(
                    id = "leaf_curling",
                    label = "Kumukulot at tumitigas ang bagong sibol na dahon (Thrips/Mites)",
                    description = "Deformed ang mga bagong dahon at humihinto ang paghaba."
                ),
                CheckUpOption(
                    id = "chewed_holes",
                    label = "Tadtad ng butas ang dahon (Flea Beetle / Caterpillars)",
                    description = "Kinakain ng uod o salagubang ang dahon hanggang maging butas-butas."
                )
            )
        )
    )

    // 5. Diseases & Leaf Spots
    list.add(
        CheckUpActivity(
            id = "disease",
            title = "🍂 Sakit at Dahon (Diseases)",
            tagalogTitle = "Foliar at Soil-borne Pathogens",
            icon = Icons.Default.Coronavirus,
            description = "Paggamot sa Early Blight, Bacterial Wilt, Powdery Mildew, at Damping-off.",
            options = listOf(
                CheckUpOption(
                    id = "early_blight",
                    label = "May pabilog at maitim na mantsa sa dahon (Leaf Spot / Blight)",
                    description = "Concentric rings na may dilaw na paligid sa ibabang dahon."
                ),
                CheckUpOption(
                    id = "bacterial_wilt",
                    label = "Biglaang pagkalanta ng buong halaman habang berde pa",
                    description = "Bacterial Wilt sa vascular bundle na nagpapatumba sa halaman sa loob ng 24 oras."
                ),
                CheckUpOption(
                    id = "powdery_mildew",
                    label = "Mapuputing parang pulbos sa ibabaw ng dahon (Powdery Mildew)",
                    description = "Puting amag na bumabalot sa dahon at pumipigil sa photosynthesis."
                ),
                CheckUpOption(
                    id = "mosaic_virus",
                    label = "Kulubot at may batik-batik na dilaw ang dahon (Mosaic Virus)",
                    description = "Viral disease na hatid ng aphids o whiteflies."
                )
            )
        )
    )

    // 6. Pruning & Suckers
    if (isStakingCrop) {
        list.add(
            CheckUpActivity(
                id = "pruning",
                title = "✂️ Pruning at Suwi (Training)",
                tagalogTitle = "Pag-aalis ng Suwi at Pagbabawas ng Dahon",
                icon = Icons.Default.ContentCut,
                description = "Pagpapanatili ng magandang sirkulasyon ng hangin at pagtutuon ng sustansya sa mga bunga.",
                options = listOf(
                    CheckUpOption(
                        id = "excess_suckers",
                        label = "Maraming suwi na tumutubo sa kili-kili ng sanga",
                        description = "Mga bagong sanga (suckers) na umagaw sa sustansya ng pangunahing puno."
                    ),
                    CheckUpOption(
                        id = "bottom_leaves",
                        label = "Dahon sa ibaba ay naninilaw at sumasayad sa lupa",
                        description = "Panganib ng soil-borne splash blight tuwing umuulan."
                    ),
                    CheckUpOption(
                        id = "dense_bush",
                        label = "Masyadong masinsin at hindi nasisikatan ng araw ang gitna",
                        description = "Kakulangan ng hangin na nagiging sanhi ng pamumuo ng fungal moisture."
                    )
                )
            )
        )
    }

    // 7. Harvesting & Picking
    list.add(
        CheckUpActivity(
            id = "harvest",
            title = "🧺 Pag-aani (Harvesting)",
            tagalogTitle = "Gabay sa Pitas at Pag-iingat",
            icon = Icons.Default.Agriculture,
            description = "Tamang yugto ng pagpitas, paraan ng paggupit, at pagpapahaba ng shelf life.",
            options = listOf(
                CheckUpOption(
                    id = "harvest_timing",
                    label = "Paano malalaman kung handa na pitasin ang pananim",
                    description = "Mga senyales ng tamang maturity para sa pinakamasarap na lasa."
                ),
                CheckUpOption(
                    id = "proper_picking",
                    label = "Tamang paraan ng pagpitas nang hindi nasisira ang puno",
                    description = "Paggamit ng pruner at pag-iingat sa mga bagong usbong na bulaklak."
                )
            )
        )
    )

    return list
}

// ═══════════════════════════════════════════════════════════════════════════════
// Direct Problem Solver Logic
// ═══════════════════════════════════════════════════════════════════════════════
private fun solveCropProblem(
    cropName: String,
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    val clean = cropName.lowercase().trim()

    return when (activityId) {
        "staking" -> {
            when {
                selectedOptions.contains("fruit_rot_soil") || selectedOptions.contains("falling_ground") -> {
                    DirectCropSolution(
                        title = "Pagtutulos at Elevation Laban sa Soil Rot ng $cropName",
                        tagalogTitle = "Ilayo ang Bunga sa Basang Lupa Gamit ang Tulos",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang pagkadikit ng bunga ng $cropName sa basang lupa ang pangunahing sanhi ng soil rot at fungal contamination. Kailangang itayo agad ang halaman gamit ang tulos na kawayan (1.5m hanggang 2m) at gupitin ang lahat ng dahong sumasayad sa lupa upang hindi makatalon ang fungal spores.",
                        actionSteps = listOf(
                            "Kumuha ng tulos na kawayan (1.5 hanggang 2 metro ang haba) at patalasin ang ibaba.",
                            "Ibaon ang tulos nang 20–30 sentimetro ang lalim, may distansyang 3–5 pulgada mula sa puno ng halaman upang hindi maputol ang pangunahing ugat.",
                            "Talian ang halaman sa tulos gamit ang malambot na tali sa hugis '8' (Figure-8 tie). Huwag higpitan sa tangkay; mag-iwan ng 1–2 pulgadang luwag para sa paglaki ng sanga.",
                            "Gupitin gamit ang malinis na gunting ang lahat ng dahon na nasa unang 30cm (1 talampakan) mula sa ibabaw ng lupa."
                        ),
                        materialsNeeded = listOf(
                            "Kawayan o patpat na may habang 1.5–2 metro",
                            "Malambot na tali (abaka, strips ng lumang t-shirt, o yarn)",
                            "Matalas na gunting o pruning shears",
                            "Kahoy na abo (wood ash) para ibudbod sa lupa sa paligid ng puno"
                        ),
                        criticalRule = "Huwag kailanman gumamit ng manipis na alambre o nylon na tali nang mahigpit dahil hihiwain nito ang tangkay kapag lumaki ang halaman at mahahanginan."
                    )
                }
                selectedOptions.contains("heavy_fruit") || selectedOptions.contains("wind_storm") -> {
                    DirectCropSolution(
                        title = "Double-Stake Suporta para sa Mabigat na Bunga ng $cropName",
                        tagalogTitle = "Pagpapatatag Laban sa Hangin at Nababaling Sanga",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Kapag marami at mabigat na ang bunga ng $cropName, hindi na kaya ng sariling tangkay ang bigat lalo na kapag may bugso ng hangin. Kailangan ng matibay na vertical stake na may side-arm support o Florida Weave system upang maipamahagi ang bigat.",
                        actionSteps = listOf(
                            "Magbaon ng pangalawang suportang tulos sa kabilang gilid ng halaman kung napakabigat na ng bunga.",
                            "Magkabit ng horizontal na suporta (bamboo cross-brace) sa pagitan ng mga tulos.",
                            "Dahan-dahang iangat ang mga sangang may bunga at isandal sa tali o tulos nang walang puwersa upang hindi malagot ang tangkay.",
                            "Maglagay ng mulch (tuyong dayami) sa base upang manatiling matatag ang ugat."
                        ),
                        materialsNeeded = listOf(
                            "2 pirasong kawayan bawat puno (1.8m ang haba)",
                            "Matibay na taling abaka o garden twine",
                            "Dayami (rice straw) para sa base"
                        ),
                        criticalRule = "Alisin ang mga dahong may mantsa bago magtali upang hindi mahawahan ang mga katabing sanga kapag nagkadikit-dikit."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Gabay sa Pagtutulos (Single-Stake / Trellis) para sa $cropName",
                        tagalogTitle = "Wastong Pamamaraan ng Pagpapatayo ng Tanim",
                        severity = "Pangangalaga (Maintenance)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang tamang pagtutulos ng $cropName ay nagpapataas ng ani ng hanggang 40% dahil mas nasisikatan ng araw ang lahat ng dahon at malinis ang sirkulasyon ng hangin.",
                        actionSteps = listOf(
                            "Ihanda ang tulos na kawayan bago pa mamulaklak ang tanim.",
                            "Ibaon sa hilagang o silangang bahagi ng halaman upang hindi harangan ang sikat ng araw sa umaga.",
                            "Gumawa ng Figure-8 tie tuwing bawat 20cm na pagtaas ng tangkay.",
                            "Panatilihing tuwid ang pangunahing tangkay."
                        ),
                        materialsNeeded = listOf(
                            "Kawayan na may habang 1.5m",
                            "Taling abaka o garden yarn"
                        ),
                        criticalRule = "Magtali tuwing umaga habang malambot at hindi malutong ang mga tangkay."
                    )
                }
            }
        }
        "watering" -> {
            when {
                selectedOptions.contains("cracked_dry") || selectedOptions.contains("midday_wilt") -> {
                    DirectCropSolution(
                        title = "Deep Morning Irrigation at Organic Mulching para sa $cropName",
                        tagalogTitle = "Solusyon sa Tuyong Lupa at Heat Stress",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = BlueAccent,
                        directAnswer = "Ang bitak-bitak na lupa at pagkalanta sa tanghali ay nagpapakita na mabilis sumingaw ang tubig sa ibabaw habang tuyot ang root zone. Huwag magdilig sa tirik ng araw! Magdilig nang malalim sa madaling-araw (6:00 AM) at agad lagyan ng 5cm makapal na mulch upang mapigilan ang 70% ng water evaporation.",
                        actionSteps = listOf(
                            "Diligan nang dahan-dahan ang base ng halaman sa madaling-araw (6:00–7:30 AM) hanggang sa lumambot ang unang 15 sentimetro ng lupa.",
                            "Maglagay agad ng 5 sentimetrong kapal ng tuyong dayami (rice straw), cocopeat, o tuyong dahon sa ibabaw ng bed ngunit iwanang 2 pulgada ang layo sa mismong puno ng halaman.",
                            "Kung matindi ang sikat ng araw sa tanghali (11 AM–2 PM), maglagay ng temporary 30% black shade net sa itaas ng bed.",
                            "Ulitin ang pagdidilig sa dakong alas-4:30 ng hapon kapag lumamig na ang lupa kung nananatiling tuyo ang ilalim."
                        ),
                        materialsNeeded = listOf(
                            "Dayami (rice straw) o cocopeat para sa mulch",
                            "Regadera o hose na may shower head",
                            "Temporary 30% shade net kung tag-init"
                        ),
                        criticalRule = "Huwag magbuhos ng malamig na tubig sa halaman kapag tirik ang araw ng alas-12 ng tanghali dahil magiging sanhi ito ng thermal shock sa mga dahon at ugat."
                    )
                }
                selectedOptions.contains("waterlogged") || selectedOptions.contains("yellow_overwater") -> {
                    DirectCropSolution(
                        title = "Drainage Aeration at Pagpigil sa Root Rot ng $cropName",
                        tagalogTitle = "Solusyon sa Binahang Ugat at Labis na Dilig",
                        severity = "Kritikal (Critical Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang paninilaw ng dahon kahit basa ang lupa ay senyales na nalulunod ang ugat (root suffocation) dahil walang hangin sa lupa. Kung hindi agad patutuyuin ang drainage, magkakaroon ng Pythium root rot ang $cropName sa loob ng 48 oras.",
                        actionSteps = listOf(
                            "Agad maghukay ng 15cm kanal sa magkabilang gilid ng plot upang mabilis na umagos palabas ang naipong tubig.",
                            "Itigil ang pagdidilig sa susunod na 3 hanggang 5 araw hanggang sa matuyo ang unang 2 pulgada ng lupa.",
                            "Magbudbod ng kalahating dakot ng pinong kahoy na abo (wood ash) sa paligid ng root crown upang matulungan ang pagpatay sa fungal spores.",
                            "Luwagan nang marahan ang ibabaw ng lupa gamit ang hand trowel (huwag laliman upang hindi masira ang ugat) para makapasok ang oxygen."
                        ),
                        materialsNeeded = listOf(
                            "Pala o hand trowel para sa drainage trench",
                            "Kahoy na abo (wood ash)",
                            "Apog (agricultural lime) kung acidic ang lupa matapos ang baha"
                        ),
                        criticalRule = "Huwag maglagay ng kemikal na pataba habang waterlogged ang lupa dahil masusunog ang nanghihinang mga ugat."
                    )
                }
                else -> null
            }
        }
        "fertilizer" -> {
            when {
                selectedOptions.contains("blossom_end_rot") -> {
                    DirectCropSolution(
                        title = "CalPhos Treatment Laban sa Blossom End Rot ng $cropName",
                        tagalogTitle = "Direktang Lunas sa Maitim at Sunog na Puwit ng Bunga",
                        severity = "Kritikal (Blossom End Rot)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang pangingitim ng puwit ng bunga ng $cropName ay hindi peste kundi Blossom End Rot dulot ng kakulangan sa Calcium at pabagu-bagong supply ng tubig sa lupa. Ang direktang lunas ay CalPhos foliar spray at regular na pagpapanatili ng pantay na moisture.",
                        actionSteps = listOf(
                            "Pitasin at itapon ang lahat ng bungang may itim sa puwit upang hindi magsayang ng sustansya ang halaman.",
                            "Mag-spray ng CalPhos (Calcium Phosphate mula sa inihaw na balat ng itlog at suka): Paghaluin ang 2 kutsara ng CalPhos sa 1 litro ng tubig. I-spray sa buong halaman tuwing alas-6 ng umaga.",
                            "Ulitin ang pag-spray tuwing 5 araw sa loob ng 3 linggo.",
                            "Panatilihing basa tulad ng 'wrung-out sponge' ang lupa; huwag hayaang maging sobrang tuyo bago diligan nang marami."
                        ),
                        materialsNeeded = listOf(
                            "CalPhos solution (Eggshell & vinegar extract)",
                            "Sprayer",
                            "Organic mulch para maging pantay ang moisture"
                        ),
                        criticalRule = "Huwag mag-spray ng mataas na Nitrogen fertilizer habang may blossom end rot dahil papabilisin nito ang paglaki ng dahon at lalong aagawan ng calcium ang bunga."
                    )
                }
                selectedOptions.contains("blossom_drop") || selectedOptions.contains("excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Bloom Booster at Pagpigil sa Flower Drop ng $cropName",
                        tagalogTitle = "Lunas sa Paglaglag ng Bulaklak at Sobrang Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang paglaglag ng bulaklak at labis na kapal ng dahon ay nangangahulugang sobra sa Nitrogen (pampalago) at kulang sa Phosphorus/Potassium (pampabulaklak). Kailangang itigil ang urea/sariwang dumi at mag-apply ng Fermented Fruit Juice (FFJ).",
                        actionSteps = listOf(
                            "Itigil ang anumang pataba na mataas sa Nitrogen (FAA o chemical urea).",
                            "Mag-apply ng Fermented Fruit Juice (FFJ mula sa saging/molasses): 2 kutsara sa 1L tubig drench sa lupa tuwing 7 araw.",
                            "Magbudbod ng 2 kutsarang kahoy na abo sa paligid ng drip line para sa natural na Potassium.",
                            "Iwasan ang pagbuhos ng tubig nang direkta sa mga bukas na bulaklak upang hindi mahugasan ang pollen."
                        ),
                        materialsNeeded = listOf(
                            "Fermented Fruit Juice (FFJ)",
                            "Kahoy na abo (wood ash para sa Potassium)",
                            "Tubig na walang chlorine"
                        ),
                        criticalRule = "Huwag diligan ng malakas na sprinkler ang ibabaw ng halaman habang namumulaklak."
                    )
                }
                selectedOptions.contains("pale_nitrogen") -> {
                    DirectCropSolution(
                        title = "Organikong Nitrogen Drench para sa Naninilaw na $cropName",
                        tagalogTitle = "Pagpapasigla sa Mapuputlang Dahon at Mabagal na Pagtubo",
                        severity = "Kailangan ng Sustansya",
                        badgeColor = LushGreen,
                        directAnswer = "Ang pamumutla at paninilaw ng buong halaman ay malinaw na kakulangan sa Nitrogen (Chlorosis). Ang pinakamabilis na organikong paraan ay drench ng Vermitea o Fermented Plant Juice (FPJ).",
                        actionSteps = listOf(
                            "Maghalo ng 2 kutsarang Fermented Plant Juice (FPJ) o Vermitea sa 1 litro ng tubig.",
                            "Idilig sa paligid ng ugat sa umaga tuwing 5 araw hanggang maging malalim na berde ang dahon.",
                            "Maglagay ng 2 dakot ng matandang vermicompost sa paligid ng puno.",
                            "Tiyaking hindi labis ang dilig dahil hinuhugasan nito ang natitirang nitrogen sa lupa."
                        ),
                        materialsNeeded = listOf(
                            "Fermented Plant Juice (FPJ) o Vermicompost tea",
                            "Vermicompost o decomposed compost",
                            "Regadera"
                        ),
                        criticalRule = "Huwag gumamit ng sariwang dumi ng manok o baka na hindi pa bulok dahil susunugin nito ang mga ugat."
                    )
                }
                else -> null
            }
        }
        "pests" -> {
            when {
                selectedOptions.contains("fruit_borer") -> {
                    DirectCropSolution(
                        title = "Organikong Solusyon Laban sa Uod ng Bunga ng $cropName",
                        tagalogTitle = "Pamuksa sa Fruit and Shoot Borer",
                        severity = "Agad na Gawin (Immediate Pest Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Fruit and Shoot Borer ay nanganganak sa bulaklak at pumapasok ang uod sa loob ng bunga. Walang kemikal na makakapasok sa loob kapag nakabaon na ang uod, kaya kailangan ng mekanikal na pagtatanggal at chili-garlic-neem spray.",
                        actionSteps = listOf(
                            "Agad pitasin at ibaon sa lupa (nang 30cm lalim) ang lahat ng apektadong bunga na may butas upang hindi makapag-pupa ang uod.",
                            "Magbawas ng mga apektadong talbos na yumuyukod dahil may uod sa loob ng stem.",
                            "Maghanda ng Chili-Garlic Spray: 10 pirasong siling labuyo + 1 buong ulo ng bawang + 1 kutsaritang sabon sa 1L tubig. I-spray sa buong halaman tuwing alas-4:30 ng hapon.",
                            "Para sa ampalaya o pipino, magbalot ng bunga gamit ang fruit bag o diyaryo matapos mamukadkad ang bulaklak."
                        ),
                        materialsNeeded = listOf(
                            "Siling labuyo at bawang",
                            "Neem oil o mild dishwashing liquid",
                            "Fruit bagging materials (paper/net bags)"
                        ),
                        criticalRule = "Huwag itapon ang mga inuod na bunga sa ibabaw ng compost heap dahil magiging paruparo muli ang mga ito at babalik sa tanim."
                    )
                }
                selectedOptions.contains("aphids_whitefly") || selectedOptions.contains("leaf_curling") -> {
                    DirectCropSolution(
                        title = "Neem Oil at Soap Drench Laban sa Aphids at Whiteflies ng $cropName",
                        tagalogTitle = "Solusyon sa Sap-Sucking Pests at Kulot na Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang aphids at whiteflies ay sumisipsip sa ilalim ng dahon at nagpapakalat ng virus na nagdudulot ng kulot na dahon. Ang sabon at neem oil ay sumisira sa protective wax coat ng mga insekto at pumapatay sa kanila sa loob ng ilang oras.",
                        actionSteps = listOf(
                            "Maghalo ng 1 kutsarang Neem Oil at 1/2 kutsaritang liquid dish soap sa 1 litro ng maligamgam na tubig.",
                            "I-spray nang masinsin sa ILALIM ng mga dahon kung saan nagtatago ang mga peste, tuwing alas-4:30 ng hapon.",
                            "Magkabit ng Yellow Sticky Traps sa taas ng tanim upang mahuli ang mga lumilipad na whiteflies.",
                            "Ulitin ang spray tuwing 3 araw sa loob ng 2 linggo hanggang maubos ang peste."
                        ),
                        materialsNeeded = listOf(
                            "Neem oil (cold-pressed)",
                            "Mild liquid soap (emulsifier)",
                            "Yellow sticky insect traps",
                            "Sprayer na may fine mist"
                        ),
                        criticalRule = "Huwag mag-spray ng neem oil sa tirik ng araw dahil mapapaso ang mga dahon ng tanim."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Pangkalahatang Organikong Pest Barrier para sa $cropName",
                        tagalogTitle = "Proteksyon Laban sa Butas-butas na Dahon",
                        severity = "Pangangalaga",
                        badgeColor = LushGreen,
                        directAnswer = "Para sa mga kumakain ng dahon tulad ng flea beetle at grasshoppers, mag-spray ng fermented plant extracts at magbudbod ng wood ash sa ibabaw ng dahon.",
                        actionSteps = listOf(
                            "I-spray ang chili-garlic extract tuwing 4 na araw sa hapon.",
                            "Magbudbod ng manipis na kahoy na abo sa mga dahon kapag may hamog sa umaga.",
                            "Manwal na pulutin ang nakikitang malalaking uod gamit ang sipit."
                        ),
                        materialsNeeded = listOf("Kahoy na abo", "Chili spray", "Sipit"),
                        criticalRule = "Huwag magbudbod ng labis na abo sa basang-basa na dahon upang hindi magbara ang pores."
                    )
                }
            }
        }
        "disease" -> {
            when {
                selectedOptions.contains("bacterial_wilt") -> {
                    DirectCropSolution(
                        title = "Emergency Quarantine Laban sa Bacterial Wilt ng $cropName",
                        tagalogTitle = "Kritikal: Biglaang Pagkalanta ng Buong Puno",
                        severity = "Kritikal (Bacterial Wilt)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Bacterial Wilt (Ralstonia solanacearum) ay soil-borne na bacteria na bumabara sa vascular water system ng halaman. Walang kemikal na lunas kapag apektado na ang puno. Kailangan itong bunutin agad upang hindi kumalat sa buong bed.",
                        actionSteps = listOf(
                            "Agad bunutin ang apektadong halaman kasama ang buong ugat at lupang nakadikit dito.",
                            "Ibalot sa plastic at sunugin o itapon sa malayo. HUWAG isama sa compost!",
                            "Ibuhos ang kumukulong tubig o lagyan ng agricultural lime (apog) ang hukay kung saan binunot ang tanim upang mamatay ang bacteria.",
                            "Huwag itaniman ng kamatis, talong, o sili ang parehong hukay sa susunod na crop rotation (magtanim ng mais o pechay sa halip)."
                        ),
                        materialsNeeded = listOf(
                            "Plastic bag para sa quarantine",
                            "Agricultural lime (apog) o kumukulong tubig",
                            "Pala na disinfected gamit ang alcohol"
                        ),
                        criticalRule = "I-sanitize ang gunting o kamay gamit ang 70% alcohol bago humawak ng ibang malulusog na halaman."
                    )
                }
                selectedOptions.contains("early_blight") || selectedOptions.contains("powdery_mildew") -> {
                    DirectCropSolution(
                        title = "Baking Soda at Milk Fungicide Spray para sa $cropName",
                        tagalogTitle = "Lunas sa Leaf Spots, Early Blight, at Powdery Mildew",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang mga mantsa sa dahon ay karaniwang fungal spores na nabubuhay sa basang dahon. Ang baking soda ay nagpapataas ng pH sa ibabaw ng dahon na pumapatay sa fungi, habang ang protina ng gatas ay lumilikha ng natural na antiseptic barrier.",
                        actionSteps = listOf(
                            "Gupitin at sunugin ang lahat ng apektadong dahon na may mantsa. Huwag iwanan sa ibabaw ng lupa.",
                            "Maghalo ng 1 kutsaritang Baking Soda + 1/2 kutsaritang sabon sa 1L tubig. I-spray sa buong halaman tuwing 5 araw.",
                            "Bilang alternatibo sa amag (mildew): Paghaluin ang 1 basong gatas sa 9 na basong tubig (1:9) at i-spray sa maaraw na umaga.",
                            "Siguraduhing sa ugat lang nagdidilig at hindi nababasa ang mga dahon."
                        ),
                        materialsNeeded = listOf(
                            "Baking soda (Sodium bicarbonate)",
                            "Mild liquid soap",
                            "Gatas (evaporated o fresh milk)",
                            "Sprayer"
                        ),
                        criticalRule = "Huwag lumampas sa 1 kutsaritang baking soda bawat litro upang hindi masunog ang talbos ng halaman."
                    )
                }
                else -> null
            }
        }
        "pruning" -> {
            DirectCropSolution(
                title = "Wastong Pruning at Pagtatanggal ng Suwi para sa $cropName",
                tagalogTitle = "Pagpapalaki ng Bunga at Pagpapabuti ng Sirkulasyon",
                severity = "Pangangalaga (Pruning)",
                badgeColor = LushGreen,
                directAnswer = "Ang pruning sa $cropName ay nag-aalis ng mga suwi (suckers) na kumakain ng sustansya nang hindi nagbubunga, at nagtatanggal ng mga lumang dahon sa ibaba na madaling kapitan ng sakit mula sa lupa.",
                actionSteps = listOf(
                    "Tingnan ang 'kili-kili' sa pagitan ng pangunahing tangkay at sanga. Tanggalin ang mga suwi habang maliliit pa (2–4 pulgada) gamit ang mga daliri.",
                    "Mag-iwan lamang ng 1 o 2 pangunahing tangkay (main stems) upang lumaki nang husto ang mga bunga.",
                    "Gupitin ang lahat ng dahon na nasa unang 30cm mula sa lupa gamit ang disinfected shears.",
                    "Gawin ang pruning sa umaga kapag maaraw upang mabilis matuyo ang sugat ng halaman."
                ),
                materialsNeeded = listOf(
                    "Pruning shears o matalas na gunting",
                    "70% alcohol para sa pag-sanitize ng gamit"
                ),
                criticalRule = "Huwag mag-prune kapag maulan o basa ang dahon dahil madaling makapasok ang bacterial infection sa basang sugat."
            )
        }
        "harvest" -> {
            DirectCropSolution(
                title = "Tamang Yugto at Pamamaraan ng Pag-aani ng $cropName",
                tagalogTitle = "Gabay sa Masaganang Pitas at Pagpapanatili ng Kasariwaan",
                severity = "Pag-aani (Harvest Time)",
                badgeColor = LushGreen,
                directAnswer = "Ang tamang pag-aani ng $cropName sa wastong oras ay nagpapahaba ng buhay ng puno at nagpapanatili ng pinakamasarap na sustansya at tamis ng gulay.",
                actionSteps = listOf(
                    "Pitasin ang $cropName sa umaga (6:00 AM–8:00 AM) bago uminit ang araw habang puno pa ng tubig ang mga selula ng gulay.",
                    "Gumamit ng matalas na gunting o pruner at mag-iwan ng 1 pulgadang tangkay sa bunga upang hindi pasukin ng mikrobyo.",
                    "Huwag bunutin o hilahin nang sapilitan ang bunga dahil mababali ang mga katabing bulaklak at sanga.",
                    "Ilagay sa malinis at may bentilasyong lalagyan (bilawo o basket) sa malamig na lugar."
                ),
                materialsNeeded = listOf(
                    "Matalas na gunting o pruning shears",
                    "Woven basket o breathable crate",
                    "Malinis na tela"
                ),
                criticalRule = "Huwag ibilad sa direktang sikat ng araw ang mga bagong aning gulay pagkatapos pitasin."
            )
        }
        else -> null
    }
}
