package com.maptanim.app.features.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.features.auth.viewmodel.AuthViewModel
import com.maptanim.app.navigation.Routes

@Composable
fun LoginCard(
    navController: NavController
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("maptanim_auth_prefs", android.content.Context.MODE_PRIVATE) }

    val savedRemember = remember { prefs.getBoolean("remember_me", false) }
    val savedEmail = remember { if (savedRemember) prefs.getString("saved_email", "") ?: "" else "" }

    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(savedRemember) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            if (rememberMe) {
                prefs.edit()
                    .putBoolean("remember_me", true)
                    .putString("saved_email", email.trim())
                    .apply()
            } else {
                prefs.edit()
                    .putBoolean("remember_me", false)
                    .remove("saved_email")
                    .apply()
            }

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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAF7)
        ),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(8.dp)
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
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111813)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sign in to continue using MapTanim.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563)
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {

                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2E7D32),
                            uncheckedColor = Color(0xFF6B7280)
                        )
                    )

                    Text(
                        text = "Remember Me",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF111813)
                    )

                }

                Text(
                    text = "Forgot Password?",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    }
                )

            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Sign In",
                onClick = {
                    authViewModel.signIn(
                        email = email,
                        password = password
                    )
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

            Spacer(modifier = Modifier.height(28.dp))

            Row {

                Text(
                    text = "Don't have an account? ",
                    color = Color(0xFF4B5563)
                )

                Text(
                    text = "Create Account",
                    color = Color(0xFF2E7D32),
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
