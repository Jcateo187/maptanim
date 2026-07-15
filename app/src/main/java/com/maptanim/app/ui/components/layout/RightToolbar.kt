package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.floating.FloatingCommunity
import com.maptanim.app.ui.components.floating.FloatingCropLibrary

@Composable
fun RightToolbar(

    modifier: Modifier = Modifier,

    onCropLibraryClick: () -> Unit = {},

    onCommunityClick: () -> Unit = {}

) {

    Column(

        modifier = modifier,

        verticalArrangement = Arrangement.spacedBy(24.dp)

    ) {

        FloatingCropLibrary(
            onClick = onCropLibraryClick
        )

        FloatingCommunity(
            onClick = onCommunityClick
        )

    }

}