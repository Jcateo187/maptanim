package com.maptanim.app.features.community.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.domain.model.CommunityPost

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

private val CATEGORIES = listOf(
    "GENERAL" to "General Discussion",
    "PEST_ALERT" to "Pest & Disease Alert",
    "FARMING_TIP" to "Farming Tips",
    "EQUIPMENT" to "Tools & Equipment"
)

/**
 * EditPostDialog — Dialog allowing post authors to modify title, category, and content.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun EditPostDialog(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf(post.title) }
    var content by remember { mutableStateOf(post.content) }
    var selectedCategory by remember { mutableStateOf(post.category) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "I-edit ang Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
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
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                // Category selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Kategorya",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(CATEGORIES) { (catKey, catLabel) ->
                            val isSelected = selectedCategory == catKey
                            Surface(
                                onClick = { selectedCategory = catKey },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) LushGreen else LightSurface,
                                border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor)
                            ) {
                                Text(
                                    text = catLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else DeepBlack,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Pamagat (Title)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        placeholder = { Text("Ilagay ang pamagat ng post...", fontSize = 12.sp, color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface
                        )
                    )
                }

                // Content Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Nilalaman (Content)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        minLines = 4,
                        maxLines = 8,
                        placeholder = { Text("Ibahagi ang iyong kaalaman o katanungan...", fontSize = 12.sp, color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface
                        )
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text(
                            text = "Kanselahin",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = DeepBlack
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSave(title.trim(), selectedCategory, content.trim())
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text(
                            text = "I-save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
