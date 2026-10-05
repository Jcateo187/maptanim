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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.features.farm.renderer.loader.AssetLoader
import com.maptanim.app.features.shared.avatar.ProfileAvatar
import com.maptanim.app.features.profile.AvatarSourceOption
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import java.io.File
import java.io.FileOutputStream

@Composable
fun ViewAvatarDialog(
    avatarAssetPath: String,
    onDismiss: () -> Unit,
    onChangeAvatarClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E261A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile Avatar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                ProfileAvatar(
                    avatarAssetPath = avatarAssetPath,
                    size = 140.dp,
                    borderWidth = 4.dp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = onChangeAvatarClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Change Avatar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChangeAvatarModal(
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
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E261A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Avatar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
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

                Divider(color = White.copy(alpha = 0.15f))

                when (currentSource) {
                    AvatarSourceOption.AVATAR_STORAGE -> {
                        Text(
                            text = "Select from Avatar Storage:",
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.8f)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF2A3424))
                                        .clickable { onSelectAvatar(avatarItem.assetPath) }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, ForestGreen, CircleShape),
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
                                        color = White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    AvatarSourceOption.TAKE_PHOTO -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Take a Photo with Camera",
                                    color = White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Take a live photo using your phone camera to set as your profile avatar.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Camera & Capture", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    AvatarSourceOption.PHOTO_ALBUM -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoAlbum,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Select Photo from Album",
                                    color = White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Choose a photo from your gallery or album to set as your profile picture.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Icon(Icons.Default.PhotoAlbum, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select from Album", fontWeight = FontWeight.Bold)
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ForestGreen else Color(0xFF2A3424),
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
                tint = White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ConfirmChoiceDialog(
    title: String = "Confirmation",
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = White) },
        text = { Text(text = message, color = White.copy(alpha = 0.9f)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("No", color = White)
            }
        },
        containerColor = Color(0xFF1E261A)
    )
}
