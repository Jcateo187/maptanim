package com.maptanim.app.ui.screens.knowledgebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.CropRepositoryImpl
import com.maptanim.app.data.repository.KnowledgeBaseRepositoryImpl
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.SeasonalWindowInfo
import com.maptanim.app.domain.model.SoilGuide
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.KnowledgeBaseRepository
import kotlinx.coroutines.flow.*

enum class LibraryTab(val title: String, val iconEmoji: String) {
    CROPS("Crop Catalog", "🌾"),
    PESTS("Pests & Diseases", "🐛"),
    SOILS("Soil Guides", "🪴"),
    CALENDAR("Planting Calendar", "📅")
}

data class LibraryUiState(
    val activeTab: LibraryTab = LibraryTab.CROPS,
    val searchQuery: String = "",
    val selectedCategoryFilter: String = "ALL",
    val crops: List<Crop> = emptyList(),
    val allCropsCount: Int = 0,
    val pests: List<PestGuide> = emptyList(),
    val soils: List<SoilGuide> = emptyList(),
    val seasonalWindows: List<SeasonalWindowInfo> = emptyList(),
    val selectedCrop: Crop? = null,
    val selectedPest: PestGuide? = null,
    val selectedSoil: SoilGuide? = null
)

private data class FilterState(
    val activeTab: LibraryTab,
    val searchQuery: String,
    val categoryFilter: String,
    val selectedCrop: Crop?,
    val selectedPest: PestGuide?,
    val selectedSoil: SoilGuide?
)

private data class KnowledgeData(
    val allCrops: List<Crop>,
    val allPests: List<PestGuide>,
    val allSoils: List<SoilGuide>,
    val allWindows: List<SeasonalWindowInfo>
)

class LibraryViewModel(
    cropRepository: CropRepository = CropRepositoryImpl(),
    knowledgeBaseRepository: KnowledgeBaseRepository = KnowledgeBaseRepositoryImpl()
) : ViewModel() {

    private val _activeTab = MutableStateFlow(LibraryTab.CROPS)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryFilter = MutableStateFlow("ALL")
    private val _selectedCrop = MutableStateFlow<Crop?>(null)
    private val _selectedPest = MutableStateFlow<PestGuide?>(null)
    private val _selectedSoil = MutableStateFlow<SoilGuide?>(null)

    private val filterStateFlow: Flow<FilterState> = combine(
        _activeTab,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedCrop,
        combine(_selectedPest, _selectedSoil) { pest, soil -> pest to soil }
    ) { tab, query, category, crop, (pest, soil) ->
        FilterState(tab, query, category, crop, pest, soil)
    }

    private val knowledgeDataFlow: Flow<KnowledgeData> = combine(
        cropRepository.observeAllCrops(),
        knowledgeBaseRepository.observePestGuides(),
        knowledgeBaseRepository.observeSoilGuides(),
        knowledgeBaseRepository.observeSeasonalWindows()
    ) { crops, pests, soils, windows ->
        KnowledgeData(crops, pests, soils, windows)
    }

    val uiState: StateFlow<LibraryUiState> = combine(
        filterStateFlow,
        knowledgeDataFlow
    ) { filters, data ->
        val query = filters.searchQuery
        val category = filters.categoryFilter

        val filteredCrops = data.allCrops.filter { crop ->
            val matchesCategory = if (category == "ALL") true else {
                crop.category.equals(category, ignoreCase = true) ||
                crop.category.contains(category, ignoreCase = true) ||
                category.contains(crop.category, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    crop.name.contains(query, ignoreCase = true) ||
                    crop.localName?.contains(query, ignoreCase = true) == true ||
                    crop.botanicalName?.contains(query, ignoreCase = true) == true
            matchesCategory && matchesQuery
        }

        val filteredPests = data.allPests.filter { pest ->
            query.isBlank() ||
                    pest.name.contains(query, ignoreCase = true) ||
                    pest.localName.contains(query, ignoreCase = true) ||
                    pest.affectedCrops.any { it.contains(query, ignoreCase = true) }
        }

        val filteredSoils = data.allSoils.filter { soil ->
            query.isBlank() ||
                    soil.title.contains(query, ignoreCase = true) ||
                    soil.localName.contains(query, ignoreCase = true) ||
                    soil.bestCrops.any { it.contains(query, ignoreCase = true) }
        }

        val filteredWindows = data.allWindows.filter { window ->
            query.isBlank() ||
                    window.cropName.contains(query, ignoreCase = true) ||
                    window.localName.contains(query, ignoreCase = true)
        }

        LibraryUiState(
            activeTab = filters.activeTab,
            searchQuery = query,
            selectedCategoryFilter = category,
            crops = filteredCrops,
            allCropsCount = data.allCrops.size,
            pests = filteredPests,
            soils = filteredSoils,
            seasonalWindows = filteredWindows,
            selectedCrop = filters.selectedCrop,
            selectedPest = filters.selectedPest,
            selectedSoil = filters.selectedSoil
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun selectTab(tab: LibraryTab) {
        _activeTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun selectCrop(crop: Crop?) {
        _selectedCrop.value = crop
    }

    fun selectPest(pest: PestGuide?) {
        _selectedPest.value = pest
    }

    fun selectSoil(soil: SoilGuide?) {
        _selectedSoil.value = soil
    }
}
