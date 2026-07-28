package com.maptanim.app.ui.screens.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.editcomponents.layout.EditBottomLayout
import com.maptanim.app.renderer.canvas.FarmCanvas
import com.maptanim.app.ui.dialogs.CropPickerDialog
import com.maptanim.app.ui.components.editcomponents.croptray.CropTray

/**
 * FarmEditorScreen — Streamlined Edit Mode screen with live Crop Selection & Planting.
 *
 * Connected to EditViewModel for tool selection, soil painting, crop planting, undo/redo, and saving layout changes.
 */
@Composable
fun FarmEditorScreen(
    navController: NavController,
    editViewModel: EditViewModel = viewModel()
) {
    val uiState by editViewModel.uiState.collectAsState()
    var showCropPicker by remember { mutableStateOf(false) }
    var activeCropName by remember { mutableStateOf("Carrot") }
    var activeCropId by remember { mutableStateOf("carrot") }

    val selectedBed = remember(uiState.selectedBedId, uiState.beds) {
        uiState.beds.firstOrNull { it.id == uiState.selectedBedId }
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
            onOpenCropPicker = { showCropPicker = true }
        )


        // ── Top Right Action Buttons: Save & Exit (Single buttons) ─────────
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

        // ── Single Button / Right Panel: Add Plant / Crops (1 Element Only) ─
        if (!showCropPicker && uiState.activeTool != com.maptanim.app.domain.model.EditTool.ADD_PLANT) {
            // Single Button on Right when panel is closed
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .clickable {
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                        showCropPicker = true
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
            // Right side Crop Selection Panel when open (Replaces floating button)
            CropTray(
                modifier = Modifier.align(Alignment.CenterEnd),
                selectedCropName = activeCropName,
                onCropSelected = { newCropName, newCropId ->
                    activeCropName = newCropName
                    activeCropId = newCropId
                    uiState.selectedBedId?.let { bedId ->
                        editViewModel.changeCrop(bedId, newCropName, newCropId)
                        editViewModel.addCropZone(bedId, newCropName)
                    }
                },
                onClose = {
                    showCropPicker = false
                    editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                }
            )
        }

        // Bottom contextual edit bar for selected plot
        EditBottomLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            uiState = uiState,
            onDuplicateClick = {
                uiState.selectedBedId?.let { editViewModel.duplicateBed(it) }
            },
            onResizeClick = { /* highlight corner handles for precision resize */ },
            onChangeCropClick = {
                showCropPicker = true
            },
            onChangeSoilClick = {
                uiState.selectedBedId?.let { editViewModel.paintSoil(it) }
            },
            onDeleteClick = {
                uiState.selectedBedId?.let { editViewModel.deleteBed(it) }
            }
        )

        // ── Save Farm Dialog (Type farm name) ───────────────────────────
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = {
                    Text("Save Farm Layout", fontWeight = FontWeight.Bold)
                },
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
                title = {
                    Text("Setup Complete", fontWeight = FontWeight.Bold)
                },
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

        // ── Crop Selection & Planting Dialog ──────────────────────────────
        if (showCropPicker && uiState.selectedBedId != null) {
            CropPickerDialog(
                bedLabel = selectedBed?.bedLabel ?: "Selected Plot",
                currentCropName = selectedBed?.cropName,
                onDismiss = { showCropPicker = false },
                onCropSelected = { newCropName, newCropId ->
                    uiState.selectedBedId?.let { bedId ->
                        editViewModel.changeCrop(bedId, newCropName, newCropId)
                        editViewModel.addCropZone(bedId, newCropName)
                    }
                }
            )
        }
    }
}