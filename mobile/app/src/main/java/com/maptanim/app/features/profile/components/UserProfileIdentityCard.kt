package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
 * UserProfileIdentityCard — Single portrait card featuring Avatar on the left
 * and Nickname on the right with an edit icon for changing name.
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
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE: Avatar
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ProfileAvatar(
                        avatarAssetPath = uiState.userProfile.avatarAssetPath,
                        size = 72.dp,
                        onClick = onOpenViewAvatar
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT SIDE: Nickname with change icon, email, and cooldown info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!uiState.isEditingNickname) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.userProfile.nickname.ifBlank { "Farmer" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = onStartEditNickname,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Name",
                                    tint = LushGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        val accountLabel = if (!uiState.userProfile.boundEmail.isNullOrBlank()) {
                            uiState.userProfile.boundEmail
                        } else if (uiState.userProfile.isAccountBound) {
                            "Verified Farmer"
                        } else {
                            "Local Farmer"
                        }
                        Text(
                            text = accountLabel,
                            fontSize = 12.sp,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val remainingDays = getDaysRemainingForNicknameChange(uiState.userProfile.nicknameUpdatedAt)
                        if (remainingDays > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
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
                                    text = "Name locked for $remainingDays day(s)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    } else {
                        // Inline name editing right inside the card
                        OutlinedTextField(
                            value = uiState.nicknameInput,
                            onValueChange = onNicknameInputChange,
                            label = { Text("New Nickname", color = MutedText, fontSize = 11.sp) },
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
                            Text(
                                text = err,
                                color = Color(0xFFC62828),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onCancelEditNickname,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Cancel", color = MutedText, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = onSubmitNicknameCheck,
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !uiState.isCheckingNickname,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                if (uiState.isCheckingNickname) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Save", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
