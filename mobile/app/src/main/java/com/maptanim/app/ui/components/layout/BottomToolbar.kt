package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.floating.FloatingEditButton

/**
 * BottomToolbar — Minimal floating button container.
 *
 * Provides the Edit button to switch to farm editor mode.
 */
@Composable
fun BottomToolbar(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        FloatingEditButton(
            isLoading = isLoading,
            onClick = onEditClick
        )
    }
}
