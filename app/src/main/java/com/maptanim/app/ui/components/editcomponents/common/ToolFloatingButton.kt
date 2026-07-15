package com.maptanim.app.ui.components.editcomponents.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ToolFloatingButton(

    icon: ImageVector,

    label: String,

    modifier: Modifier = Modifier,

    onClick: () -> Unit

) {

    Column(

        modifier = modifier
            .size(width = 64.dp, height = 72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Icon(

            imageVector = icon,

            contentDescription = label,

            modifier = Modifier.size(32.dp),

            tint = MaterialTheme.colorScheme.onBackground

        )

        Text(

            text = label,

            style = MaterialTheme.typography.labelSmall,

            color = MaterialTheme.colorScheme.onBackground,

            textAlign = TextAlign.Center

        )

    }

}