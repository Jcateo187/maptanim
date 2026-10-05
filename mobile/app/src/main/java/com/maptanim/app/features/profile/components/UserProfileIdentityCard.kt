package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.getDaysRemainingForNicknameChange
import com.maptanim.app.features.profile.model.ProfileUiState
import com.maptanim.app.features.shared.avatar.ProfileAvatar

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * UserProfileIdentityCard — Displays user avatar, nickname, and nickname editing in Daylight theme.
 */
@Composable
fun UserProfileIdentityCard(
    uiState: ProfileUiState,
    onOpenViewAvatar: () -> Unit,
    onStartEditNickname: () -> Unit,
    onNicknameInputChange: (String) -> Unit,
    onSubmitNicknameCheck: () -> Unit,
    onCancelEditNickname: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar(
                    avatarAssetPath = uiState.userProfile.avatarAssetPath,
                    size = 80.dp,
                    onClick = onOpenViewAvatar
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.userProfile.nickname.ifBlank { "Farmer" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Text(
                    text = "Tap avatar to view or change",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }

        // Nickname & Edit Nickname Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nickname",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = DeepBlack
                        )
                    }

                    if (!uiState.isEditingNickname) {
                        TextButton(onClick = onStartEditNickname) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Edit", color = LushGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (uiState.isEditingNickname) {
                    OutlinedTextField(
                        value = uiState.nicknameInput,
                        onValueChange = onNicknameInputChange,
                        label = { Text("Enter Nickname", color = MutedText, fontSize = 12.sp) },
                        isError = uiState.nicknameError != null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    uiState.nicknameError?.let { err ->
                        Text(text = err, color = Color(0xFFC62828), fontSize = 11.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onCancelEditNickname) {
                            Text("Cancel", color = MutedText, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = onSubmitNicknameCheck,
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isCheckingNickname
                        ) {
                            if (uiState.isCheckingNickname) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Text(
                        text = uiState.userProfile.nickname.ifBlank { "Farmer" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack
                    )
                    val remainingDays = getDaysRemainingForNicknameChange(uiState.userProfile.nicknameUpdatedAt)
                    if (remainingDays > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Next change available in $remainingDays day(s)",
                                fontSize = 10.sp,
                                color = Color(0xFFD97706)
                            )
                        }
                    } else {
                        Text(
                            text = "Can be changed once every 15 days",
                            fontSize = 10.sp,
                            color = MutedText
                        )
                    }
                }
            }
        }
    }
}
