package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.floating.FloatingLayouts
import com.maptanim.app.ui.components.floating.FloatingMonitor

@Composable
fun LeftToolbar(

    modifier: Modifier = Modifier,

    onMonitorClick: () -> Unit = {},

    onLayoutClick: () -> Unit = {}

) {

    Column(

        modifier = modifier,

        verticalArrangement = Arrangement.spacedBy(24.dp)

    ) {

        FloatingMonitor(
            onClick = onMonitorClick
        )

        FloatingLayouts(
            onClick = onLayoutClick
        )

    }

}