package com.maptanim.app.features.farm.renderer.loader

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import java.io.IOException

/**
 * AssetLoader — Lightweight loader for application UI assets and avatars.
 * Obsolete procedural textures, background scenery, and sprite generator pipelines removed.
 */
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
