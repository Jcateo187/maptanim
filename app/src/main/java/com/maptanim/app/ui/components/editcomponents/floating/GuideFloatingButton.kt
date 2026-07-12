package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton

@Composable
fun GuideFloatingButton(

    onClick: () -> Unit

) {

    ToolFloatingButton(

        icon = Icons.Default.MenuBook,

        label = "Guide",

        onClick = onClick

    )

}