package com.maptanim.app.ui.components.editcomponents.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.runtime.Composable
import com.maptanim.app.ui.components.editcomponents.common.ToolFloatingButton
import com.maptanim.app.ui.components.isometric.world.farm.FarmEditState

@Composable
fun ResizeFloatingButton(

    farmEditState: FarmEditState

) {

    ToolFloatingButton(

        icon = Icons.Default.OpenInFull,

        label = "Resize",

        onClick = {
            farmEditState.isResizeMode =
                !farmEditState.isResizeMode
        }

    )

}