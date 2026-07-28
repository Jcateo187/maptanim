package com.maptanim.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun updateProfile(

        userId: String,

        nickname: String,

        avatar: String? = null

    ) {

        viewModelScope.launch {

            val result = repository.updateProfile(

                userId = userId,

                nickname = nickname,

                avatar = avatar

            )

            _isSaved.value = result.isSuccess

        }

    }

}