package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton

@Composable
fun SaveFloatingButton(

    onClick: () -> Unit

) {

    ToolFloatingButton(

        icon = Icons.Default.Save,

        label = "Save",

        onClick = onClick

    )

}