package com.maptanim.app.ui.screens.dss

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

// ─── Visual Theme Tokens ──────────────────────────────────────────────────────
private val DssBackground = Color(0xFF0F150D)
private val DssCardBg = Color(0xFF161E14)
private val DssSurfaceHighlight = Color(0xFF1E281B)
private val DssBorderColor = Color(0xFF283624)
private val DssAccentGreen = Color(0xFF4CAF50)
private val DssWarningOrange = Color(0xFFFF9800)
private val DssDangerRed = Color(0xFFE53935)
private val DssInfoBlue = Color(0xFF29B6F6)
private val DssTextMuted = Color(0xFF9EAC98)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DssScreen(
    navController: NavController,
    farmId: String? = null,
    viewModel: DssViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(farmId) {
        viewModel.initialize(farmId)
    }

    // Toast feedback banner auto-clear
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearToast()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = DssBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP APP BAR ───────────────────────────────────────────────────
            DssTopAppBar(
                farmName = uiState.farmName,
                sessionId = uiState.session?.sessionId,
                isRefreshing = uiState.isRefreshing,
                onBack = { navController.popBackStack() },
                onRefresh = { viewModel.refreshEvaluation() }
            )

            // Toast feedback banner
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.toastMessage ?: "",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (uiState.isLoading && uiState.allDecisions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DssAccentGreen)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Consulting Agricultural Decision Engine...",
                            color = DssTextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Evaluating conditions via DA-BPI & DA-BAR rules",
                            color = Color(0xFF6E8068),
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
                ) {
                    // 1. KPI Summary Cards
                    item {
                        DssKpiSummarySection(summary = uiState.summary)
                    }

                    // 2. Data Limitations / Transparent Validation Notices
                    if (uiState.insufficientDataNotices.isNotEmpty()) {
                        item {
                            DssDataLimitationsCard(notices = uiState.insufficientDataNotices)
                        }
                    }

                    // 3. Category & Filter Chips
                    item {
                        DssFilterChipsSection(
                            selectedCategory = uiState.selectedCategory,
                            selectedPriority = uiState.selectedPriority,
                            onSelectCategory = { viewModel.selectCategory(it) },
                            onSelectPriority = { viewModel.selectPriority(it) }
                        )
                    }

                    // 4. Header for Findings List
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RESEARCH-BASED ADVISORIES (${uiState.filteredDecisions.size})",
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (uiState.session != null) {
                                Text(
                                    text = "Engine v${uiState.session?.engineVersion}",
                                    color = Color(0xFF6E8068),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // 5. Findings List
                    if (uiState.filteredDecisions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "All Clear",
                                        tint = DssAccentGreen.copy(alpha = 0.6f),
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No active warnings or care tasks for this filter.",
                                        color = DssTextMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.filteredDecisions, key = { it.id }) { decision ->
                            DssDecisionCard(
                                decision = decision,
                                onOpenDetail = { viewModel.openDecisionDetail(decision) },
                                onActOnDecision = { viewModel.actOnDecision(decision) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Deep-Dive Agronomic Explanation Dialog
    uiState.selectedDecisionForDetail?.let { decision ->
        DssDecisionDetailDialog(
            decision = decision,
            onDismiss = { viewModel.closeDecisionDetail() },
            onAct = { viewModel.actOnDecision(decision) }
        )
    }
}

// ─── Top App Bar ──────────────────────────────────────────────────────────────

@Composable
private fun DssTopAppBar(
    farmName: String,
    sessionId: String?,
    isRefreshing: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DssBackground)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DECISION SUPPORT SYSTEM",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(DssAccentGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, DssAccentGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text("DA-BPI", color = DssAccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "$farmName • ${sessionId ?: "Active Session"}",
                    color = DssTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        IconButton(onClick = onRefresh, enabled = !isRefreshing) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = DssAccentGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Re-evaluate",
                    tint = DssAccentGreen
                )
            }
        }
    }
}

// ─── KPI Summary Cards ────────────────────────────────────────────────────────

@Composable
private fun DssKpiSummarySection(summary: com.maptanim.app.dss.model.DssResultSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DssKpiMiniCard(
            modifier = Modifier.weight(1f),
            count = summary.criticalAlerts,
            label = "Alerts",
            icon = Icons.Default.Warning,
            color = DssDangerRed
        )
        DssKpiMiniCard(
            modifier = Modifier.weight(1f),
            count = summary.tasksCount,
            label = "Care Tasks",
            icon = Icons.AutoMirrored.Filled.Assignment,
            color = DssWarningOrange
        )
        DssKpiMiniCard(
            modifier = Modifier.weight(1f),
            count = summary.recommendationsCount,
            label = "Advisories",
            icon = Icons.Default.Lightbulb,
            color = DssAccentGreen
        )
        DssKpiMiniCard(
            modifier = Modifier.weight(1f),
            count = summary.monitoredPlotsCount,
            label = "Beds",
            icon = Icons.Default.Yard,
            color = DssInfoBlue
        )
    }
}

@Composable
private fun DssKpiMiniCard(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DssCardBg),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, DssBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = count.toString(),
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = DssTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── Data Limitations / Transparent Notices Card ──────────────────────────────

@Composable
private fun DssDataLimitationsCard(notices: List<com.maptanim.app.dss.model.DssValidationIssue>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261E14)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF5D4023))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = DssWarningOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Data Limitations & Insufficient Info (${notices.size})",
                        color = DssWarningOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = DssWarningOrange,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The DSS never invents values for missing information. The following data points are needed for complete evaluation:",
                    color = Color(0xFFDCCDBE),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    notices.forEach { issue ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", color = DssWarningOrange, fontSize = 11.sp)
                            Text(issue.message, color = White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── Filter Chips Section ─────────────────────────────────────────────────────

@Composable
private fun DssFilterChipsSection(
    selectedCategory: DssCategory?,
    selectedPriority: DssPriority?,
    onSelectCategory: (DssCategory?) -> Unit,
    onSelectPriority: (DssPriority?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                DssFilterChip(
                    label = "All Advisories",
                    isSelected = selectedCategory == null && selectedPriority == null,
                    onClick = {
                        onSelectCategory(null)
                        onSelectPriority(null)
                    }
                )
            }
            item {
                DssFilterChip(
                    label = "🚨 Urgent / Critical",
                    isSelected = selectedPriority == DssPriority.CRITICAL,
                    onClick = { onSelectPriority(DssPriority.CRITICAL) }
                )
            }
            item {
                DssFilterChip(
                    label = "💧 Crop Care",
                    isSelected = selectedCategory == DssCategory.GROWTH_CARE,
                    onClick = { onSelectCategory(DssCategory.GROWTH_CARE) }
                )
            }
            item {
                DssFilterChip(
                    label = "🐛 Pest & Disease",
                    isSelected = selectedCategory == DssCategory.PEST_DISEASE,
                    onClick = { onSelectCategory(DssCategory.PEST_DISEASE) }
                )
            }
            item {
                DssFilterChip(
                    label = "🤝 Companions",
                    isSelected = selectedCategory == DssCategory.COMPANION_INTERCROPPING,
                    onClick = { onSelectCategory(DssCategory.COMPANION_INTERCROPPING) }
                )
            }
            item {
                DssFilterChip(
                    label = "🔄 Crop Rotation",
                    isSelected = selectedCategory == DssCategory.CROP_ROTATION_FALLOW,
                    onClick = { onSelectCategory(DssCategory.CROP_ROTATION_FALLOW) }
                )
            }
            item {
                DssFilterChip(
                    label = "🌾 Harvest Timing",
                    isSelected = selectedCategory == DssCategory.HARVEST_READINESS,
                    onClick = { onSelectCategory(DssCategory.HARVEST_READINESS) }
                )
            }
            item {
                DssFilterChip(
                    label = "🧪 Soil Match",
                    isSelected = selectedCategory == DssCategory.SOIL_COMPATIBILITY,
                    onClick = { onSelectCategory(DssCategory.SOIL_COMPATIBILITY) }
                )
            }
        }
    }
}

@Composable
private fun DssFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) DssAccentGreen else DssSurfaceHighlight
    val textColor = if (isSelected) Color.Black else DssTextMuted
    val borderColor = if (isSelected) DssAccentGreen else DssBorderColor

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// ─── Decision Card ────────────────────────────────────────────────────────────

@Composable
private fun DssDecisionCard(
    decision: DssDecision,
    onOpenDetail: () -> Unit,
    onActOnDecision: () -> Unit
) {
    val priorityColor = when (decision.priority) {
        DssPriority.CRITICAL -> DssDangerRed
        DssPriority.HIGH -> DssWarningOrange
        DssPriority.MEDIUM -> DssAccentGreen
        DssPriority.LOW -> Color(0xFF64B5F6)
        DssPriority.INFO -> Color(0xFF90A4AE)
    }

    val typeBadgeColor = when (decision.decisionType) {
        DssDecisionType.ALERT -> DssDangerRed
        DssDecisionType.TASK -> DssWarningOrange
        DssDecisionType.RECOMMENDATION -> DssAccentGreen
        DssDecisionType.INSUFFICIENT_INFO -> Color(0xFF90A4AE)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail),
        colors = CardDefaults.cardColors(containerColor = DssCardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DssBorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header: Priority & Type Badges + Plot Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Priority Pill
                    Box(
                        modifier = Modifier
                            .background(priorityColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(0.8.dp, priorityColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = decision.priority.name,
                            color = priorityColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Decision Type Pill
                    Box(
                        modifier = Modifier
                            .background(typeBadgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = decision.decisionType.name,
                            color = typeBadgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Plot or Crop identifier
                if (!decision.plotLabel.isNullOrBlank()) {
                    Text(
                        text = "${decision.plotLabel} • ${decision.cropName ?: "Bed"}",
                        color = DssTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = decision.title,
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Summary
            Text(
                text = decision.summary,
                color = Color(0xFFD4E2CF),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Research Explanation Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DssSurfaceHighlight, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Research Rationale",
                            tint = DssAccentGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Agronomic Rationale:",
                            color = DssAccentGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = decision.explanation,
                        color = Color(0xFFB0C0AB),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Academic Source Tag + Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source Citation
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Source",
                        tint = Color(0xFF7B8E77),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = decision.source,
                        color = Color(0xFF8A9C86),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action Button
                if (decision.actionText != null && decision.isActionable) {
                    Button(
                        onClick = onActOnDecision,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = decision.actionText,
                            fontSize = 11.sp,
                            color = White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ─── Deep-Dive Detail Dialog ──────────────────────────────────────────────────

@Composable
private fun DssDecisionDetailDialog(
    decision: DssDecision,
    onDismiss: () -> Unit,
    onAct: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, DssAccentGreen.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AGRONOMIC ADVISORY",
                        color = DssAccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = decision.title,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!decision.plotLabel.isNullOrBlank()) {
                    Text(
                        text = "Target: ${decision.plotLabel} (${decision.cropName ?: "General Bed"})",
                        color = DssTextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Recommendation & Guidance:",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = decision.summary,
                    color = Color(0xFFD2E0CC),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Why this is recommended (Scientific Rationale):",
                    color = DssAccentGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = decision.explanation,
                    color = Color(0xFFB5C7B0),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Source Citation Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B2518), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "OFFICIAL RESEARCH SOURCE:",
                            color = Color(0xFF7A8E76),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = decision.source,
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = DssTextMuted)
                    }
                    if (decision.actionText != null && decision.isActionable) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onAct()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(decision.actionText, color = White)
                        }
                    }
                }
            }
        }
    }
}
