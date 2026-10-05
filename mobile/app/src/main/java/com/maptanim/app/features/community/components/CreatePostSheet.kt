package com.maptanim.app.features.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CreatePostSheet — Inline post creation form.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CreatePostSheet(
    currentUserName: String = "You",
    isPublishing: Boolean = false,
    onCancel: () -> Unit,
    onSubmit: (title: String, category: String, content: String, authorName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isTitleFocused by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                IconButton(
                    onClick = onCancel,
                    enabled = !isPublishing,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(LushGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUserName.take(1).uppercase().ifBlank { "Y" },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Create Community Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DeepBlack
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !isPublishing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBlack),
                    border = BorderStroke(1.dp, CardBorderColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Cancel", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank() && !isPublishing) {
                            onSubmit(title, "GENERAL", content, currentUserName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                    enabled = title.isNotBlank() && content.isNotBlank() && !isPublishing,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publishing...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text("Publish Post", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Title Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(10.dp),
            color = LightSurface,
            border = BorderStroke(
                1.dp,
                if (isTitleFocused) LushGreen else CardBorderColor
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "Post title or summary (e.g. Organic remedy for Eggplant borer)...",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = title,
                    onValueChange = { if (!isPublishing) title = it },
                    readOnly = isPublishing,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = DeepBlack,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(LushGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isTitleFocused = it.isFocused }
                )
            }
        }

        // Content Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(10.dp),
            color = LightSurface,
            border = BorderStroke(
                1.dp,
                if (isContentFocused) LushGreen else CardBorderColor
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (content.isEmpty()) {
                    Text(
                        text = "Write your advice, questions, field observations, or harvest details here...",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = content,
                    onValueChange = { if (!isPublishing) content = it },
                    readOnly = isPublishing,
                    textStyle = TextStyle(
                        color = DeepBlack,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(LushGreen),
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { isContentFocused = it.isFocused }
                )
            }
        }
    }
}
