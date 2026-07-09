package com.maptanim.app.ui.screens.onboarding

import Routes
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptanim.app.R

@Composable
fun OnboardingScreen(
    navController: NavController
) {

    var accepted by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    val activity = LocalActivity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.onboarding_background), // change if keeping old filename
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "MapTanim",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!accepted) {

                Card(
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Terms & Conditions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Please read and accept the Terms & Conditions before using MapTanim."
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.toggleable(
                                value = checked,
                                onValueChange = {
                                    checked = it
                                }
                            )
                        ) {

                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                }
                            )

                            Text(
                                text = "I agree"
                            )

                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            OutlinedButton(
                                onClick = {
                                    activity?.finish()
                                }
                            ) {

                                Text("Decline")

                            }

                            Button(
                                enabled = checked,
                                onClick = {

                                    accepted = true

                                }
                            ) {

                                Text("Accept")

                            }

                        }

                    }

                }

            } else {

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Login later
                    }
                ) {

                    Text("Sign In")

                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        navController.navigate(Routes.LOADING)


                    }
                ) {

                    Text("Continue as Guest")

                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        // Register later
                    }
                ) {

                    Text("Create Account")

                }

            }

        }

    }

}