package com.maptanim.app.features.profile.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.model.ProfileUiState
import com.maptanim.app.features.shared.guide.TutorialViewModel
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * SettingsTabContent — App settings, audio controls, account binding, and support in Daylight theme.
 */
@Composable
fun SettingsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    var showAudioSettingsModal by remember { mutableStateOf(false) }
    val soundManager = LocalSoundManager.current
    var isAudioMuted by remember { mutableStateOf(soundManager.isMuted) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // 1. Audio & Sound Settings Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    showAudioSettingsModal = true
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = if (isAudioMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = LushGreen
                    )
                    Column {
                        Text("Audio & Sound Effects", fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAudioMuted) "Master Audio Muted" else "Music & Sound Effects Active",
                            color = if (isAudioMuted) Color(0xFFC62828) else LushGreen,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
        }

        // 2. Replay Tutorial Section
        val tutorialViewModel: TutorialViewModel = viewModel()
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    tutorialViewModel.restartTutorial()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = LushGreen
                    )
                    Column {
                        Text("Replay Farmer Guide Tutorial", fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Restart Tatay Juan step-by-step interactive onboarding",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
        }

        // 3. Bind Account Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Account Cloud Backup", fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (uiState.userProfile.isAccountBound)
                            "Bound to ${uiState.userProfile.boundEmail}"
                        else "Not synced to external account",
                        color = if (uiState.userProfile.isAccountBound) LushGreen else MutedText,
                        fontSize = 12.sp
                    )
                }

                if (!uiState.userProfile.isAccountBound) {
                    Button(
                        onClick = { viewModel.openBindAccount() },
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Bind", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Bound", tint = LushGreen)
                }
            }
        }

        // 4. Report Issue / Feedback Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openReportIssue() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Report Issue / Feedback", fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Submit agricultural bug or system feedback to Admin", color = MutedText, fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
        }

        // 5. About MapTanim Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    navController.navigate(Routes.ABOUT)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LushGreen
                    )
                    Column {
                        Text("About MapTanim", fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Research citation, project info, and terms",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
        }

        // 6. Log Out Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openLogoutConfirm() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 14.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFC62828))
            }
        }
    }

    // Modal: Audio Settings Dialog
    if (showAudioSettingsModal) {
        Dialog(onDismissRequest = { showAudioSettingsModal = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Audio Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepBlack)
                    Text(
                        text = "Toggle background sound and interactive feedback effects.",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mute All Audio", fontSize = 14.sp, color = DeepBlack)
                        Switch(
                            checked = isAudioMuted,
                            onCheckedChange = { muted ->
                                soundManager.isMuted = muted
                                isAudioMuted = muted
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LushGreen
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAudioSettingsModal = false },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                        ) {
                            Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Bind Account
    if (uiState.showBindAccountModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeBindAccount() },
            title = { Text("Bind Account", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter email to bind your local farm data to cloud backup:", color = MutedText, fontSize = 13.sp)
                    OutlinedTextField(
                        value = uiState.bindEmailInput,
                        onValueChange = { viewModel.updateBindEmailInput(it) },
                        label = { Text("Email Address", color = MutedText) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitBindAccount() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Bind", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeBindAccount() }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal: Report Issue
    if (uiState.showReportIssueModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeReportIssue() },
            title = { Text("Report Issue to Admin", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                OutlinedTextField(
                    value = uiState.issueTextInput,
                    onValueChange = { viewModel.updateIssueInput(it) },
                    label = { Text("Describe the issue...", color = MutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LushGreen,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitReportIssue() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Send", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeReportIssue() }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal: Logout Confirm
    if (uiState.showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelLogout() },
            title = { Text("Log Out Confirmation", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = { Text("Are you sure you want to log out?", color = DeepBlack, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Yes, Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.cancelLogout() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Text("Cancel", color = DeepBlack)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
