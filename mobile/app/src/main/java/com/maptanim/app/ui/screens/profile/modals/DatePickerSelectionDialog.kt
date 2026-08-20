package com.maptanim.app.ui.screens.profile.modals

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
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import java.time.LocalDate

@Composable
fun DatePickerSelectionDialog(
    selectedDate: String?,
    onDateSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var dateInput by remember { mutableStateOf(selectedDate ?: LocalDate.now().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E261A),
            border = BorderStroke(1.dp, ForestGreen),
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
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Activity Date", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = White)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = White)
                    }
                }

                Text(
                    text = "Filter and view all recorded farm harvest and community activities on a specific day.",
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.7f)
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
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = White
                        )
                    )
                    FilterChip(
                        selected = dateInput == yesterdayStr,
                        onClick = { dateInput = yesterdayStr },
                        label = { Text("Yesterday", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = White
                        )
                    )
                    FilterChip(
                        selected = selectedDate == null,
                        onClick = { onDateSelected(null); onDismiss() },
                        label = { Text("Show All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD48806),
                            selectedLabelColor = White
                        )
                    )
                }

                // Custom Date Entry Field
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter Date (YYYY-MM-DD)", color = White.copy(alpha = 0.7f), fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDateSelected(null); onDismiss() }) {
                        Text("Reset / All", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { onDateSelected(dateInput.trim()); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Apply Date Filter", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
