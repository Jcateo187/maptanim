package com.maptanim.app.ui.screens.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.editcomponents.layout.EditBottomLayout
import com.maptanim.app.ui.components.editcomponents.layout.EditRightToolbar
import com.maptanim.app.ui.components.editcomponents.layout.EditTopLayout

@Composable
fun FarmEditorScreen(

    navController: NavController

) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Background
        HomeBackground()

        // Future Isometric Grid goes here
        /*
        FarmCanvas(
            modifier = Modifier.fillMaxSize()
        )
        */

        EditTopLayout(

            modifier = Modifier.align(Alignment.TopCenter)

        )

        EditRightToolbar(

            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)

        )

        EditBottomLayout(

            modifier = Modifier.align(Alignment.BottomCenter)

        )

    }

}