package com.maptanim.app.ui.screens.dss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.repository.FarmRepository
import com.maptanim.app.domain.usecase.ActOnDssDecisionUseCase
import com.maptanim.app.domain.usecase.ObserveDssDecisionsUseCase
import com.maptanim.app.domain.usecase.RunDssEvaluationUseCase
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.model.DssResultSummary
import com.maptanim.app.dss.model.DssSession
import com.maptanim.app.dss.model.DssValidationIssue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Decision Support System screen.
 */
data class DssUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val farmId: String = "",
    val farmName: String = "",
    val session: DssSession? = null,
    val allDecisions: List<DssDecision> = emptyList(),
    val filteredDecisions: List<DssDecision> = emptyList(),
    val selectedCategory: DssCategory? = null,
    val selectedPriority: DssPriority? = null,
    val selectedDecisionForDetail: DssDecision? = null,
    val insufficientDataNotices: List<DssValidationIssue> = emptyList(),
    val summary: DssResultSummary = DssResultSummary(),
    val toastMessage: String? = null
)

/**
 * DSS ViewModel: Manages UI state, coordinates with DSS Use Cases,
 * and contains NO agricultural rules, database queries, or Supabase operations.
 */
class DssViewModel(
    private val runEvaluation: RunDssEvaluationUseCase = RunDssEvaluationUseCase(),
    private val observeDecisions: ObserveDssDecisionsUseCase = ObserveDssDecisionsUseCase(),
    private val actOnDecisionUseCase: ActOnDssDecisionUseCase = ActOnDssDecisionUseCase(),
    private val farmRepository: FarmRepository = RepositoryProvider.farmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DssUiState())
    val uiState: StateFlow<DssUiState> = _uiState.asStateFlow()

    fun initialize(farmId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Determine active farm
            val targetFarmId = if (!farmId.isNullOrBlank()) {
                farmId
            } else {
                farmRepository.observeFarms("").firstOrNull()?.firstOrNull()?.id ?: "farm_default"
            }

            val farm = farmRepository.observeFarm(targetFarmId).firstOrNull()
            val farmName = farm?.farmName ?: "Main Farm"

            _uiState.update {
                it.copy(
                    farmId = targetFarmId,
                    farmName = farmName
                )
            }

            // Observe cached decisions in real-time
            launch {
                observeDecisions(targetFarmId).collect { cached ->
                    _uiState.update { current ->
                        current.copy(
                            allDecisions = cached,
                            filteredDecisions = applyFilters(cached, current.selectedCategory, current.selectedPriority),
                            summary = computeSummary(cached)
                        )
                    }
                }
            }

            // Trigger fresh evaluation through Use Case
            evaluateFarm(targetFarmId)
        }
    }

    fun refreshEvaluation() {
        val currentFarmId = _uiState.value.farmId
        if (currentFarmId.isNotBlank()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isRefreshing = true) }
                evaluateFarm(currentFarmId)
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun evaluateFarm(farmId: String) {
        try {
            val result = runEvaluation(farmId)
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    session = result.session,
                    allDecisions = result.decisions,
                    filteredDecisions = applyFilters(result.decisions, current.selectedCategory, current.selectedPriority),
                    insufficientDataNotices = result.insufficientDataNotices,
                    summary = result.summary,
                    toastMessage = "DSS Evaluation Complete (${result.decisions.size} findings)"
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    toastMessage = "Evaluation error: ${e.message}"
                )
            }
        }
    }

    fun selectCategory(category: DssCategory?) {
        _uiState.update { current ->
            val nextCategory = if (current.selectedCategory == category) null else category
            current.copy(
                selectedCategory = nextCategory,
                filteredDecisions = applyFilters(current.allDecisions, nextCategory, current.selectedPriority)
            )
        }
    }

    fun selectPriority(priority: DssPriority?) {
        _uiState.update { current ->
            val nextPriority = if (current.selectedPriority == priority) null else priority
            current.copy(
                selectedPriority = nextPriority,
                filteredDecisions = applyFilters(current.allDecisions, current.selectedCategory, nextPriority)
            )
        }
    }

    fun openDecisionDetail(decision: DssDecision) {
        _uiState.update { it.copy(selectedDecisionForDetail = decision) }
    }

    fun closeDecisionDetail() {
        _uiState.update { it.copy(selectedDecisionForDetail = null) }
    }

    fun actOnDecision(decision: DssDecision) {
        viewModelScope.launch {
            try {
                actOnDecisionUseCase(decision)
                _uiState.update { current ->
                    current.copy(
                        toastMessage = "Action recorded: ${decision.title} ✅",
                        selectedDecisionForDetail = null
                    )
                }
                // Automatically re-evaluate farm to reflect farmer's update
                refreshEvaluation()
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = "Action failed: ${e.message}") }
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun applyFilters(
        decisions: List<DssDecision>,
        category: DssCategory?,
        priority: DssPriority?
    ): List<DssDecision> {
        return decisions.filter { d ->
            val categoryMatch = category == null || d.category == category
            val priorityMatch = priority == null || d.priority == priority
            categoryMatch && priorityMatch
        }
    }

    private fun computeSummary(decisions: List<DssDecision>): DssResultSummary {
        return DssResultSummary(
            totalDecisions = decisions.size,
            criticalAlerts = decisions.count { it.priority == DssPriority.CRITICAL },
            highPriorityCount = decisions.count { it.priority == DssPriority.HIGH },
            recommendationsCount = decisions.count { it.decisionType == DssDecisionType.RECOMMENDATION },
            tasksCount = decisions.count { it.decisionType == DssDecisionType.TASK },
            monitoredPlotsCount = decisions.mapNotNull { it.plotId }.distinct().size
        )
    }
}
