package com.maptanim.app.features.about.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val PrimaryGreen = Color(0xFF2E7D32)
private val TextDark = Color(0xFF111813)
private val TextMuted = Color(0xFF4B5563)
private val BorderColor = Color(0xFFE5E7EB)
private val BackgroundCanvas = Color(0xFFF7FAF7)

@Composable
fun AboutScreen(navController: NavController) {
    var activeLegalDialog by remember { mutableStateOf<com.maptanim.app.features.shared.legal.LegalType?>(null) }

    activeLegalDialog?.let { type ->
        com.maptanim.app.features.shared.legal.LegalDialog(
            initialType = type,
            onDismiss = { activeLegalDialog = null }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Header Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ABOUT MAPTANIM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Project Overview Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = PrimaryGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Capstone Project Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "MapTanim: A Mobile-Based Interactive Farm Management with Agricultural Decision Support for Vegetable Farmers",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryGreen,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "STI West Negros University — College of Information and Communications Technology (2026)\nBachelor of Science in Information Technology",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Project Team Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Development Team & Attributions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            val team = listOf(
                                "Jomarey D. Parreño" to "Project Manager",
                                "John Ryan R. Vasquez" to "System Analyst",
                                "Jason B. Juanillo" to "Lead Programmer",
                                "James M. Cateo" to "UI/UX Designer & Assistant Programmer"
                            )
                            team.forEach { (name, role) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                                    Text(text = role, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Academic Supervision",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Adviser: Ms. Danica S. Duazo", fontSize = 12.sp, color = TextMuted)
                            Text(text = "Coordinator: Engr. Nahdem C. Columida, CpE", fontSize = 12.sp, color = TextMuted)
                            Text(text = "Dean: Mae B. Lodana, PhD TM", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }

                // Legal Documents & Compliance Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = PrimaryGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Legal Documents & Compliance",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { activeLegalDialog = com.maptanim.app.features.shared.legal.LegalType.TERMS_AND_CONDITIONS },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Terms & Conditions", fontSize = 12.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { activeLegalDialog = com.maptanim.app.features.shared.legal.LegalType.PRIVACY_POLICY },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Privacy Policy", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
