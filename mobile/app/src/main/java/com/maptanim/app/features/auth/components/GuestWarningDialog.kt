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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Overlay advisory displayed when the user clicks 'Continue as Guest'.
 * Warns that guest data is stored locally on this device only and can be lost
 * upon app uninstallation/clearing cache, but can be safely linked/bound
 * to a verified Google account in Settings at any time.
 *
 * Designed with MapTanim's standard daylight color system:
 * - Pure White surface (#FFFFFF) with neutral light border (#E5E7EB)
 * - Daylight canvas container (#F7FAF7)
 * - Crisp dark typography (#111813 / #4B5563)
 * - MapTanim Primary Forest Green CTA (#2E7D32)
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
                .widthIn(min = 340.dp, max = 480.dp)
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
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
                        // Warning Badge
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, Color(0xFFFDE68A)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Advisory",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Guest Mode Advisory",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111813)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Temporary Local Session",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Minimal Close ('X') Chip
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                            .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Advisory Container Area in standard daylight canvas (#F7FAF7)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7FAF7), RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), RoundedCornerShape(16.dp))
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
                            tint = Color(0xFFDC2626),
                            bgColor = Color(0xFFFEE2E2),
                            borderColor = Color(0xFFFECACA)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Unsaved Farm Data Risk",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Your farm plots, companion plans, and daily logs are stored locally on this device only. If you uninstall the app or clear app cache, your local progress will be lost.",
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 17.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

                    // Advisory Point 2: Binding Account in Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        ModernAdvisoryIconBadge(
                            icon = Icons.Default.CheckCircle,
                            tint = Color(0xFF16A34A),
                            bgColor = Color(0xFFDCFCE7),
                            borderColor = Color(0xFFBBF7D0)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bind Account Anytime in Settings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "You can link your guest farm to a verified Google account at any time in App Settings to safely back up everything to the cloud.",
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 17.sp
                            )
                        }
                    }

                    if (onSignIn != null) {
                        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

                        // Advisory Point 3: Sign In Option
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            ModernAdvisoryIconBadge(
                                icon = Icons.Default.Info,
                                tint = Color(0xFF2563EB),
                                bgColor = Color(0xFFEFF6FF),
                                borderColor = Color(0xFFDBEAFE)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Have an existing account?",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Sign in directly to sync your existing farm profile and automated DSS companion recommendations.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4B5563),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onSignIn != null) {
                        TextButton(
                            onClick = onSignIn,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Sign In Instead",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF4B5563)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = onProceed,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Proceed as Guest",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

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
