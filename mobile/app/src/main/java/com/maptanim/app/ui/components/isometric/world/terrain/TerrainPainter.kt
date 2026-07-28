package com.maptanim.app.ui.components.isometric.world.terrain

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import com.maptanim.app.renderer.AssetLoader

object TerrainPainter {

    fun getTexture(
        resources: Resources,
        index: Int,
        context: Context? = null
    ): ImageBitmap {
        return if (context != null) {
            AssetLoader.getGrassTexture(context, index)
        } else {
            AssetLoader.loadFromDrawable(resources, when (index) {
                1 -> com.maptanim.app.R.drawable.grass_01
                2 -> com.maptanim.app.R.drawable.grass_02
                3 -> com.maptanim.app.R.drawable.grass_03
                4 -> com.maptanim.app.R.drawable.grass_04
                else -> com.maptanim.app.R.drawable.grass_05
            })
        }
    }
}