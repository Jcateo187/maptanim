package com.maptanim.app.features.splash.screen

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.splash.viewmodel.LoadingDestination
import com.maptanim.app.features.splash.viewmodel.LoadingViewModel

private val CanvasWhiteBg = Color(0xFFF7FAF7)
private val TextBrandDark = Color(0xFF111813)
private val BrandLushGreen = Color(0xFF2E7D32)
private val TrackColor = Color(0xFFE5E7EB)

@Composable
fun LoadingScreen(
    navController: NavController
) {
    val loadingViewModel: LoadingViewModel = viewModel()
    val uiState by loadingViewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }

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
            .background(CanvasWhiteBg),
        contentAlignment = Alignment.Center
    ) {
        // Center: App Logo and App Name Only (No container or form)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(uiAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "MapTanim Logo",
                modifier = Modifier.size(110.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // "Map" in white with a bold black outer layer (stroke) so it is perfectly visible on the white canvas
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Map",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(
                            color = Color.Black,
                            drawStyle = Stroke(
                                width = 8f,
                                join = StrokeJoin.Round,
                                cap = StrokeCap.Round
                            )
                        )
                    )
                    Text(
                        text = "Map",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = "Tanim",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandLushGreen
                )
            }
        }

        // Bottom: Loading bar only, lush green, with text in the bottom of loading bar (No container or form)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = if (isLandscape) 24.dp else 48.dp)
                .padding(horizontal = 32.dp)
                .widthIn(max = if (isLandscape) 460.dp else 360.dp)
                .fillMaxWidth()
                .alpha(uiAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loading Bar (bare, no container/form)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(TrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0.02f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BrandLushGreen)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text in the bottom of loading bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.statusText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBrandDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandLushGreen
                )
            }
        }
    }
}
