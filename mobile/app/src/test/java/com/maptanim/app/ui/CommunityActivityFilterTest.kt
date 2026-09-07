package com.maptanim.app.ui

import com.maptanim.app.domain.model.CommunityPost
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for community forum user-based activity filtering logic.
 * Requirements:
 * 1. User's authored post must appear in profile activity.
 * 2. Unreacted posts by other users must NOT appear in profile activity.
 * 3. Posts by other users that the current user reacted to (liked) MUST appear in profile activity.
 * 4. Posts by other users that are unreacted (unliked) must NOT appear in profile activity.
 */
class CommunityActivityFilterTest {

    private val currentUserId = "user-123"
    private val currentUserNickname = "Ka-Pedro"

    private fun filterUserActivityPosts(
        posts: List<CommunityPost>,
        userId: String,
        nickname: String,
        myLocalPostIds: Set<String> = emptySet(),
        myLocalLikedIds: Set<String> = emptySet()
    ): List<CommunityPost> {
        return posts.filter { post ->
            val isAuthoredByMe = (post.authorId != null && post.authorId == userId) ||
                    (nickname.isNotBlank() && post.authorName.equals(nickname, ignoreCase = true)) ||
                    post.authorName.equals("You", ignoreCase = true) ||
                    post.id in myLocalPostIds

            val isReactedByMe = post.isLikedByMe || post.id in myLocalLikedIds

            isAuthoredByMe || isReactedByMe
        }
    }

    @Test
    fun `user authored post is included in profile activity`() {
        val myPost = CommunityPost(
            id = "post-1",
            authorId = currentUserId,
            authorName = currentUserNickname,
            category = "FARMING_TIP",
            title = "Organic Pest Repellent with Neem",
            content = "Use neem oil spray in the late afternoon.",
            timestamp = "Just now",
            isLikedByMe = false
        )
        val posts = listOf(myPost)

        val result = filterUserActivityPosts(posts, currentUserId, currentUserNickname)
        assertEquals(1, result.size)
        assertEquals("post-1", result[0].id)
    }

    @Test
    fun `other user unreacted post is excluded from profile activity`() {
        val otherPost = CommunityPost(
            id = "post-2",
            authorId = "user-456",
            authorName = "Maria Farmer",
            category = "PEST_ALERT",
            title = "Armyworm warning in Region IV-A",
            content = "Inspect corn leaves immediately.",
            timestamp = "Just now",
            isLikedByMe = false
        )
        val posts = listOf(otherPost)

        val result = filterUserActivityPosts(posts, currentUserId, currentUserNickname)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `other user reacted post is included in profile activity`() {
        val reactedPost = CommunityPost(
            id = "post-3",
            authorId = "user-456",
            authorName = "Maria Farmer",
            category = "EQUIPMENT",
            title = "Best small hand tiller for clay soil",
            content = "Looking for recommendations for 200 sqm plot.",
            timestamp = "Just now",
            isLikedByMe = true
        )
        val posts = listOf(reactedPost)

        val result = filterUserActivityPosts(posts, currentUserId, currentUserNickname)
        assertEquals(1, result.size)
        assertEquals("post-3", result[0].id)
    }

    @Test
    fun `mixed stream only retains user authored and reacted posts`() {
        val myPost = CommunityPost(
            id = "post-1",
            authorId = currentUserId,
            authorName = currentUserNickname,
            category = "FARMING_TIP",
            title = "Tomato Pruning",
            content = "Pruning suckers gives bigger fruit.",
            timestamp = "Just now",
            isLikedByMe = false
        )
        val unreactedOtherPost = CommunityPost(
            id = "post-2",
            authorId = "user-999",
            authorName = "Juan",
            category = "GENERAL",
            title = "Weather forecast this week",
            content = "Rain expected starting Tuesday.",
            timestamp = "Just now",
            isLikedByMe = false
        )
        val reactedOtherPost = CommunityPost(
            id = "post-3",
            authorId = "user-888",
            authorName = "Lita",
            category = "EQUIPMENT",
            title = "Drip irrigation nozzle review",
            content = "Tested 3 models, here are the findings.",
            timestamp = "Just now",
            isLikedByMe = true
        )

        val allPosts = listOf(myPost, unreactedOtherPost, reactedOtherPost)
        val result = filterUserActivityPosts(allPosts, currentUserId, currentUserNickname)

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "post-1" })
        assertFalse(result.any { it.id == "post-2" })
        assertTrue(result.any { it.id == "post-3" })
    }
}
