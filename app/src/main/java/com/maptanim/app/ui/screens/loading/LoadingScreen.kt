package com.maptanim.app.ui.screens.loading

import Routes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.maptanim.app.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    navController: NavController
) { LaunchedEffect(Unit) {

    delay(2000)

    navController.navigate(Routes.HOME) {

        popUpTo(Routes.LOADING) {
            inclusive = true
        }

    }

}

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.loading_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.85f)
                )
            ) {

                Text(
                    text = "By continuing, you agree to the\nTerms & Conditions",
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                progress = { 0.65f },      // temporary
                modifier = Modifier
                    .width(320.dp)
                    .height(12.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Loading...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

        }

    }

}