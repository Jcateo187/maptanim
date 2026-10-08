package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FavoriteCropsManager — Local persistence manager for user favorite crops.
 * Synchronizes favoriting actions in CropInformationDialog with the Profile screen.
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

    private fun loadFavorites(): Set<String> {
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        val saved = currentPrefs?.getStringSet(KEY_FAVORITES, null)
        return if (saved != null) {
            saved.toSet()
        } else {
            // Initial seed with classic Philippine staple crops
            val initial = setOf("Tomatoes", "Eggplant", "Pechay")
            currentPrefs?.edit()?.putStringSet(KEY_FAVORITES, initial)?.apply()
            initial
        }
    }

    fun isFavorite(cropName: String): Boolean {
        val clean = cleanName(cropName)
        return _favoriteCrops.value.any { cleanName(it) == clean }
    }

    fun toggleFavorite(cropName: String): Boolean {
        val clean = cropName.trim()
        val current = _favoriteCrops.value.toMutableSet()
        val isFav = current.any { cleanName(it) == cleanName(clean) }
        if (isFav) {
            current.removeAll { cleanName(it) == cleanName(clean) }
        } else {
            current.add(clean)
        }
        val currentPrefs = prefs ?: (RepositoryProvider.appContext ?: ctx)?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.also { prefs = it }
        currentPrefs?.edit()?.putStringSet(KEY_FAVORITES, current)?.apply()
        _favoriteCrops.value = current
        return !isFav
    }

    fun setFavorite(cropName: String, favorite: Boolean) {
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
        currentPrefs?.edit()?.putStringSet(KEY_FAVORITES, current)?.apply()
        _favoriteCrops.value = current
    }

    fun getFavoritesList(): List<String> = _favoriteCrops.value.toList()

    private fun cleanName(name: String): String = name.trim().lowercase()

    companion object {
        private const val PREFS_NAME = "maptanim_favorite_crops"
        private const val KEY_FAVORITES = "favorite_crop_names"

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
