package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * SpecMetricCard — Compact specification tile with circular icon badge.
 */
@Composable
fun SpecMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BulletItem(text: String, dotColor: Color = LushGreen) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = dotColor,
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
        ) {}
        Text(
            text = text,
            fontSize = 12.sp,
            color = DeepBlack,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun ProcedureCard(stepNumber: Int, title: String, instruction: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE8F5E9),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = instruction,
                fontSize = 11.sp,
                color = MutedText,
                lineHeight = 15.sp
            )
        }
    }
}
