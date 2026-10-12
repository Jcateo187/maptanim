package com.maptanim.app.features.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * CreateFarmDialog — Modal dialog to register a new farm workspace in Daylight theme.
 */
@Composable
fun CreateFarmDialog(
    farmName: String,
    errorMessage: String?,
    onFarmNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Create New Farm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DeepBlack
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "Add a new farm workspace to configure and manage your crop plots and tasks.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )

                // Input Field
                OutlinedTextField(
                    value = farmName,
                    onValueChange = onFarmNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Farm Name", color = MutedText, fontSize = 13.sp) },
                    placeholder = { Text("e.g. Backyard Vegetable Garden", color = MutedText.copy(alpha = 0.6f), fontSize = 13.sp) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack,
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor,
                        errorBorderColor = Color(0xFFC62828),
                        focusedLabelColor = LushGreen,
                        cursorColor = LushGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBlack)
                    ) {
                        Text("Cancel", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Create Farm", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * RenameFarmDialog — Modal dialog to rename an existing farm in Daylight theme.
 */
@Composable
fun RenameFarmDialog(
    currentFarmName: String,
    nameInput: String,
    errorMessage: String?,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Rename Farm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DeepBlack
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "Enter a new name for '$currentFarmName':",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )

                // Input Field
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Farm Name", color = MutedText, fontSize = 13.sp) },
                    placeholder = { Text("Enter farm name", color = MutedText.copy(alpha = 0.6f), fontSize = 13.sp) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack,
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor,
                        errorBorderColor = Color(0xFFC62828),
                        focusedLabelColor = LushGreen,
                        cursorColor = LushGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBlack)
                    ) {
                        Text("Cancel", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
