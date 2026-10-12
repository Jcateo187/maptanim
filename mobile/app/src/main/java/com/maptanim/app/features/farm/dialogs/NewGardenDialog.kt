package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.SoilType
import java.util.Locale

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val AlertRed = Color(0xFFD32F2F)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * NewGardenDialog — Dialog to create a new garden:
 * - At top: "Create new garden" in center
 * - Garden name text input
 * - Width and height CARD with scrollable size selection (1 to 40 ft)
 * - Cancel button in left side, Continue button in right side
 */
@Composable
fun NewGardenDialog(
    existingCount: Int = 0,
    onDismiss: () -> Unit,
    onSave: (label: String, widthFt: Float, heightFt: Float) -> Unit
) {
    var label by remember { mutableStateOf("Garden #${existingCount + 1}") }
    var selectedWidthFt by remember { mutableFloatStateOf(10f) }
    var selectedHeightFt by remember { mutableFloatStateOf(8f) }

    // List of integer foot sizes from 1 to 40 ft (realistic home garden dimensions)
    val availableSizes = remember { (1..40).map { it.toFloat() } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Top Title in Center: "Create new garden" ────────────────
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create new garden",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                HorizontalDivider(color = CardBorderColor.copy(alpha = 0.7f))

                // ── Garden Name Text Input ──────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Garden name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        placeholder = { Text("e.g. Likod Balay", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // ── Width and Height CARD with Scrollable Size Selection ─────
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card Header with Live Dimension Badge
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
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Garden Dimensions",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "${selectedWidthFt.toInt()}ft (${(selectedWidthFt * 12).toInt()}\") × ${selectedHeightFt.toInt()}ft (${(selectedHeightFt * 12).toInt()}\")",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = "Choose garden dimensions (up to 40 ft / 480 inches):",
                            fontSize = 10.5.sp,
                            color = Color.Gray
                        )

                        // 1. WIDTH SELECTION
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Width", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = DeepBlack)
                                Text("${selectedWidthFt.toInt()} ft (${(selectedWidthFt * 12).toInt()}\")", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(availableSizes) { sizeFt ->
                                    val isSelected = selectedWidthFt == sizeFt
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) LushGreen else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .clickable { selectedWidthFt = sizeFt }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 10.dp)
                                        ) {
                                            Text(
                                                text = "${sizeFt.toInt()}ft (${(sizeFt * 12).toInt()}\")",
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else DeepBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))

                        // 2. HEIGHT SELECTION
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Height", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = DeepBlack)
                                Text("${selectedHeightFt.toInt()} ft (${(selectedHeightFt * 12).toInt()}\")", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(availableSizes) { sizeFt ->
                                    val isSelected = selectedHeightFt == sizeFt
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) LushGreen else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .clickable { selectedHeightFt = sizeFt }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 10.dp)
                                        ) {
                                            Text(
                                                text = "${sizeFt.toInt()}ft (${(sizeFt * 12).toInt()}\")",
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else DeepBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ── In the bottom: Cancel in left side, Continue in right side ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text("Cancel", color = DeepBlack, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            val w = selectedWidthFt.coerceIn(1f, 40f)
                            val h = selectedHeightFt.coerceIn(1f, 40f)
                            onSave(label.trim().ifBlank { "Garden #${existingCount + 1}" }, w, h)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * RenameGardenDialog — Modal to rename an existing garden.
 */
@Composable
fun RenameGardenDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (newName: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(initialName) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Rename Garden",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Garden Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack,
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text("Cancel", color = DeepBlack)
                    }

                    Button(
                        onClick = {
                            val trimmed = nameInput.trim()
                            if (trimmed.isNotBlank()) {
                                onConfirm(trimmed)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * AdjustGardenSizeDialog — Dialog for adjusting existing garden size up to 40 ft max.
 */
@Composable
fun AdjustGardenSizeDialog(
    initialWidthFt: Float,
    initialHeightFt: Float,
    gardenLabel: String,
    onDismiss: () -> Unit,
    onApply: (widthFt: Float, heightFt: Float) -> Unit
) {
    var widthInput by remember { mutableStateOf(String.format(Locale.US, "%.1f", initialWidthFt)) }
    var heightInput by remember { mutableStateOf(String.format(Locale.US, "%.1f", initialHeightFt)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ADJUST GARDEN SIZE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = gardenLabel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.DarkGray)
                    }
                }

                HorizontalDivider(color = CardBorderColor)

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F8E9),
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Maximum width is 40 ft and maximum height is 40 ft.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = widthInput,
                        onValueChange = {
                            widthInput = it.filter { ch -> ch.isDigit() || ch == '.' }
                            errorMessage = null
                        },
                        label = { Text("Width (ft)") },
                        supportingText = { Text("Max: 40 ft", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedLabelColor = LushGreen
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = {
                            heightInput = it.filter { ch -> ch.isDigit() || ch == '.' }
                            errorMessage = null
                        },
                        label = { Text("Height (ft)") },
                        supportingText = { Text("Max: 40 ft", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedLabelColor = LushGreen
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = AlertRed, fontSize = 11.5.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text("Cancel", color = DeepBlack)
                    }

                    Button(
                        onClick = {
                            val w = widthInput.toFloatOrNull()
                            val h = heightInput.toFloatOrNull()
                            if (w == null || h == null || w <= 0f || h <= 0f) {
                                errorMessage = "Please enter valid width and height values."
                                return@Button
                            }
                            if (w > 40f || h > 40f) {
                                errorMessage = "Maximum width and height is 40 ft."
                                return@Button
                            }
                            onApply(w, h)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
