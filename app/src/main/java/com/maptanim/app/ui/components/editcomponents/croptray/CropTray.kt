package com.maptanim.app.ui.components.editcomponents.croptray

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CropTray(

    modifier: Modifier = Modifier

) {

    var expanded by rememberSaveable {
        mutableStateOf(true)
    }

    Row(

        modifier = modifier.fillMaxWidth(),

        verticalAlignment = Alignment.Top,

        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {

        Surface(

            modifier = Modifier.size(44.dp),

            shape = RoundedCornerShape(12.dp),

            color = Color.White,

            shadowElevation = 6.dp

        ) {

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(

                        interactionSource = remember {
                            MutableInteractionSource()
                        },

                        indication = null

                    ) {

                        expanded = !expanded

                    },

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    imageVector =
                        if (expanded)
                            Icons.Default.Remove
                        else
                            Icons.Default.Add,

                    contentDescription = null,

                    modifier = Modifier.size(26.dp)

                )

            }

        }

        AnimatedVisibility(

            visible = expanded

        ) {

            FlowRow(

                horizontalArrangement = Arrangement.spacedBy(6.dp),

                maxItemsInEachRow = 10

            ) {

                CropChip("🍅")
                CropChip("🥬")
                CropChip("🌶")
                CropChip("🧄")
                CropChip("🧅")
                CropChip("🫑")
                CropChip("🥒")
                CropChip("🥕")
                CropChip("🌽")
                CropChip("➕")

            }

        }

    }

}

@Composable
private fun CropChip(

    text: String,

    size: Int = 54,

    onClick: () -> Unit = {}

) {

    Surface(

        modifier = Modifier.size(size.dp),

        shape = RoundedCornerShape(12.dp),

        color = Color.White,

        shadowElevation = 4.dp

    ) {

        Box(

            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable(

                    interactionSource = remember {
                        MutableInteractionSource()
                    },

                    indication = null,

                    onClick = onClick

                ),

            contentAlignment = Alignment.Center

        ) {

            Text(

                text = text,

                fontSize = 28.sp

            )

        }

    }

}