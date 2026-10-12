package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.Farm

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * UserFarmsListCard — Displays user registered farms list in Daylight theme.
 */
@Composable
fun UserFarmsListCard(
    farms: List<Farm>,
    activeFarmId: String?,
    onCreateFarmClick: () -> Unit,
    onSeeMoreClick: () -> Unit,
    onSelectActiveFarm: (String) -> Unit,
    onRenameFarmClick: (Farm) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Farms List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepBlack
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(
                        onClick = onCreateFarmClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Farm",
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "New Farm",
                            color = LushGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (farms.size > 2) {
                        TextButton(
                            onClick = onSeeMoreClick,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "See More (${farms.size})",
                                color = LushGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (farms.isEmpty()) {
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No farms registered yet.",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                    OutlinedButton(
                        onClick = onCreateFarmClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, LushGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Create First Farm",
                            color = LushGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val visibleFarms = farms.take(2)
                visibleFarms.forEach { farm ->
                    val isActive = farm.id == activeFarmId
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isActive) LushGreen else CardBorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = farm.farmName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DeepBlack
                                )
                                Text(
                                    text = if (farm.createdAt.isNotBlank()) "Created: ${farm.createdAt}" else "Farm Workspace",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9),
                                        border = BorderStroke(1.dp, LushGreen)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LushGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onSelectActiveFarm(farm.id) },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        border = BorderStroke(1.dp, LushGreen),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(
                                            text = "Select",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = LushGreen
                                        )
                                    }
                                }

                                // Rename Button
                                IconButton(
                                    onClick = { onRenameFarmClick(farm) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Rename Farm",
                                        tint = DeepBlack,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
