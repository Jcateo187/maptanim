package com.maptanim.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.maptanim.app.R
import com.maptanim.app.core.audio.preferences.AudioPreferencesManager

enum class SoundEffect {
    TAP_BUTTON, PLOT_PLACE, PLOT_SELECT, PLOT_DELETE,
    PLANT_SEED, WATER_SPRAY, FERTILIZE_APPLY, HARVEST_SUCCESS,
    PEST_ALERT, SAVE_SUCCESS
}

enum class BackgroundTrack {
    PEACEFUL_FARM, EDITOR_FOCUS, APP_LAUNCH, NONE
}

enum class AmbientSound {
    DAY_BIRDS, RAIN_WET_SEASON, NIGHT_CRICKETS, NONE
}

class SoundManager private constructor(
    private val context: Context?
) {
    private val preferencesManager = AudioPreferencesManager(context)

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundEffect, Int>()
    
    private var bgmPlayer: MediaPlayer? = null
    private var ambientPlayer: MediaPlayer? = null

    private var currentBgmTrack: BackgroundTrack = BackgroundTrack.NONE
    private var currentAmbientSound: AmbientSound = AmbientSound.NONE

    var isMuted: Boolean
        get() = preferencesManager.isMuted
        set(value) {
            preferencesManager.isMuted = value
            if (value) {
                pauseAll()
            } else {
                resumeAll()
            }
        }

    var sfxVolume: Float
        get() = preferencesManager.sfxVolume
        set(value) {
            preferencesManager.sfxVolume = value
        }

    var bgmVolume: Float
        get() = preferencesManager.bgmVolume
        set(value) {
            preferencesManager.bgmVolume = value
            bgmPlayer?.let {
                if (it.isPlaying) {
                    val vol = if (isMuted) 0f else preferencesManager.bgmVolume
                    it.setVolume(vol, vol)
                }
            }
        }

    var ambientVolume: Float
        get() = preferencesManager.ambientVolume
        set(value) {
            preferencesManager.ambientVolume = value
            ambientPlayer?.let {
                if (it.isPlaying) {
                    val vol = if (isMuted) 0f else preferencesManager.ambientVolume
                    it.setVolume(vol, vol)
                }
            }
        }

    init {
        if (context != null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build()

            loadSoundPool()
        }
    }

    private fun loadSoundPool() {
        val ctx = context ?: return
        val sp = soundPool ?: return

        try {
            soundMap[SoundEffect.TAP_BUTTON] = sp.load(ctx, R.raw.sfx_tap_button, 1)
            soundMap[SoundEffect.PLOT_PLACE] = sp.load(ctx, R.raw.sfx_plot_place, 1)
            soundMap[SoundEffect.PLOT_SELECT] = sp.load(ctx, R.raw.sfx_plot_select, 1)
            soundMap[SoundEffect.PLOT_DELETE] = sp.load(ctx, R.raw.sfx_plot_delete, 1)
            soundMap[SoundEffect.PLANT_SEED] = sp.load(ctx, R.raw.sfx_plant_seed, 1)
            soundMap[SoundEffect.WATER_SPRAY] = sp.load(ctx, R.raw.sfx_water_spray, 1)
            soundMap[SoundEffect.FERTILIZE_APPLY] = sp.load(ctx, R.raw.sfx_fertilize_apply, 1)
            soundMap[SoundEffect.HARVEST_SUCCESS] = sp.load(ctx, R.raw.sfx_harvest_success, 1)
            soundMap[SoundEffect.PEST_ALERT] = sp.load(ctx, R.raw.sfx_pest_alert, 1)
            soundMap[SoundEffect.SAVE_SUCCESS] = sp.load(ctx, R.raw.sfx_save_success, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSfx(effect: SoundEffect) {
        if (isMuted) return
        val soundId = soundMap[effect] ?: return
        val vol = sfxVolume
        soundPool?.play(soundId, vol, vol, 1, 0, 1.0f)
    }

    fun playBgm(track: BackgroundTrack) {
        if (track == currentBgmTrack && bgmPlayer?.isPlaying == true) return

        currentBgmTrack = track
        stopBgm()

        if (isMuted || track == BackgroundTrack.NONE || context == null) return

        val resId = when (track) {
            BackgroundTrack.PEACEFUL_FARM -> R.raw.bgm_peaceful_farm
            BackgroundTrack.EDITOR_FOCUS -> R.raw.bgm_editor_focus
            BackgroundTrack.APP_LAUNCH -> R.raw.bgm_app_launch
            BackgroundTrack.NONE -> return
        }

        try {
            bgmPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                val vol = bgmVolume
                setVolume(vol, vol)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopBgm() {
        try {
            bgmPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bgmPlayer = null
        }
    }

    fun playAmbient(ambient: AmbientSound) {
        if (ambient == currentAmbientSound && ambientPlayer?.isPlaying == true) return

        currentAmbientSound = ambient
        stopAmbient()

        if (isMuted || ambient == AmbientSound.NONE || context == null) return

        val resId = when (ambient) {
            AmbientSound.DAY_BIRDS -> R.raw.ambient_day_birds
            AmbientSound.RAIN_WET_SEASON -> R.raw.ambient_rain_wet_season
            AmbientSound.NIGHT_CRICKETS -> R.raw.ambient_night_crickets
            AmbientSound.NONE -> return
        }

        try {
            ambientPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                val vol = ambientVolume
                setVolume(vol, vol)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAmbient() {
        try {
            ambientPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ambientPlayer = null
        }
    }

    fun pauseAll() {
        try {
            if (bgmPlayer?.isPlaying == true) {
                bgmPlayer?.pause()
            }
            if (ambientPlayer?.isPlaying == true) {
                ambientPlayer?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumeAll() {
        if (isMuted) return
        try {
            bgmPlayer?.let {
                if (!it.isPlaying && currentBgmTrack != BackgroundTrack.NONE) {
                    it.start()
                }
            } ?: run {
                if (currentBgmTrack != BackgroundTrack.NONE) {
                    playBgm(currentBgmTrack)
                }
            }

            ambientPlayer?.let {
                if (!it.isPlaying && currentAmbientSound != AmbientSound.NONE) {
                    it.start()
                }
            } ?: run {
                if (currentAmbientSound != AmbientSound.NONE) {
                    playAmbient(currentAmbientSound)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        stopBgm()
        stopAmbient()
        instance = null
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context?): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context?.applicationContext ?: context).also { instance = it }
            }
        }
    }
}
