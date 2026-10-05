package com.maptanim.app.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.repository.CropRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class VegetablesViewModel(
    cropRepository: CropRepository = CropRepositoryImpl()
) : ViewModel() {

    val crops: StateFlow<List<Crop>> = cropRepository.observeAllCrops()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
