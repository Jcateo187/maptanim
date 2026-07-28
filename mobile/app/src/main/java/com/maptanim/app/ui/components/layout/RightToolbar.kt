package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * RightToolbar — View Mode right HUD buttons.
 *
 * Edit button removed (Edit Mode accessed via single bottom edit button).
 * Translucent HUD style with padding from screen edge.
 */
@Composable
fun RightToolbar(
    modifier: Modifier = Modifier,
    onLibraryClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HudRightButton(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            label = "Library",
            onClick = onLibraryClick
        )

        HudRightButton(
            icon = Icons.Default.Forum,
            label = "Community",
            onClick = onCommunityClick
        )
    }
}

@Composable
private fun HudRightButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.65f),
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(56.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color.White
            )
        }
    }
}
