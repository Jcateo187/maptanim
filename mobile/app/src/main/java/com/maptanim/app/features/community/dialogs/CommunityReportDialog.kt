package com.maptanim.app.features.community.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.features.community.model.ReportTarget

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)
private val AlertRed = Color(0xFFC62828)

/**
 * CommunityReportDialog — Report offensive content, spam, or misconduct to admin moderation.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CommunityReportDialog(
    target: ReportTarget,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, details: String) -> Unit
) {
    val reasons = remember(target.type) {
        if (target.type == "USER") {
            listOf(
                "Abusive / Harassing Behavior",
                "Spamming Unsolicited Messages",
                "Impersonation / Fake Account",
                "Prohibited Agricultural Selling",
                "Other Violations"
            )
        } else {
            listOf(
                "Inappropriate / Offensive Content",
                "Spam / Misleading Farming Advice",
                "Harassment / Abusive Remarks",
                "Fake Seeds / Counterfeit Product Scam",
                "Other Community Guideline Violation"
            )
        }
    }

    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var detailsText by remember { mutableStateOf("") }
    var isDetailsFocused by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(min = 320.dp, max = 460.dp)
                .fillMaxWidth(0.95f)
                .padding(6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dialog Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AlertRed.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Report ${target.type.lowercase().replaceFirstChar { it.uppercase() }}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                            Text(
                                text = "Notify admin moderation team",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                // Target Context Summary Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (target.type) {
                                    "USER" -> "Reported User:"
                                    "COMMENT" -> "Reported Comment by:"
                                    else -> "Reported Post by:"
                                },
                                fontSize = 11.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = target.name,
                                fontSize = 11.sp,
                                color = DeepBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (!target.content.isNullOrBlank()) {
                            Text(
                                text = "\"${target.content}\"",
                                fontSize = 11.sp,
                                color = MutedText,
                                maxLines = 2
                            )
                        }
                    }
                }

                // Reason Selection
                Text(
                    text = "Select Violation Reason:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reasons.forEach { reason ->
                        val isSelected = reason == selectedReason
                        Surface(
                            onClick = { selectedReason = reason },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFFFEBEE) else LightSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AlertRed else CardBorderColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) AlertRed else Color.Transparent)
                                        .border(1.2.dp, if (isSelected) AlertRed else MutedText, CircleShape)
                                )
                                Text(
                                    text = reason,
                                    fontSize = 11.sp,
                                    color = if (isSelected) AlertRed else DeepBlack,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Optional Details Field
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isDetailsFocused) LushGreen else CardBorderColor
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (detailsText.isEmpty()) {
                            Text(
                                text = "Additional context or remarks for admin (optional)...",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                        BasicTextField(
                            value = detailsText,
                            onValueChange = { detailsText = it },
                            textStyle = TextStyle(color = DeepBlack, fontSize = 11.sp),
                            cursorBrush = SolidColor(LushGreen),
                            modifier = Modifier
                                .fillMaxSize()
                                .onFocusChanged { isDetailsFocused = it.isFocused }
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBlack),
                        border = BorderStroke(1.dp, CardBorderColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Cancel", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSubmit(selectedReason, detailsText.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Submit Report",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
