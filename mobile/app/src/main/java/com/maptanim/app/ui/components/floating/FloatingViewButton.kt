package com.maptanim.app.ui.components.floating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextDark
import com.maptanim.app.ui.theme.White

@Composable
fun FloatingViewButton(

    onClick: () -> Unit

) {

    Surface(

        modifier = Modifier
            .width(76.dp)
            .height(86.dp),

        shape = RoundedCornerShape(18.dp),

        shadowElevation = 8.dp,

        color = White

    ) {

        Column(

            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(White)
                .clickable(

                    interactionSource = remember {
                        MutableInteractionSource()
                    },

                    indication = null,

                    onClick = onClick

                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center

        ) {

            Icon(

                imageVector = Icons.Default.Visibility,

                contentDescription = "View",

                modifier = Modifier.size(28.dp),

                tint = ForestGreen

            )

            Text(

                text = "View",

                style = MaterialTheme.typography.bodySmall,

                color = TextDark

            )

        }

    }

}