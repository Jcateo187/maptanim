package com.maptanim.app.core.orientation

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

data class RotationSuggestion(
    val targetOrientation: Int,
    val label: String,
    val timestamp: Long = System.currentTimeMillis()
)

object OrientationHelper {

    private val _currentSuggestion = mutableStateOf<RotationSuggestion?>(null)
    val currentSuggestion: State<RotationSuggestion?> = _currentSuggestion

    /**
     * Checks if the Android system Auto-Rotate setting is enabled.
     * When disabled, the phone is in portrait lock mode.
     */
    fun isSystemAutoRotateOn(context: Context): Boolean {
        return try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                0
            ) == 1
        } catch (_: Exception) {
            false
        }
    }

    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * Triggers the 2-second in-app rotation suggestion overlay in the bottom left.
     */
    fun showSuggestion(targetOrientation: Int, label: String) {
        _currentSuggestion.value = RotationSuggestion(targetOrientation, label, System.currentTimeMillis())
    }

    fun dismissSuggestion() {
        _currentSuggestion.value = null
    }

    fun applyRotation(activity: Activity?) {
        if (activity == null) return
        val isCurrentLandscape = isLandscape(activity)
        if (isCurrentLandscape) {
            setPortrait(activity)
        } else {
            setLandscape(activity)
        }
    }

    fun setPortrait(activity: Activity?) {
        if (activity == null) return
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        dismissSuggestion()
    }

    fun setLandscape(activity: Activity?) {
        if (activity == null) return
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        dismissSuggestion()
    }
}
