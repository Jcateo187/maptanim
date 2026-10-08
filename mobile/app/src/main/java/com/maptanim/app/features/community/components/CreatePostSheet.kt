package com.maptanim.app.features.community.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CreatePostSheet — Streamlined forum post creation:
 * - Top: Back button on left, Publish button on right (Cancel button removed).
 * - Topic category: 2 rows of optional hashtags (#General, #Vegetables, etc.).
 * - Content: No separate title or details label, single prominent "Share your thoughts..." editor.
 * - Image attachment options: Gallery or Camera.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CreatePostSheet(
    currentUserName: String = "You",
    isPublishing: Boolean = false,
    onCancel: () -> Unit,
    onSubmit: (title: String, category: String, content: String, authorName: String, imagePath: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>("#General") }
    var attachedImagePath by remember { mutableStateOf<String?>(null) }
    var isContentFocused by remember { mutableStateOf(false) }

    // Hashtags arranged in 2 rows (optional for user)
    val hashtagRow1 = listOf(
        "#General",
        "#Vegetables",
        "#PestControl",
        "#FarmingTips"
    )
    val hashtagRow2 = listOf(
        "#Harvest",
        "#SoilHealth",
        "#Equipment",
        "#AskCommunity"
    )

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = savePostImageFromUri(context, uri)
            if (saved != null) {
                attachedImagePath = saved
            } else {
                Toast.makeText(context, "Could not process selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = savePostImageFromUri(context, uri)
            if (saved != null) {
                attachedImagePath = saved
            } else {
                Toast.makeText(context, "Could not process selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val saved = savePostImageFromBitmap(context, bitmap)
            if (saved != null) {
                attachedImagePath = saved
            } else {
                Toast.makeText(context, "Could not save photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── TOP BAR: Back Navigation + User Info (left) + Publish Button (right) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(LushGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUserName.take(1).uppercase().ifBlank { "Y" },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Create Post",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepBlack
                    )
                    Text(
                        text = "Posting as $currentUserName",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            }

            // Publish Post Button (Cancel button removed per requirement)
            Button(
                onClick = {
                    if (content.isNotBlank() && !isPublishing) {
                        val derivedTitle = content.lineSequence().firstOrNull { it.isNotBlank() }?.take(60)?.trim()
                            ?: "Community Post"
                        val finalCategory = selectedCategory?.removePrefix("#") ?: "General"
                        onSubmit(derivedTitle, finalCategory, content.trim(), currentUserName, attachedImagePath)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                enabled = content.isNotBlank() && !isPublishing,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publishing...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Text("Publish Post", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── SCROLLABLE BODY ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── TOPIC CATEGORY HASHTAGS (2 ROWS) ────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Topic Hashtags (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )

                // Row 1 of Hashtags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    hashtagRow1.forEach { tag ->
                        val isSelected = selectedCategory == tag
                        Surface(
                            onClick = {
                                selectedCategory = if (isSelected) null else tag
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) LushGreen else LightSurface,
                            border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else DeepBlack
                                )
                            }
                        }
                    }
                }

                // Row 2 of Hashtags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    hashtagRow2.forEach { tag ->
                        val isSelected = selectedCategory == tag
                        Surface(
                            onClick = {
                                selectedCategory = if (isSelected) null else tag
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) LushGreen else LightSurface,
                            border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else DeepBlack
                                )
                            }
                        }
                    }
                }
            }

            // ── THOUGHTS / CONTENT INPUT (Clean & Edge-to-Edge) ─────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                shape = RoundedCornerShape(8.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, if (isContentFocused) LushGreen else CardBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (content.isEmpty()) {
                        Text(
                            text = "Share your thoughts...",
                            color = MutedText,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = content,
                        onValueChange = { newText ->
                            if (!isPublishing && newText.length <= 50_000) {
                                content = newText
                            }
                        },
                        readOnly = isPublishing,
                        textStyle = TextStyle(
                            color = DeepBlack,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(LushGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                            .onFocusChanged { isContentFocused = it.isFocused }
                    )
                }
            }

            // Character Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${content.length} / 50,000 characters",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (content.length >= 50_000) Color(0xFFD32F2F) else MutedText
                )
            }

            // ── PHOTO ATTACHMENT SECTION ─────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Add Photo (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )

                if (attachedImagePath != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp, max = 240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightSurface)
                            .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = File(attachedImagePath!!),
                            contentDescription = "Attached photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { attachedImagePath = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    getContentLauncher.launch("image/*")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Gallery",
                                    tint = LushGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Choose Photo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LushGreen
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Camera not available", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = LightSurface,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Camera",
                                    tint = DeepBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Take Photo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun savePostImageFromUri(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val imagesDir = File(context.filesDir, "community_images").apply { mkdirs() }
        val targetFile = File(imagesDir, "post_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(targetFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        null
    }
}

private fun savePostImageFromBitmap(context: Context, bitmap: Bitmap): String? {
    return try {
        val imagesDir = File(context.filesDir, "community_images").apply { mkdirs() }
        val targetFile = File(imagesDir, "post_${System.currentTimeMillis()}.jpg")
        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
