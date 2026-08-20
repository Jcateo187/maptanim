# 38. Audio & Sound Assets Planning

> 📌 **Navigation**: [◀ 37. System Specifications & Scope Refinements](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [39. Crop View Interaction & Variety Simulation ▶](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)

---
> **Document Version**: 1.0  
> **Target Platform**: Android (Kotlin + Jetpack Compose)  
> **Status**: Approved Audio Specification Baseline

---

## 1. Executive Overview & Sound Design Philosophy

MapTanim's audio system is designed to complement the visual aesthetics of smallholder vegetable farming in the Philippines. Grounded in a **peaceful, rural Philippine agrarian theme**, the audio design creates an immersive, relaxing atmosphere during farm mapping, crop monitoring, and daily task management.

### Key Principles
1. **Acoustic & Organic Aesthetic**: Emphasizes natural soundscapes (gentle rural bird calls, wind through foliage, rain showers) and organic acoustic instruments (fingerpicked acoustic guitar, bamboo flute, light wooden percussion).
2. **Non-Intrusive User Experience**: Audio levels are carefully balanced so background music and ambient sounds never distract or cause listener fatigue during extended planning sessions in outdoor field environments.
3. **High Performance & Battery Optimization**: 
   - Audio files are compressed using **Ogg Vorbis (`.ogg`)** to minimize APK size and memory usage.
   - Low-latency sound effects (SFX) are pre-loaded via Android **`SoundPool`** for instant playback upon user interaction.
   - Background music and environmental ambiance are streamed via **`MediaPlayer`** with smooth 1-second cross-fades.

---

## 2. Complete Audio Asset Catalog

### 2.1 Background Music (BGM)
Looped, high-fidelity acoustic tracks for main application modes.

| File Name | Asset Name | Description & Mood | Format / Spec | Usage / Screen |
|---|---|---|---|---|
| `bgm_peaceful_farm.ogg` | Peaceful Farm Theme | Relaxing fingerpicked acoustic guitar with subtle bamboo flute melody; warm, rural Philippine morning vibe. | Ogg Vorbis, 44.1 kHz, 128 kbps, Looped | View Mode (Home Dashboard), Farms Screen, Calendar Screen |
| `bgm_editor_focus.ogg` | Focus Mapping Theme | Soft, ambient acoustic guitar rhythm without prominent melodies to encourage focus during farm layout design. | Ogg Vorbis, 44.1 kHz, 128 kbps, Looped | Edit Mode (`FarmEditorScreen`) |

---

### 2.2 Ambient Environment Audio (BGA)
Looping environmental nature soundscapes that respond to seasonal context and day/night cycles.

| File Name | Asset Name | Description & Elements | Format / Spec | Trigger / Condition |
|---|---|---|---|---|
| `ambient_day_birds.ogg` | Rural Day Ambiance | Gentle Philippine rural bird calls (maya, cuckoo), soft wind rustling through foliage. | Ogg Vorbis, 44.1 kHz, 96 kbps, Looped | Daytime (06:00 – 17:59) in View Mode |
| `ambient_rain_wet_season.ogg` | Wet Season Rain | Soft monsoon rainfall dripping on vegetable leaves with distant, gentle thunder roll. | Ogg Vorbis, 44.1 kHz, 96 kbps, Looped | Active Wet Season / Heavy Rain condition |
| `ambient_night_crickets.ogg` | Night Evening Ambiance | Soft evening crickets chirping and light night breeze. | Ogg Vorbis, 44.1 kHz, 96 kbps, Looped | Evening (18:00 – 05:59) in View Mode |

---

### 2.3 User Interaction Sound Effects (SFX)
Short, crisp audio cues providing tactile feedback for user actions. All SFX are under 1.5 seconds and preloaded in `SoundPool`.

| File Name | Sound Effect | Audio Description | Format / Spec | Trigger Action |
|---|---|---|---|---|
| `sfx_tap_button.ogg` | Button Tap | Soft, clean wooden click | Ogg, 44.1 kHz, Mono | Tapping buttons, navigation tabs, dialog actions |
| `sfx_plot_place.ogg` | Plot Placement | Satisfying earthy thud / soil pat | Ogg, 44.1 kHz, Mono | Dropping a `CropPlot` onto farm grid in Edit Mode |
| `sfx_plot_select.ogg` | Plot Select | Crisp wooden selection chime | Ogg, 44.1 kHz, Mono | Tapping to select a plot or object on the canvas |
| `sfx_plot_delete.ogg` | Plot Delete / Clear | Quick rustling brush / sweep sound | Ogg, 44.1 kHz, Mono | Deleting a plot, zone, or item |
| `sfx_plant_seed.ogg` | Planting Crop | Soft soil digging and seed drop sound | Ogg, 44.1 kHz, Mono | Assigning a crop to a plot zone in `CropTray` |
| `sfx_water_spray.ogg` | Watering Spray | Refreshing water spray / droplet mist | Ogg, 44.1 kHz, Mono | Checking off a **WATER** task in Today's Tasks |
| `sfx_fertilize_apply.ogg` | Fertilize Scatter | Organic granule scatter / soil amendment sound | Ogg, 44.1 kHz, Mono | Checking off a **FERTILIZE** task |
| `sfx_harvest_success.ogg` | Harvest Chime | Delightful acoustic chime + basket rustle | Ogg, 44.1 kHz, Stereo | Confirming a **HARVEST** record yield |
| `sfx_pest_alert.ogg` | Pest Advisory Warning | Soft, cautionary acoustic bell chime | Ogg, 44.1 kHz, Mono | Tapping a **PEST_ALERT** warning pin or notification |
| `sfx_save_success.ogg` | Save Confirmation | Pleasant major acoustic chord | Ogg, 44.1 kHz, Stereo | Tapping **Save** and completing farm layout save |

---

## 3. Android Audio System Architecture

The audio system is implemented as a clean, decoupled Hilt singleton manager (`SoundManager.kt`) exposed to ViewModels and Composables.

```
┌──────────────────────────────────────────────────────────────────────────┐
│                            PRESENTATION LAYER                            │
│ Compose UI Screens (HomeScreen, EditScreen, SettingsScreen)             │
│ ViewModels invoke SoundManager.playSfx() or SoundManager.playBgm()       │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
┌────────────────────────────────────▼─────────────────────────────────────┐
│                           AUDIO ENGINE LAYER                             │
│                                                                          │
│  ┌─────────────────────────────────┐   ┌──────────────────────────────┐  │
│  │ SoundPool Pipeline              │   │ MediaPlayer Pipeline         │  │
│  │ • Preloads all 10 short SFX     │   │ • Streams BGM & Nature Loops │  │
│  │ • Low-latency playback (<50ms)  │   │ • 1-second cross-fade        │  │
│  │ • Up to 6 simultaneous streams  │   │ • Background pause/resume    │  │
│  └────────────────┬────────────────┘   └──────────────┬───────────────┘  │
│                   │                                   │                  │
│                   ▼                                   ▼                  │
│   EncryptedPreferencesManager (Master & Channel Volume Controls)         │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Audio Manager Implementation Specification (`SoundManager.kt`)

```kotlin
package com.maptanim.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.maptanim.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class SoundEffect {
    TAP_BUTTON, PLOT_PLACE, PLOT_SELECT, PLOT_DELETE,
    PLANT_SEED, WATER_SPRAY, FERTILIZE_APPLY, HARVEST_SUCCESS,
    PEST_ALERT, SAVE_SUCCESS
}

enum class BackgroundTrack {
    PEACEFUL_FARM, EDITOR_FOCUS, NONE
}

enum class AmbientSound {
    DAY_BIRDS, RAIN_WET_SEASON, NIGHT_CRICKETS, NONE
}

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<SoundEffect, Int>()
    private var bgmPlayer: MediaPlayer? = null
    private var ambientPlayer: MediaPlayer? = null

    var isMuted: Boolean = false
    var sfxVolume: Float = 1.0f
    var bgmVolume: Float = 0.7f
    var ambientVolume: Float = 0.5f

    init {
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

    private fun loadSoundPool() {
        soundMap[SoundEffect.TAP_BUTTON] = soundPool.load(context, R.raw.sfx_tap_button, 1)
        soundMap[SoundEffect.PLOT_PLACE] = soundPool.load(context, R.raw.sfx_plot_place, 1)
        soundMap[SoundEffect.PLOT_SELECT] = soundPool.load(context, R.raw.sfx_plot_select, 1)
        soundMap[SoundEffect.PLOT_DELETE] = soundPool.load(context, R.raw.sfx_plot_delete, 1)
        soundMap[SoundEffect.PLANT_SEED] = soundPool.load(context, R.raw.sfx_plant_seed, 1)
        soundMap[SoundEffect.WATER_SPRAY] = soundPool.load(context, R.raw.sfx_water_spray, 1)
        soundMap[SoundEffect.FERTILIZE_APPLY] = soundPool.load(context, R.raw.sfx_fertilize_apply, 1)
        soundMap[SoundEffect.HARVEST_SUCCESS] = soundPool.load(context, R.raw.sfx_harvest_success, 1)
        soundMap[SoundEffect.PEST_ALERT] = soundPool.load(context, R.raw.sfx_pest_alert, 1)
        soundMap[SoundEffect.SAVE_SUCCESS] = soundPool.load(context, R.raw.sfx_save_success, 1)
    }

    fun playSfx(effect: SoundEffect) {
        if (isMuted) return
        soundMap[effect]?.let { soundId ->
            soundPool.play(soundId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        }
    }

    fun playBgm(track: BackgroundTrack) {
        if (isMuted || track == BackgroundTrack.NONE) {
            stopBgm()
            return
        }
        val resId = when (track) {
            BackgroundTrack.PEACEFUL_FARM -> R.raw.bgm_peaceful_farm
            BackgroundTrack.EDITOR_FOCUS -> R.raw.bgm_editor_focus
            else -> return
        }
        stopBgm()
        bgmPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            setVolume(bgmVolume, bgmVolume)
            start()
        }
    }

    fun stopBgm() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
    }

    fun release() {
        soundPool.release()
        stopBgm()
        ambientPlayer?.release()
        ambientPlayer = null
    }
}
```

---

## 4. Resource File Placement & Directory Structure

All audio files reside in the native Android resources raw folder:

```
mobile/app/src/main/res/raw/
├── bgm_editor_focus.ogg
├── bgm_peaceful_farm.ogg
├── ambient_day_birds.ogg
├── ambient_night_crickets.ogg
├── ambient_rain_wet_season.ogg
├── sfx_fertilize_apply.ogg
├── sfx_harvest_success.ogg
├── sfx_pest_alert.ogg
├── sfx_plant_seed.ogg
├── sfx_plot_delete.ogg
├── sfx_plot_place.ogg
├── sfx_plot_select.ogg
├── sfx_save_success.ogg
├── sfx_tap_button.ogg
└── sfx_water_spray.ogg
```

---

## 5. User Audio Settings & Volume Controls

The MapTanim Settings Screen provides independent audio control sliders, persisted across app restarts via `EncryptedPreferencesManager`:

1. **Master Mute Switch**: Toggles all app audio ON/OFF.
2. **Music Volume Slider (0% – 100%)**: Adjusts `bgmVolume` (default: 70%).
3. **Ambient Nature Slider (0% – 100%)**: Adjusts `ambientVolume` (default: 50%).
4. **Sound Effects Slider (0% – 100%)**: Adjusts `sfxVolume` (default: 100%).

---

## 6. Licensing & Asset Source Compliance

All sound assets included in MapTanim adhere strictly to open-source and royalty-free licensing:
- **Creative Commons Zero (CC0)** / **Public Domain**: Audio recordings sourced from verified public domain repositories (e.g., FreeSound.org CC0, OpenGameArt.org).
- **Self-Created / Custom Rendered**: Acoustic guitar motifs and custom UI sound effects synthesized and exported specifically for the MapTanim project.
- **No Trademarked Audio**: Zero commercial or copyrighted audio clips are used.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
