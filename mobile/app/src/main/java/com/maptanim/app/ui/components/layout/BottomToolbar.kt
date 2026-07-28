package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.floating.FloatingEditButton

/**
 * BottomToolbar — Minimal floating button container.
 *
 * Removes redundant bottom tab bar to maximize 2D map screen area.
 */
@Composable
fun BottomToolbar(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        FloatingEditButton(
            onClick = onEditClick
        )
    }
}