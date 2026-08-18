package com.maptanim.app.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.CommunityRepositoryImpl
import com.maptanim.app.domain.model.CommunityComment
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.repository.CommunityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CommunityUiState(
    val searchQuery: String = "",
    val posts: List<CommunityPost> = emptyList(),
    val totalPostsCount: Int = 0,
    val selectedPost: CommunityPost? = null,
    val selectedPostComments: List<CommunityComment> = emptyList(),
    val reportNotice: String? = null
)

class CommunityViewModel(
    private val repository: CommunityRepository = CommunityRepositoryImpl()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedPost = MutableStateFlow<CommunityPost?>(null)
    private val _reportNotice = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val activePostComments: Flow<List<CommunityComment>> = _selectedPost.flatMapLatest { post ->
        if (post != null) {
            repository.observeCommentsForPost(post.id)
        } else {
            flowOf(emptyList())
        }
    }

    val uiState: StateFlow<CommunityUiState> = combine(
        _searchQuery,
        _selectedPost,
        repository.observePosts(),
        activePostComments,
        _reportNotice
    ) { query, selectedPost, allPosts, comments, notice ->
        val filtered = allPosts.filter { post ->
            query.isBlank() ||
                    post.title.contains(query, ignoreCase = true) ||
                    post.content.contains(query, ignoreCase = true) ||
                    post.authorName.contains(query, ignoreCase = true) ||
                    post.tags.any { it.contains(query, ignoreCase = true) }
        }

        CommunityUiState(
            searchQuery = query,
            posts = filtered,
            totalPostsCount = allPosts.size,
            selectedPost = selectedPost,
            selectedPostComments = comments,
            reportNotice = notice
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

    fun createPost(title: String, category: String, content: String, authorName: String) {
        viewModelScope.launch {
            repository.addPost(title, category, content, authorName)
        }
    }


    fun addComment(postId: String, content: String, authorName: String) {
        viewModelScope.launch {
            repository.addComment(postId, content, authorName)
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
        viewModelScope.launch {
            repository.submitReport(
                targetType = targetType,
                targetId = targetId,
                targetName = targetName,
                targetContent = targetContent,
                reason = reason,
                details = details,
                reporterName = reporterName
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
        }
    }
}


