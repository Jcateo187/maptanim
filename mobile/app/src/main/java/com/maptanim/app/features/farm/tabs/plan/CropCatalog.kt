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
        "stringbeans", "sitaw", "string_beans" -> "sitaw.png"
        "eggplant", "talong" -> "eggplant.png"
        "tomato", "kamatis" -> "tomato.png"
        "onion", "sibuyas" -> "onion.png"
        "pumpkin", "squash", "kalabasa" -> "pumpkin.png"
        "corn", "mais" -> "corn.png"
        "cabbage", "repolyo" -> "cabbage.png"
        "pechay" -> "pechay.png"
        "ampalaya", "bittergourd", "bitter_gourd" -> "ampalaya.png"
        "okra" -> "okra.png"
        "sili", "chili", "chili_pepper", "pepper" -> "sili.png"
        "cucumber", "pipino" -> "pipino.png"
        "kangkong", "water_spinach" -> "kangkong.png"
        "lettuce", "litsugas" -> "lettuce.png"
        "carrot", "karot" -> "carrot.png"
        else -> "${cleanId}.png"
    }

    val emojiIcon = when (category.lowercase()) {
        "root" -> "🥕"
        "fruit" -> "🍅"
        "leafy" -> "🥬"
        "podded" -> "🫘"
        "bulb" -> "🧅"
        "stem" -> "🌽"
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
    // ── Vegetables ────────────────────────────────────────────────────────
    CropOption("carrot", "Carrot", "Karot", "🥕", "Root", lifeType = "Seasonal", imageFileName = "carrot.png"),
    CropOption("stringbeans", "String Beans", "Sitaw", "🫘", "Podded", lifeType = "Seasonal", imageFileName = "sitaw.png"),
    CropOption("eggplant", "Eggplant", "Talong", "🍆", "Fruit", lifeType = "Permanent", imageFileName = "eggplant.png"),
    CropOption("tomato", "Tomato", "Kamatis", "🍅", "Fruit", lifeType = "Semi Permanent", imageFileName = "tomato.png"),
    CropOption("onion", "Onion", "Sibuyas", "🧅", "Bulb", lifeType = "Seasonal", imageFileName = "onion.png"),
    CropOption("pumpkin", "Squash", "Kalabasa", "🎃", "Fruit", lifeType = "Seasonal", imageFileName = "pumpkin.png"),
    CropOption("corn", "Corn", "Mais", "🌽", "Stem", lifeType = "Seasonal", imageFileName = "corn.png"),
    CropOption("cabbage", "Cabbage", "Repolyo", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "cabbage.png"),
    CropOption("pechay", "Pechay", "Pechay", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "pechay.png"),
    CropOption("ampalaya", "Ampalaya", "Ampalaya", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "ampalaya.png"),
    CropOption("okra", "Okra", "Okra", "🌿", "Fruit", lifeType = "Seasonal", imageFileName = "okra.png"),
    CropOption("sili", "Chili Pepper", "Sili", "🌶️", "Fruit", lifeType = "Permanent", imageFileName = "sili.png"),
    CropOption("cucumber", "Cucumber", "Pipino", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "pipino.png"),
    CropOption("kangkong", "Kangkong", "Kangkong", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "kangkong.png"),
    CropOption("lettuce", "Lettuce", "Litsugas", "🥗", "Leafy", lifeType = "Seasonal", imageFileName = "lettuce.png")
)

val CATEGORY_OPTIONS = listOf(
    "All", "Leafy", "Root", "Bulb", "Stem", "Flower", "Podded", "Tuber", "Fruit"
)
