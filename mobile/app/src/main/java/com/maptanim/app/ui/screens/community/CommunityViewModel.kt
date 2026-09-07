package com.maptanim.app.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.core.preferences.CommunityPreferencesManager
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.ProfileRepository
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.repository.CommunityRepository
import com.maptanim.app.domain.repository.UserRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CommunityMember(
    val id: String,
    val name: String,
    val statusText: String,
    val avatarUrl: String? = null
)

data class CommunityUiState(
    val searchQuery: String = "",
    val posts: List<CommunityPost> = emptyList(),
    val totalPostsCount: Int = 0,
    val selectedPost: CommunityPost? = null,
    val selectedPostComments: List<CommunityComment> = emptyList(),
    val reportNotice: String? = null,
    val currentUserName: String = "You",
    val currentUserId: String? = null,
    val communityMembers: List<CommunityMember> = emptyList(),
    val friends: List<CommunityMember> = emptyList(),
    val isPublishingPost: Boolean = false,
    val postNotice: String? = null,
    val postNoticeIsError: Boolean = false
)

class CommunityViewModel(
    private val repository: CommunityRepository = RepositoryProvider.communityRepository,
    private val userRepository: UserRepository = RepositoryProvider.userRepository,
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedPost = MutableStateFlow<CommunityPost?>(null)
    private val _reportNotice = MutableStateFlow<String?>(null)
    private val _currentUserName = MutableStateFlow("You")
    private val _currentUserId = MutableStateFlow<String?>(null)
    private val _communityMembers = MutableStateFlow<List<CommunityMember>>(emptyList())
    private val _friendIds = MutableStateFlow<Set<String>>(emptySet())
    private val _isPublishingPost = MutableStateFlow(false)
    private val _postNotice = MutableStateFlow<String?>(null)
    private val _postNoticeIsError = MutableStateFlow(false)

    init {
        val authId = try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
        _currentUserId.value = authId
        _friendIds.value = CommunityPreferencesManager.getInstance().getFriendIds(authId)

        viewModelScope.launch {
            userRepository.observeUserProfile().collect { userProfile ->
                val name = userProfile.nickname.ifBlank {
                    userProfile.boundEmail?.substringBefore('@')?.ifBlank { null } ?: "You"
                }
                _currentUserName.value = name
                val resolvedId = authId ?: userProfile.id.ifBlank { null }
                _currentUserId.value = resolvedId
                _friendIds.value = CommunityPreferencesManager.getInstance().getFriendIds(resolvedId)
            }
        }
        viewModelScope.launch {
            loadCommunityMembers()
        }
    }

    private suspend fun loadCommunityMembers() {
        val profiles = profileRepository.getAllProfiles()
        if (profiles.isNotEmpty()) {
            val members = profiles.map { p ->
                val shortId = p.id.replace("-", "").take(6).uppercase()
                val displayName = p.nickname?.takeIf { it.isNotBlank() } ?: "Ka-Tanim #$shortId"
                CommunityMember(
                    id = p.id,
                    name = displayName,
                    statusText = "Active",
                    avatarUrl = p.avatar
                )
            }
            _communityMembers.value = members
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val activePostComments: Flow<List<CommunityComment>> = _selectedPost.flatMapLatest { post ->
        if (post != null) {
            repository.observeCommentsForPost(post.id)
        } else {
            flowOf(emptyList())
        }
    }

    private data class FeedMeta(
        val query: String,
        val selectedPost: CommunityPost?,
        val reportNotice: String?,
        val isPublishingPost: Boolean,
        val postNotice: String?,
        val postNoticeIsError: Boolean
    )

    private data class PublishingMeta(
        val isPublishingPost: Boolean,
        val postNotice: String?,
        val postNoticeIsError: Boolean
    )

    private val publishingFlow: Flow<PublishingMeta> = combine(
        _isPublishingPost,
        _postNotice,
        _postNoticeIsError
    ) { isPub, pn, pnErr ->
        PublishingMeta(isPub, pn, pnErr)
    }

    private val feedMetaFlow: Flow<FeedMeta> = combine(
        _searchQuery,
        _selectedPost,
        _reportNotice,
        publishingFlow
    ) { q, sp, rn, pub ->
        FeedMeta(q, sp, rn, pub.isPublishingPost, pub.postNotice, pub.postNoticeIsError)
    }

    private data class UserMeta(
        val name: String,
        val userId: String?,
        val members: List<CommunityMember>,
        val friendIds: Set<String>
    )

    private val userMetaFlow: Flow<UserMeta> = combine(
        _currentUserName,
        _currentUserId,
        _communityMembers,
        _friendIds
    ) { name, uid, members, fIds ->
        UserMeta(name, uid, members, fIds)
    }

    val uiState: StateFlow<CommunityUiState> = combine(
        feedMetaFlow,
        repository.observePosts(),
        activePostComments,
        userMetaFlow
    ) { meta, allPosts, comments, user ->
        val filtered = allPosts.filter { post ->
            meta.query.isBlank() ||
                    post.title.contains(meta.query, ignoreCase = true) ||
                    post.content.contains(meta.query, ignoreCase = true) ||
                    post.authorName.contains(meta.query, ignoreCase = true) ||
                    post.tags.any { it.contains(meta.query, ignoreCase = true) }
        }

        val friendMembers = user.members.filter { it.id in user.friendIds }

        CommunityUiState(
            searchQuery = meta.query,
            posts = filtered,
            totalPostsCount = allPosts.size,
            selectedPost = meta.selectedPost,
            selectedPostComments = comments,
            reportNotice = meta.reportNotice,
            currentUserName = user.name,
            currentUserId = user.userId,
            communityMembers = user.members,
            friends = friendMembers,
            isPublishingPost = meta.isPublishingPost,
            postNotice = meta.postNotice,
            postNoticeIsError = meta.postNoticeIsError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPost(post: CommunityPost?) {
        _selectedPost.value = post
    }

    fun toggleLikePost(postId: String) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
            _selectedPost.value?.let { current ->
                if (current.id == postId) {
                    val newLiked = !current.isLikedByMe
                    val newCount = if (newLiked) current.likesCount + 1 else current.likesCount - 1
                    _selectedPost.value = current.copy(isLikedByMe = newLiked, likesCount = newCount.coerceAtLeast(0))
                }
            }
        }
    }

    fun createPost(
        title: String,
        category: String,
        content: String,
        authorName: String = "",
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isPublishingPost.value = true
            val finalAuthor = authorName.ifBlank { _currentUserName.value }
            val result = repository.addPost(title, category, content, finalAuthor)
            _isPublishingPost.value = false
            if (result.isSuccess) {
                _postNotice.value = "Post published successfully! 🌾"
                _postNoticeIsError.value = false
                onResult(true)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage
                    ?: "Failed to publish post. Please check your network connection."
                _postNotice.value = errorMsg
                _postNoticeIsError.value = true
                onResult(false)
            }
        }
    }

    fun clearPostNotice() {
        _postNotice.value = null
    }

    fun addFriend(memberId: String) {
        val uid = _currentUserId.value
        CommunityPreferencesManager.getInstance().addFriend(uid, memberId)
        _friendIds.value = CommunityPreferencesManager.getInstance().getFriendIds(uid)
    }

    fun removeFriend(memberId: String) {
        val uid = _currentUserId.value
        CommunityPreferencesManager.getInstance().removeFriend(uid, memberId)
        _friendIds.value = CommunityPreferencesManager.getInstance().getFriendIds(uid)
    }

    fun addComment(postId: String, content: String, authorName: String = "") {
        val finalAuthor = authorName.ifBlank { _currentUserName.value }
        viewModelScope.launch {
            repository.addComment(postId, content, finalAuthor)
        }
    }

    fun submitReport(
        targetType: String,
        targetId: String,
        targetName: String,
        targetContent: String?,
        reason: String,
        details: String?,
        reporterName: String = "You"
    ) {
        val finalReporter = if (reporterName.isBlank() || reporterName == "You") _currentUserName.value.ifBlank { "You" } else reporterName
        viewModelScope.launch {
            repository.submitReport(
                targetType = targetType,
                targetId = targetId,
                targetName = targetName,
                targetContent = targetContent,
                reason = reason,
                details = details,
                reporterName = finalReporter
            )
            val typeLabel = when (targetType.uppercase()) {
                "USER" -> "User account"
                "COMMENT" -> "Comment"
                else -> "Post"
            }
            _reportNotice.value = "$typeLabel reported to Admin. Thank you for keeping our community safe."
        }
    }

    fun clearReportNotice() {
        _reportNotice.value = null
    }

    fun refreshPosts() {
        viewModelScope.launch {
            repository.refreshPosts()
            loadCommunityMembers()
        }
    }
}
