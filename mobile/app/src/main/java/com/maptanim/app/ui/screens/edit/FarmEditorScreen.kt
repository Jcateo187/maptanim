package com.maptanim.app.ui.screens.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.res.Configuration
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.maptanim.app.renderer.canvas.TopDownCamera
import com.maptanim.app.renderer.canvas.TopDownFarmCanvas
import com.maptanim.app.renderer.canvas.TopDownProjection

import com.maptanim.app.ui.components.editcomponents.croptray.CropTray
import com.maptanim.app.ui.components.editcomponents.layout.EditBottomLayout
import com.maptanim.app.ui.components.editcomponents.summary.CropsSummaryOverlay

import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.TrackBgmEffect

// ── Design Tokens ────────────────────────────────────────────────────────────
private val ChromeDark    = Color(0xFF1A1F16)
private val ChromeBorder  = Color(0xFF2E3D28)
private val AccentGreen   = Color(0xFF4CAF50)
private val MutedGreen    = Color(0xFF81C784)
private val SaveGreen     = Color(0xFF2E7D32)
private val TextPrimary   = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFAAAAAA)
private val ToolbarBg     = Color(0xFF1C2418)
private val ActiveToolBg  = Color(0xFF2E5930)

/**
 * FarmEditorScreen — Professional Canva/Miro-style 2D top-down farm canvas editor.
 *
 * Layout:
 *   ┌──────────────────────────────────────────────────┐
 *   │ ← Farm Name                    ↶  ↷      ✓ Save │  ← Slim top bar (44dp)
 *   ├──────────────────────────────────────────────────┤
 *   │                                                  │
 *   │         2D TOP-DOWN FARM CANVAS (~90%)           │
 *   │         (flat orthographic view)                 │
 *   │                                                  │
 *   │                                          ┌──┐   │
 *   │                                          │+ │   │  ← Right zoom controls
 *   │                                          │− │   │
 *   │                                          │⌂ │   │
 *   │          ┌──────────────────────┐        └──┘   │
 *   │          │↖ Select │🟫 Bed │🌱 Crop│            │  ← Bottom-center tools
 *   │          └──────────────────────┘                │
 *   └──────────────────────────────────────────────────┘
 */
@Composable
fun FarmEditorScreen(
    navController: NavController,
    editViewModel: EditViewModel = viewModel(),
    tutorialViewModel: com.maptanim.app.viewmodel.TutorialViewModel = viewModel()
) {
    TrackBgmEffect(BackgroundTrack.EDITOR_FOCUS)

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiState by editViewModel.uiState.collectAsState()
    val tutorialUiState by tutorialViewModel.uiState.collectAsState()
    var activeCropName by remember { mutableStateOf("") }
    var activeCropId by remember { mutableStateOf("") }
    var isRightPanelVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { editViewModel.refresh() }

    LaunchedEffect(tutorialUiState.currentStep, isRightPanelVisible) {
        if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON) {
            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT)
        }
        if (isRightPanelVisible && (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT ||
                                    tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON)) {
            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
        }
    }

    // 2D Camera state — flat orthographic, no isometric
    var liveCamera by remember { mutableStateOf(TopDownCamera()) }

    // Drag & Drop state
    var isDraggingCrop by remember { mutableStateOf(false) }
    var dragCropName by remember { mutableStateOf("Carrot") }
    var dragCropId by remember { mutableStateOf("carrot") }
    var dragCropImageUrl by remember { mutableStateOf<String?>(null) }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }

    // Hover position: convert screen drag position → world using flat 2D projection
    val hoverWorldPos = remember(isDraggingCrop, dragTouchPos, liveCamera) {
        if (isDraggingCrop) {
            val rawWorld = TopDownProjection.screenToWorld(dragTouchPos.x, dragTouchPos.y, liveCamera)
            val snapped = TopDownProjection.snapToGrid(rawWorld)
            Offset(snapped.x.coerceIn(0f, 44.0f), snapped.y.coerceIn(0f, 44.0f))
        } else null
    }

    val isValidPlacement = remember(isDraggingCrop, dragCropId, dragCropName, hoverWorldPos, uiState.plots) {
        if (isDraggingCrop && hoverWorldPos != null) {
            val hx = hoverWorldPos.x; val hy = hoverWorldPos.y
            val inBounds = hx >= 0f && hy >= 0f && (hx + 1.0f) <= 45.0f && (hy + 1.0f) <= 45.0f
            val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)

            val existingBed = uiState.plots.firstOrNull { plot ->
                hx >= plot.posX && hx < (plot.posX + plot.widthM) &&
                hy >= plot.posY && hy < (plot.posY + plot.heightM) &&
                (plot.cropName == "Bed" || plot.cropId?.startsWith("bed") == true)
            }

            if (!isBedDrag) {
                inBounds && existingBed != null
            } else {
                val overlaps = uiState.plots.any { plot ->
                    hx < (plot.posX + plot.widthM) && (hx + 1.0f) > plot.posX &&
                    hy < (plot.posY + plot.heightM) && (hy + 1.0f) > plot.posY
                }
                inBounds && !overlaps
            }
        } else true
    }

    var showCropsSummaryOverlay by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var farmNameInput by remember { mutableStateOf("Murcia Farm") }

    Box(modifier = Modifier.fillMaxSize()) {

        // ═════════════════════════════════════════════════════════════════
        // 1. 2D TOP-DOWN FARM CANVAS — occupies full screen
        // ═════════════════════════════════════════════════════════════════
        TopDownFarmCanvas(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            uiState = uiState,
            editViewModel = editViewModel,
            activeCropName = activeCropName,
            activeCropId = activeCropId,
            hoverWorldPos = hoverWorldPos,
            isValidPlacement = isValidPlacement,
            isDraggingCrop = isDraggingCrop,
            onCameraChanged = { liveCamera = it }
        )

        BackHandler {
            editViewModel.discardChanges()
            navController.popBackStack()
        }

        // ═════════════════════════════════════════════════════════════════
        // 2. SLIM TOP BAR — ← Farm Name          ↶  ↷  ✓ Save
        // ═════════════════════════════════════════════════════════════════
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding(),
            color = ChromeDark.copy(alpha = 0.92f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ← Back
                IconButton(onClick = {
                    editViewModel.discardChanges()
                    navController.popBackStack()
                }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }

                // Farm Name (plain text display only - rename not included)
                Text(
                    text = farmNameInput,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                // Undo
                IconButton(
                    onClick = { editViewModel.undo() },
                    enabled = uiState.canUndo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, "Undo",
                        tint = if (uiState.canUndo) TextPrimary else TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(19.dp))
                }

                // Redo
                IconButton(
                    onClick = { editViewModel.redo() },
                    enabled = uiState.canRedo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Redo, "Redo",
                        tint = if (uiState.canRedo) TextPrimary else TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(19.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                // ✓ Save pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SaveGreen,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .height(32.dp)
                        .clickable {
                            editViewModel.onSavePressed(
                                onReturnToFarm = {
                                    if (tutorialUiState.isTutorialActive) { tutorialViewModel.completeTutorial() }
                                    navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                                        popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                                    }
                                }
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Check, "Save", tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (uiState.isSaving) "Saving…" else "Save",
                            fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // ── Drop / Placement Feedback Banner ─────────────────────────
        if (uiState.dropFeedbackMessage != null) {
            LaunchedEffect(uiState.dropFeedbackMessage) {
                kotlinx.coroutines.delay(3500)
                editViewModel.clearDropFeedbackMessage()
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 50.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFC62828).copy(alpha = 0.94f),
                border = BorderStroke(1.dp, Color(0xFFFF8A80)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(
                        text = uiState.dropFeedbackMessage ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { editViewModel.clearDropFeedbackMessage() },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // ═════════════════════════════════════════════════════════════════
        // 3. FLOATING ZOOM CONTROLS (Left in landscape, top-left near header in portrait)
        // ═════════════════════════════════════════════════════════════════
        val zoomControlsMod = if (isLandscape) {
            Modifier
                .align(Alignment.CenterStart)
                .navigationBarsPadding()
                .padding(start = 12.dp)
        } else {
            Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 50.dp, start = 12.dp)
        }

        Column(
            modifier = zoomControlsMod,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val topPad = with(density) { 56.dp.toPx() }
            val botPad = with(density) { if (isLandscape) 20.dp.toPx() else 60.dp.toPx() }
            val leftPad = with(density) { if (isLandscape) 64.dp.toPx() else 48.dp.toPx() }
            val rightPad = with(density) { if (isLandscape) 72.dp.toPx() else 16.dp.toPx() }

            ZoomButton(Icons.Default.Add, "Zoom In") {
                val sw = configuration.screenWidthDp.toFloat() * context.resources.displayMetrics.density
                val sh = configuration.screenHeightDp.toFloat() * context.resources.displayMetrics.density
                val center = Offset(sw / 2f, sh / 2f)
                val worldAtCenter = TopDownProjection.screenToWorld(center.x, center.y, liveCamera)
                val newZoom = (liveCamera.zoom * 1.3f).coerceIn(0.15f, 3.5f)
                val rawCam = liveCamera.copy(
                    zoom = newZoom,
                    panX = center.x - worldAtCenter.x * TopDownProjection.PPM * newZoom,
                    panY = center.y - worldAtCenter.y * TopDownProjection.PPM * newZoom
                )
                liveCamera = TopDownProjection.clampCamera(
                    rawCam, farmW = 45f, farmH = 45f, screenW = sw, screenH = sh,
                    topPadding = topPad, bottomPadding = botPad,
                    leftPadding = leftPad, rightPadding = rightPad
                )
            }
            ZoomButton(Icons.Default.Remove, "Zoom Out") {
                val sw = configuration.screenWidthDp.toFloat() * context.resources.displayMetrics.density
                val sh = configuration.screenHeightDp.toFloat() * context.resources.displayMetrics.density
                val center = Offset(sw / 2f, sh / 2f)
                val worldAtCenter = TopDownProjection.screenToWorld(center.x, center.y, liveCamera)
                val newZoom = (liveCamera.zoom * 0.75f).coerceIn(0.15f, 3.5f)
                val rawCam = liveCamera.copy(
                    zoom = newZoom,
                    panX = center.x - worldAtCenter.x * TopDownProjection.PPM * newZoom,
                    panY = center.y - worldAtCenter.y * TopDownProjection.PPM * newZoom
                )
                liveCamera = TopDownProjection.clampCamera(
                    rawCam, farmW = 45f, farmH = 45f, screenW = sw, screenH = sh,
                    topPadding = topPad, bottomPadding = botPad,
                    leftPadding = leftPad, rightPadding = rightPad
                )
            }
            ZoomButton(Icons.Default.FitScreen, "Fit Farm") {
                val sw = configuration.screenWidthDp.toFloat() * context.resources.displayMetrics.density
                val sh = configuration.screenHeightDp.toFloat() * context.resources.displayMetrics.density
                liveCamera = TopDownProjection.fitCamera(
                    farmW = 45f, farmH = 45f, screenW = sw, screenH = sh,
                    topPadding = topPad, bottomPadding = botPad,
                    leftPadding = leftPad, rightPadding = rightPad
                )
            }
            ZoomButton(if (uiState.isGridEnabled) Icons.Default.GridOn else Icons.Default.GridOff, "Toggle Grid") {
                editViewModel.toggleGrid()
            }
        }

        // ═════════════════════════════════════════════════════════════════
        // 4. FLOATING TOOLBAR — Bed | Crop (Bottom in portrait, Right in landscape)
        // ═════════════════════════════════════════════════════════════════
        if (!isRightPanelVisible && !isDraggingCrop) {
            if (uiState.selectedPlotId != null || uiState.selectedZoneId != null) {
                // Contextual edit bar for selected plot/bed or individual crop zone
                EditBottomLayout(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    uiState = uiState,
                    onDuplicateClick = {
                        val zoneId = uiState.selectedZoneId
                        if (zoneId != null) {
                            editViewModel.duplicateCropZone(zoneId)
                        } else {
                            uiState.selectedPlotId?.let { editViewModel.duplicatePlot(it) }
                        }
                    },
                    onResizeClick = { editViewModel.toggleResizeMode() },
                    onRotateClick = { uiState.selectedPlotId?.let { editViewModel.rotatePlot(it) } },
                    onChangeCropClick = { },
                    onChangeSoilClick = { uiState.selectedPlotId?.let { editViewModel.paintSoil(it) } },
                    onDeleteClick = {
                        val zoneId = uiState.selectedZoneId
                        if (zoneId != null) {
                            editViewModel.removeCropFromBed(zoneId)
                        } else {
                            showDeleteConfirmDialog = true
                        }
                    }
                )
            } else {
                // Main tool selector: Bed & Crop
                val toolbarMod = if (isLandscape) {
                    Modifier
                        .align(Alignment.CenterEnd)
                        .navigationBarsPadding()
                        .padding(end = 12.dp)
                } else {
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                }

                Surface(
                    modifier = toolbarMod,
                    shape = RoundedCornerShape(14.dp),
                    color = ToolbarBg.copy(alpha = 0.95f),
                    shadowElevation = 10.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChromeBorder)
                ) {
                    if (isLandscape) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ToolButton(
                                icon = Icons.Default.ViewModule, label = "Bed",
                                isActive = isRightPanelVisible && activeCropId.startsWith("bed", ignoreCase = true),
                                tintOverride = Color(0xFF8D6E63),
                                onClick = {
                                    isRightPanelVisible = true
                                    activeCropName = "Bed"; activeCropId = "bed"
                                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                                    }
                                }
                            )
                            ToolDividerHorizontal()
                            ToolButton(
                                icon = Icons.Default.Grass, label = "Crop",
                                isActive = isRightPanelVisible && !activeCropId.startsWith("bed", ignoreCase = true),
                                tintOverride = AccentGreen,
                                onClick = {
                                    isRightPanelVisible = true
                                    activeCropName = ""; activeCropId = ""
                                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                                    }
                                }
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ToolButton(
                                icon = Icons.Default.ViewModule, label = "Bed",
                                isActive = isRightPanelVisible && activeCropId.startsWith("bed", ignoreCase = true),
                                tintOverride = Color(0xFF8D6E63),
                                onClick = {
                                    isRightPanelVisible = true
                                    activeCropName = "Bed"; activeCropId = "bed"
                                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                                    }
                                }
                            )
                            ToolDivider()
                            ToolButton(
                                icon = Icons.Default.Grass, label = "Crop",
                                isActive = isRightPanelVisible && !activeCropId.startsWith("bed", ignoreCase = true),
                                tintOverride = AccentGreen,
                                onClick = {
                                    isRightPanelVisible = true
                                    activeCropName = ""; activeCropId = ""
                                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // ═════════════════════════════════════════════════════════════════
        // 5. CROP TRAY PANEL
        // ═════════════════════════════════════════════════════════════════
        if (isRightPanelVisible) {
            val trayMod = if (isLandscape) Modifier.align(Alignment.CenterEnd)
            else Modifier.align(Alignment.BottomCenter).navigationBarsPadding()

            CropTray(
                modifier = trayMod,
                selectedCropName = activeCropName,
                availableCrops = uiState.availableCrops,
                isSyncing = uiState.isSyncingCrops,
                onSyncRequested = { editViewModel.refreshCropsFromRemote() },
                onCropSelected = { newCropName, newCropId ->
                    if (activeCropName.equals(newCropName, ignoreCase = true)) {
                        activeCropName = ""; activeCropId = ""
                    } else {
                        activeCropName = newCropName; activeCropId = newCropId
                    }
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP)
                    }
                },
                onCropDragStart = { cropName, cropId, imageUrl, startOffset ->
                    isDraggingCrop = true
                    dragCropName = cropName; dragCropId = cropId
                    dragCropImageUrl = imageUrl; dragTouchPos = startOffset
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP)
                    }
                },
                onCropDragging = { currentOffset -> dragTouchPos = currentOffset },
                onCropDragEnd = { dropOffset ->
                    if (isDraggingCrop) {
                        val dropWorld = TopDownProjection.screenToWorld(dropOffset.x, dropOffset.y, liveCamera)
                        val snapped = TopDownProjection.snapToGrid(dropWorld)
                        val safeX = snapped.x.coerceIn(0f, 44.0f)
                        val safeY = snapped.y.coerceIn(0f, 44.0f)
                        val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)

                        val targetBed = uiState.plots.firstOrNull { plot ->
                            safeX >= plot.posX && safeX < (plot.posX + plot.widthM) &&
                            safeY >= plot.posY && safeY < (plot.posY + plot.heightM) &&
                            (plot.cropName == "Bed" || plot.cropId?.startsWith("bed") == true)
                        }

                        isDraggingCrop = false

                        if (isBedDrag) {
                            val canDrop = safeX >= 0f && safeY >= 0f && (safeX + 1.0f) <= 45.0f && (safeY + 1.0f) <= 45.0f &&
                                    !uiState.plots.any { plot ->
                                        safeX < (plot.posX + plot.widthM) && (safeX + 1.0f) > plot.posX &&
                                        safeY < (plot.posY + plot.heightM) && (safeY + 1.0f) > plot.posY
                                    }
                            if (canDrop) {
                                editViewModel.addDirectPlantingPlot(safeX, safeY, "Bed", "bed")
                                advanceTutorialAfterDrop(tutorialUiState, tutorialViewModel)
                            } else {
                                editViewModel.reportInvalidDropLocation("⚠️ Hindi maaaring maglagay ng kama dito: May nakaharang o lagpas sa sakahan.")
                            }
                        } else {
                            if (targetBed != null) {
                                val planted = editViewModel.plantCropInBed(targetBed.id, dragCropName, dragCropId, safeX, safeY)
                                if (planted) {
                                    advanceTutorialAfterDrop(tutorialUiState, tutorialViewModel)
                                }
                            } else {
                                editViewModel.reportInvalidDropLocation("⚠️ Hindi wasto ang lokasyon: Paki-lagay ang pananim sa loob ng isang Garden Bed.")
                            }
                        }
                    }
                },
                onClose = {
                    isRightPanelVisible = false
                    activeCropName = ""; activeCropId = ""
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SAVE_FARM)
                    }
                }
            )
        }

        // ═════════════════════════════════════════════════════════════════
        // 6. FLOATING DRAG PREVIEW
        // ═════════════════════════════════════════════════════════════════
        if (isDraggingCrop) {
            val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)
            val density = androidx.compose.ui.platform.LocalDensity.current
            val halfSizePx = with(density) { 36.dp.roundToPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = dragTouchPos.x.toInt() - halfSizePx,
                            y = dragTouchPos.y.toInt() - halfSizePx
                        )
                    }
                    .size(72.dp)
                    .shadow(10.dp, CircleShape)
                    .background(ChromeDark.copy(alpha = 0.92f), CircleShape)
                    .border(2.dp, AccentGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isBedDrag) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF6D4C41))
                            .border(1.5.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                            val w = size.width; val h = size.height
                            val diamond = Path().apply {
                                moveTo(w / 2f, 2f); lineTo(w - 2f, h / 2f)
                                lineTo(w / 2f, h - 2f); lineTo(2f, h / 2f); close()
                            }
                            drawPath(diamond, Color.White.copy(alpha = 0.4f), style = Stroke(width = 1.5.dp.toPx()))
                        }
                    }
                } else {
                    val imageModel: Any = com.maptanim.app.data.datasource.CropMetadataAssetDataSource.resolveCropImage(
                        cropId = dragCropName, cropName = dragCropName, imageUrl = dragCropImageUrl
                    )
                    AsyncImage(model = imageModel, contentDescription = "Floating Crop",
                        contentScale = ContentScale.Fit, modifier = Modifier.size(50.dp))
                }
            }
        }

        // ═════════════════════════════════════════════════════════════════
        // 7. DELETE CONFIRMATION DIALOG
        // ═════════════════════════════════════════════════════════════════
        if (showDeleteConfirmDialog) {
            val selectedPlot = uiState.plots.firstOrNull { it.id == uiState.selectedPlotId }
            val isSelectedBed = selectedPlot?.cropName == "Bed" || selectedPlot?.cropId == "bed"
            val containedCrops = uiState.cropZones.filter { it.plotId == uiState.selectedPlotId && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
            val hasCrops = containedCrops.isNotEmpty()

            val deleteTitle = if (isSelectedBed) {
                if (hasCrops) "Delete Bed with Active Crops?" else "Delete Garden Bed"
            } else "Delete Crop Plot"

            val deleteMsg = when {
                isSelectedBed && hasCrops -> {
                    val cropsSummary = containedCrops.mapNotNull { it.cropName }.distinct().joinToString(", ")
                    "Babala: May ${containedCrops.size} pananim ($cropsSummary) na nakatanim sa bed na ito! Sigurado ka bang gusto mong burahin ang bed kasama ang lahat ng pananim nito?"
                }
                isSelectedBed -> "Sigurado ka bang gusto mong burahin ang bakanteng bed na ito sa map?"
                else -> "Sigurado ka bang gusto mong burahin ang pananim na ito sa map?"
            }

            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text(deleteTitle, fontWeight = FontWeight.Bold) },
                text = { Text(deleteMsg, fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        onClick = {
                            uiState.selectedPlotId?.let { editViewModel.deletePlot(it) }
                            showDeleteConfirmDialog = false
                        }
                    ) { Text(if (hasCrops) "Oo (Burahin Lahat)" else "Oo (Burahin)", color = Color.White, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Hindi (I-cancel)", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }



        // ═════════════════════════════════════════════════════════════════
        // 8. CROPS PLANTING SUMMARY OVERLAY
        // ═════════════════════════════════════════════════════════════════
        if (uiState.showCropSummaryOverlay) {
            CropsSummaryOverlay(
                farmName = farmNameInput,
                cropPlantings = uiState.pendingCropPlantings,
                errorMessage = uiState.saveErrorMessage,
                isSaving = uiState.isSaving,
                onCancel = { editViewModel.dismissCropSummary() },
                onSave = { updatedDrafts ->
                    editViewModel.confirmSaveCropPlantings(
                        drafts = updatedDrafts,
                        onReturnToFarm = {
                            if (tutorialUiState.isTutorialActive) { tutorialViewModel.completeTutorial() }
                            navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                                popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }
            )
        }

        // ═════════════════════════════════════════════════════════════════
        // 9. TUTORIAL WALKTHROUGH OVERLAYS
        // ═════════════════════════════════════════════════════════════════
        if (tutorialUiState.isTutorialActive && !isDraggingCrop) {
            when (tutorialUiState.currentStep) {
                com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON,
                com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT -> {
                    val openTrayAction = {
                        isRightPanelVisible = true
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                    }
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Pindutin ang 'Bed' o 'Crop' sa toolbar sa ibaba para magdagdag ng garden bed at pananim!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true, compactMode = true, scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopStart,
                        nextButtonText = "Open Tray", onNext = openTrayAction,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp),
                                contentAlignment = Alignment.BottomCenter) {
                                Row(modifier = Modifier.clickable { openTrayAction() },
                                    verticalAlignment = Alignment.CenterVertically) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.DOWN,
                                        label = "TAP TOOLBAR"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    com.maptanim.app.ui.components.guide.SpotlightPulseRing(size = 72.dp)
                                }
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP -> {
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Pumili ng pananim at i-drag papunta sa farm canvas!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true, compactMode = true, scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopStart,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(modifier = Modifier.fillMaxSize().padding(end = 80.dp),
                                contentAlignment = Alignment.CenterEnd) {
                                com.maptanim.app.ui.components.guide.PointingHandSprite(
                                    direction = com.maptanim.app.ui.components.guide.PointingDirection.RIGHT,
                                    label = "CLICK & DRAG CROP"
                                )
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP -> { /* Hidden during free drag */ }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN -> {
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Magaling! Pwede mong i-duplicate, i-resize, o i-delete ang iyong pananim gamit ang toolbar sa ibaba.",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true, compactMode = true, scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopCenter,
                        secondaryButtonText = "Continue Editing", nextButtonText = "Proceed",
                        onSkip = { tutorialViewModel.skipTutorial() },
                        onSecondaryClick = { tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP) },
                        onNext = { tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY) },
                        pointingHandTarget = {
                            Box(modifier = Modifier.fillMaxSize().padding(bottom = 75.dp),
                                contentAlignment = Alignment.BottomCenter) {
                                com.maptanim.app.ui.components.guide.PointingHandSprite(
                                    direction = com.maptanim.app.ui.components.guide.PointingDirection.DOWN,
                                    label = "BOTTOM TOOLBAR"
                                )
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY -> {
                    val closeTrayAction = {
                        isRightPanelVisible = false; activeCropName = ""; activeCropId = ""
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SAVE_FARM)
                    }
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Pindutin ang 'X' button sa crop tray para isara ito!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true, compactMode = true, scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.BottomStart,
                        nextButtonText = "Close Tray", onNext = closeTrayAction,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(modifier = Modifier.fillMaxSize().padding(top = 16.dp, end = 16.dp),
                                contentAlignment = Alignment.TopEnd) {
                                Row(modifier = Modifier.clickable { closeTrayAction() },
                                    verticalAlignment = Alignment.CenterVertically) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.UP,
                                        label = "CLOSE TRAY ('X')"
                                    )
                                    com.maptanim.app.ui.components.guide.SpotlightPulseRing(size = 56.dp)
                                }
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_SAVE_FARM -> {
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Napakagaling! Pindutin ang 'Save' button sa taas para mai-save ang layout ng iyong sakahan!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true, compactMode = true, scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.BottomStart,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(modifier = Modifier.fillMaxSize().padding(top = 12.dp, end = 70.dp),
                                contentAlignment = Alignment.TopEnd) {
                                Row(
                                    modifier = Modifier.clickable {
                                        editViewModel.onSavePressed(
                                            onReturnToFarm = {
                                                if (tutorialUiState.isTutorialActive) { tutorialViewModel.completeTutorial() }
                                                navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                                                    popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                                                }
                                            }
                                        )
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.UP,
                                        label = "CLICK SAVE"
                                    )
                                    com.maptanim.app.ui.components.guide.SpotlightPulseRing(size = 64.dp)
                                }
                            }
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private helper composables
// ═══════════════════════════════════════════════════════════════════════════════

private fun advanceTutorialAfterDrop(
    tutorialUiState: com.maptanim.app.viewmodel.TutorialUiState,
    tutorialViewModel: com.maptanim.app.viewmodel.TutorialViewModel
) {
    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP ||
        tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP ||
        tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN)
    }
}

/** Compact square zoom/view control button (34×34dp) */
@Composable
private fun ZoomButton(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ToolbarBg.copy(alpha = 0.90f),
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ChromeBorder),
        modifier = Modifier.size(34.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, desc, tint = TextPrimary, modifier = Modifier.size(17.dp))
        }
    }
}

/** Bottom toolbar tool button — Select / Bed / Crop / Label */
@Composable
private fun ToolButton(
    icon: ImageVector, label: String, isActive: Boolean,
    tintOverride: Color? = null, onClick: () -> Unit
) {
    val bgColor = if (isActive) ActiveToolBg else Color.Transparent
    val contentColor = when {
        isActive -> Color.White
        tintOverride != null -> tintOverride
        else -> TextSecondary
    }
    Surface(
        shape = RoundedCornerShape(8.dp), color = bgColor,
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(icon, label, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(label, fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = contentColor, maxLines = 1)
        }
    }
}

/** Thin vertical divider between toolbar items */
@Composable
private fun ToolDivider() {
    Box(modifier = Modifier.width(1.dp).height(28.dp).background(ChromeBorder))
}

/** Thin horizontal divider between vertical toolbar items */
@Composable
private fun ToolDividerHorizontal() {
    Box(modifier = Modifier.height(1.dp).width(28.dp).background(ChromeBorder))
}
