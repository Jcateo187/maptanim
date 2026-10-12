package com.maptanim.app.features.shared.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maptanim.app.core.orientation.OrientationHelper
import kotlinx.coroutines.delay

/**
 * 2-Second Rotation Suggestion Icon Overlay
 *
 * Positions the floating circular rotate icon strictly on:
 * - Left side (bottom-left) when in Portrait mode
 * - Right side (bottom-right) when in Landscape mode
 *
 * Programmatically overrides screen orientation on tap,
 * respecting phone's rotate-lock until the user taps the button.
 * Automatically disappears after 2 seconds if not tapped.
 */
@Composable
fun RotationSuggestionOverlay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val currentSuggestion = OrientationHelper.currentSuggestion.value
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val buttonAlignment = if (isLandscape) {
        Alignment.BottomEnd // Right side in landscape
    } else {
        Alignment.BottomStart // Left side in portrait
    }

    val buttonPadding = if (isLandscape) {
        Modifier
            .navigationBarsPadding()
            .padding(end = 20.dp, bottom = 20.dp)
    } else {
        Modifier
            .navigationBarsPadding()
            .padding(start = 20.dp, bottom = 20.dp)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(9999f),
        contentAlignment = buttonAlignment
    ) {
        AnimatedVisibility(
            visible = currentSuggestion != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = buttonPadding
        ) {
            if (currentSuggestion != null) {
                LaunchedEffect(currentSuggestion.timestamp) {
                    delay(2000L)
                    OrientationHelper.dismissSuggestion()
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xF2101912))
                        .border(1.5.dp, Color(0xFF4CAF50), CircleShape)
                        .clickable {
                            OrientationHelper.applyRotation(activity)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLandscape)
                            Icons.Default.ScreenLockPortrait
                        else
                            Icons.Default.ScreenRotation,
                        contentDescription = currentSuggestion.label,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
