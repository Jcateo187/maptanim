package com.maptanim.app.ui.components.isometric.world.farm

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import com.maptanim.app.renderer.AssetLoader

object SoilPainter {

    fun getTexture(
        resources: Resources,
        index: Int,
        context: Context? = null
    ): ImageBitmap {
        return if (context != null) {
            AssetLoader.getSoilTexture(context, index)
        } else {
            AssetLoader.loadFromDrawable(resources, when (index) {
                1 -> com.maptanim.app.R.drawable.soil_01
                2 -> com.maptanim.app.R.drawable.soil_02
                3 -> com.maptanim.app.R.drawable.soil_03
                4 -> com.maptanim.app.R.drawable.soil_04
                5 -> com.maptanim.app.R.drawable.soil_05
                6 -> com.maptanim.app.R.drawable.soil_06
                7 -> com.maptanim.app.R.drawable.soil_07
                else -> com.maptanim.app.R.drawable.soil_08
            })
        }
    }
}