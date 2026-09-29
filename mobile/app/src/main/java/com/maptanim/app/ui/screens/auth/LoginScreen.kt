package com.maptanim.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.ui.components.auth.LoginCard
import com.maptanim.app.ui.components.support.CustomerServiceChatDialog
import com.maptanim.app.ui.components.support.ModernCustomerServiceButton

@Composable
fun LoginScreen(
    navController: NavController
) {
    var showCustomerSupport by remember { mutableStateOf(false) }

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
            LoginCard(
                navController = navController
            )
        }

        // Modern Customer Service Icon in Left Bottom Area
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 20.dp, bottom = 24.dp)
        ) {
            ModernCustomerServiceButton(
                onClick = { showCustomerSupport = true }
            )
        }

        // Customer Service Chat Dialog
        if (showCustomerSupport) {
            CustomerServiceChatDialog(
                navController = navController,
                onDismiss = { showCustomerSupport = false }
            )
        }
    }
}