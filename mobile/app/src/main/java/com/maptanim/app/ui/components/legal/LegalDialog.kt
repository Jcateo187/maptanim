package com.maptanim.app.ui.components.legal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.data.local.LegalContent

enum class LegalType {
    TERMS_AND_CONDITIONS,
    PRIVACY_POLICY
}

@Composable
fun LegalDialog(
    initialType: LegalType = LegalType.TERMS_AND_CONDITIONS,
    title: String = if (initialType == LegalType.TERMS_AND_CONDITIONS) "Terms & Conditions" else "Privacy Policy",
    content: String = if (initialType == LegalType.TERMS_AND_CONDITIONS) LegalContent.TERMS_AND_CONDITIONS else LegalContent.PRIVACY_POLICY,
    onDismiss: () -> Unit,
    onAccept: (() -> Unit)? = null
) {
    var currentType by remember(initialType, title) {
        mutableStateOf(
            if (title.contains("Privacy", ignoreCase = true)) {
                LegalType.PRIVACY_POLICY
            } else {
                initialType
            }
        )
    }

    val isTerms = currentType == LegalType.TERMS_AND_CONDITIONS
    val displayContent = if (isTerms) LegalContent.TERMS_AND_CONDITIONS else LegalContent.PRIVACY_POLICY

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 440.dp, max = 560.dp)
                .fillMaxWidth(0.72f)
                .fillMaxHeight(0.96f)
                .padding(vertical = 6.dp, horizontal = 10.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF131F17),
            border = BorderStroke(1.5.dp, Color(0xFF2E5A3C)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Compact Top Bar: Integrated Segmented Switcher & Close button (Height: ~34dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Segmented Toggle (No text duplication, single place to switch)
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF0A120C), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1B3825), RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTerms) Color(0xFF2E5A3C) else Color.Transparent)
                                .clickable { currentType = LegalType.TERMS_AND_CONDITIONS }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Terms & Conditions",
                                fontSize = 12.sp,
                                fontWeight = if (isTerms) FontWeight.Bold else FontWeight.Normal,
                                color = if (isTerms) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isTerms) Color(0xFF2E5A3C) else Color.Transparent)
                                .clickable { currentType = LegalType.PRIVACY_POLICY }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Privacy Policy",
                                fontSize = 12.sp,
                                fontWeight = if (!isTerms) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isTerms) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Maximized Scrollable Reading Content Area (>80% of total height)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF0A120C), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF1B3825), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        val lines = displayContent.lines()
                        lines.forEach { line ->
                            val trimmed = line.trim()
                            when {
                                trimmed.isEmpty() -> {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                trimmed.startsWith("MAPTANIM") -> {
                                    Text(
                                        text = trimmed,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                trimmed.startsWith("Effective Date") || trimmed.startsWith("Compliance") || trimmed.startsWith("STI") -> {
                                    Text(
                                        text = trimmed,
                                        fontSize = 11.sp,
                                        color = Color(0xFF81C784),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                trimmed.matches(Regex("""^\d+\..*""")) -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = trimmed,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81C784)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                                else -> {
                                    Text(
                                        text = trimmed,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFFE0E6E1),
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Slim Bottom Action Bar (Height: ~34dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTerms) "Version 1.2 • STI WNU" else "Version 1.2 • RA 10173",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.45f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Close",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }

                        if (onAccept != null) {
                            Button(
                                onClick = {
                                    onAccept()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5A3C)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "I Understand & Accept",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
