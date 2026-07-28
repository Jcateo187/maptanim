package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton

@Composable
fun BedFloatingButton(

    onClick: () -> Unit

) {

    ToolFloatingButton(

        icon = Icons.Default.CropSquare,

        label = "Bed",

        onClick = onClick

    )

}