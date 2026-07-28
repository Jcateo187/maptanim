package com.maptanim.app.ui.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextPrimary
import com.maptanim.app.ui.theme.White


@Composable
fun ProfileAvatar(

    onClick: () -> Unit = {}

) {

    Surface(

        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),

        shape = CircleShape,

        shadowElevation = 6.dp,

        color = ForestGreen

    ) {

        Box(

            modifier = Modifier
                .background(ForestGreen)
                .border(
                    width = 3.dp,
                    color = White,
                    shape = CircleShape
                ),

            contentAlignment = Alignment.Center

        ) {

            Icon(

                imageVector = Icons.Default.Person,

                contentDescription = "Profile",

                tint = TextPrimary,

                modifier = Modifier.size(34.dp)

            )

        }

    }

}