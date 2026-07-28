package com.maptanim.app.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(

    value: String,

    onValueChange: (String) -> Unit,

    label: String,

    modifier: Modifier = Modifier

) {

    OutlinedTextField(

        value = value,

        onValueChange = onValueChange,

        label = {

            Text(label)

        },

        modifier = modifier.fillMaxWidth(),

        singleLine = true,

        shape = RoundedCornerShape(14.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedBorderColor = MaterialTheme.colorScheme.outline

        )

    )

}