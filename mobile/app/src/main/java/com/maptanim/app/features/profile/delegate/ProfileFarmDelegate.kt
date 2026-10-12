package com.maptanim.app.features.profile.delegate

import com.maptanim.app.core.preferences.FarmPreferencesManager
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.features.profile.model.ProfileUiState
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ProfileFarmDelegate — Encapsulates farm management operations (CRUD & active farm selection)
 * to keep ProfileViewModel modular and under the 500-line Gate 3 limit.
 */
class ProfileFarmDelegate(
    private val scope: CoroutineScope,
    private val state: MutableStateFlow<ProfileUiState>
) {
    fun openCreateFarm() {
        state.update {
            it.copy(
                showCreateFarmModal = true,
                createFarmNameInput = "",
                createFarmError = null
            )
        }
    }

    fun closeCreateFarm() {
        state.update { it.copy(showCreateFarmModal = false, createFarmError = null) }
    }

    fun updateCreateFarmNameInput(name: String) {
        state.update { it.copy(createFarmNameInput = name, createFarmError = null) }
    }

    fun confirmCreateFarm() {
        val name = state.value.createFarmNameInput.trim()
        if (name.isBlank()) {
            state.update { it.copy(createFarmError = "Farm name cannot be empty") }
            return
        }
        if (name.length < 2) {
            state.update { it.copy(createFarmError = "Farm name must be at least 2 characters") }
            return
        }

        scope.launch {
            state.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Creating farm workspace...") }
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            val newFarmId = "farm_${UUID.randomUUID().toString().take(8)}"
            val now = java.time.LocalDate.now().toString()
            val newFarm = Farm(
                id = newFarmId,
                farmerId = userId,
                farmName = name,
                createdAt = now,
                updatedAt = now
            )
            RepositoryProvider.farmRepository.upsertFarm(newFarm)
            FarmPreferencesManager.getInstance().setActiveFarmId(userId, newFarmId)
            state.update {
                it.copy(
                    showCreateFarmModal = false,
                    createFarmNameInput = "",
                    activeFarmId = newFarmId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm '$name' created successfully!"
                )
            }
        }
    }

    fun openRenameFarm(farm: Farm) {
        state.update {
            it.copy(
                farmToRename = farm,
                renameFarmInput = farm.farmName,
                renameFarmError = null
            )
        }
    }

    fun closeRenameFarm() {
        state.update { it.copy(farmToRename = null, renameFarmError = null) }
    }

    fun updateRenameFarmNameInput(name: String) {
        state.update { it.copy(renameFarmInput = name, renameFarmError = null) }
    }

    fun confirmRenameFarm() {
        val farm = state.value.farmToRename ?: return
        val newName = state.value.renameFarmInput.trim()
        if (newName.isBlank()) {
            state.update { it.copy(renameFarmError = "Farm name cannot be empty") }
            return
        }
        if (newName == farm.farmName) {
            state.update { it.copy(farmToRename = null) }
            return
        }

        scope.launch {
            state.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Renaming farm...") }
            val updatedFarm = farm.copy(
                farmName = newName,
                updatedAt = java.time.LocalDate.now().toString()
            )
            RepositoryProvider.farmRepository.upsertFarm(updatedFarm)
            state.update {
                it.copy(
                    farmToRename = null,
                    renameFarmInput = "",
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm renamed to '$newName'!"
                )
            }
        }
    }

    fun openDeleteFarm(farm: Farm) {
        state.update { it.copy(farmToDelete = farm) }
    }

    fun closeDeleteFarm() {
        state.update { it.copy(farmToDelete = null) }
    }

    fun confirmDeleteFarm() {
        val farm = state.value.farmToDelete ?: return
        scope.launch {
            state.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Deleting farm...") }
            RepositoryProvider.farmRepository.deleteFarm(farm.id)
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            val remainingFarms = state.value.farms.filter { it.id != farm.id }
            val newActiveId = if (state.value.activeFarmId == farm.id) {
                remainingFarms.firstOrNull()?.id
            } else {
                state.value.activeFarmId
            }
            if (newActiveId != null) {
                FarmPreferencesManager.getInstance().setActiveFarmId(userId, newActiveId)
            } else {
                FarmPreferencesManager.getInstance().clearActiveFarmId(userId)
            }
            state.update {
                it.copy(
                    farmToDelete = null,
                    activeFarmId = newActiveId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm '${farm.farmName}' deleted."
                )
            }
        }
    }

    fun selectActiveFarm(farmId: String) {
        scope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            state.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Switching active farm...") }
            FarmPreferencesManager.getInstance().setActiveFarmId(userId, farmId)
            val selectedFarm = state.value.farms.firstOrNull { it.id == farmId }
            val name = selectedFarm?.farmName ?: "Farm"
            kotlinx.coroutines.delay(200)
            state.update {
                it.copy(
                    activeFarmId = farmId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Active farm set to '$name'!"
                )
            }
        }
    }
}
