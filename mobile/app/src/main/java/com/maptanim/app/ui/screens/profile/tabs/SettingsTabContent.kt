package com.maptanim.app.ui.screens.profile.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.screens.profile.ProfileUiState
import com.maptanim.app.ui.screens.profile.ProfileViewModel
import com.maptanim.app.ui.screens.settings.AudioSettingsDialog
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import com.maptanim.app.viewmodel.TutorialViewModel

@Composable
fun SettingsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    var showAudioSettingsModal by remember { mutableStateOf(false) }
    val soundManager = LocalSoundManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Audio & Sound Settings Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
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
                        imageVector = if (soundManager.isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = ForestGreen
                    )
                    Column {
                        Text("Audio Adjustment", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (soundManager.isMuted) "Master Audio Muted" else "Music & SFX Enabled",
                            color = if (soundManager.isMuted) Color(0xFFEF9A9A) else ForestGreen,
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
        }

        if (showAudioSettingsModal) {
            AudioSettingsDialog(
                onDismissRequest = { showAudioSettingsModal = false }
            )
        }

        // Replay Tutorial Section
        val tutorialViewModel: TutorialViewModel = viewModel()
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
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
                        tint = ForestGreen
                    )
                    Column {
                        Text("Replay Farm Guide Tutorial", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Restart Tatay Juan step-by-step interactive guide",
                            color = White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
        }

        // Bind Account Section
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
                    Text("Account Binding", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (uiState.userProfile.isAccountBound)
                            "Already bound (${uiState.userProfile.boundEmail})"
                        else "Not bound to external cloud",
                        color = if (uiState.userProfile.isAccountBound) ForestGreen else White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }

                if (!uiState.userProfile.isAccountBound) {
                    Button(
                        onClick = { viewModel.openBindAccount() },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Bind Account")
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Bound", tint = ForestGreen)
                }
            }
        }

        // System Recommendation & Report Issue
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
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
                    Text("Report Issue / Feedback", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Send system feedback or issues to Admin", color = White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
        }

        // Log out Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
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
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 15.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Red)
            }
        }
    }

    // Modal: Bind Account
    if (uiState.showBindAccountModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeBindAccount() },
            title = { Text("Bind Account", color = White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter email to bind your local farm data to Supabase cloud:", color = White.copy(alpha = 0.8f))
                    OutlinedTextField(
                        value = uiState.bindEmailInput,
                        onValueChange = { viewModel.updateBindEmailInput(it) },
                        label = { Text("Email Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen, focusedTextColor = White, unfocusedTextColor = White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitBindAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Create & Bind")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeBindAccount() }) {
                    Text("Cancel", color = White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }

    // Modal: Report Issue
    if (uiState.showReportIssueModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeReportIssue() },
            title = { Text("Report Issue to Admin", color = White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = uiState.issueTextInput,
                    onValueChange = { viewModel.updateIssueInput(it) },
                    label = { Text("Describe the issue...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen, focusedTextColor = White, unfocusedTextColor = White)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitReportIssue() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Send to Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeReportIssue() }) {
                    Text("Cancel", color = White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }

    // Modal: Logout Confirm
    if (uiState.showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelLogout() },
            title = { Text("Log Out Confirmation", color = White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?", color = White.copy(alpha = 0.9f)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Yes, Log Out")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.cancelLogout() }) {
                    Text("No, Stay in Audio Adjustment", color = White)
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }
}
