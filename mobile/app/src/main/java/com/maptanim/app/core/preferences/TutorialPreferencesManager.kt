package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.repository.RepositoryProvider

class TutorialPreferencesManager(context: Context? = null) {

    private val ctx: Context? = context?.applicationContext ?: RepositoryProvider.appContext
    private val prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isCompletedForUser(userId: String?): Boolean {
        val key = getKeyForUser(userId)
        return prefs?.getBoolean(key, false) ?: false
    }

    fun markCompletedForUser(userId: String?) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.putBoolean(key, true)?.apply()
    }

    fun resetTutorialForUser(userId: String?) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.putBoolean(key, false)?.apply()
    }

    private fun getKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "tutorial_completed_guest" else "tutorial_completed_$userId"
    }

    companion object {
        private const val PREFS_NAME = "maptanim_tutorial_prefs"

        @Volatile
        private var instance: TutorialPreferencesManager? = null

        fun getInstance(context: Context? = null): TutorialPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: TutorialPreferencesManager(context).also { instance = it }
            }
        }
    }
}

