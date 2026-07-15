package com.maptanim.app.ui.components.isometric.world.terrain

import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.maptanim.app.R

object TerrainPainter {

    fun getTexture(

        resources: Resources,

        index: Int

    ): ImageBitmap {

        return when (index) {

            1 -> ImageBitmap.imageResource(resources, R.drawable.grass_01)

            2 -> ImageBitmap.imageResource(resources, R.drawable.grass_02)

            3 -> ImageBitmap.imageResource(resources, R.drawable.grass_03)

            4 -> ImageBitmap.imageResource(resources, R.drawable.grass_04)

            else -> ImageBitmap.imageResource(resources, R.drawable.grass_05)

        }

    }

}