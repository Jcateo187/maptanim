package com.maptanim.app

import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.maptanim.app.core.audio.ProvideSoundManager
import com.maptanim.app.core.audio.SoundManager
import com.maptanim.app.navigation.AppNavGraph
import com.maptanim.app.ui.theme.MapTanimTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MapTanimTheme {
                ProvideSoundManager {
                    AppNavGraph()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        SoundManager.getInstance(applicationContext).pauseAll()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.getInstance(applicationContext).resumeAll()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {

            WindowInsetsControllerCompat(window, window.decorView).apply {

                hide(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )

                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}