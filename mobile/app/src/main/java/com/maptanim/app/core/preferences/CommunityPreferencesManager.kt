package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.repository.RepositoryProvider

/**
 * Local preference manager to track user-specific community forum interactions:
 * 1. Post IDs created / authored by the user.
 * 2. Post IDs liked / reacted to by the user.
 *
 * Persisted per user ID (with fallback to "guest") so activity remains accurate across cold starts.
 */
class CommunityPreferencesManager(context: Context? = null) {

    private val ctx: Context? = context?.applicationContext ?: RepositoryProvider.appContext
    private val prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLikedPostIds(userId: String?): Set<String> {
        val key = getLikedKeyForUser(userId)
        return prefs?.getStringSet(key, emptySet())?.toSet() ?: emptySet()
    }

    fun isPostLiked(userId: String?, postId: String): Boolean {
        return getLikedPostIds(userId).contains(postId)
    }

    fun setPostLiked(userId: String?, postId: String, liked: Boolean) {
        val key = getLikedKeyForUser(userId)
        val currentSet = prefs?.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (liked) {
            currentSet.add(postId)
        } else {
            currentSet.remove(postId)
        }
        prefs?.edit()?.putStringSet(key, currentSet)?.apply()
    }

    fun getMyPostIds(userId: String?): Set<String> {
        val key = getAuthoredKeyForUser(userId)
        return prefs?.getStringSet(key, emptySet())?.toSet() ?: emptySet()
    }

    fun isMyPost(userId: String?, postId: String): Boolean {
        return getMyPostIds(userId).contains(postId)
    }

    fun addMyPostId(userId: String?, postId: String) {
        val key = getAuthoredKeyForUser(userId)
        val currentSet = prefs?.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(postId)
        prefs?.edit()?.putStringSet(key, currentSet)?.apply()
    }

    fun getFriendIds(userId: String?): Set<String> {
        val key = getFriendsKeyForUser(userId)
        return prefs?.getStringSet(key, emptySet())?.toSet() ?: emptySet()
    }

    fun isFriend(userId: String?, friendId: String): Boolean {
        return getFriendIds(userId).contains(friendId)
    }

    fun addFriend(userId: String?, friendId: String) {
        val key = getFriendsKeyForUser(userId)
        val currentSet = prefs?.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(friendId)
        prefs?.edit()?.putStringSet(key, currentSet)?.apply()
    }

    fun removeFriend(userId: String?, friendId: String) {
        val key = getFriendsKeyForUser(userId)
        val currentSet = prefs?.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.remove(friendId)
        prefs?.edit()?.putStringSet(key, currentSet)?.apply()
    }

    private fun getLikedKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "liked_post_ids_guest" else "liked_post_ids_$userId"
    }

    private fun getAuthoredKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "authored_post_ids_guest" else "authored_post_ids_$userId"
    }

    private fun getFriendsKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "friends_ids_guest" else "friends_ids_$userId"
    }

    companion object {
        private const val PREFS_NAME = "maptanim_community_prefs"

        @Volatile
        private var instance: CommunityPreferencesManager? = null

        fun getInstance(context: Context? = null): CommunityPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: CommunityPreferencesManager(context).also { instance = it }
            }
        }
    }
}
