package com.maptanim.app.ui.components.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TermsCheckbox(

    checked: Boolean,

    onCheckedChange: (Boolean) -> Unit

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically

    ) {

        Checkbox(

            checked = checked,

            onCheckedChange = onCheckedChange

        )

        Text(

            text = "I agree to the Terms & Conditions",

            modifier = Modifier.clickable {

                onCheckedChange(!checked)

            }

        )

    }

}