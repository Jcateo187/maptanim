package com.maptanim.app.features.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GuestButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) Color.White else Color(0xFFF3F4F6),
            contentColor = Color(0xFF2E7D32),
            disabledContainerColor = Color(0xFFF3F4F6),
            disabledContentColor = Color(0xFF9CA3AF)
        ),
        border = BorderStroke(
            width = 1.2.dp,
            color = if (enabled) Color(0xFF2E7D32) else Color(0xFFE5E7EB)
        )
    ) {
        Text(
            text = "Continue as Guest",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) Color(0xFF2E7D32) else Color(0xFF9CA3AF)
        )
    }
}