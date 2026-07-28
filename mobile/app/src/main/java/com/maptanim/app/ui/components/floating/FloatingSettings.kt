package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun FloatingSettings(onClick:()-> Unit) {
    FloatingButton(

        icon = Icons.Default.Settings,

        title = "Settings",

        onClick = onClick

    )
}