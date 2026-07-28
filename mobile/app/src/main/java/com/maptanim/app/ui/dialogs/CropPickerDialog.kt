package com.maptanim.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType

/**
 * CropPickerDialog — Dialog for selecting a crop to plant in a bed during Edit Mode.
 *
 * Features:
 *   - Grid view of DA/PSA 13 high-value vegetable crops
 *   - Search / Filter bar
 *   - Displays crop name, local Tagalog name, growth days, and category
 *   - Clicking a crop returns (cropName, cropId) to calling screen
 */
@Composable
fun CropPickerDialog(
    bedLabel: String = "PLOT 3",
    currentCropName: String? = null,
    onDismiss: () -> Unit,
    onCropSelected: (cropName: String, cropId: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val cropsList = remember {
        listOf(
            CropItem("1", "Tomato", "Kamatis", "FRUIT", 60, "🍅", Color(0xFFE53935)),
            CropItem("2", "Eggplant", "Talong", "FRUIT", 75, "🍆", Color(0xFF7B1FA2)),
            CropItem("3", "Cucumber", "Pipino", "FRUIT", 50, "🥒", Color(0xFF43A047)),
            CropItem("4", "Lettuce", "Latsuga", "LEAFY", 45, "🥬", Color(0xFF66BB6A)),
            CropItem("5", "Cabbage", "Repolyo", "LEAFY", 70, "🥬", Color(0xFF81C784)),
            CropItem("6", "Carrot", "Karat", "ROOT", 75, "🥕", Color(0xFFFB8C00)),
            CropItem("7", "String Beans", "Sitaw", "FRUIT", 60, "🫛", Color(0xFF2E7D32)),
            CropItem("8", "Pepper", "Sili", "FRUIT", 70, "🌶️", Color(0xFFD84315)),
            CropItem("9", "Squash", "Kalabasa", "FRUIT", 85, "🎃", Color(0xFFF57C00)),
            CropItem("10", "Okra", "Okra", "FRUIT", 55, "🌱", Color(0xFF388E3C)),
            CropItem("11", "Onion", "Sibuyas", "BULB", 90, "🧅", Color(0xFF8E24AA)),
            CropItem("12", "Garlic", "Bawang", "BULB", 100, "🧄", Color(0xFF795548)),
            CropItem("13", "Watermelon", "Pakwan", "FRUIT", 80, "🍉", Color(0xFFE91E63))
        )
    }

    val filteredCrops = remember(searchQuery) {
        if (searchQuery.isBlank()) cropsList
        else cropsList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.localName.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Crop to Plant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Planting into $bedLabel",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search crop or local name...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                // Crops Grid (3 columns)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCrops) { crop ->
                        val isCurrent = crop.name.equals(currentCropName, ignoreCase = true)
                        CropCard(
                            crop = crop,
                            isSelected = isCurrent,
                            onClick = {
                                onCropSelected(crop.name, crop.id)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

private data class CropItem(
    val id: String,
    val name: String,
    val localName: String,
    val category: String,
    val daysToHarvest: Int,
    val emoji: String,
    val accentColor: Color
)

@Composable
private fun CropCard(
    crop: CropItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF9F9F9),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1B5E20)) else null,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = crop.emoji, fontSize = 24.sp)
            Text(
                text = crop.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFF1B5E20) else Color.Black
            )
            Text(
                text = "(${crop.localName})",
                fontSize = 9.sp,
                color = Color.Gray
            )
            Text(
                text = "${crop.daysToHarvest} days",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = crop.accentColor
            )
        }
    }
}
