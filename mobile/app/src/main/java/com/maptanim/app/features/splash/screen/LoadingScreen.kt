package com.maptanim.app.features.splash.screen

import android.content.res.Configuration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.TrackBgmEffect
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.splash.viewmodel.LoadingDestination
import com.maptanim.app.features.splash.viewmodel.LoadingViewModel

@Composable
fun LoadingScreen(
    navController: NavController
) {
    TrackBgmEffect(BackgroundTrack.APP_LAUNCH)

    val loadingViewModel: LoadingViewModel = viewModel()
    val uiState by loadingViewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val bgDrawableRes = if (isLandscape) {
        R.drawable.landscape_bg_loading
    } else {
        R.drawable.portrait_bg_loading
    }

    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }

    // Subtle cinematic zoom-in effect on the artwork
    val backgroundScale by animateFloatAsState(
        targetValue = if (isStarted) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 3500, easing = LinearOutSlowInEasing),
        label = "bg_scale"
    )

    val uiAlpha by animateFloatAsState(
        targetValue = if (isStarted) 1.0f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "ui_alpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 350),
        label = "progress"
    )

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            is LoadingDestination.Welcome -> {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }
            is LoadingDestination.Home -> {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is LoadingDestination.None -> {
                // Still initializing
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09140E)),
        contentAlignment = Alignment.Center
    ) {
        // 1. Fullscreen Image (Landscape or Portrait) with subtle entrance zoom
        Image(
            painter = painterResource(bgDrawableRes),
            contentDescription = if (isLandscape) "MapTanim Landscape Loading Background" else "MapTanim Portrait Loading Background",
            modifier = Modifier
                .fillMaxSize()
                .scale(backgroundScale),
            contentScale = ContentScale.Crop
        )

        // 2. Bottom UI: Clean Progress bar & changing status text directly below it
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = if (isLandscape) 20.dp else 32.dp)
                .padding(horizontal = 24.dp)
                .widthIn(max = if (isLandscape) 420.dp else 340.dp)
                .fillMaxWidth()
                .alpha(uiAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Modern Slim Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF1B382B).copy(alpha = 0.8f)
            )

            // Bottom of progress bar: Text changes information & percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA5D6A7),
                    maxLines = 1
                )

                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784)
                )
            }
        }
    }
}

