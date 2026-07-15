package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.editcomponents.croptray.CropTray

@Composable
fun EditBottomLayout(

    modifier: Modifier = Modifier

) {

    CropTray(

        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 15.dp,
                vertical = 5.dp

            )

    )

}