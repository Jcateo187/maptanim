package com.maptanim.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.buttons.AppButton
import com.maptanim.app.ui.components.textfields.AppTextField

@Composable
fun HomeScreen() {

    var email by rememberSaveable {

        mutableStateOf("")


    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center

    ) {

        Text(

            text = "Component Test",

            style = MaterialTheme.typography.headlineMedium

        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(

            value = email,

            onValueChange = {

                email = it

            },

            label = "Email"

        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(

            text = "Login",

            onClick = {}

        )

    }

}