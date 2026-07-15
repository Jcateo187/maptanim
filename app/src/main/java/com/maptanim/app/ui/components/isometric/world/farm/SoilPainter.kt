package com.maptanim.app.ui.components.isometric.world.farm

import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.maptanim.app.R

object SoilPainter {

    private val cache = HashMap<Int, ImageBitmap>()

    fun getTexture(

        resources: Resources,

        index: Int

    ): ImageBitmap {

        return cache.getOrPut(index) {

            ImageBitmap.imageResource(

                resources,

                when (index) {

                    1 -> R.drawable.soil_01
                    2 -> R.drawable.soil_02
                    3 -> R.drawable.soil_03
                    4 -> R.drawable.soil_04
                    5 -> R.drawable.soil_05
                    6 -> R.drawable.soil_06
                    7 -> R.drawable.soil_07
                    else -> R.drawable.soil_08

                }

            )

        }

    }

}