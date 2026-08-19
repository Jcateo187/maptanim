package com.maptanim.app.ui.screens.profile.utils

import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getCropEmoji(name: String): String = when (name.lowercase().replace(" ", "")) {
    "carrot", "karot" -> "🥕"
    "stringbeans", "sitaw", "beans" -> "🫘"
    "eggplant", "talong" -> "🍆"
    "tomato", "kamatis" -> "🍅"
    "onion", "sibuyas" -> "🧅"
    "pumpkin", "squash", "kalabasa" -> "🎃"
    "corn", "mais" -> "🌽"
    "cabbage", "repolyo" -> "🥬"
    "pechay" -> "🥬"
    "ampalaya" -> "🥒"
    "okra" -> "🌿"
    "sili", "chilipepper", "chili" -> "🌶️"
    "cucumber", "pipino" -> "🥒"
    else -> "🌱"
}

fun formatActivityTime(rawTimestamp: String?): String {
    if (rawTimestamp.isNullOrBlank()) return "Recently"
    return try {
        val zdt = ZonedDateTime.parse(rawTimestamp)
        val localZdt = zdt.withZoneSameInstant(ZoneId.systemDefault())
        val now = ZonedDateTime.now()
        val diffHours = Duration.between(localZdt, now).toHours()
        val diffMins = Duration.between(localZdt, now).toMinutes()
        when {
            diffMins < 1 -> "Just now"
            diffMins < 60 -> "$diffMins mins ago"
            diffHours < 24 && localZdt.dayOfMonth == now.dayOfMonth -> "Today at ${localZdt.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            diffHours < 48 -> "Yesterday at ${localZdt.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            else -> localZdt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))
        }
    } catch (e: Exception) {
        try {
            val date = LocalDate.parse(rawTimestamp.take(10))
            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e2: Exception) {
            rawTimestamp.take(16).replace("T", " ")
        }
    }
}
