package com.maptanim.app.ui.components.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.common.FloatingButton

@Composable
fun  FloatingLayouts(
    onClick: () -> Unit
){
    FloatingButton(

        icon = Icons.Default.Save,

        title = "Layouts",

        onClick = onClick

    )
}