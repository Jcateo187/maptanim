package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)

/**
 * HarvestRecordDialog — Modal dialog to record harvest yield in kilograms
 * and optionally reset the bed for the next crop succession.
 */
@Composable
fun HarvestRecordDialog(
    selectedPlot: CropPlot?,
    onDismiss: () -> Unit,
    onConfirm: (yieldKg: Float, notes: String, isFinalHarvest: Boolean) -> Unit
) {
    var yieldInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var isFinalHarvestChecked by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Record Harvest Yield",
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Plot: ${selectedPlot?.plotLabel ?: "Bed #1"} • ${selectedPlot?.cropName ?: "Produce"}",
                    fontSize = 13.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = yieldInput,
                    onValueChange = { yieldInput = it },
                    label = { Text("Yield (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LushGreen,
                        focusedLabelColor = LushGreen
                    )
                )

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LushGreen,
                        focusedLabelColor = LushGreen
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFinalHarvestChecked,
                        onCheckedChange = { isFinalHarvestChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                    )
                    Text(
                        text = "Reset bed for next crop rotation",
                        fontSize = 12.sp,
                        color = DeepBlack
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val yield = yieldInput.toFloatOrNull() ?: 0f
                    onConfirm(yield, notesInput, isFinalHarvestChecked)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
            ) {
                Text("Save Record", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DeepBlack)
            }
        },
        containerColor = Color.White
    )
}
