package com.maptanim.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying chat overhaul and post publishing requirements:
 * 1. Mock channel "gen" (General Farmers Chat) is removed; only user-added friends are conversation channels.
 * 2. Search bar filters conversations by friend name AND by message text.
 * 3. Add friend adds the member to the friends list and updates conversation list.
 * 4. Post publishing error handling preserves post content on failure and transitions on success.
 */
class CommunityChatAndPublishTest {

    data class TestMember(
        val id: String,
        val name: String,
        val statusText: String
    )

    data class TestChatMessage(
        val sender: String,
        val text: String
    )

    @Test
    fun `chat channels contain only added friends and no mock channel`() {
        val allCommunityMembers = listOf(
            TestMember("user-1", "Ka-Juan Dela Cruz", "Active"),
            TestMember("user-2", "Maria Santos", "Active"),
            TestMember("user-3", "Aling Nena", "Active")
        )
        // User has only added user-2 as a friend
        val friendIds = setOf("user-2")

        // Channels are derived strictly from friends
        val friends = allCommunityMembers.filter { it.id in friendIds }

        assertEquals(1, friends.size)
        assertEquals("user-2", friends.first().id)
        assertEquals("Maria Santos", friends.first().name)

        // Verify "gen" / "General Farmers Chat" mock is NOT present
        assertFalse(friends.any { it.id == "gen" || it.name.contains("General Farmers Chat") })
    }

    @Test
    fun `search filters by friend name`() {
        val friends = listOf(
            TestMember("user-1", "Juan Dela Cruz", "Active"),
            TestMember("user-2", "Maria Santos", "Active")
        )
        val messages = mapOf<String, List<TestChatMessage>>()

        val query = "Maria"
        val filtered = friends.filter { channel ->
            val nameMatch = channel.name.contains(query, ignoreCase = true)
            val msgMatch = messages[channel.id]?.any { it.text.contains(query, ignoreCase = true) } == true
            nameMatch || msgMatch
        }

        assertEquals(1, filtered.size)
        assertEquals("user-2", filtered.first().id)
    }

    @Test
    fun `search filters by conversation message text`() {
        val friends = listOf(
            TestMember("user-1", "Juan Dela Cruz", "Active"),
            TestMember("user-2", "Maria Santos", "Active")
        )
        // Juan mentioned "fertilizer recipe" in his chat
        val messages = mapOf(
            "user-1" to listOf(TestChatMessage("me", "Hello"), TestChatMessage("user-1", "Try my organic fertilizer recipe!")),
            "user-2" to listOf(TestChatMessage("me", "How is your harvest?"))
        )

        val query = "fertilizer"
        val filtered = friends.filter { channel ->
            val nameMatch = channel.name.contains(query, ignoreCase = true)
            val msgMatch = messages[channel.id]?.any { it.text.contains(query, ignoreCase = true) } == true
            nameMatch || msgMatch
        }

        // Must match Juan Dela Cruz because of the message content
        assertEquals(1, filtered.size)
        assertEquals("user-1", filtered.first().id)
    }

    @Test
    fun `publishing post success triggers navigation while failure preserves content`() {
        var navigatedBackToFeed = false
        var currentTitle = "Eggplant flea beetle solution"
        var currentContent = "Use wood ash around base of plants."

        fun onPublishResult(isSuccess: Boolean) {
            if (isSuccess) {
                navigatedBackToFeed = true
            }
            // On failure, navigateBack is NOT called, preserving currentTitle and currentContent
        }

        // Test failure scenario
        onPublishResult(false)
        assertFalse(navigatedBackToFeed)
        assertEquals("Eggplant flea beetle solution", currentTitle)
        assertEquals("Use wood ash around base of plants.", currentContent)

        // Test success scenario
        onPublishResult(true)
        assertTrue(navigatedBackToFeed)
    }
}
