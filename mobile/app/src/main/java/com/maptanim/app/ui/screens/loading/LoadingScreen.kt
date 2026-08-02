package com.maptanim.app.ui.screens.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.navigation.Routes
import com.maptanim.app.viewmodel.LoadingDestination
import com.maptanim.app.viewmodel.LoadingViewModel
import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.TrackBgmEffect
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    navController: NavController
) {
    TrackBgmEffect(BackgroundTrack.APP_LAUNCH)

    val loadingViewModel: LoadingViewModel = viewModel()
    val destination by loadingViewModel.destination.collectAsState()

    var progress by remember { mutableFloatStateOf(0.15f) }
    var statusText by remember { mutableStateOf("Initializing Agroecological Engine...") }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        delay(500)
        progress = 0.40f
        statusText = "Loading Farm Workspace..."
        delay(800)
        progress = 0.80f
        statusText = "Syncing Philippine Crop Database..."
        delay(700)
        progress = 1.0f
        statusText = "Ready!"
    }

    LaunchedEffect(destination) {
        when (destination) {
            is LoadingDestination.Welcome -> {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }
            is LoadingDestination.WelcomeGuide -> {
                navController.navigate(Routes.WELCOME_GUIDE) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }
            is LoadingDestination.Home -> {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
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
        Image(
            painter = painterResource(R.drawable.loading_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF09140E).copy(alpha = 0.85f),
                            Color(0xFF132A1F).copy(alpha = 0.85f),
                            Color(0xFF09140E).copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Card(
                modifier = Modifier.size(150.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "MapTanim App Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.45f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "MapTanim Workspace",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA5D6A7),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .width(260.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF1B382B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF81C784)
            )
        }
    }
}