package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFavoriteCropDto(
    @SerialName("user_id") val userId: String,
    @SerialName("crop_name") val cropName: String
)

/**
 * FavoriteCropsManager — Local & Cloud persistence manager for user favorite crops.
 * Synchronizes favoriting actions in CropInformationDialog with the Profile screen
 * and the Supabase user_favorite_crops table for the Admin Dashboard.
 * Isolated per authenticated user.
 */
class FavoriteCropsManager(context: Context? = null) {

    private var ctx: Context? = context?.applicationContext ?: RepositoryProvider.appContext
    private var prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _favoriteCrops = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteCrops: StateFlow<Set<String>> = _favoriteCrops.asStateFlow()

    fun bindContext(context: Context) {
        if (ctx == null) {
            ctx = context.applicationContext
            prefs = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            _favoriteCrops.value = loadFavorites()
        }
    }

    private fun getCurrentUserId(): String? {
        return try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (_: Exception) {
            null
        }
    }

    private fun getScopedKey(userId: String? = null): String {
        val uid = userId ?: getCurrentUserId()
        return if (uid.isNullOrBlank()) KEY_FAVORITES_ANON else "${KEY_FAVORITES}_$uid"
    }

    fun reloadFavorites(userId: String? = null) {
        _favoriteCrops.value = loadFavorites(userId)

        // Asynchronously sync from Supabase cloud if online
        val uid = userId ?: getCurrentUserId()
        if (!uid.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val remoteList = SupabaseClient.client.from("user_favorite_crops")
                        .select {
                            filter { eq("user_id", uid) }
                        }
                        .decodeList<UserFavoriteCropDto>()
                    if (remoteList.isNotEmpty()) {
                        val remoteSet = remoteList.map { it.cropName }.toSet()
                        val merged = _favoriteCrops.value + remoteSet
                        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        currentPrefs?.edit()?.putStringSet(getScopedKey(userId), merged)?.apply()
                        _favoriteCrops.value = merged
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun loadFavorites(userId: String? = null): Set<String> {
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        val key = getScopedKey(userId)
        val saved = currentPrefs?.getStringSet(key, null)

        val hasExplicitlyFavorited = currentPrefs?.getBoolean("user_explicitly_favorited", false) ?: false

        // Clear legacy un-scoped auto-seed if it was never explicitly favorited by user
        val legacySaved = currentPrefs?.getStringSet(KEY_LEGACY_FAVORITES, null)
        if (!hasExplicitlyFavorited && legacySaved != null && legacySaved == setOf("Tomatoes", "Eggplant", "Pechay")) {
            currentPrefs?.edit()?.remove(KEY_LEGACY_FAVORITES)?.apply()
        }

        return if (saved != null) {
            if (!hasExplicitlyFavorited && saved == setOf("Tomatoes", "Eggplant", "Pechay")) {
                currentPrefs?.edit()?.remove(key)?.apply()
                emptySet()
            } else {
                saved.toSet()
            }
        } else {
            emptySet()
        }
    }

    fun isFavorite(cropName: String, userId: String? = null): Boolean {
        val clean = cleanName(cropName)
        val list = if (userId != null && userId != getCurrentUserId()) {
            getFavoritesForUser(userId)
        } else {
            _favoriteCrops.value
        }
        return list.any { cleanName(it) == clean }
    }

    fun toggleFavorite(cropName: String, userId: String? = null): Boolean {
        val clean = cropName.trim()
        val current = _favoriteCrops.value.toMutableSet()
        val isFav = current.any { cleanName(it) == cleanName(clean) }
        val willBeFavorite = !isFav
        if (isFav) {
            current.removeAll { cleanName(it) == cleanName(clean) }
        } else {
            current.add(clean)
        }
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        val key = getScopedKey(userId)
        currentPrefs?.edit()
            ?.putStringSet(key, current)
            ?.putBoolean("user_explicitly_favorited", true)
            ?.apply()
        _favoriteCrops.value = current

        // Synchronize with Supabase cloud
        val uid = userId ?: getCurrentUserId()
        if (!uid.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (willBeFavorite) {
                        SupabaseClient.client.from("user_favorite_crops").upsert(
                            UserFavoriteCropDto(userId = uid, cropName = clean)
                        )
                    } else {
                        SupabaseClient.client.from("user_favorite_crops").delete {
                            filter {
                                eq("user_id", uid)
                                eq("crop_name", clean)
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        }

        return willBeFavorite
    }

    fun setFavorite(cropName: String, favorite: Boolean, userId: String? = null) {
        val clean = cropName.trim()
        val current = _favoriteCrops.value.toMutableSet()
        if (favorite) {
            if (current.none { cleanName(it) == cleanName(clean) }) {
                current.add(clean)
            }
        } else {
            current.removeAll { cleanName(it) == cleanName(clean) }
        }
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        val key = getScopedKey(userId)
        currentPrefs?.edit()
            ?.putStringSet(key, current)
            ?.putBoolean("user_explicitly_favorited", true)
            ?.apply()
        _favoriteCrops.value = current

        // Synchronize with Supabase cloud
        val uid = userId ?: getCurrentUserId()
        if (!uid.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (favorite) {
                        SupabaseClient.client.from("user_favorite_crops").upsert(
                            UserFavoriteCropDto(userId = uid, cropName = clean)
                        )
                    } else {
                        SupabaseClient.client.from("user_favorite_crops").delete {
                            filter {
                                eq("user_id", uid)
                                eq("crop_name", clean)
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun getFavoritesList(): List<String> = _favoriteCrops.value.toList()

    fun getFavoritesForUser(userId: String): Set<String> {
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        val key = getScopedKey(userId)
        return currentPrefs?.getStringSet(key, null)?.toSet() ?: emptySet()
    }

    private fun cleanName(name: String): String = name.trim().lowercase()

    companion object {
        private const val PREFS_NAME = "maptanim_favorite_crops"
        private const val KEY_LEGACY_FAVORITES = "favorite_crop_names"
        private const val KEY_FAVORITES = "fav_crops"
        private const val KEY_FAVORITES_ANON = "fav_crops_anon"

        @Volatile
        private var instance: FavoriteCropsManager? = null

        fun getInstance(context: Context? = null): FavoriteCropsManager {
            val existing = instance
            if (existing != null) {
                if (context != null) {
                    existing.bindContext(context)
                }
                return existing
            }
            return synchronized(this) {
                instance ?: FavoriteCropsManager(context).also { instance = it }
            }
        }
    }
}
