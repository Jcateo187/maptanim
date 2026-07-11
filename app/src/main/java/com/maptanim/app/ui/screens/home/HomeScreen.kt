package com.maptanim.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.layout.BottomToolbar
import com.maptanim.app.ui.components.layout.LeftToolbar
import com.maptanim.app.ui.components.layout.RightToolbar
import com.maptanim.app.ui.components.layout.TopBar

@Composable
fun HomeScreen(

    navController: NavHostController

) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        HomeBackground()

        TopBar(
            modifier = Modifier.align(Alignment.TopCenter)
        )

        LeftToolbar(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
        )

        RightToolbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        )

        BottomToolbar(

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 24.dp,
                    bottom = 24.dp
                ),

            onEditClick = {

                navController.navigate(Routes.EDIT)

            }

        )

    }

}