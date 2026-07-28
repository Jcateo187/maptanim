package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.maptanim.app.ui.components.floating.FloatingViewButton

@Composable
fun EditBottomToolbar(

    modifier: Modifier = Modifier,

    navController: NavController

) {

    Box(

        modifier = modifier

    ) {

        FloatingViewButton(

            onClick = {

                navController.popBackStack()

            }

        )

    }

}