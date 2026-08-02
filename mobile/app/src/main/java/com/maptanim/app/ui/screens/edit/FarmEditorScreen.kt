package com.maptanim.app.ui.screens.edit

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
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.renderer.canvas.FarmCanvas
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.CameraState
import com.maptanim.app.renderer.model.IsometricProjection
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.editcomponents.croptray.CropTray
import com.maptanim.app.ui.components.editcomponents.layout.EditBottomLayout

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
    editViewModel: EditViewModel = viewModel()
) {
    TrackBgmEffect(BackgroundTrack.EDITOR_FOCUS)

    val soundManager = LocalSoundManager.current
    val context = LocalContext.current
    val uiState by editViewModel.uiState.collectAsState()
    var activeCropName by remember { mutableStateOf("") }
    var activeCropId by remember { mutableStateOf("") }
    var isRightPanelVisible by remember { mutableStateOf(false) }

    // Live Camera State tracked from FarmCanvas for accurate drop conversion
    var liveCameraState by remember { mutableStateOf(CameraState()) }

    // CoC Floating Drag & Drop State (Right Panel drags)
    var isDraggingCrop by remember { mutableStateOf(false) }
    var dragCropName by remember { mutableStateOf("Carrot") }
    var dragCropId by remember { mutableStateOf("carrot") }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }

    // Compute snapped hover tile position bounded strictly inside farm area (0 to 30m)
    val hoverWorldPos = remember(isDraggingCrop, dragTouchPos, liveCameraState, uiState.selectedPlotId, uiState.plots) {
        if (isDraggingCrop) {
            val rawWorld = IsometricProjection.toWorld(dragTouchPos.x, dragTouchPos.y, liveCameraState)
            val snapped = FarmCanvasRenderer.snapToGrid(rawWorld)
            Offset(snapped.x.coerceIn(0f, 29.0f), snapped.y.coerceIn(0f, 29.0f))
        } else if (uiState.selectedPlotId != null) {
            val selectedPlot = uiState.plots.firstOrNull { it.id == uiState.selectedPlotId }
            if (selectedPlot != null) {
                val snapped = FarmCanvasRenderer.snapToGrid(Offset(selectedPlot.posX, selectedPlot.posY))
                val maxX = (30.0f - selectedPlot.widthM).coerceAtLeast(0f)
                val maxY = (30.0f - selectedPlot.heightM).coerceAtLeast(0f)
                Offset(snapped.x.coerceIn(0f, maxX), snapped.y.coerceIn(0f, maxY))
            } else null
        } else null
    }

    val isValidPlacement = remember(isDraggingCrop, hoverWorldPos, uiState.plots, uiState.selectedPlotId) {
        if (hoverWorldPos != null) {
            val (w, h) = if (isDraggingCrop) {
                1.0f to 1.0f
            } else {
                val selPlot = uiState.plots.firstOrNull { it.id == uiState.selectedPlotId }
                (selPlot?.widthM ?: 1.0f) to (selPlot?.heightM ?: 1.0f)
            }

            val hx = hoverWorldPos.x
            val hy = hoverWorldPos.y
            val inBounds = hx >= 0f && hy >= 0f && (hx + w) <= 30.0f && (hy + h) <= 30.0f

            val overlaps = uiState.plots.any { plot ->
                if (plot.id == uiState.selectedPlotId) false
                else {
                    hx < (plot.posX + plot.widthM) && (hx + w) > plot.posX &&
                    hy < (plot.posY + plot.heightM) && (hy + h) > plot.posY
                }
            }
            inBounds && !overlaps
        } else true
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var farmNameInput by remember { mutableStateOf("Murcia Farm") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        HomeBackground()

        // Interactive 2D Isometric Farm Canvas
        FarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            editViewModel = editViewModel,
            activeCropName = activeCropName,
            activeCropId = activeCropId,
            hoverWorldPos = hoverWorldPos,
            isValidPlacement = isValidPlacement,
            onCameraStateChanged = { liveCameraState = it }
        )

        // ── Top Right Action Buttons: Save & Exit ─────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Save Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF2E7D32),
                shadowElevation = 6.dp,
                modifier = Modifier.clickable { showSaveDialog = true }
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

            // Exit Button
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
                },
                onCropDragStart = { cropName, cropId, startOffset ->
                    isDraggingCrop = true
                    dragCropName = cropName
                    dragCropId = cropId
                    dragTouchPos = startOffset
                },
                onCropDragging = { currentOffset ->
                    dragTouchPos = currentOffset
                },
                onCropDragEnd = { dropOffset ->
                    if (isDraggingCrop) {
                        val wasValid = isValidPlacement
                        isDraggingCrop = false
                        val dropWorld = com.maptanim.app.renderer.model.IsometricProjection.toWorld(dropOffset.x, dropOffset.y, liveCameraState)
                        val snapped = com.maptanim.app.renderer.canvas.FarmCanvasRenderer.snapToGrid(dropWorld)
                        val safeX = snapped.x.coerceIn(0f, 29.0f)
                        val safeY = snapped.y.coerceIn(0f, 29.0f)
                        if (wasValid) {
                            editViewModel.addDirectPlantingPlot(safeX, safeY, dragCropName, dragCropId)
                        }
                    }
                },
                onClose = {
                    isRightPanelVisible = false
                    activeCropName = ""
                    activeCropId = ""
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                }
            )
        }

        // ── CoC Floating Single Crop Sprite Preview Layer (Right Panel Drags) ─────
        if (isDraggingCrop) {
            val cropClean = when (dragCropName.lowercase().replace(" ", "")) {
                "stringbeans", "sitaw", "beans" -> "crop_stringbeans"
                "eggplant", "talong" -> "crop_eggplant"
                "tomato", "kamatis" -> "crop_tomato"
                "onion", "sibuyas" -> "crop_onion"
                "pumpkin", "kalabasa" -> "crop_pumpkin"
                "corn", "mais" -> "crop_corn"
                else -> "crop_carrot"
            }
            val assetBitmap = remember(cropClean) {
                AssetLoader.loadFromAssets(context, "crops/${cropClean}_1.png")
                    ?: AssetLoader.loadFromAssets(context, "crops/crop_carrot_1.png")
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = dragTouchPos.x.toInt() - 40,
                            y = dragTouchPos.y.toInt() - 40
                        )
                    }
                    .size(80.dp)
                    .shadow(12.dp, CircleShape)
                    .background(Color(0xFFE8F5E9).copy(alpha = 0.9f), CircleShape)
                    .border(2.dp, Color(0xFF1B5E20), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (assetBitmap != null) {
                    Image(
                        bitmap = assetBitmap,
                        contentDescription = "Floating Crop",
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    val emoji = when (cropClean) {
                        "crop_stringbeans" -> "🫘"
                        "crop_eggplant" -> "🍆"
                        "crop_tomato" -> "🍅"
                        "crop_onion" -> "🧅"
                        "crop_pumpkin" -> "🎃"
                        "crop_corn" -> "🌽"
                        else -> "🥕"
                    }
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }
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
                uiState.selectedPlotId?.let { editViewModel.deletePlot(it) }
            }
        )

        // ── Save Farm Dialog ───────────────────────────
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Farm Layout", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Type farm name to save your layout to Supabase / Local Storage:", fontSize = 14.sp)
                        OutlinedTextField(
                            value = farmNameInput,
                            onValueChange = { farmNameInput = it },
                            label = { Text("Farm Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        onClick = {
                            editViewModel.saveChanges(
                                farmName = farmNameInput,
                                isGuest = false,
                                onSaveComplete = {
                                    showSaveDialog = false
                                    showSuccessDialog = true
                                }
                            )
                        }
                    ) {
                        Text("Okay", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }

        // ── Success Confirmation Message Dialog ────────────────────────
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Setup Complete", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Excellent Successful set up the farm",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B5E20)
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                                popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Okay", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}