package com.maptanim.app.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.buttons.PrimaryButton
import com.maptanim.app.ui.components.textfields.AppTextField
import com.maptanim.app.viewmodel.ProfileViewModel
import io.github.jan.supabase.auth.auth

@Composable
fun WelcomeGuideCard(
    navController: NavController
) {

    var nickname by remember { mutableStateOf("") }

    val profileViewModel: ProfileViewModel = viewModel()

    val isSaved by profileViewModel.isSaved.collectAsState()

    LaunchedEffect(isSaved) {

        if (isSaved) {

            navController.navigate(Routes.HOME) {

                popUpTo(0) {
                    inclusive = true
                }

                launchSingleTop = true

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
                text = "Welcome to MapTanim 🌱",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a nickname to personalize your account.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                value = nickname,
                onValueChange = {
                    nickname = it
                },
                label = "Nickname"
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(

                text = "Continue",

                onClick = {

                    val user = SupabaseClient.client.auth.currentUserOrNull()

                    if (user != null) {

                        profileViewModel.updateProfile(

                            userId = user.id,

                            nickname = nickname

                        )

                    }

                }

            )

        }

    }

}