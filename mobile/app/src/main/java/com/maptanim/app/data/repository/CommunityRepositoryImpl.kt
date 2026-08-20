package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.CommunityRemoteDataSource
import com.maptanim.app.data.remote.dto.CommunityCommentDto
import com.maptanim.app.data.remote.dto.CommunityPostDto
import com.maptanim.app.data.remote.dto.toDomain
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.repository.CommunityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CommunityRepositoryImpl(
    private val remoteDataSource: CommunityRemoteDataSource = CommunityRemoteDataSource()
) : CommunityRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val postsState = MutableStateFlow(defaultCommunityPosts)
    private val commentsState = MutableStateFlow(defaultCommunityComments)

    init {
        // Asynchronously fetch latest remote community posts from Supabase
        scope.launch {
            refreshPosts()
        }
    }

    override fun observePosts(): Flow<List<CommunityPost>> = postsState.map { it }

    override fun observeCommentsForPost(postId: String): Flow<List<CommunityComment>> {
        // Fetch fresh comments from Supabase for this specific post
        scope.launch {
            remoteDataSource.getCommentsForPost(postId).onSuccess { remoteComments ->
                if (remoteComments.isNotEmpty()) {
                    val domainComments = remoteComments.map { it.toDomain() }
                    val currentOtherComments = commentsState.value.filter { it.postId != postId }
                    commentsState.value = currentOtherComments + domainComments
                }
            }
        }
        return commentsState.map { list -> list.filter { it.postId == postId } }
    }

    override suspend fun refreshPosts() {
        remoteDataSource.getAllPosts().onSuccess { remoteList ->
            if (remoteList.isNotEmpty()) {
                val currentLikedIds = postsState.value.filter { it.isLikedByMe }.map { it.id }.toSet()
                val domainPosts = remoteList.map { dto ->
                    dto.toDomain(isLikedByMe = dto.id in currentLikedIds)
                }
                postsState.value = domainPosts
            }
        }
    }

    override suspend fun toggleLikePost(postId: String) {
        var nextLikesCount = 0
        postsState.value = postsState.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLikedByMe
                val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                val sanitized = newCount.coerceAtLeast(0)
                nextLikesCount = sanitized
                post.copy(isLikedByMe = newLiked, likesCount = sanitized)
            } else post
        }

        // Sync like count to Supabase
        scope.launch {
            remoteDataSource.updatePostLikes(postId, nextLikesCount)
        }
    }

    override suspend fun addPost(
        title: String,
        category: String,
        content: String,
        authorName: String
    ) {
        val newId = "post_${System.currentTimeMillis()}"
        val sanitizedAuthor = authorName.ifBlank { "Local Farmer" }

        val newPost = CommunityPost(
            id = newId,
            authorName = sanitizedAuthor,
            category = category,
            title = title,
            content = content,
            likesCount = 1,
            commentsCount = 0,
            timestamp = "Just now",
            isLikedByMe = true,
            tags = listOf(category, "Murcia", "NegrosOccidental")
        )
        postsState.value = listOf(newPost) + postsState.value

        // Sync to Supabase PostgREST table
        scope.launch {
            val dto = CommunityPostDto(
                id = newId,
                author_name = sanitizedAuthor,
                category = category,
                title = title,
                content = content,
                likes_count = 1,
                comments_count = 0,
                is_pinned = false,
                tags = listOf(category, "Murcia", "NegrosOccidental")
            )
            remoteDataSource.createPost(dto)
        }
    }

    override suspend fun addComment(postId: String, content: String, authorName: String) {
        val newId = "comm_${System.currentTimeMillis()}"
        val sanitizedAuthor = authorName.ifBlank { "Farmer Partner" }

        val newComment = CommunityComment(
            id = newId,
            postId = postId,
            authorName = sanitizedAuthor,
            content = content,
            timestamp = "Just now"
        )
        commentsState.value = commentsState.value + newComment

        // Update comment count on local post
        postsState.value = postsState.value.map { post ->
            if (post.id == postId) {
                post.copy(commentsCount = post.commentsCount + 1)
            } else post
        }

        // Sync comment to Supabase
        scope.launch {
            val dto = CommunityCommentDto(
                id = newId,
                post_id = postId,
                author_name = sanitizedAuthor,
                content = content
            )
            remoteDataSource.addComment(dto)
        }
    }

    override suspend fun submitReport(
        targetType: String,
        targetId: String,
        targetName: String,
        targetContent: String?,
        reason: String,
        details: String?,
        reporterName: String
    ): Result<Unit> {
        val newId = "rep_${System.currentTimeMillis()}"
        val dto = com.maptanim.app.data.remote.dto.CommunityReportDto(
            id = newId,
            reporter_name = reporterName.ifBlank { "Farmer Member" },
            target_type = targetType,
            target_id = targetId,
            target_name = targetName,
            target_content = targetContent,
            reason = reason,
            details = details,
            status = "PENDING"
        )
        return remoteDataSource.submitReport(dto)
    }
}


internal val defaultCommunityPosts = listOf(
    CommunityPost(
        id = "post_1",
        authorName = "Mang Jose Parreño",
        category = "PEST_ALERT",
        title = "🚨 Fall Armyworm Outbreak in Murcia & Talisay Bed Plots",
        content = "Attention fellow vegetable growers! We spotted Fall Armyworm caterpillars on early sweet corn and bean plots around Barangay Canlandog, Murcia. Spraying Neem oil extract mixed with soapy water early morning has proven effective. Check your leaves for tiny hole punctures!",
        likesCount = 18,
        commentsCount = 2,
        timestamp = "2 hours ago",
        isLikedByMe = false,
        tags = listOf("PestAlert", "Armyworm", "Corn", "Murcia")
    ),
    CommunityPost(
        id = "post_2",
        authorName = "Ka Ryan Vasquez",
        category = "FARMING_TIP",
        title = "💡 High-Yield Tomato Diamante Max F1 Double A-Frame Trellising",
        content = "For those planting Diamante Max F1 tomato this dry season, using a 2-meter bamboo A-frame trellis with nylon twine stringing doubled our yield harvest compared to single stake poles. It provides superior airflow and keeps lower branches off damp ground.",
        likesCount = 24,
        commentsCount = 1,
        timestamp = "5 hours ago",
        isLikedByMe = true,
        tags = listOf("FarmingTip", "Tomato", "Trellis", "HighYield")
    ),
    CommunityPost(
        id = "post_3",
        authorName = "Aling Maria Juanillo",
        category = "EQUIPMENT",
        title = "🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds",
        content = "I have 50 extra bundles of treated 6ft bamboo stakes and 3 packets of certified Sitaw (String Beans) seeds available for trade in Silay. Looking to trade for surplus Pechay or Lettuce seeds. Send me a message!",
        likesCount = 12,
        commentsCount = 0,
        timestamp = "Yesterday",
        isLikedByMe = false,
        tags = listOf("SeedSwap", "BambooStakes", "Sitaw", "Silay")
    ),
    CommunityPost(
        id = "post_4",
        authorName = "Tatay Juan Cateo",
        category = "GENERAL",
        title = "❓ Best Organic Solution for Flea Beetles on Talong Leaves?",
        content = "Magandang araw mga kasama. My 40-day old Eggplant (Talong) plot is starting to show small pinhole damage from flea beetles. Is baking soda spray or wood ash dusting better for organic pest control without burning young leaves?",
        likesCount = 9,
        commentsCount = 1,
        timestamp = "2 days ago",
        isLikedByMe = false,
        tags = listOf("Question", "Eggplant", "OrganicPestControl", "Talong")
    )
)

internal val defaultCommunityComments = listOf(
    CommunityComment(
        id = "comm_1",
        postId = "post_1",
        authorName = "Aling Danica",
        content = "Salamat sa babala Mang Jose! Applied wood ash around our corn whorls this morning, so far it contained the spread.",
        timestamp = "1 hour ago"
    ),
    CommunityComment(
        id = "comm_2",
        postId = "post_1",
        authorName = "Jason B.",
        content = "You can also release Trichogramma parasitic wasps from the BPI office to control egg clusters naturally.",
        timestamp = "45 mins ago"
    ),
    CommunityComment(
        id = "comm_3",
        postId = "post_2",
        authorName = "James C.",
        content = "Tested this A-frame method on plot 3 last week! Stems are upright even after heavy afternoon wind.",
        timestamp = "3 hours ago"
    ),
    CommunityComment(
        id = "comm_4",
        postId = "post_4",
        authorName = "Ka Ryan Vasquez",
        content = "Wood ash mixed with dry sand (1:1 ratio) dusted lightly early morning while dew is present works best against flea beetles!",
        timestamp = "1 day ago"
    )
)

