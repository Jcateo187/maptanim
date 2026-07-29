package com.maptanim.app.ui.components.profile

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
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.screens.profile.AvatarSourceOption
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

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
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                            Text("Take a Photo with Camera", color = White, fontSize = 14.sp)
                            Button(
                                onClick = { onSelectAvatar("Avatar/Male_Avatar.png") },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                            ) {
                                Text("Capture & Save")
                            }
                        }
                    }

                    AvatarSourceOption.PHOTO_ALBUM -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Icon(Icons.Default.PhotoAlbum, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                            Text("Select Photo from Album", color = White, fontSize = 14.sp)
                            Button(
                                onClick = { onSelectAvatar("Avatar/Female_Avatar.png") },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                            ) {
                                Text("Choose & Save")
                            }
                        }
                    }
                }
            }
        }
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
