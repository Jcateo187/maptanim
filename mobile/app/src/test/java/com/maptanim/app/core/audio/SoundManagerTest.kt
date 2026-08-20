package com.maptanim.app.core.audio

import com.maptanim.app.core.audio.preferences.AudioPreferencesManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SoundManagerTest {

    private lateinit var preferencesManager: AudioPreferencesManager
    private lateinit var soundManager: SoundManager

    @Before
    fun setUp() {
        preferencesManager = AudioPreferencesManager(null)
        soundManager = SoundManager.getInstance(null)
    }

    @Test
    fun testPreferencesDefaults() {
        assertFalse("Default isMuted should be false", preferencesManager.isMuted)
        assertEquals("Default sfxVolume should be 1.0f", 1.0f, preferencesManager.sfxVolume, 0.001f)
        assertEquals("Default bgmVolume should be 0.7f", 0.7f, preferencesManager.bgmVolume, 0.001f)
        assertEquals("Default ambientVolume should be 0.5f", 0.5f, preferencesManager.ambientVolume, 0.001f)
    }

    @Test
    fun testVolumeClamping() {
        preferencesManager.sfxVolume = 1.5f
        assertEquals("SFX volume above 1.0 should clamp to 1.0", 1.0f, preferencesManager.sfxVolume, 0.001f)

        preferencesManager.sfxVolume = -0.5f
        assertEquals("SFX volume below 0.0 should clamp to 0.0", 0.0f, preferencesManager.sfxVolume, 0.001f)

        preferencesManager.bgmVolume = 2.0f
        assertEquals("BGM volume above 1.0 should clamp to 1.0", 1.0f, preferencesManager.bgmVolume, 0.001f)

        preferencesManager.ambientVolume = -1.0f
        assertEquals("Ambient volume below 0.0 should clamp to 0.0", 0.0f, preferencesManager.ambientVolume, 0.001f)
    }

    @Test
    fun testMuteToggle() {
        soundManager.isMuted = true
        assertTrue("isMuted should be true after setting to true", soundManager.isMuted)

        soundManager.isMuted = false
        assertFalse("isMuted should be false after setting to false", soundManager.isMuted)
    }

    @Test
    fun testSoundEffectEnumValues() {
        val effects = SoundEffect.values()
        assertEquals(10, effects.size)
        assertTrue(effects.contains(SoundEffect.TAP_BUTTON))
        assertTrue(effects.contains(SoundEffect.PLOT_PLACE))
        assertTrue(effects.contains(SoundEffect.PLOT_SELECT))
        assertTrue(effects.contains(SoundEffect.PLOT_DELETE))
        assertTrue(effects.contains(SoundEffect.PLANT_SEED))
        assertTrue(effects.contains(SoundEffect.WATER_SPRAY))
        assertTrue(effects.contains(SoundEffect.FERTILIZE_APPLY))
        assertTrue(effects.contains(SoundEffect.HARVEST_SUCCESS))
        assertTrue(effects.contains(SoundEffect.PEST_ALERT))
        assertTrue(effects.contains(SoundEffect.SAVE_SUCCESS))
    }

    @Test
    fun testBackgroundTrackEnumValues() {
        val tracks = BackgroundTrack.values()
        assertEquals(4, tracks.size)
        assertTrue(tracks.contains(BackgroundTrack.PEACEFUL_FARM))
        assertTrue(tracks.contains(BackgroundTrack.EDITOR_FOCUS))
        assertTrue(tracks.contains(BackgroundTrack.APP_LAUNCH))
        assertTrue(tracks.contains(BackgroundTrack.NONE))
    }

    @Test
    fun testAmbientSoundEnumValues() {
        val ambients = AmbientSound.values()
        assertEquals(4, ambients.size)
        assertTrue(ambients.contains(AmbientSound.DAY_BIRDS))
        assertTrue(ambients.contains(AmbientSound.RAIN_WET_SEASON))
        assertTrue(ambients.contains(AmbientSound.NIGHT_CRICKETS))
        assertTrue(ambients.contains(AmbientSound.NONE))
    }
}
