package com.maptanim.app.ui.components.top

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextPrimary
import com.maptanim.app.ui.theme.TextSecondary

@Composable
fun MiniStatistics(

    beds: Int = 0,

    crops: Int = 0,

    harvest: Int = 0

) {

    Row(

        modifier = Modifier.padding(horizontal = 8.dp),

        horizontalArrangement = Arrangement.spacedBy(12.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {

        StatisticCard(
            icon = Icons.Default.Agriculture,
            title = "Plots",
            value = beds.toString()
        )

        StatisticCard(
            icon = Icons.Default.Grass,
            title = "Crops",
            value = crops.toString()
        )

        StatisticCard(
            icon = Icons.Default.Inventory2,
            title = "Harvest",
            value = harvest.toString()
        )

    }

}

@Composable
private fun StatisticCard(

    icon: ImageVector,

    title: String,

    value: String

) {

    Surface(

        modifier = Modifier.height(34.dp),

        shape = RoundedCornerShape(18.dp),

        color = Color(0xFF2D3B45).copy(alpha = 0.78f),

        shadowElevation = 2.dp,

        border = BorderStroke(
            1.dp,
            ForestGreen.copy(alpha = 0.18f)
        )

    ) {

        Row(

            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.spacedBy(4.dp)

        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = ForestGreen,

                modifier = Modifier.size(14.dp)

            )

            Text(

                text = value,

                style = MaterialTheme.typography.labelLarge,

                color = TextPrimary

            )

            Text(

                text = title,

                style = MaterialTheme.typography.bodySmall,

                color = TextSecondary

            )

        }

    }

}