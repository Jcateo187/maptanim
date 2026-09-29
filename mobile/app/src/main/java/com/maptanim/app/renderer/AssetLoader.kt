package com.maptanim.app.renderer

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import com.maptanim.app.R
import java.io.IOException


object AssetLoader {

    private val assetCache = HashMap<String, ImageBitmap?>()
    private val drawableCache = HashMap<Int, ImageBitmap>()

   
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

    fun loadFromDrawable(resources: Resources, resId: Int): ImageBitmap {
        return drawableCache.getOrPut(resId) {
            ImageBitmap.imageResource(resources, resId)
        }
    }






    fun getBackgroundTexture(context: Context, fileName: String = "background_scenery/backgound_1.png"): ImageBitmap? {
        val primary = loadFromAssets(context, fileName)
        if (primary != null) return primary

        val fallbacks = listOf(
            "background_scenery/backgound_1.png",
            "backgrounds/farm_bg_45x45.png",
            "backgrounds/farm_bg_v2.png",
            "background_scenery/farm_background_2to1.png"
        )
        for (path in fallbacks) {
            val loaded = loadFromAssets(context, path)
            if (loaded != null) return loaded
        }
        return null
    }

    /** Clear in-memory caches if memory is low */
    fun clearCache() {
        assetCache.clear()
        drawableCache.clear()
    }

    /**
     * Loads an avatar ImageBitmap from an asset path, local file path, or content URI.
     */
    fun loadAvatarImage(context: Context, path: String?): ImageBitmap? {
        if (path.isNullOrBlank()) return null
        return try {
            when {
                path.startsWith("content://") -> {
                    val uri = android.net.Uri.parse(path)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }
                path.startsWith("/") || path.startsWith("file://") -> {
                    val filePath = if (path.startsWith("file://")) {
                        android.net.Uri.parse(path).path ?: path.removePrefix("file://")
                    } else {
                        path
                    }
                    val file = java.io.File(filePath)
                    if (file.exists()) {
                        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                    } else {
                        loadFromAssets(context, path.removePrefix("file:///android_asset/"))
                    }
                }
                else -> loadFromAssets(context, path)
            }
        } catch (_: Exception) {
            null
        }
    }
}

