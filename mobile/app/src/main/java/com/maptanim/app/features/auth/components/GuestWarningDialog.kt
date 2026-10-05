package com.maptanim.app.features.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Overlay advisory displayed when the user clicks 'Continue as Guest'.
 * Warns that guest data is stored only on the local device and can be lost
 * upon app uninstallation/clearing cache, but can be safely linked/bound
 * to a verified Google account in Settings at any time.
 *
 * Designed with modern aesthetic vector iconography, glassmorphic badges,
 * and high-contrast readable advisory rows.
 */
@Composable
fun GuestWarningDialog(
    onDismiss: () -> Unit,
    onProceed: () -> Unit,
    onSignIn: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 380.dp, max = 500.dp)
                .fillMaxWidth(0.90f)
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF111C15),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2E6340),
                        Color(0xFF1B3D28)
                    )
                )
            ),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Row: Title with Modern Amber Glow Badge & Minimal 'X' Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Modern Glow Warning Badge
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0x33FFB300),
                                            Color(0x10FF8F00)
                                        )
                                    ),
                                    shape = RoundedCornerShape(13.dp)
                                )
                                .border(
                                    BorderStroke(1.2.dp, Color(0x66FFB300)),
                                    shape = RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Advisory",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Guest Mode Advisory",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Temporary Local Session",
                                fontSize = 11.5.sp,
                                color = Color(0xFFA5D6A7),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Minimal Modern Close ('X') Chip
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            .border(BorderStroke(0.8.dp, Color.White.copy(alpha = 0.15f)), CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Spacious Advisory Container Area with Modern Vector Badges
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A120D), RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, Color(0xFF1E3A27).copy(alpha = 0.7f)), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Advisory Point 1: Data Loss Warning
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        ModernAdvisoryIconBadge(
                            icon = Icons.Default.Warning,
                            tint = Color(0xFFFF8A65),
                            bgColor = Color(0x28FF5722),
                            borderColor = Color(0x45FF7043)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Unsaved Farm Data Risk",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC80)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Your farm plots, companion plans, and daily logs are stored locally on this device only. If you uninstall the app or clear app cache, your local progress will be lost.",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCFD8DC),
                                lineHeight = 16.5.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1B3825), thickness = 0.8.dp)

                    // Advisory Point 2: Binding Account in Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        ModernAdvisoryIconBadge(
                            icon = Icons.Default.CheckCircle,
                            tint = Color(0xFF81C784),
                            bgColor = Color(0x244CAF50),
                            borderColor = Color(0x4581C784)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bind Account Anytime in Settings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "You can link your guest farm to a verified Google account at any time in App Settings to safely back up everything to the cloud.",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCFD8DC),
                                lineHeight = 16.5.sp
                            )
                        }
                    }

                    if (onSignIn != null) {
                        HorizontalDivider(color = Color(0xFF1B3825), thickness = 0.8.dp)

                        // Advisory Point 3: Sign In Option
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            ModernAdvisoryIconBadge(
                                icon = Icons.Default.Info,
                                tint = Color(0xFF90CAF9),
                                bgColor = Color(0x282196F3),
                                borderColor = Color(0x4564B5F6)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Have an existing account?",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF90CAF9)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Sign in directly to sync your existing farm profile and automated DSS companion recommendations.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFCFD8DC),
                                    lineHeight = 16.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Minimal Action Buttons: Cancel and Proceed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onSignIn != null) {
                        TextButton(
                            onClick = onSignIn,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Sign In Instead",
                                color = Color(0xFF90CAF9),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minimal Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF37474F)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFCFD8DC)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Minimal Proceed Button
                        Button(
                            onClick = onProceed,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Proceed as Guest",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern vector icon badge with subtle glassmorphic styling and crisp border.
 */
@Composable
private fun ModernAdvisoryIconBadge(
    icon: ImageVector,
    tint: Color,
    bgColor: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(bgColor, RoundedCornerShape(9.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.dp)
        )
    }
}
