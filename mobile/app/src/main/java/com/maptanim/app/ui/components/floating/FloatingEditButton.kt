package com.maptanim.app.ui.components.floating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextDark
import com.maptanim.app.ui.theme.White

@Composable
fun FloatingEditButton(
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(76.dp)
            .height(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                enabled = !isLoading,
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp,
        color = White
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    color = ForestGreen,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextDark
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Add Crops",
                    modifier = Modifier.size(28.dp),
                    tint = ForestGreen
                )
                Text(
                    text = "Add Crops",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark
                )
            }
        }
    }
}