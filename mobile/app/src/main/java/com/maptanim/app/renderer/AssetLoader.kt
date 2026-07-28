package com.maptanim.app.renderer

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import com.maptanim.app.R
import java.io.IOException

/**
 * High-performance cached asset loader for MapTanim renderer.
 *
 * Supports loading isometric PNG assets from:
 * 1. `assets/` subfolders (`assets/grass/`, `assets/soil/`, `assets/fences/`, `assets/tiles/`, `assets/trees_and_rocks/`, `assets/crops/`, `assets/structures/`)
 * 2. `res/drawable/` fallback
 */
object AssetLoader {

    private val assetCache = HashMap<String, ImageBitmap?>()
    private val drawableCache = HashMap<Int, ImageBitmap>()

    /**
     * Load an ImageBitmap from the `assets/` directory by relative path.
     * Example paths:
     *   - "grass/grass_01.png"
     *   - "soil/soil_01.png"
     *   - "fences/fence_left.png"
     *   - "tiles/path_straight_h.png"
     *   - "trees_and_rocks/tree_mango.png"
     *   - "crops/crop_tomato_4.png"
     */
    fun loadFromAssets(context: Context, path: String): ImageBitmap? {
        if (assetCache.containsKey(path)) {
            return assetCache[path]
        }

        return try {
            context.assets.open(path).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val imageBitmap = bitmap?.asImageBitmap()
                assetCache[path] = imageBitmap
                imageBitmap
            }
        } catch (e: IOException) {
            assetCache[path] = null
            null
        }
    }

    /**
     * Load an ImageBitmap from `res/drawable/` with caching.
     */
    fun loadFromDrawable(resources: Resources, resId: Int): ImageBitmap {
        return drawableCache.getOrPut(resId) {
            ImageBitmap.imageResource(resources, resId)
        }
    }

    /**
     * Helper to load grass textures from `assets/grass/` with fallback to `res/drawable/grass_0X`.
     */
    fun getGrassTexture(context: Context, index: Int): ImageBitmap {
        val fileName = String.format("grass/grass_%02d.png", index)
        val fromAssets = loadFromAssets(context, fileName)
        if (fromAssets != null) return fromAssets

        val resId = when (index) {
            1 -> R.drawable.grass_01
            2 -> R.drawable.grass_02
            3 -> R.drawable.grass_03
            4 -> R.drawable.grass_04
            else -> R.drawable.grass_05
        }
        return loadFromDrawable(context.resources, resId)
    }

    /**
     * Helper to load soil textures from `assets/soil/` with fallback to `res/drawable/soil_0X`.
     */
    fun getSoilTexture(context: Context, index: Int): ImageBitmap {
        val fileName = String.format("soil/soil_%02d.png", index)
        val fromAssets = loadFromAssets(context, fileName)
        if (fromAssets != null) return fromAssets

        val resId = when (index) {
            1 -> R.drawable.soil_01
            2 -> R.drawable.soil_02
            3 -> R.drawable.soil_03
            4 -> R.drawable.soil_04
            5 -> R.drawable.soil_05
            6 -> R.drawable.soil_06
            7 -> R.drawable.soil_07
            else -> R.drawable.soil_08
        }
        return loadFromDrawable(context.resources, resId)
    }

    /** Clear in-memory caches if memory is low */
    fun clearCache() {
        assetCache.clear()
        drawableCache.clear()
    }
}
