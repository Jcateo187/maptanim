package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.features.farm.renderer.canvas.CardSvgRenderer

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFD0D8CC)
private val LightSurface = Color(0xFFFFFFFF)

@Composable
fun GuideRecommendationCard(recommendation: DssLogEvaluator.LogRecommendation) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F8E9),
        border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        CardSvgRenderer.drawAgronomicRecommendationBadge(
                            drawScope = this,
                            center = Offset(size.width / 2f, size.height / 2f),
                            sizePx = size.width
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = recommendation.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepBlack
                )
                Text(
                    text = recommendation.content,
                    fontSize = 13.5.sp,
                    color = DeepBlack,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun GuideEmptyStateCard(title: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LightSurface,
        border = BorderStroke(1.2.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        CardSvgRenderer.drawAllGoodShieldBadge(
                            drawScope = this,
                            center = Offset(size.width / 2f, size.height / 2f),
                            sizePx = size.width
                        )
                    }
                }
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = DeepBlack
            )
            Text(
                text = description,
                fontSize = 13.5.sp,
                color = DeepBlack.copy(alpha = 0.8f),
                lineHeight = 19.sp
            )
        }
    }
}
