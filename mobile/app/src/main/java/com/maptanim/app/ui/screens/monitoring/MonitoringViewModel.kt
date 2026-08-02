package com.maptanim.app.ui.screens.monitoring

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MonitoringNavSection(val title: String) {
    MY_PLANTS("My Plants"),
    TIMELINE("Timeline"),
    CALENDAR("Calendar"),
    COMPANIONS("Companions"),
    GROWING_TIPS("Growing Tips"),
    PEST("Pest")
}

enum class SeasonalityFilter(val label: String) {
    ALL("All"),
    SEASONAL("Seasonal"),
    PERMANENT("Permanent"),
    SEMI_PERMANENT("Semi Permanent")
}

enum class CropCategoryFilter(val label: String) {
    ALL("All Categories"),
    LEAFY("Leafy"),
    ROOT("Root"),
    BULB("Bulb"),
    STEM("Stem"),
    FLOWER("Flower"),
    PODDED("Podded"),
    TUBER("Tuber"),
    FRUIT("Fruit")
}

data class MonitoredPlant(
    val id: String,
    val cropName: String,
    val localName: String,
    val plotLabel: String,
    val seasonality: SeasonalityFilter,
    val category: CropCategoryFilter,
    val currentStageIndex: Int, // 0..3
    val stageName: String,
    val daysPlanted: Int,
    val daysToHarvest: Int,
    val healthStatus: String,
    val companionCrop: String,
    val companionStatus: String,
    val growingTip: String,
    val pestInfo: String,
    val assetPath: String,
    val isMonitoringStarted: Boolean = false
)

data class MonitoringUiState(
    val selectedNavSection: MonitoringNavSection = MonitoringNavSection.MY_PLANTS,
    val selectedSeasonality: SeasonalityFilter = SeasonalityFilter.ALL,
    val selectedCategory: CropCategoryFilter = CropCategoryFilter.ALL,
    val searchQuery: String = "",
    val isCategoryDropdownExpanded: Boolean = false,
    val plantedCrops: List<MonitoredPlant> = emptyList()
)

class MonitoringViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        loadPlantedCrops()
    }

    private fun loadPlantedCrops() {
        val initialCrops = listOf(
            MonitoredPlant(
                id = "p1",
                cropName = "Carrot",
                localName = "Karot",
                plotLabel = "PLOT 1",
                seasonality = SeasonalityFilter.SEASONAL,
                category = CropCategoryFilter.ROOT,
                currentStageIndex = 1,
                stageName = "Vegetative",
                daysPlanted = 28,
                daysToHarvest = 75,
                healthStatus = "Healthy",
                companionCrop = "String Beans",
                companionStatus = "Good Companions, No Problems",
                growingTip = "Keep soil loose and well-drained. Water deeply once per week to encourage straight taproot development.",
                pestInfo = "Watch out for Carrot Rust Fly. Use row covers and intercrop with legumes or onions.",
                assetPath = "crops/crop_carrot_1.png",
                isMonitoringStarted = true
            ),
            MonitoredPlant(
                id = "p2",
                cropName = "String Beans",
                localName = "Sitaw",
                plotLabel = "PLOT 2",
                seasonality = SeasonalityFilter.SEASONAL,
                category = CropCategoryFilter.PODDED,
                currentStageIndex = 0,
                stageName = "Seedling",
                daysPlanted = 0,
                daysToHarvest = 60,
                healthStatus = "Pending Start",
                companionCrop = "Carrot",
                companionStatus = "Good Companions, No Problems",
                growingTip = "Provide sturdy trellis support for climbing vines. Harvest pods when firm and crisp.",
                pestInfo = "Inspect under leaves for aphids and pod borers. Apply neem spray during early podding.",
                assetPath = "crops/crop_stringbeans_1.png",
                isMonitoringStarted = false
            )
        )
        _uiState.update { it.copy(plantedCrops = initialCrops) }
    }

    fun startMonitoring(plantId: String) {
        _uiState.update { state ->
            val updated = state.plantedCrops.map { plant ->
                if (plant.id == plantId) {
                    plant.copy(
                        isMonitoringStarted = true,
                        daysPlanted = 1,
                        healthStatus = "Active Monitoring"
                    )
                } else plant
            }
            state.copy(plantedCrops = updated)
        }
    }

    fun updatePlantedCrops(crops: List<MonitoredPlant>) {
        _uiState.update { it.copy(plantedCrops = crops) }
    }

    fun selectNavSection(section: MonitoringNavSection) {
        _uiState.update { it.copy(selectedNavSection = section) }
    }

    fun selectSeasonality(seasonality: SeasonalityFilter) {
        _uiState.update { it.copy(selectedSeasonality = seasonality) }
    }

    fun selectCategory(category: CropCategoryFilter) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryDropdownExpanded = false) }
    }

    fun toggleCategoryDropdown(expanded: Boolean) {
        _uiState.update { it.copy(isCategoryDropdownExpanded = expanded) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredCrops(): List<MonitoredPlant> {
        val state = _uiState.value
        return state.plantedCrops.filter { plant ->
            val matchSeason = state.selectedSeasonality == SeasonalityFilter.ALL || plant.seasonality == state.selectedSeasonality
            val matchCategory = state.selectedCategory == CropCategoryFilter.ALL || plant.category == state.selectedCategory
            val matchSearch = state.searchQuery.isBlank() ||
                    plant.cropName.contains(state.searchQuery, ignoreCase = true) ||
                    plant.localName.contains(state.searchQuery, ignoreCase = true) ||
                    plant.plotLabel.contains(state.searchQuery, ignoreCase = true)
            matchSeason && matchCategory && matchSearch
        }
    }
}
