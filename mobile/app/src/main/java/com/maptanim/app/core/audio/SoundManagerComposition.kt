package com.maptanim.app.core.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * CompositionLocal key for accessing [SoundManager] inside UI composables.
 */
val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    error("No SoundManager provided. Wrap your UI in ProvideSoundManager.")
}

/**
 * CompositionLocal provider wrapper to make SoundManager accessible throughout the Compose hierarchy.
 */
@Composable
fun ProvideSoundManager(
    soundManager: SoundManager = rememberSoundManager(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSoundManager provides soundManager) {
        content()
    }
}

/**
 * Creates and remembers a [SoundManager] instance bound to the current Context.
 */
@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        SoundManager.getInstance(context)
    }
}

/**
 * Helper effect to play background music when a Screen enters composition and stop when leaving.
 */
@Composable
fun TrackBgmEffect(track: BackgroundTrack) {
    val soundManager = LocalSoundManager.current
    DisposableEffect(track) {
        soundManager.playBgm(track)
        onDispose {
            soundManager.stopBgm()
        }
    }
}

/**
 * Helper effect to play ambient environment audio when a Screen enters composition and stop when leaving.
 */
@Composable
fun TrackAmbientEffect(ambient: AmbientSound) {
    val soundManager = LocalSoundManager.current
    DisposableEffect(ambient) {
        soundManager.playAmbient(ambient)
        onDispose {
            soundManager.stopAmbient()
        }
    }
}
