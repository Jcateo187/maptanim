package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.editcomponents.floating.DeleteFloatingButton
import com.maptanim.app.ui.components.editcomponents.floating.ResizeFloatingButton

@Composable
fun EditLeftToolbar(

    modifier: Modifier = Modifier,

    onResizeClick: () -> Unit = {},

    onDeleteClick: () -> Unit = {}

) {

    Column(

        modifier = modifier,

        verticalArrangement = Arrangement.spacedBy(24.dp)

    ) {

        ResizeFloatingButton(
            onClick = onResizeClick
        )

        DeleteFloatingButton(
            onClick = onDeleteClick
        )

    }

}