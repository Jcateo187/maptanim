package com.maptanim.app.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(

    value: String,

    onValueChange: (String) -> Unit,

    label: String,

    modifier: Modifier = Modifier

) {

    var passwordVisible by remember {

        mutableStateOf(false)

    }

    OutlinedTextField(

        value = value,

        onValueChange = onValueChange,

        label = {

            Text(label)

        },

        modifier = modifier.fillMaxWidth(),

        singleLine = true,

        shape = RoundedCornerShape(14.dp),

        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),

        trailingIcon = {

            IconButton(

                onClick = {

                    passwordVisible = !passwordVisible

                }

            ) {

                Icon(

                    imageVector = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,

                    contentDescription = null

                )

            }

        },

        colors = OutlinedTextFieldDefaults.colors(

            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedBorderColor = MaterialTheme.colorScheme.outline

        )

    )

}