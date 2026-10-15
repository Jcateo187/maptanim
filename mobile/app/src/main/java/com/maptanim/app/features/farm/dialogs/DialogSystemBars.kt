package com.maptanim.app.features.farm.dialogs

import android.graphics.Color
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Configures the hosting Dialog's Android [android.view.Window] for full-screen edge-to-edge
 * with dark (visible) status bar and navigation bar icons on light theme backgrounds.
 */
@Composable
fun ConfigureDialogSystemBars(
    isLightStatusBar: Boolean = true,
    isLightNavigationBar: Boolean = true
) {
    val view = LocalView.current
    DisposableEffect(view, isLightStatusBar, isLightNavigationBar) {
        var p: Any? = view
        while (p != null && p !is DialogWindowProvider) {
            p = (p as? View)?.parent
        }
        (p as? DialogWindowProvider)?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                isAppearanceLightStatusBars = isLightStatusBar
                isAppearanceLightNavigationBars = isLightNavigationBar
            }
        }
        onDispose {}
    }
}
