package com.maptanim.app.features.farm.tabs.plan

import androidx.compose.ui.graphics.Color
import com.maptanim.app.domain.model.Crop

data class CropOption(
    val id: String,
    val name: String,
    val localName: String? = null,
    val emoji: String = "🌱",
    val category: String,
    val lifeType: String = "Seasonal",
    val imageFileName: String = "${id}.png",
    val imageUrl: String? = null,
    val hasAsset: Boolean = true,
    val isBed: Boolean = false,
    val bedColor: Color? = null
)

fun Crop.toCropOption(): CropOption {
    val cleanId = id.lowercase().replace(" ", "_")
    val defaultAssetFileName = when (cleanId) {
        "stringbeans", "sitaw", "string_beans", "yardlong_bean" -> "sitaw.png"
        "eggplant", "talong" -> "eggplant.png"
        "tomato", "kamatis" -> "tomato.png"
        "bell_pepper", "bellpepper", "sweet_pepper", "sweetpepper", "siling_atsal", "atsal" -> "bell_pepper.png"
        "green_onion", "greenonion", "scallion", "spring_onion", "springonion", "dahon_ng_sibuyas", "onion", "sibuyas" -> "green_onion.png"
        "pumpkin", "squash", "kalabasa" -> "pumpkin.png"
        "cabbage", "repolyo" -> "cabbage.png"
        "pechay", "petsay", "bokchoy" -> "pechay.png"
        "ampalaya", "bittergourd", "bitter_gourd" -> "ampalaya.png"
        "cucumber", "pipino" -> "pipino.png"
        "lettuce", "litsugas" -> "lettuce.png"
        "carrot", "karot" -> "carrot.png"
        "potato", "patatas" -> "potato.png"
        else -> "${cleanId}.png"
    }

    val emojiIcon = when (category.lowercase()) {
        "root" -> "🥕"
        "tuber" -> "🥔"
        "fruit" -> "🍅"
        "leafy" -> "🥬"
        "podded" -> "🫘"
        "bulb" -> "🧅"
        else -> "🌱"
    }

    return CropOption(
        id = id,
        name = name,
        localName = localName,
        emoji = emojiIcon,
        category = category.ifBlank { "Vegetable" },
        lifeType = "Seasonal",
        imageFileName = defaultAssetFileName,
        imageUrl = imageUrl,
        hasAsset = !imageUrl.isNullOrBlank()
    )
}

val AVAILABLE_CROP_CATALOG = listOf(
    // ── Garden Bed (Color Only) ──────────────────────────────────────────
    CropOption(
        id = "bed",
        name = "Bed",
        localName = "Kama ng Halaman",
        emoji = "🟫",
        category = "Bed",
        lifeType = "Permanent",
        imageFileName = "",
        isBed = true,
        bedColor = Color(0xFF6D4C41)
    ),
    // ── 13 Canonical Philippine Vegetables ────────────────────────────────
    CropOption("carrot", "Carrot", "Karot", "🥕", "Root", lifeType = "Seasonal", imageFileName = "carrot.png"),
    CropOption("potato", "Potato", "Patatas", "🥔", "Tuber", lifeType = "Seasonal", imageFileName = "potato.png"),
    CropOption("stringbeans", "String Beans", "Sitaw", "🫘", "Podded", lifeType = "Seasonal", imageFileName = "sitaw.png"),
    CropOption("eggplant", "Eggplant", "Talong", "🍆", "Fruit", lifeType = "Permanent", imageFileName = "eggplant.png"),
    CropOption("tomato", "Tomato", "Kamatis", "🍅", "Fruit", lifeType = "Semi Permanent", imageFileName = "tomato.png"),
    CropOption("bell_pepper", "Bell Pepper", "Siling Atsal", "🫑", "Fruit", lifeType = "Seasonal", imageFileName = "bell_pepper.png"),
    CropOption("green_onion", "Green Onion", "Dahon ng Sibuyas", "🧅", "Bulb", lifeType = "Seasonal", imageFileName = "green_onion.png"),
    CropOption("squash", "Squash", "Kalabasa", "🎃", "Fruit", lifeType = "Seasonal", imageFileName = "pumpkin.png"),
    CropOption("cabbage", "Cabbage", "Repolyo", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "cabbage.png"),
    CropOption("pechay", "Pechay", "Petsay", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "pechay.png"),
    CropOption("ampalaya", "Ampalaya", "Ampalaya", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "ampalaya.png"),
    CropOption("cucumber", "Cucumber", "Pipino", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "pipino.png"),
    CropOption("lettuce", "Lettuce", "Litsugas", "🥗", "Leafy", lifeType = "Seasonal", imageFileName = "lettuce.png")
)

val CATEGORY_OPTIONS = listOf(
    "All", "Leafy", "Root", "Bulb", "Podded", "Tuber", "Fruit"
)
