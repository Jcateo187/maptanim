package com.maptanim.app.ui.components.top

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextPrimary

@Composable
fun FarmName(

    farmName: String = "My Farm",

    onClick: () -> Unit = {}

) {

    Surface(

        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(

                interactionSource = remember {
                    MutableInteractionSource()
                },

                indication = ripple(
                    bounded = true
                ),

                onClick = onClick

            ),

        shape = RoundedCornerShape(20.dp),

        color = Color(0xFF2D3B45).copy(alpha = 0.82f),

        shadowElevation = 10.dp,

        border = BorderStroke(
            1.dp,
            ForestGreen.copy(alpha = 0.25f)
        )

    ) {

        Row(

            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 9.dp
            ),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {

            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = ForestGreen
            )

            Text(
                text = farmName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = TextPrimary
            )

        }

    }

}