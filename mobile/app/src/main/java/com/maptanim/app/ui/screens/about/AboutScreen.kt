package com.maptanim.app.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Code
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
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@Composable
fun AboutScreen(navController: NavController) {
    var showTermsModal by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }

    if (showTermsModal) {
        com.maptanim.app.ui.components.legal.LegalDialog(
            title = "Terms & Conditions",
            content = com.maptanim.app.data.local.LegalContent.TERMS_AND_CONDITIONS,
            onDismiss = { showTermsModal = false }
        )
    }

    if (showPrivacyModal) {
        com.maptanim.app.ui.components.legal.LegalDialog(
            title = "Privacy Policy",
            content = com.maptanim.app.data.local.LegalContent.PRIVACY_POLICY,
            onDismiss = { showPrivacyModal = false }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF10160F)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFF1B2317), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ABOUT MAPTANIM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Project Overview Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = ForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Capstone Project Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MapTanim: A Mobile-Based Interactive Farm Management with Agricultural Decision Support for Vegetable Farmers",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "STI West Negros University — College of Information and Communications Technology (2026)\nBachelor of Science in Information Technology",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Project Team Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = ForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Development Team & Attributions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
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
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = White)
                                    Text(text = role, fontSize = 12.sp, color = ForestGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Academic Supervision",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = White.copy(alpha = 0.9f)
                            )
                            Text(text = "Adviser: Ms. Danica S. Duazo", fontSize = 12.sp, color = White.copy(alpha = 0.7f))
                            Text(text = "Coordinator: Engr. Nahdem C. Columida, CpE", fontSize = 12.sp, color = White.copy(alpha = 0.7f))
                            Text(text = "Dean: Mae B. Lodana, PhD TM", fontSize = 12.sp, color = White.copy(alpha = 0.7f))
                        }
                    }
                }

                // Technical Stack Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = ForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Technology Stack & Architecture",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val techStack = listOf(
                                "Mobile Client" to "Kotlin 2.2.10, AGP 9.2.1, Jetpack Compose, Room DB",
                                "Backend & Cloud" to "Supabase PostgreSQL, Row Level Security, Edge Functions",
                                "Admin Dashboard" to "React 18, Vite 6, TypeScript 5.7, Tailwind CSS 4",
                                "DevOps & CI/CD" to "Docker Compose, Nginx, GitHub Actions Workflow",
                                "Asset Engine" to "Python 3.10+ PIL Crop & Scenery Pipelines"
                            )
                            techStack.forEach { (layer, tech) ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(text = layer, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreen)
                                    Text(text = tech, fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }

                // Legal Documents & Compliance Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2317)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = ForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Legal Documents & Compliance",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showTermsModal = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4D3E))
                                ) {
                                    Text("Terms & Conditions", fontSize = 12.sp, color = White)
                                }
                                Button(
                                    onClick = { showPrivacyModal = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4D3E))
                                ) {
                                    Text("Privacy Policy", fontSize = 12.sp, color = White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
