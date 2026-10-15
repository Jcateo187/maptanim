package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.getCropHeroDrawable
import com.maptanim.app.features.farm.dialogs.dss_checkup.CheckUpActivity
import com.maptanim.app.features.farm.dialogs.dss_checkup.CropDssRegistry
import com.maptanim.app.features.farm.dialogs.dss_checkup.DirectCropSolution
import com.maptanim.app.features.farm.schedule.CheckUpLockedDialog
import com.maptanim.app.features.farm.schedule.CheckUpRecorder
import com.maptanim.app.features.farm.schedule.CropScheduleStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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



/**
 * CropCareDssDialog — State-of-the-Art Full-Screen Direct Agronomic Check-Up Screen.
 * Triggered directly from the Canvas floating "Check Up" button.
 * Automatically detects the tapped crop, lets the user select an activity and checkboxes,
 * and provides immediate, prescriptive solutions without reading long encyclopedia text.
 *
 * @param scheduleStatus when provided and the schedule has not started (No schedule / Upcoming),
 *                       a lock dialog is shown instead of the form.
 * @param onOpenSchedule action for the lock dialog (open Calendar / set planting date).
 */
@Composable
fun CropCareDssDialog(
    cropName: String,
    varietyName: String = "",
    gardenLabel: String = "Garden",
    plotId: String = "",
    currentStage: ManagementStage = ManagementStage.VEGETATIVE_GROWTH,
    scheduleStatus: CropScheduleStatus? = null,
    onOpenSchedule: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (scheduleStatus != null && !scheduleStatus.canCheckUp) {
        CheckUpLockedDialog(
            cropName = cropName,
            status = scheduleStatus,
            onOpenSchedule = onOpenSchedule,
            onDismiss = onDismiss
        )
    } else {
        CropCareDssDialogContent(
            cropName = cropName,
            varietyName = varietyName,
            gardenLabel = gardenLabel,
            plotId = plotId,
            currentStage = scheduleStatus?.stageOrNull ?: currentStage,
            dayNumber = scheduleStatus?.dayNumberOrNull,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun CropCareDssDialogContent(
    cropName: String,
    varietyName: String,
    gardenLabel: String,
    plotId: String,
    currentStage: ManagementStage,
    dayNumber: Int?,
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
    var isSeeAllExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        ConfigureDialogSystemBars(isLightStatusBar = true, isLightNavigationBar = true)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
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
                            .statusBarsPadding()
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
                                    text = "Vegetables Check Up",
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
                            // Native Crop Hero Avatar
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f)),
                                modifier = Modifier.size(54.dp)
                            ) {
                                val heroResId = getCropHeroDrawable(cropName)
                                Image(
                                    painter = painterResource(id = heroResId),
                                    contentDescription = cropName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
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

                                val stageText = buildString {
                                    if (dayNumber != null) append("Araw $dayNumber • ")
                                    if (varietyName.isNotBlank()) append("Barayti: $varietyName • ")
                                    append("Yugto: ${currentStage.label}")
                                }
                                Text(
                                    text = stageText,
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
                            Column {
                                Text(
                                    text = "1. PUMILI NG GAWAIN O KATEGORYA",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestGreen,
                                    letterSpacing = 0.4.sp
                                )
                                Text(
                                    text = "I-scroll pakanan para makita lahat (${activities.size}) 👉",
                                    fontSize = 11.sp,
                                    color = MutedText,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // See All interactive toggle button
                            Surface(
                                onClick = { isSeeAllExpanded = !isSeeAllExpanded },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSeeAllExpanded) ForestGreen else LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isSeeAllExpanded) "I-collapse" else "See all",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSeeAllExpanded) Color.White else ForestGreen
                                    )
                                    Icon(
                                        imageVector = if (isSeeAllExpanded) Icons.Default.KeyboardArrowUp else Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "See all",
                                        tint = if (isSeeAllExpanded) Color.White else ForestGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        if (isSeeAllExpanded) {
                            // Expanded Grid of all activity category buttons
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                activities.chunked(2).forEach { rowActs ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowActs.forEach { act ->
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
                                                shadowElevation = if (isSelected) 2.dp else 1.dp,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = act.icon,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color.White else ForestGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = act.title,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                        color = if (isSelected) Color.White else DeepBlack,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                        if (rowActs.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        } else {
                            // Horizontal Activity Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
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

                                // Trailing See All Pill
                                Surface(
                                    onClick = { isSeeAllExpanded = true },
                                    shape = RoundedCornerShape(12.dp),
                                    color = LightGreenBg,
                                    border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "See All (${activities.size})",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
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
                            val activeChecked = selectedOptionIds.filterValues { it }.keys.toSet()
                            val sol = solveCropProblem(
                                cropName = cropName,
                                activityId = selectedActivityId,
                                selectedOptions = activeChecked
                            )
                            evaluatedSolution = sol

                            // Automatically record check-up report to CropLog repository
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val activeOptions = currentActivity.options
                                        .filter { selectedOptionIds[it.id] == true }
                                    val log = CheckUpRecorder.buildLog(
                                        cropName = cropName,
                                        varietyName = varietyName,
                                        gardenLabel = gardenLabel,
                                        plotId = plotId,
                                        stage = currentStage,
                                        activity = currentActivity,
                                        selected = activeOptions,
                                        solution = sol
                                    )
                                    CheckUpRecorder.save(log)
                                } catch (_: Exception) {}
                            }
                            isSavedToLog = true
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
                                            if (!isSavedToLog) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        val activeOptions = currentActivity.options
                                                            .filter { selectedOptionIds[it.id] == true }
                                                        val log = CheckUpRecorder.buildLog(
                                                            cropName = cropName,
                                                            varietyName = varietyName,
                                                            gardenLabel = gardenLabel,
                                                            plotId = plotId,
                                                            stage = currentStage,
                                                            activity = currentActivity,
                                                            selected = activeOptions,
                                                            solution = sol
                                                        )
                                                        CheckUpRecorder.save(log)
                                                    } catch (_: Exception) {}
                                                }
                                                isSavedToLog = true
                                            }
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


// -------------------------------------------------------------------------------
// Delegating to modular dss_checkup folder (TomatoDssCheckup / EggplantDssCheckup)
// -------------------------------------------------------------------------------
private fun getCheckUpActivitiesForCrop(cropName: String): List<CheckUpActivity> {
    return CropDssRegistry.getCheckUpActivitiesForCrop(cropName)
}

private fun solveCropProblem(
    cropName: String,
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    return CropDssRegistry.solveCropProblem(cropName, activityId, selectedOptions)
}
