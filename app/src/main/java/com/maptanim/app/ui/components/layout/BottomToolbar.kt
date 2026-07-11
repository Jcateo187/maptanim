package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptanim.app.ui.components.floating.FloatingEditButton

@Composable
fun BottomToolbar(

    modifier: Modifier = Modifier,

    onEditClick: () -> Unit = {}

) {

    Box(
        modifier = modifier
    ) {

        FloatingEditButton(
            onClick = onEditClick
        )

    }

}