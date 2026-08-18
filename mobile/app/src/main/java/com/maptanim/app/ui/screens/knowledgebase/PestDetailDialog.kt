package com.maptanim.app.ui.screens.knowledgebase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.maptanim.app.domain.model.PestGuide

@Composable
fun PestDetailDialog(
    pest: PestGuide,
    isImageHidden: Boolean = false,
    onDismiss: () -> Unit
) {
    var isHidden by remember(isImageHidden) { mutableStateOf(isImageHidden) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Color(0xFFE57373).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = Color(0xFF1E2420)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Entire dialog is scrollable so hero photo scrolls with text
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Prominent Hero Pest Image Banner (240dp tall)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color(0xFF161B18)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHidden) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = "Image Hidden",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Text(
                                    text = "Graphic Pest Image Hidden",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                OutlinedButton(
                                    onClick = { isHidden = false },
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reveal Photo", fontSize = 11.sp)
                                }
                            }
                        } else {
                            if (!pest.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = formatImageUrl(pest.imageUrl),
                                    contentDescription = pest.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.3f),
                                                Color.Transparent,
                                                Color(0xFF1E2420)
                                            ),
                                            startY = 0f
                                        )
                                    )
                            )
                        }
                    }

                    // Content Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column {
                            Text(
                                text = pest.name,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pest.localName,
                                color = Color(0xFFFF8A80),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = pest.scientificName,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }

                        PestSection(title = "🌾 Affected Philippine Crops") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                pest.affectedCrops.forEach { crop ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF2E7D32).copy(alpha = 0.4f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(crop, color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        PestSection(title = "🌿 Organic & Biological Interventions (Recommended)") {
                            Text(
                                text = pest.organicControl,
                                color = Color(0xFFA5D6A7),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }

                        PestSection(title = "🧪 Severe Outbreak Chemical Controls (DA Standards)") {
                            Text(
                                text = pest.chemicalControl,
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }

                        PestSection(title = "🛡️ Preventive Cultural Practices") {
                            Text(
                                text = pest.preventionTips,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Floating Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PestSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A362E))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, color = Color(0xFFFF8A80), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}
