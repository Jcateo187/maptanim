package com.maptanim.app.features.auth.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.features.auth.components.RegisterCard
import com.maptanim.app.features.shared.support.CustomerServiceChatDialog
import com.maptanim.app.features.shared.support.ModernCustomerServiceButton

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    var showCustomerSupport by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            RegisterCard(navController)
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