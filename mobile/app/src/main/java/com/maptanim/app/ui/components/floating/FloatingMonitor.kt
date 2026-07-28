package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun FloatingMonitor(

    onClick: () -> Unit

) {

    FloatingButton(

        icon = Icons.Default.MonitorHeart,

        title = "Monitor",

        onClick = onClick

    )

}