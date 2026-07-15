package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun FloatingCommunity(

    onClick: () -> Unit
) {
    FloatingButton(

        icon = Icons.Default.Groups,

        title = "Community",

        onClick = onClick

    )
}