package com.maptanim.app

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.maptanim.app.core.audio.ProvideSoundManager
import com.maptanim.app.core.audio.SoundManager
import com.maptanim.app.core.notification.NotificationHelper
import com.maptanim.app.core.orientation.OrientationHelper
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.navigation.AppNavGraph
import com.maptanim.app.ui.components.orientation.RotationSuggestionOverlay
import com.maptanim.app.ui.theme.MapTanimTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var currentTargetOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    companion object {
        // Cooldown when user manually taps in-app rotate button
        var manualRotationLockUntil: Long = 0L
    }

    private val sensorListener = object : SensorEventListener {
        private var candidateOrientation: Int? = null
        private var candidateStartTime: Long = 0L
        private var lastTriggeredPhysicalOrientation: Int? = null

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

            val now = System.currentTimeMillis()
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // If phone is flat on a table (Z is dominant gravity, X & Y near zero), ignore
            if (abs(z) > 8.5f && abs(x) < 2.0f && abs(y) < 2.0f) {
                candidateOrientation = null
                return
            }

            val absX = abs(x)
            val absY = abs(y)

            // Direct gravity ratio between X and Y axes:
            // Reliably detects landscape even when phone is tilted back at 30°-60° angles
            val detected = when {
                absX > absY + 1.8f && absX > 2.5f -> {
                    if (x > 0) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    else ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
                absY > absX + 1.8f && absY > 2.5f -> {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                else -> null
            }

            if (detected != null) {
                if (candidateOrientation == detected) {
                    if (now - candidateStartTime > 250L) {
                        val isCurrentlyLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                        val isDetectedLandscape = detected == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
                                detected == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

                        if (isDetectedLandscape && !isCurrentlyLandscape) {
                            // Physically turned to landscape while screen is in portrait
                            if (lastTriggeredPhysicalOrientation != detected) {
                                lastTriggeredPhysicalOrientation = detected
                                OrientationHelper.showSuggestion(
                                    targetOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                                    label = "Rotate to Landscape"
                                )
                            }
                        } else if (!isDetectedLandscape && isCurrentlyLandscape) {
                            // Physically turned to portrait while screen is in landscape
                            if (lastTriggeredPhysicalOrientation != detected) {
                                lastTriggeredPhysicalOrientation = detected
                                OrientationHelper.showSuggestion(
                                    targetOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                                    label = "Rotate to Portrait"
                                )
                            }
                        } else {
                            // Physical orientation matches current display: dismiss suggestion
                            if (lastTriggeredPhysicalOrientation != detected) {
                                lastTriggeredPhysicalOrientation = detected
                                OrientationHelper.dismissSuggestion()
                            }
                        }
                    }
                } else {
                    candidateOrientation = detected
                    candidateStartTime = now
                }
            } else {
                candidateOrientation = null
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle notification permission result gracefully
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        // Initialize farm notification channels
        NotificationHelper.createNotificationChannels(applicationContext)

        // Request notification permission on Android 13+ (API 33+) if not yet granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationHelper.hasNotificationPermission(this)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Setup raw accelerometer sensor so phone directly rotates even if system rotate-lock is on
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        RepositoryProvider.initialize(applicationContext)

        setContent {
            MapTanimTheme {
                ProvideSoundManager {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavGraph()
                        RotationSuggestionOverlay()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        accelerometer?.let {
            sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(sensorListener)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorListener)
        SoundManager.getInstance(applicationContext).pauseAll()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        SoundManager.getInstance(applicationContext).resumeAll()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
}