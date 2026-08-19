package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FarmPreferencesManager(context: Context? = null) {

    private val ctx: Context? = context?.applicationContext ?: RepositoryProvider.appContext
    private val prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _activeFarmChanges = MutableSharedFlow<Pair<String?, String>>(extraBufferCapacity = 5)
    val activeFarmChanges: SharedFlow<Pair<String?, String>> = _activeFarmChanges.asSharedFlow()

    fun getActiveFarmId(userId: String?): String? {
        val key = getKeyForUser(userId)
        return prefs?.getString(key, null)
    }

    fun setActiveFarmId(userId: String?, farmId: String) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.putString(key, farmId)?.apply()
        _activeFarmChanges.tryEmit(Pair(userId, farmId))
    }

    fun clearActiveFarmId(userId: String?) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.remove(key)?.apply()
    }

    private fun getKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "active_farm_id_guest" else "active_farm_id_$userId"
    }

    companion object {
        private const val PREFS_NAME = "maptanim_farm_prefs"

        @Volatile
        private var instance: FarmPreferencesManager? = null

        fun getInstance(context: Context? = null): FarmPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: FarmPreferencesManager(context).also { instance = it }
            }
        }
    }
}
