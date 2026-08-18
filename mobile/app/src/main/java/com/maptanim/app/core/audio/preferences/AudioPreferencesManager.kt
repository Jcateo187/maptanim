package com.maptanim.app.core.audio.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists and manages audio settings (Master Mute, BGM Volume, Ambient Volume, SFX Volume).
 */
class AudioPreferencesManager(context: Context?) {

    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isMuted: Boolean
        get() = prefs?.getBoolean(KEY_IS_MUTED, DEFAULT_IS_MUTED) ?: memoryIsMuted
        set(value) {
            memoryIsMuted = value
            prefs?.edit()?.putBoolean(KEY_IS_MUTED, value)?.apply()
        }

    var sfxVolume: Float
        get() = prefs?.getFloat(KEY_SFX_VOLUME, DEFAULT_SFX_VOLUME) ?: memorySfxVolume
        set(value) {
            val clamped = value.coerceIn(0.0f, 1.0f)
            memorySfxVolume = clamped
            prefs?.edit()?.putFloat(KEY_SFX_VOLUME, clamped)?.apply()
        }

    var bgmVolume: Float
        get() = prefs?.getFloat(KEY_BGM_VOLUME, DEFAULT_BGM_VOLUME) ?: memoryBgmVolume
        set(value) {
            val clamped = value.coerceIn(0.0f, 1.0f)
            memoryBgmVolume = clamped
            prefs?.edit()?.putFloat(KEY_BGM_VOLUME, clamped)?.apply()
        }

    var ambientVolume: Float
        get() = prefs?.getFloat(KEY_AMBIENT_VOLUME, DEFAULT_AMBIENT_VOLUME) ?: memoryAmbientVolume
        set(value) {
            val clamped = value.coerceIn(0.0f, 1.0f)
            memoryAmbientVolume = clamped
            prefs?.edit()?.putFloat(KEY_AMBIENT_VOLUME, clamped)?.apply()
        }

    // In-memory fallbacks for unit tests or uninitialized context
    private var memoryIsMuted = DEFAULT_IS_MUTED
    private var memorySfxVolume = DEFAULT_SFX_VOLUME
    private var memoryBgmVolume = DEFAULT_BGM_VOLUME
    private var memoryAmbientVolume = DEFAULT_AMBIENT_VOLUME

    companion object {
        private const val PREFS_NAME = "maptanim_audio_preferences"
        const val KEY_IS_MUTED = "audio_is_muted"
        const val KEY_SFX_VOLUME = "audio_sfx_volume"
        const val KEY_BGM_VOLUME = "audio_bgm_volume"
        const val KEY_AMBIENT_VOLUME = "audio_ambient_volume"

        const val DEFAULT_IS_MUTED = false
        const val DEFAULT_SFX_VOLUME = 1.0f
        const val DEFAULT_BGM_VOLUME = 0.7f
        const val DEFAULT_AMBIENT_VOLUME = 0.5f
    }
}
