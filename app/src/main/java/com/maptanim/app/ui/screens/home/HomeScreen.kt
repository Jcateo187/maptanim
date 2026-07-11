package com.maptanim.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.background.HomeBackground
import com.maptanim.app.ui.components.layout.BottomToolbar
import com.maptanim.app.ui.components.layout.LeftToolbar
import com.maptanim.app.ui.components.layout.RightToolbar
import com.maptanim.app.ui.components.layout.TopBar

@Composable
fun HomeScreen() {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Layer 1 - Background
        HomeBackground()

        // Layer 2 - Top Navigation
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Layer 3 - Left Toolbar
        LeftToolbar(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
        )

        // Layer 4 - Right Toolbar
        RightToolbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        )

        // Layer 5 - Bottom Toolbar
        BottomToolbar(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 24.dp,
                    bottom = 24.dp
                )
        )

    }
}