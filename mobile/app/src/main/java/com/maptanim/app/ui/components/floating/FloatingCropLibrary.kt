package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun FloatingCropLibrary(

    onClick: () -> Unit

) {
    FloatingButton(

        icon = Icons.Default.MenuBook,

        title = "Crop Library",

        onClick = onClick

    )
}