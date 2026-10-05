package com.maptanim.app.features.auth.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.core.validation.AuthValidator
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.auth.components.PrimaryButton
import com.maptanim.app.features.auth.components.AppTextField
import com.maptanim.app.features.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private const val STANDARD_COOLDOWN_SECONDS = 60
private const val LOCKOUT_COOLDOWN_SECONDS = 300 // 5 minutes lockout after max attempts
private const val MAX_RESET_ATTEMPTS = 3

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    // Rate-limiting and Cooldown State
    var cooldownSeconds by remember { mutableIntStateOf(0) }
    var resetAttempts by remember { mutableIntStateOf(0) }
    val isLockedOut = resetAttempts >= MAX_RESET_ATTEMPTS

    // Countdown timer effect
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
            if (cooldownSeconds == 0 && isLockedOut) {
                // Reset attempt counter after lockout cooldown expires
                resetAttempts = 0
            }
        }
    }

    val trimmedEmail = email.trim()
    val isGmailDomain = trimmedEmail.endsWith("@gmail.com", ignoreCase = true) ||
                        trimmedEmail.endsWith("@googlemail.com", ignoreCase = true)
    val emailHasContent = trimmedEmail.isNotBlank()
    val isEmailValid = emailHasContent && isGmailDomain && AuthValidator.validateGoogleEmail(trimmedEmail).isValid

    val isButtonEnabled = isEmailValid && cooldownSeconds == 0 && !uiState.isLoading && !isLockedOut

    val buttonText = when {
        uiState.isLoading -> "Sending..."
        isLockedOut -> "Limit Reached (${cooldownSeconds}s)"
        cooldownSeconds > 0 -> "Resend in ${cooldownSeconds}s"
        uiState.isSuccess -> "Resend Reset Link"
        else -> "Send Reset Link"
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.onboarding_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(16.dp),
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
                        text = "Enter your Google email to receive a password reset link.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Google Email (Gmail)",
                        placeholder = "e.g. juandelacruz@gmail.com",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = if (isGmailDomain) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (isGmailDomain) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Valid Gmail",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        supportingText = {
                            if (emailHasContent && !isGmailDomain) {
                                Text(
                                    text = "Must be a valid @gmail.com address to receive OTPs",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = "Password reset OTP will be sent to your Gmail inbox",
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        isError = emailHasContent && !isGmailDomain,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Reset button with rate-limiting, cooldown, and gray disabled state
                    PrimaryButton(
                        text = buttonText,
                        enabled = isButtonEnabled,
                        onClick = {
                            if (isButtonEnabled) {
                                resetAttempts += 1
                                cooldownSeconds = if (resetAttempts >= MAX_RESET_ATTEMPTS) {
                                    LOCKOUT_COOLDOWN_SECONDS
                                } else {
                                    STANDARD_COOLDOWN_SECONDS
                                }
                                authViewModel.resetPassword(trimmedEmail)
                            }
                        }
                    )

                    // Cooldown & Lockout Feedback
                    if (isLockedOut) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f), shape = MaterialTheme.shapes.small)
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Limit Warning",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Maximum attempts reached ($MAX_RESET_ATTEMPTS/$MAX_RESET_ATTEMPTS). Please wait $cooldownSeconds seconds before trying again.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp
                            )
                        }
                    } else if (cooldownSeconds > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Please wait $cooldownSeconds seconds before requesting another reset email.",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }

                    if (uiState.isSuccess) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Check your Gmail inbox for instructions to reset your password.",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    uiState.errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
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
