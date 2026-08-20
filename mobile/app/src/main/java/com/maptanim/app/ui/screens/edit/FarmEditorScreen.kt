package com.maptanim.app.ui.screens.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.renderer.canvas.FarmCanvas
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.CameraState
import com.maptanim.app.renderer.model.IsometricProjection

import com.maptanim.app.ui.components.editcomponents.croptray.CropTray
import com.maptanim.app.ui.components.editcomponents.layout.EditBottomLayout
import com.maptanim.app.ui.components.editcomponents.summary.CropsSummaryOverlay

import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.core.audio.TrackBgmEffect

/**
 * FarmEditorScreen — Streamlined Edit Mode screen with live CoC-style Drag & Drop Planting.
 */
@Composable
fun FarmEditorScreen(
    navController: NavController,
    editViewModel: EditViewModel = viewModel(),
    tutorialViewModel: com.maptanim.app.viewmodel.TutorialViewModel = viewModel()
) {
    TrackBgmEffect(BackgroundTrack.EDITOR_FOCUS)

    val soundManager = LocalSoundManager.current
    val context = LocalContext.current
    val uiState by editViewModel.uiState.collectAsState()
    val tutorialUiState by tutorialViewModel.uiState.collectAsState()
    var activeCropName by remember { mutableStateOf("") }
    var activeCropId by remember { mutableStateOf("") }
    var isRightPanelVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        editViewModel.refresh()
    }

    LaunchedEffect(tutorialUiState.currentStep, isRightPanelVisible) {
        if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON) {
            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT)
        }
        if (isRightPanelVisible && (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT ||
                                    tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.SPOTLIGHT_EDIT_BUTTON)) {
            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
        }
    }

    // Live Camera State tracked from FarmCanvas for accurate drop conversion
    var liveCameraState by remember { mutableStateOf(CameraState()) }

    // CoC Floating Drag & Drop State (Right Panel drags & Canvas touch hover)
    var isDraggingCrop by remember { mutableStateOf(false) }
    var dragCropName by remember { mutableStateOf("Carrot") }
    var dragCropId by remember { mutableStateOf("carrot") }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }
    var canvasTouchPos by remember { mutableStateOf<Offset?>(null) }

    // Compute snapped hover tile position bounded strictly inside farm area (0 to 45m)
    // Hover highlight is active ONLY when dragging a crop from the CropTray onto the farm
    val hoverWorldPos = remember(isDraggingCrop, dragTouchPos, liveCameraState) {
        if (isDraggingCrop) {
            val rawWorld = IsometricProjection.toWorld(dragTouchPos.x, dragTouchPos.y, liveCameraState)
            val snapped = FarmCanvasRenderer.snapToGrid(rawWorld)
            Offset(snapped.x.coerceIn(0f, 44.0f), snapped.y.coerceIn(0f, 44.0f))
        } else {
            null
        }
    }

    val isValidPlacement = remember(isDraggingCrop, hoverWorldPos, uiState.plots) {
        if (isDraggingCrop && hoverWorldPos != null) {
            val hx = hoverWorldPos.x
            val hy = hoverWorldPos.y
            val inBounds = hx >= 0f && hy >= 0f && (hx + 1.0f) <= 45.0f && (hy + 1.0f) <= 45.0f

            val overlaps = uiState.plots.any { plot ->
                hx < (plot.posX + plot.widthM) && (hx + 1.0f) > plot.posX &&
                hy < (plot.posY + plot.heightM) && (hy + 1.0f) > plot.posY
            }
            inBounds && !overlaps
        } else true
    }

    var showCropsSummaryOverlay by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var farmNameInput by remember { mutableStateOf("Murcia Farm") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        // Interactive 2D Isometric Farm Canvas
        FarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            editViewModel = editViewModel,
            canvasMode = com.maptanim.app.domain.model.CanvasMode.EDIT,
            animateEntranceZoom = true,
            activeCropName = activeCropName,
            activeCropId = activeCropId,
            hoverWorldPos = hoverWorldPos,
            isValidPlacement = isValidPlacement,
            isDraggingCrop = isDraggingCrop,
            onCameraStateChanged = { liveCameraState = it },
            onCanvasTouchPosChanged = { canvasTouchPos = it }
        )

        // ── Top Right Action Buttons: Save & Exit ─────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Save Button (Opens Crops Planting Summary directly)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF2E7D32),
                shadowElevation = 6.dp,
                modifier = Modifier.clickable { showCropsSummaryOverlay = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            // System Back Handler — Discard unsaved changes on exit
            BackHandler {
                editViewModel.discardChanges()
                navController.popBackStack()
            }

            // Exit Button (Discards uncommitted in-memory edits and navigates back)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFC62828),
                shadowElevation = 6.dp,
                modifier = Modifier.clickable {
                    editViewModel.discardChanges()
                    navController.popBackStack()
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Exit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Exit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }

        // ── Right Panel: Add Plant / Crops ─
        if (!isRightPanelVisible) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .clickable {
                        isRightPanelVisible = true
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                        if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                            tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = "Add Plant/Crops",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Add Plant / Crops",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        } else {
            CropTray(
                modifier = Modifier.align(Alignment.CenterEnd),
                selectedCropName = activeCropName,
                onCropSelected = { newCropName, newCropId ->
                    if (activeCropName.equals(newCropName, ignoreCase = true)) {
                        activeCropName = ""
                        activeCropId = ""
                    } else {
                        activeCropName = newCropName
                        activeCropId = newCropId
                    }
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP)
                    }
                },
                onCropDragStart = { cropName, cropId, startOffset ->
                    isDraggingCrop = true
                    dragCropName = cropName
                    dragCropId = cropId
                    dragTouchPos = startOffset
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP)
                    }
                },
                onCropDragging = { currentOffset ->
                    dragTouchPos = currentOffset
                },
                onCropDragEnd = { dropOffset ->
                    if (isDraggingCrop) {
                        val dropWorld = com.maptanim.app.renderer.model.IsometricProjection.toWorld(dropOffset.x, dropOffset.y, liveCameraState)
                        val snapped = com.maptanim.app.renderer.canvas.FarmCanvasRenderer.snapToGrid(dropWorld)
                        val safeX = snapped.x.coerceIn(0f, 44.0f)
                        val safeY = snapped.y.coerceIn(0f, 44.0f)

                        // Strict collision check at target drop location
                        val canDrop = safeX >= 0f && safeY >= 0f && (safeX + 1.0f) <= 45.0f && (safeY + 1.0f) <= 45.0f &&
                                !uiState.plots.any { plot ->
                                    safeX < (plot.posX + plot.widthM) && (safeX + 1.0f) > plot.posX &&
                                    safeY < (plot.posY + plot.heightM) && (safeY + 1.0f) > plot.posY
                                }

                        isDraggingCrop = false

                        if (canDrop) {
                            editViewModel.addDirectPlantingPlot(safeX, safeY, dragCropName, dragCropId)
                            if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP ||
                                tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_SELECT_CROP ||
                                tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_ADD_PLANT) {
                                tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN)
                            }
                        }
                    }
                },
                onClose = {
                    isRightPanelVisible = false
                    activeCropName = ""
                    activeCropId = ""
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                    if (tutorialUiState.currentStep == com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY) {
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SAVE_FARM)
                    }
                }
            )
        }

        // ── CoC Floating Single Crop Sprite Preview Layer (Right Panel Drags) ─────
        if (isDraggingCrop) {
            val cropImageFile = when (dragCropName.lowercase().replace(" ", "")) {
                "stringbeans", "sitaw", "beans" -> "sitaw.png"
                "eggplant", "talong" -> "eggplant.png"
                "tomato", "kamatis" -> "tomato.png"
                "onion", "sibuyas" -> "onion.png"
                "pumpkin", "kalabasa", "squash" -> "pumpkin.png"
                "corn", "mais" -> "corn.png"
                "cabbage", "repolyo" -> "cabbage.png"
                "pechay" -> "pechay.png"
                "ampalaya", "bittergourd" -> "ampalaya.png"
                "okra" -> "okra.png"
                "sili", "chili", "chilipepper", "pepper" -> "sili.png"
                "cucumber", "pipino" -> "pipino.png"
                "kangkong", "waterspinach" -> "kangkong.png"
                "lettuce", "litsugas" -> "lettuce.png"
                else -> "carrot.png"
            }

            val density = androidx.compose.ui.platform.LocalDensity.current
            val halfSizePx = with(density) { 40.dp.roundToPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = dragTouchPos.x.toInt() - halfSizePx,
                            y = dragTouchPos.y.toInt() - halfSizePx
                        )
                    }
                    .size(80.dp)
                    .shadow(12.dp, CircleShape)
                    .background(Color(0xFFE8F5E9).copy(alpha = 0.95f), CircleShape)
                    .border(2.5.dp, Color(0xFF1B5E20), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val assetUri = "file:///android_asset/metadata/crops_images/$cropImageFile"
                AsyncImage(
                    model = assetUri,
                    contentDescription = "Floating Crop",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        // Bottom contextual edit bar for selected crop (Duplicate, Resize, Delete)
        EditBottomLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            uiState = uiState,
            onDuplicateClick = {
                uiState.selectedPlotId?.let { editViewModel.duplicatePlot(it) }
            },
            onResizeClick = {
                editViewModel.toggleResizeMode()
            },
            onChangeCropClick = { },
            onChangeSoilClick = {
                uiState.selectedPlotId?.let { editViewModel.paintSoil(it) }
            },
            onDeleteClick = {
                showDeleteConfirmDialog = true
            }
        )

        // ── Delete Confirmation Dialog ───────────────────────────
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete Crop Plot", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Sigurado ka bang gusto mong burahin ang pananim na ito sa map?", fontSize = 14.sp)
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        onClick = {
                            uiState.selectedPlotId?.let { editViewModel.deletePlot(it) }
                            showDeleteConfirmDialog = false
                        }
                    ) {
                        Text("Oo (Burahin)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                        }
                    ) {
                        Text("Hindi (I-cancel)", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // ── Crops Planting Summary Overlay (Opens Directly on Save) ───────────
        if (showCropsSummaryOverlay) {
            CropsSummaryOverlay(
                farmName = farmNameInput,
                plots = uiState.plots,
                onCancel = {
                    showCropsSummaryOverlay = false
                },
                onSave = { updatedDates, updatedVarieties ->
                    editViewModel.saveChanges(
                        farmName = farmNameInput,
                        isGuest = false,
                        plantedDatesMap = updatedDates,
                        varietiesMap = updatedVarieties,
                        onSaveComplete = {
                            showCropsSummaryOverlay = false
                            if (tutorialUiState.isTutorialActive) {
                                tutorialViewModel.completeTutorial()
                            }
                            navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                                popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }
            )
        }

        // ── Interactive Guided Walkthrough Steps ──────────────────────
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
                        dialogText = "Pindutin ang 'Add Plant / Crops' button sa kanan para buksan ang listahan ng mga pananim!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        compactMode = true,
                        scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopStart,
                        nextButtonText = "Open Tray",
                        onNext = openTrayAction,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(
                                    modifier = Modifier.clickable { openTrayAction() },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.RIGHT,
                                        label = "TAP TO ADD CROPS"
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
                        dialogText = "Pumili ng pananim at i-drag o i-tap papunta sa isometric soil map!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        compactMode = true,
                        scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopStart,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 80.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                com.maptanim.app.ui.components.guide.PointingHandSprite(
                                    direction = com.maptanim.app.ui.components.guide.PointingDirection.RIGHT,
                                    label = "CLICK & DRAG CROP"
                                )
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP -> {
                    // Overlay and pointing hand hidden completely while user is selecting / dragging crops freely
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN -> {
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Magaling! Pwede mong i-duplicate, i-resize, o i-delete ang iyong pananim gamit ang toolbar sa ibaba.",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        compactMode = true,
                        scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.TopCenter,
                        secondaryButtonText = "Continue Editing",
                        nextButtonText = "Proceed",
                        onSkip = { tutorialViewModel.skipTutorial() },
                        onSecondaryClick = { tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_DRAGGING_CROP) },
                        onNext = { tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY) },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 75.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    com.maptanim.app.ui.components.guide.PointingHandSprite(
                                        direction = com.maptanim.app.ui.components.guide.PointingDirection.DOWN,
                                        label = "BOTTOM TOOLBAR"
                                    )
                                }
                            }
                        }
                    )
                }
                com.maptanim.app.viewmodel.TutorialStep.EDIT_CLOSE_TRAY -> {
                    val closeTrayAction = {
                        isRightPanelVisible = false
                        activeCropName = ""
                        activeCropId = ""
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                        tutorialViewModel.setStep(com.maptanim.app.viewmodel.TutorialStep.EDIT_SAVE_FARM)
                    }
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Pindutin ang 'X' button sa crop tray para isara ito!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        compactMode = true,
                        scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.BottomStart,
                        nextButtonText = "Close Tray",
                        onNext = closeTrayAction,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp, end = 16.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Row(
                                    modifier = Modifier.clickable { closeTrayAction() },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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
                    val saveFarmAction = {
                        showCropsSummaryOverlay = true
                    }
                    com.maptanim.app.ui.components.guide.OldManFarmerGuideOverlay(
                        dialogText = "Napakagaling! Pindutin ang 'Save' button sa taas para mai-save ang iskedyul at layout ng iyong sakahan!",
                        titleText = "Tatay Juan (Farm Guide)",
                        showSkip = true,
                        compactMode = true,
                        scrimAlpha = 0.0f,
                        dialogAlignment = Alignment.BottomStart,
                        onSkip = { tutorialViewModel.skipTutorial() },
                        pointingHandTarget = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 12.dp, end = 70.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Row(
                                    modifier = Modifier.clickable { saveFarmAction() },
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
