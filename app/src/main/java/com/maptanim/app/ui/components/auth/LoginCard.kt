package com.maptanim.app.ui.components.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.buttons.GoogleButton
import com.maptanim.app.ui.components.buttons.GuestButton
import com.maptanim.app.ui.components.buttons.PrimaryButton
import com.maptanim.app.ui.components.textfields.AppTextField
import com.maptanim.app.ui.components.textfields.PasswordTextField
import com.maptanim.app.viewmodel.AuthViewModel

@Composable
fun LoginCard(
    navController: NavController
) {

    val authViewModel: AuthViewModel = viewModel()

    val uiState by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            navController.navigate(Routes.LOADING) {

                popUpTo(Routes.LOGIN) {
                    inclusive = true
                }

            }

        }

    }

    Card(
        modifier = Modifier
            .width(430.dp)
            .padding(24.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sign in to continue using MapTanim.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = "Email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(

                text = "Sign In",

                onClick = {

                    if (

                        email.isNotBlank() &&
                        password.isNotBlank()

                    ) {

                        authViewModel.signIn(

                            email = email,

                            password = password

                        )

                    }

                }

            )

            if (uiState.isLoading) {

                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator()

            }

            uiState.errorMessage?.let {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            GoogleButton(
                onClick = {

                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            GuestButton(
                onClick = {

                    navController.navigate(Routes.WELCOME_GUIDE)

                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row {

                Text("Don't have an account? ")

                Text(
                    text = "Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {

                        navController.navigate(Routes.WELCOME)

                    }
                )

            }

        }

    }

}