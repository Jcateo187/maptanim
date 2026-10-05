package com.maptanim.app.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.CropKnowledgeRepository
import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.features.library.mapper.AgronomicGuideMapper
import com.maptanim.app.features.library.model.VegetableAgronomicGuide
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * LibraryViewModel — Drives the Crop Library and deep agronomic reference guide.
 * Observes crops directly from Room DB and dynamically synthesizes the full
 * VegetableAgronomicGuide from Room knowledge tables.
 */
class LibraryViewModel(
    private val cropRepository: CropRepository = CropRepositoryImpl(),
    private val cropKnowledgeRepository: CropKnowledgeRepository = RepositoryProvider.cropKnowledgeRepository
) : ViewModel() {

    val crops: StateFlow<List<Crop>> = cropRepository.observeAllCrops()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCrop = MutableStateFlow<Crop?>(null)
    val selectedCrop: StateFlow<Crop?> = _selectedCrop.asStateFlow()

    private val _selectedCropGuide = MutableStateFlow<VegetableAgronomicGuide?>(null)
    val selectedCropGuide: StateFlow<VegetableAgronomicGuide?> = _selectedCropGuide.asStateFlow()

    private val _isLoadingGuide = MutableStateFlow(false)
    val isLoadingGuide: StateFlow<Boolean> = _isLoadingGuide.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSelectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectCrop(crop: Crop?) {
        _selectedCrop.value = crop
        if (crop == null) {
            _selectedCropGuide.value = null
            return
        }

        viewModelScope.launch {
            _isLoadingGuide.value = true
            try {
                val cropName = crop.name
                val varieties = cropKnowledgeRepository.getVarieties(cropName)
                val growthStages = cropKnowledgeRepository.getGrowthStages(cropName)
                val soilCompatibilities = cropKnowledgeRepository.getAllSoilCompatibilities()
                    .filter { it.cropName.equals(cropName, ignoreCase = true) }
                val pestGuides = cropKnowledgeRepository.getAllPestGuides()
                    .filter { it.cropName.equals(cropName, ignoreCase = true) }
                val yieldStudies = cropKnowledgeRepository.getYieldStudies(cropName)

                val guide = AgronomicGuideMapper.mapToGuide(
                    crop = crop,
                    varieties = varieties,
                    growthStages = growthStages,
                    soilCompatibilities = soilCompatibilities,
                    pestGuides = pestGuides,
                    yieldStudies = yieldStudies
                )
                _selectedCropGuide.value = guide
            } catch (e: Exception) {
                // Fallback to minimal mapper if unexpected error
                _selectedCropGuide.value = AgronomicGuideMapper.mapToGuide(
                    crop = crop,
                    varieties = emptyList(),
                    growthStages = emptyList(),
                    soilCompatibilities = emptyList(),
                    pestGuides = emptyList(),
                    yieldStudies = emptyList()
                )
            } finally {
                _isLoadingGuide.value = false
            }
        }
    }
}
