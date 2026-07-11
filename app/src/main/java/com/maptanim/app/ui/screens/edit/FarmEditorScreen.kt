package com.maptanim.app.ui.screens.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.layout.EditBottomToolbar

@Composable
fun FarmEditorScreen(

    navController: NavController

) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Same background as Home
        HomeBackground()



        // Bottom
        EditBottomToolbar(
            modifier = Modifier.align(Alignment.BottomEnd),
            navController = navController
        )

    }

}