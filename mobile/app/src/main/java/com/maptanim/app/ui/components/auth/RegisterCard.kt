package com.maptanim.app.ui.components.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.core.validation.AuthValidator
import com.maptanim.app.data.local.AppDatabase
import com.maptanim.app.data.local.entity.PolicyConsentEntity
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.buttons.GuestButton
import com.maptanim.app.ui.components.buttons.PrimaryButton
import com.maptanim.app.ui.components.checkbox.TermsCheckbox
import com.maptanim.app.ui.components.textfields.AppTextField
import com.maptanim.app.ui.components.textfields.PasswordTextField
import com.maptanim.app.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterCard(
    navController: NavController
) {
    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val consentDao = remember(context) {
        RepositoryProvider.policyConsentDao ?: AppDatabase.getInstance(context).policyConsentDao()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var activeLegalDialog by remember { mutableStateOf<com.maptanim.app.ui.components.legal.LegalType?>(null) }
    var showGuestWarningDialog by remember { mutableStateOf(false) }

    // Read policy acceptance state from Room SQL on initial load
    LaunchedEffect(Unit) {
        try {
            val savedConsent = consentDao?.getConsent(PolicyConsentEntity.KEY_TERMS_AND_PRIVACY)
            if (savedConsent != null) {
                acceptedTerms = savedConsent.isAccepted
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun persistPolicyConsent(isAccepted: Boolean) {
        acceptedTerms = isAccepted
        coroutineScope.launch(Dispatchers.IO) {
            try {
                consentDao?.saveConsent(
                    PolicyConsentEntity(
                        consentKey = PolicyConsentEntity.KEY_TERMS_AND_PRIVACY,
                        isAccepted = isAccepted
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val trimmedEmail = email.trim()
    val isGmailDomain = trimmedEmail.endsWith("@gmail.com", ignoreCase = true) ||
                        trimmedEmail.endsWith("@googlemail.com", ignoreCase = true)
    val emailHasContent = trimmedEmail.isNotBlank()

    // Form completeness validation: All required fields filled and policy accepted
    val isEmailValid = emailHasContent && isGmailDomain && AuthValidator.validateGoogleEmail(trimmedEmail).isValid
    val isPasswordValid = password.length >= 6
    val isConfirmPasswordValid = confirmPassword.isNotBlank() && confirmPassword == password
    val isFormCompleteAndValid = isEmailValid && isPasswordValid && isConfirmPasswordValid && acceptedTerms

    activeLegalDialog?.let { legalType ->
        com.maptanim.app.ui.components.legal.LegalDialog(
            initialType = legalType,
            onDismiss = { activeLegalDialog = null },
            onAccept = {
                persistPolicyConsent(true)
            }
        )
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Routes.LOADING) {
                popUpTo(Routes.WELCOME) {
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
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Start your smart farming journey.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Google Email (Gmail) Input with Real-Time Domain Verification
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Google Email (Gmail)",
                placeholder = "e.g. juan@gmail.com",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Google Email",
                        tint = if (isGmailDomain) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (isGmailDomain) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Valid Google Email",
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
                            text = "Requires active Gmail to receive password reset OTPs",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 11.sp
                        )
                    }
                },
                isError = emailHasContent && !isGmailDomain,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Password Input
            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                supportingText = {
                    if (password.isNotEmpty() && password.length < 6) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }
                },
                isError = password.isNotEmpty() && password.length < 6
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Confirm Password Input
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                supportingText = {
                    if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }
                },
                isError = confirmPassword.isNotEmpty() && confirmPassword != password
            )

            Spacer(modifier = Modifier.height(16.dp))

            TermsCheckbox(
                checked = acceptedTerms,
                onCheckedChange = { isChecked ->
                    persistPolicyConsent(isChecked)
                },
                onOpenTerms = { activeLegalDialog = com.maptanim.app.ui.components.legal.LegalType.TERMS_AND_CONDITIONS },
                onOpenPrivacy = { activeLegalDialog = com.maptanim.app.ui.components.legal.LegalType.PRIVACY_POLICY }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Create Account button: Disabled & grayed out until all inputs are filled and policy is checked
            PrimaryButton(
                text = "Create Account",
                enabled = isFormCompleteAndValid && !uiState.isLoading,
                onClick = {
                    if (isFormCompleteAndValid) {
                        authViewModel.signUp(
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            acceptedTerms = acceptedTerms
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
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(20.dp))

            GuestButton(
                enabled = acceptedTerms && !uiState.isLoading,
                onClick = {
                    if (acceptedTerms) {
                        showGuestWarningDialog = true
                    }
                }
            )

            if (!acceptedTerms) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please accept the Terms & Privacy Policy to continue as guest",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("Already have an account? ")
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.LOGIN)
                    }
                )
            }
        }
    }

    if (showGuestWarningDialog) {
        GuestWarningDialog(
            onDismiss = { showGuestWarningDialog = false },
            onProceed = {
                showGuestWarningDialog = false
                authViewModel.signInAnonymously()
            },
            onSignIn = {
                showGuestWarningDialog = false
                navController.navigate(Routes.LOGIN)
            }
        )
    }
}