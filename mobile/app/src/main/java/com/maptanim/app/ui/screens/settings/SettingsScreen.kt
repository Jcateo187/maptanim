package com.maptanim.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

/**
 * SettingsScreen — Dedicated Settings page in MapTanim.
 * Houses Audio & Sound controls, Farm Notifications, Account Sync, and Log Out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val soundManager = LocalSoundManager.current
    var isMuted by remember { mutableStateOf(soundManager.isMuted) }
    var bgmVolume by remember { mutableFloatStateOf(soundManager.bgmVolume) }
    var ambientVolume by remember { mutableFloatStateOf(soundManager.ambientVolume) }
    var sfxVolume by remember { mutableFloatStateOf(soundManager.sfxVolume) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var enableNotifications by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings", fontWeight = FontWeight.Bold, color = White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF132A1F))
            )
        },
        containerColor = Color(0xFF0F1A13)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. AUDIO & SOUND SECTION ─────────────────────────────────────
            Text(
                text = "AUDIO & SOUND",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E261A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Master App Audio", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                            Text(
                                text = if (isMuted) "All sounds muted" else "Music & SFX active",
                                color = if (isMuted) Color(0xFFEF9A9A) else ForestGreen,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = !isMuted,
                            onCheckedChange = { isChecked ->
                                isMuted = !isChecked
                                soundManager.isMuted = isMuted
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White,
                                checkedTrackColor = ForestGreen
                            )
                        )
                    }

                    AudioChannelSlider(
                        title = "Background Music (BGM)",
                        icon = Icons.Default.MusicNote,
                        value = bgmVolume,
                        enabled = !isMuted,
                        onValueChange = {
                            bgmVolume = it
                            soundManager.bgmVolume = it
                        }
                    )

                    AudioChannelSlider(
                        title = "Nature & Ambient",
                        icon = Icons.Default.Nature,
                        value = ambientVolume,
                        enabled = !isMuted,
                        onValueChange = {
                            ambientVolume = it
                            soundManager.ambientVolume = it
                        }
                    )

                    AudioChannelSlider(
                        title = "Sound Effects (SFX)",
                        icon = Icons.Default.GraphicEq,
                        value = sfxVolume,
                        enabled = !isMuted,
                        onValueChange = {
                            sfxVolume = it
                            soundManager.sfxVolume = it
                        }
                    )
                }
            }

            // ── 2. NOTIFICATIONS SECTION ────────────────────────────────────
            Text(
                text = "FARM NOTIFICATIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E261A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Task & Care Reminders", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        Text("Irrigation and harvest notifications", color = White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                    Switch(
                        checked = enableNotifications,
                        onCheckedChange = { enableNotifications = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = ForestGreen
                        )
                    )
                }
            }

            // ── 3. LOG OUT SECTION ──────────────────────────────────────────
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showLogoutConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out", tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out Account", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
            }
        }
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold, color = White) },
            text = { Text("Are you sure you want to log out of MapTanim?", color = White.copy(alpha = 0.85f)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Log Out", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel", color = White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF132A1F)
        )
    }
}

/**
 * SettingsDialog — Modal dialog version of System Settings.
 */
@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    onLogoutClick: (() -> Unit)? = null
) {
    val soundManager = LocalSoundManager.current

    var isMuted by remember { mutableStateOf(soundManager.isMuted) }
    var bgmVolume by remember { mutableFloatStateOf(soundManager.bgmVolume) }
    var ambientVolume by remember { mutableFloatStateOf(soundManager.ambientVolume) }
    var sfxVolume by remember { mutableFloatStateOf(soundManager.sfxVolume) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF132A1F)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio Adjustment",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Audio Adjustment",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                // Master Audio Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B382B), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Master App Audio",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isMuted) "All sounds muted" else "All sound channels active",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = !isMuted,
                        onCheckedChange = { isChecked ->
                            isMuted = !isChecked
                            soundManager.isMuted = isMuted
                            if (isChecked) {
                                soundManager.playSfx(SoundEffect.TAP_BUTTON)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF2E4D3E)
                        )
                    )
                }

                AudioChannelSlider(
                    title = "Background Music (BGM)",
                    icon = Icons.Default.MusicNote,
                    value = bgmVolume,
                    enabled = !isMuted,
                    onValueChange = {
                        bgmVolume = it
                        soundManager.bgmVolume = it
                    }
                )

                AudioChannelSlider(
                    title = "Nature & Ambient",
                    icon = Icons.Default.Nature,
                    value = ambientVolume,
                    enabled = !isMuted,
                    onValueChange = {
                        ambientVolume = it
                        soundManager.ambientVolume = it
                    }
                )

                AudioChannelSlider(
                    title = "Sound Effects (SFX)",
                    icon = Icons.Default.GraphicEq,
                    value = sfxVolume,
                    enabled = !isMuted,
                    onValueChange = {
                        sfxVolume = it
                        soundManager.sfxVolume = it
                    },
                    onValueChangeFinished = {
                        soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    }
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                Button(
                    onClick = {
                        soundManager.playSfx(SoundEffect.TAP_BUTTON)
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = "Done",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * Backward-compatible wrapper for AudioSettingsDialog -> SettingsDialog.
 */
@Composable
fun AudioSettingsDialog(
    onDismissRequest: () -> Unit,
    onLogoutClick: (() -> Unit)? = null
) {
    SettingsDialog(
        onDismissRequest = onDismissRequest,
        onLogoutClick = onLogoutClick
    )
}

@Composable
private fun AudioChannelSlider(
    title: String,
    icon: ImageVector,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B382B).copy(alpha = if (enabled) 1.0f else 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF81C784) else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (enabled) Color.White else Color.Gray
                )
            }
            Text(
                text = "${(value * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (enabled) Color(0xFF81C784) else Color.Gray
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            enabled = enabled,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF81C784),
                activeTrackColor = Color(0xFF4CAF50),
                inactiveTrackColor = Color(0xFF2E4D3E),
                disabledThumbColor = Color.Gray,
                disabledActiveTrackColor = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
