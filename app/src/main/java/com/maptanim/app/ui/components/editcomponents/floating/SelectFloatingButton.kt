package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton

@Composable
fun SelectFloatingButton(

    onClick: () -> Unit

) {

    ToolFloatingButton(

        icon = Icons.Default.TouchApp,

        label = "Select",

        onClick = onClick

    )

}