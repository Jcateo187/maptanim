package com.maptanim.app.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.navigation.Routes
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun CompanyLogoScreen(
    navController: NavController
) {

    var startAnimation by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(900),
        label = ""
    )

    LaunchedEffect(Unit) {

        startAnimation = true

        delay(2.seconds)

        navController.navigate(Routes.LOADING) {

            popUpTo(Routes.COMPANY) {
                inclusive = true
            }

        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFDFBFB),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Card(
            modifier = Modifier
                .size(170.dp)
                .scale(scale)
                .alpha(alpha)
                .shadow(
                    elevation = 70.dp,
                    shape = RoundedCornerShape(70.dp)
                ),
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.company_logo),
                    contentDescription = "Company Logo",
                    modifier = Modifier.size(200.dp)
                )

            }

        }

    }

}
