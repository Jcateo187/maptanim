package com.maptanim.app.viewmodel

data class AuthUiState(

    val isLoading: Boolean = false,

    val isSuccess: Boolean = false,

    val errorMessage: String? = null

)