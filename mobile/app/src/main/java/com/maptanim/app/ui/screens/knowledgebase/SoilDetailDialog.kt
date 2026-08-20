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
import androidx.compose.runtime.Composable
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
import com.maptanim.app.domain.model.SoilGuide
import com.maptanim.app.domain.model.SoilType

@Composable
fun SoilDetailDialog(
    soil: SoilGuide,
    onDismiss: () -> Unit
) {
    val soilColor = getSoilColor(soil.soilType)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, soilColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = Color(0xFF1E2822)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Entire dialog is scrollable so soil image banner scrolls with specs & text
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Prominent Soil Photo Banner (240dp tall)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color(0xFF141C18))
                    ) {
                        if (!soil.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = formatImageUrl(soil.imageUrl),
                                contentDescription = soil.title,
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
                                            Color(0xFF1E2822)
                                        ),
                                        startY = 0f
                                    )
                                )
                        )
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
                                text = soil.title,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = soil.localName,
                                color = Color(0xFFFFD54F),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        SoilSection(title = "📝 Soil Profile Description") {
                            Text(
                                text = soil.description,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }

                        SoilSection(title = "📊 Technical Soil Metrics") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                MetricRow("Drainage Speed", soil.drainageSpeed, Color(0xFF0288D1))
                                MetricRow("pH Range", soil.phRange, Color(0xFF8E24AA))
                                MetricRow("Texture Profile", soil.texture, Color(0xFF4CAF50))
                                MetricRow("Physical Characteristics", soil.characteristics, Color(0xFFF59E0B))
                            }
                        }

                        SoilSection(title = "🌾 Recommended Philippine Crops") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                soil.bestCrops.forEach { crop ->
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
fun SoilSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF28342C))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, color = Color(0xFF81C784), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

fun getSoilColor(soilType: SoilType): Color = when (soilType) {
    SoilType.LOAM -> Color(0xFF795548)
    SoilType.CLAY -> Color(0xFF8D6E63)
    SoilType.SANDY -> Color(0xFFFBC02D)
    SoilType.SILTY -> Color(0xFFA1887F)
    SoilType.PEATY -> Color(0xFF424242)
    SoilType.CHALKY -> Color(0xFFB0BEC5)
}
