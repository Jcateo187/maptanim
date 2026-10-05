package com.maptanim.app.features.library.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * LibraryCategory — Vegetable taxonomical categories for MapTanim knowledge base.
 */
enum class LibraryCategory(val id: String, val label: String, val icon: ImageVector) {
    ALL("ALL", "All Vegetables", Icons.Default.LocalFlorist),
    FRUIT("FRUIT", "Solanaceous & Fruit", Icons.Default.Spa),
    CUCURBIT("CUCURBIT", "Cucurbits & Gourds", Icons.Default.Eco),
    LEAFY("LEAFY", "Leafy Greens", Icons.Default.Grass),
    LEGUME("LEGUME", "Legumes & Beans", Icons.Default.Yard),
    ROOT("ROOT", "Root & Tubers", Icons.Default.Park),
    BULB("BULB", "Bulb & Stem", Icons.Default.Agriculture)
}

/**
 * AgronomicTab — Numbered protocols for crop production.
 */
enum class AgronomicTab(val tabNumber: Int, val title: String, val icon: ImageVector) {
    PREPARATION(1, "Preparation", Icons.Default.Landscape),
    PLANTING(2, "Planting", Icons.Default.Spa),
    CARE_MAINTENANCE(3, "Care & Maintenance", Icons.Default.WaterDrop),
    HARVEST_METHOD(4, "Harvest Method", Icons.Default.ShoppingBasket)
}
