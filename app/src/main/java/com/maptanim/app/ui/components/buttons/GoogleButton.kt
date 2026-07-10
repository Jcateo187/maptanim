package com.maptanim.app.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GoogleButton(

    onClick: () -> Unit

) {

    Button(

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),

        shape = RoundedCornerShape(14.dp),

        colors = ButtonDefaults.buttonColors(

            containerColor = Color.White,

            contentColor = Color.Black

        )

    ) {

        Icon(

            imageVector = Icons.Default.AccountCircle,

            contentDescription = null

        )

        Text(

            text = " Continue with Google"

        )

    }

}