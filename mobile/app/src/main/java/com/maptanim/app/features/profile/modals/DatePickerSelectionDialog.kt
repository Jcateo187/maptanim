package com.maptanim.app.features.profile.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * DatePickerSelectionDialog — Filter activity date dialog in Daylight theme.
 */
@Composable
fun DatePickerSelectionDialog(
    selectedDate: String?,
    availableDates: List<String> = emptyList(),
    onSelectDate: (String?) -> Unit = {},
    onDateSelected: (String?) -> Unit = onSelectDate,
    onDismiss: () -> Unit
) {
    var dateInput by remember { mutableStateOf(selectedDate ?: LocalDate.now().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Activity Date",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DeepBlack
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepBlack)
                    }
                }

                Text(
                    text = "Filter and view all recorded farm harvest and community activities on a specific day.",
                    fontSize = 12.sp,
                    color = MutedText
                )

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val todayStr = LocalDate.now().toString()
                    val yesterdayStr = LocalDate.now().minusDays(1).toString()

                    FilterChip(
                        selected = dateInput == todayStr,
                        onClick = { dateInput = todayStr },
                        label = { Text("Today", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LushGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = dateInput == yesterdayStr,
                        onClick = { dateInput = yesterdayStr },
                        label = { Text("Yesterday", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LushGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedDate == null,
                        onClick = {
                            onDateSelected(null)
                            onDismiss()
                        },
                        label = { Text("Show All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LushGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Custom Date Entry Field
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter Date (YYYY-MM-DD)", color = MutedText, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        onDateSelected(null)
                        onDismiss()
                    }) {
                        Text("Reset / All", color = MutedText, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            onDateSelected(dateInput.trim())
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply Filter", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
