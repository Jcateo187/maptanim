package com.maptanim.app.features.community.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.maptanim.app.features.community.model.getAvatarColor
import com.maptanim.app.features.community.viewmodel.CommunityMember

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)
private val FavoriteRed = Color(0xFFD32F2F)

/**
 * UserProfilePreviewDialog — Peer Profile preview for community members.
 * Displays ONLY:
 * 1. User Avatar (profile image or initial with dedicated color)
 * 2. Nickname
 * 3. Favorite Vegetables (if any, with heart indicator)
 * 4. Add Friend / Remove Friend / Chat action
 *
 * Strict Privacy: All private farm plots, crop beds, and harvest totals are hidden.
 * Daylight High-Contrast Theme compliant.
 */
@Composable
fun UserProfilePreviewDialog(
    member: CommunityMember,
    isFriend: Boolean,
    isCurrentUser: Boolean = false,
    onDismiss: () -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit = {},
    onOpenChat: (String) -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── HEADER: Title + Close Icon ───────────────────────────────
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Profile ng Magsasaka",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepBlack
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                // ── AVATAR & NICKNAME ────────────────────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(member.name)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!member.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = member.avatarUrl,
                                contentDescription = member.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = member.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        }
                    }

                    Text(
                        text = member.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightSurface,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text(
                            text = if (isCurrentUser) "Ikaw (Kasalukuyang User)" else "Kasaping Magsasaka",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LushGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }

                // ── FAVORITE VEGETABLES SECTION ─────────────────────────────
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LightSurface,
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = FavoriteRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Mga Paboritong Gulay",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                        }

                        if (member.favoriteCrops.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(member.favoriteCrops) { cropName ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Eco,
                                                contentDescription = null,
                                                tint = LushGreen,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = cropName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = DeepBlack
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Wala pang paboritong gulay na nakalista sa profile.",
                                fontSize = 11.sp,
                                color = MutedText,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // ── PRIVACY NOTICE ───────────────────────────────────────────
                Text(
                    text = "Protektado ang privacy: Avatar, Nickname, at Paboritong Gulay lamang ang makikita.",
                    fontSize = 10.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )

                // ── ACTIONS / BUTTONS ────────────────────────────────────────
                if (isCurrentUser) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text(
                            text = "Isara",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                } else if (isFriend) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onOpenChat(member.id)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kaibigan Na ✓",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onRemoveFriend(member.id)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = "Remove friend",
                                tint = MutedText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onAddFriend(member.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Idagdag Bilang Kaibigan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
