package com.maptanim.app.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuestButton(

    onClick: () -> Unit

) {

    OutlinedButton(

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),

        shape = RoundedCornerShape(14.dp)

    ) {

        Text("Continue as Guest")

    }

}