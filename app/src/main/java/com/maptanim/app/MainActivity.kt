package com.maptanim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.maptanim.app.navigation.AppNavGraph
import com.maptanim.app.ui.theme.MapTanimTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MapTanimTheme {
                AppNavGraph()
            }
        }
    }
}