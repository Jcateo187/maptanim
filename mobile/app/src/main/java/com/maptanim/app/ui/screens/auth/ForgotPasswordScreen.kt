package com.maptanim.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.buttons.PrimaryButton
import com.maptanim.app.ui.components.textfields.AppTextField

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var resetSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.onboarding_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
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
                        text = "Reset Password",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your email to receive a password reset link.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = if (resetSent) "Reset Link Sent!" else "Send Reset Link",
                        onClick = {
                            if (email.isNotBlank()) {
                                resetSent = true
                            }
                        }
                    )

                    if (resetSent) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Check your email inbox for instructions to reset your password.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Text("Remember your password? ")
                        Text(
                            text = "Sign In",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
