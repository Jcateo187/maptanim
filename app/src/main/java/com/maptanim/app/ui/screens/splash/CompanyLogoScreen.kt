package com.maptanim.app.ui.screens.splash

import Routes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.R
import kotlinx.coroutines.delay

@Composable
fun CompanyLogoScreen(
    navController: NavController
) {

    LaunchedEffect(Unit) {

        delay(4000)

        navController.navigate(Routes.ONBOARDING) {

            popUpTo(Routes.COMPANY) {
                inclusive = true
            }

        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(R.drawable.company_logo),
            contentDescription = "Company Logo",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(25.dp)) // Adds the corner radius

        )

    }

}