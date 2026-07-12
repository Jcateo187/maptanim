package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.editcomponents.floating.GuideFloatingButton
import com.maptanim.app.ui.components.editcomponents.floating.SaveFloatingButton

@Composable
fun EditTopLayout(

    modifier: Modifier = Modifier,

    onGuideClick: () -> Unit = {},

    onSaveClick: () -> Unit = {}

) {

    Row(

        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        GuideFloatingButton(

            onClick = onGuideClick

        )

        SaveFloatingButton(

            onClick = onSaveClick

        )

    }

}