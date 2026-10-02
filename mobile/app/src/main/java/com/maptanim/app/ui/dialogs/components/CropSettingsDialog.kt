package com.maptanim.app.ui.dialogs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.ui.theme.White

@Composable
fun CropSettingsDialog(
    initialVariety: String,
    initialDate: String,
    initialMethod: String,
    initialApproach: String,
    onDismiss: () -> Unit,
    onSave: (variety: String, date: String, method: String, approach: String) -> Unit
) {
    var variety by remember { mutableStateOf(initialVariety) }
    var plantingDate by remember { mutableStateOf(initialDate) }
    var plantingMethod by remember { mutableStateOf(initialMethod) }
    var growingApproach by remember { mutableStateOf(initialApproach) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF141C13),
            border = BorderStroke(1.dp, Color(0xFF2B3A26)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Edit Crop Information",
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = variety,
                    onValueChange = { variety = it },
                    label = { Text("Crop Variety") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = Color(0xFFC0CDC0),
                        focusedBorderColor = Color(0xFF81C784),
                        unfocusedBorderColor = Color(0xFF384A33)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = plantingDate,
                    onValueChange = { plantingDate = it },
                    label = { Text("Planting Date (YYYY-MM-DD)") },
                    supportingText = {
                        Text(
                            text = "Changing date reschedules crop to Planned / Unplanted at Stage 1",
                            fontSize = 10.sp,
                            color = Color(0xFFA0B09A)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = Color(0xFFC0CDC0),
                        focusedBorderColor = Color(0xFF81C784),
                        unfocusedBorderColor = Color(0xFF384A33)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Method selection: Direct Seeding vs Transplanting
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Planting Method", color = Color(0xFFA0B09A), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Direct Seeding", "Transplanting").forEach { method ->
                            val isSelected = plantingMethod.equals(method, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF1B2419),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF81C784) else Color(0xFF2E3E29)),
                                modifier = Modifier.weight(1f).clickable { plantingMethod = method }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = method,
                                        color = if (isSelected) White else Color(0xFFA0B09A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Growing Approach: Organic vs Conventional
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Growing Approach", color = Color(0xFFA0B09A), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Organic", "Conventional").forEach { approach ->
                            val isSelected = growingApproach.equals(approach, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF1B2419),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF81C784) else Color(0xFF2E3E29)),
                                modifier = Modifier.weight(1f).clickable { growingApproach = approach }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = approach,
                                        color = if (isSelected) White else Color(0xFFA0B09A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF384A33)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA0B09A))
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onSave(variety, plantingDate, plantingMethod, growingApproach)
                        },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = White
                        )
                    ) {
                        Text("Save Changes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
