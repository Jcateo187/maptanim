package com.maptanim.app.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.ProfileRepository
import com.maptanim.app.navigation.Routes
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoadingScreen(
    navController: NavController
) {

    LaunchedEffect(Unit) {

        delay(1.2.seconds)

        val session =
            SupabaseClient.client.auth.currentSessionOrNull()

        if (session == null) {

            navController.navigate(Routes.WELCOME) {

                popUpTo(Routes.LOADING) {
                    inclusive = true
                }

            }

        }

        else {

            val repository = ProfileRepository()

            val user = SupabaseClient.client.auth.currentUserOrNull()

            if (user == null) {

                navController.navigate(Routes.WELCOME) {

                    popUpTo(Routes.LOADING) {
                        inclusive = true
                    }

                }

                return@LaunchedEffect

            }

            val profile = repository.getProfile(user.id)

            if (profile == null) {

                navController.navigate(Routes.WELCOME) {

                    popUpTo(Routes.LOADING) {
                        inclusive = true
                    }

                }

                return@LaunchedEffect
            }

            if (profile.onboarding_completed) {

                navController.navigate(Routes.HOME) {

                    popUpTo(0) {
                        inclusive = true
                    }

                    launchSingleTop = true

                }

            } else {

                navController.navigate(Routes.WELCOME_GUIDE) {

                    popUpTo(Routes.LOADING) {
                        inclusive = true
                    }

                }

            }

        }

    }

    android.util.Log.d(
        "AUTH",
        "Session = ${SupabaseClient.client.auth.currentSessionOrNull()}"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()

    }

}