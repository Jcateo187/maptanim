package com.maptanim.app.ui.screens.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
    val cropVariety: String? = null,
    val plotLabel: String,
    val seasonality: SeasonalityFilter,
    val category: CropCategoryFilter,
    val currentStageIndex: Int,
    val stageName: String,
    val daysPlanted: Int,
    val daysToHarvest: Int,
    val healthStatus: String,
    val companionCrop: String,
    val companionStatus: String,
    val growingTip: String,
    val pestInfo: String,
    val assetPath: String,
    val imageUrl: String? = null,
    val rawPlantedDate: String? = null,
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

class MonitoringViewModel(
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        observeLivePlantedCrops()
    }

    private fun observeLivePlantedCrops() {
        val farmerId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "farmer-1"
        viewModelScope.launch {
            combine(
                plotRepository.observeAllPlotsWithCrop(farmerId),
                cropRepository.observeAllCrops()
            ) { plots, crops ->
                plots.map { plot ->
                    val crop = crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                    val plantedDate = plot.plantedDate?.let {
                        try { LocalDate.parse(it.take(10)) } catch (e: Exception) { null }
                    }

                    val daysPlanted = if (plantedDate != null) ChronoUnit.DAYS.between(plantedDate, LocalDate.now()).toInt().coerceAtLeast(0) else 0
                    val varietyName = plot.cropVariety
                    val daysToHarvest = getVarietyDurationDays(plot.cropName ?: "", varietyName) ?: crop?.daysToHarvest ?: 60

                    val isStarted = plot.plantedDate != null && daysPlanted >= 0
                    val stageProgress = if (daysToHarvest > 0 && isStarted) (daysPlanted.toFloat() / daysToHarvest).coerceIn(0f, 1f) else 0f
                    val stageIndex = when {
                        stageProgress < 0.25f -> 0
                        stageProgress < 0.60f -> 1
                        stageProgress < 0.90f -> 2
                        else -> 3
                    }
                    val stageName = when (stageIndex) {
                        0 -> "Germination / Seedling"
                        1 -> "Vegetative Growth"
                        2 -> "Flowering / Podding"
                        else -> "Ready for Harvest"
                    }

                    MonitoredPlant(
                        id = plot.id,
                        cropName = crop?.name ?: plot.cropName ?: "Vegetable",
                        localName = crop?.localName ?: plot.cropName ?: "Gulay",
                        cropVariety = varietyName,
                        plotLabel = plot.plotLabel,
                        seasonality = SeasonalityFilter.SEASONAL,
                        category = mapCategory(crop?.category),
                        currentStageIndex = stageIndex,
                        stageName = stageName,
                        daysPlanted = daysPlanted,
                        daysToHarvest = daysToHarvest,
                        healthStatus = if (isStarted) "Active Monitoring" else "Pending Start",
                        companionCrop = crop?.companionPlants?.joinToString(", ") ?: "None listed",
                        companionStatus = "Good Companions, Healthy Growth",
                        growingTip = crop?.description ?: "Maintain consistent irrigation and soil organic matter.",
                        pestInfo = crop?.commonPests?.joinToString(", ") ?: "Inspect weekly for caterpillars and aphids.",
                        assetPath = "crops/crop_${(crop?.name ?: "carrot").lowercase().replace(" ", "")}_1.png",
                        imageUrl = crop?.imageUrl,
                        rawPlantedDate = plot.plantedDate,
                        isMonitoringStarted = isStarted
                    )
                }
            }.collect { monitoredPlants ->
                _uiState.update { it.copy(plantedCrops = monitoredPlants) }
            }
        }
    }

    private fun mapCategory(cat: String?): CropCategoryFilter = when (cat?.uppercase()) {
        "LEAFY" -> CropCategoryFilter.LEAFY
        "ROOT" -> CropCategoryFilter.ROOT
        "BULB" -> CropCategoryFilter.BULB
        "STEM" -> CropCategoryFilter.STEM
        "FLOWER" -> CropCategoryFilter.FLOWER
        "FRUIT", "PODDED" -> CropCategoryFilter.PODDED
        "TUBER" -> CropCategoryFilter.TUBER
        else -> CropCategoryFilter.ALL
    }

    private fun getVarietyDurationDays(cropName: String, variety: String?): Int? {
        if (variety.isNullOrBlank()) return null
        return when (variety.lowercase()) {
            "sandigan f1" -> 48
            "galante f1" -> 52
            "morena f1" -> 75
            "dumaguete long purple" -> 85
            "diamante max f1", "diamante max" -> 60
            "apollo" -> 72
            "terracotta f1" -> 85
            "kuroda improved", "kuroda" -> 95
            "red pinoy f1", "red pinoy" -> 110
            "yellow granex" -> 100
            "suprema f1", "suprema" -> 80
            "machismo f1" -> 65
            "ipb var 6" -> 72
            "k-s cross f1" -> 60
            "pavon" -> 28
            "jade star xl f1" -> 55
            "smooth green" -> 45
            "django f1" -> 65
            else -> null
        }
    }

    fun startMonitoring(plantId: String, targetDate: String = LocalDate.now().toString(), varietyName: String = "Standard Hybrid") {
        viewModelScope.launch {
            plotRepository.observePlot(plantId).take(1).collect { plot ->
                plot?.let {
                    val updatedPlot = it.copy(
                        plantedDate = targetDate.ifBlank { LocalDate.now().toString() },
                        cropVariety = varietyName.ifBlank { "Standard Hybrid" }
                    )
                    plotRepository.upsertPlot(updatedPlot)
                }
            }
        }
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
