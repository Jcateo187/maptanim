package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun FloatingNotification(onClick:()-> Unit) {
    FloatingButton(

        icon = Icons.Default.Notifications,

        title = "Alerts",

        onClick = onClick

    )
}