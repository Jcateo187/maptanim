package com.maptanim.app.ui.components.editcomponents.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.editcomponents.floating.BedFloatingButton
import com.maptanim.app.ui.components.editcomponents.floating.DeleteFloatingButton
import com.maptanim.app.ui.components.editcomponents.floating.PlantFloatingButton
import com.maptanim.app.ui.components.editcomponents.floating.SelectFloatingButton

@Composable
fun EditRightToolbar(

    modifier: Modifier = Modifier,

    onBedClick: () -> Unit = {},

    onPlantClick: () -> Unit = {},

    onSelectClick: () -> Unit = {},

    onDeleteClick: () -> Unit = {}

) {

    Column(

        modifier = modifier,

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        BedFloatingButton(
            onClick = onBedClick
        )

        PlantFloatingButton(
            onClick = onPlantClick
        )

        SelectFloatingButton(
            onClick = onSelectClick
        )

        DeleteFloatingButton(
            onClick = onDeleteClick
        )

    }

}