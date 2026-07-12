package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton

@Composable
fun DeleteFloatingButton(

    onClick: () -> Unit

) {

    ToolFloatingButton(

        icon = Icons.Default.Delete,

        label = "Delete",

        onClick = onClick

    )

}