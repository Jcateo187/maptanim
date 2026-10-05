package com.maptanim.app.features.profile.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.features.farm.renderer.loader.AssetLoader
import com.maptanim.app.features.profile.model.AvatarSourceOption
import java.io.File
import java.io.FileOutputStream

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * AvatarPickerModal — Modal to pick from preset avatars, capture with camera, or pick from gallery.
 * Converted strictly to the Daylight High-Contrast Theme (Pure White, Lush Green, Deep Black).
 */
@Composable
fun ChangeAvatarModal(
    availableAvatars: List<AvatarItem>,
    currentSource: AvatarSourceOption,
    onSelectSource: (AvatarSourceOption) -> Unit,
    onSelectAvatar: (String) -> Unit,
    onDismiss: () -> Unit
) = AvatarPickerModal(availableAvatars, currentSource, onSelectSource, onSelectAvatar, onDismiss)

@Composable
fun AvatarPickerModal(
    availableAvatars: List<AvatarItem>,
    currentSource: AvatarSourceOption,
    onSelectSource: (AvatarSourceOption) -> Unit,
    onSelectAvatar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // ── Photo Album Pickers & Storage Permission ──────────────────────────────
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveImageUriToInternalStorage(context, uri)
            if (savedPath != null) {
                onSelectAvatar(savedPath)
            } else {
                Toast.makeText(context, "Could not process selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val getContentFallbackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveImageUriToInternalStorage(context, uri)
            if (savedPath != null) {
                onSelectAvatar(savedPath)
            } else {
                Toast.makeText(context, "Could not process selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } catch (_: Exception) {
                getContentFallbackLauncher.launch("image/*")
            }
        } else {
            Toast.makeText(context, "Photos permission is required to select from album", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Camera Capture & Camera Permission ────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedPath = saveBitmapToInternalStorage(context, bitmap)
            if (savedPath != null) {
                onSelectAvatar(savedPath)
            } else {
                Toast.makeText(context, "Could not save captured photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture a photo", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Avatar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DeepBlack
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 3 Source Selector Tabs (Take photo, Avatar Storage, Photo Album)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SourceTabButton(
                        icon = Icons.Default.CameraAlt,
                        label = "Camera",
                        isSelected = currentSource == AvatarSourceOption.TAKE_PHOTO,
                        onClick = { onSelectSource(AvatarSourceOption.TAKE_PHOTO) },
                        modifier = Modifier.weight(1f)
                    )
                    SourceTabButton(
                        icon = Icons.Default.Storage,
                        label = "Preset",
                        isSelected = currentSource == AvatarSourceOption.AVATAR_STORAGE,
                        onClick = { onSelectSource(AvatarSourceOption.AVATAR_STORAGE) },
                        modifier = Modifier.weight(1f)
                    )
                    SourceTabButton(
                        icon = Icons.Default.PhotoAlbum,
                        label = "Album",
                        isSelected = currentSource == AvatarSourceOption.PHOTO_ALBUM,
                        onClick = { onSelectSource(AvatarSourceOption.PHOTO_ALBUM) },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = CardBorderColor)

                when (currentSource) {
                    AvatarSourceOption.AVATAR_STORAGE -> {
                        Text(
                            text = "Select from Preset Avatars:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            items(availableAvatars) { avatarItem ->
                                val bitmap = remember(avatarItem.assetPath) {
                                    AssetLoader.loadFromAssets(context, avatarItem.assetPath)
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(LightSurface)
                                        .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp))
                                        .clickable { onSelectAvatar(avatarItem.assetPath) }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, LushGreen, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap,
                                                contentDescription = avatarItem.displayName,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = avatarItem.displayName,
                                        fontSize = 11.sp,
                                        color = DeepBlack,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    AvatarSourceOption.TAKE_PHOTO -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(LushGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Take a Photo with Camera",
                                    color = DeepBlack,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Capture a live photo using your phone camera to set as your profile avatar.",
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    val isCameraGranted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (isCameraGranted) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Camera & Capture", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    AvatarSourceOption.PHOTO_ALBUM -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(LushGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoAlbum,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Select Photo from Album",
                                    color = DeepBlack,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Choose a photo from your gallery or album to set as your profile picture.",
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    val isStorageGranted = ContextCompat.checkSelfPermission(
                                        context,
                                        storagePermission
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (isStorageGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        try {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        } catch (_: Exception) {
                                            getContentFallbackLauncher.launch("image/*")
                                        }
                                    } else {
                                        storagePermissionLauncher.launch(storagePermission)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(Icons.Default.PhotoAlbum, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select from Album", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Saves a picked image Uri to app internal storage and returns the local file path.
 */
private fun saveImageUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val dir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }
        val file = File(dir, "avatar_gallery_${System.currentTimeMillis()}.png")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (_: Exception) {
        null
    }
}

/**
 * Saves a camera bitmap to app internal storage and returns the local file path.
 */
private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
    return try {
        val dir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }
        val file = File(dir, "avatar_camera_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        file.absolutePath
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun SourceTabButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) LushGreen else LightSurface,
        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else DeepBlack,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isSelected) Color.White else DeepBlack,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

