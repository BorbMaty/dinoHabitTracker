package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dinoHabitTracker.app.repository.ScheduleRepository
import com.dinoHabitTracker.app.repository.ProgressRepository
import com.dinoHabitTracker.app.ui.screens.schedule.ScheduleDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleDetailsViewModel(
    private val repo: ScheduleRepository,
    private val scheduleId: Long,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleDetailsUiState())
    val uiState: StateFlow<ScheduleDetailsUiState> = _uiState.asStateFlow()

    // Delete állapotok
    val deleting = MutableStateFlow(false)
    val deleted = MutableStateFlow(false)
    val deleteError = MutableStateFlow<String?>(null)

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            repo.getScheduleDetails(scheduleId)
                .onSuccess { dto ->
                    _uiState.value = ScheduleDetailsUiState(
                        loading = false,
                        schedule = dto,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = ScheduleDetailsUiState(
                        loading = false,
                        schedule = null,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }

    fun delete() {
        viewModelScope.launch {
            deleting.value = true
            deleteError.value = null

            repo.deleteSchedule(scheduleId)
                .onSuccess {
                    deleting.value = false
                    deleted.value = true
                }
                .onFailure { e ->
                    deleting.value = false
                    deleteError.value = e.message ?: "Unknown error"
                }
        }
    }

    /**
     * Progress hozzáadása az adott schedule-hez.
     *
     * @param loggedMinutes opcionális idő percben (pl. 30), lehet null
     * @param notes opcionális jegyzet
     * @param isCompleted jelöljük-e teljesítettnek
     */
    fun addProgress(
        loggedMinutes: Int?,
        notes: String?,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Mai dátum YYYY-MM-DD formátumban
                val today = LocalDate.now().toString()

                // 1) Hívjuk a ProgressRepository-t
                progressRepository.addProgress(
                    scheduleId = scheduleId,
                    date = today,
                    loggedMinutes = loggedMinutes,
                    notes = notes,
                    isCompleted = isCompleted
                )

                // 2) Siker esetén frissítjük a schedule-t (progress history is frissül)
                load()
            } catch (e: Exception) {
                // 3) Hiba esetén beírjuk az uiState error mezőjébe
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to add progress"
                )
            }
        }
    }

    class Factory(
        private val context: Context,
        private val scheduleId: Long
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ScheduleRepository(context)

            val progressRepo = ProgressRepository(
                com.dinoHabitTracker.app.data.remote.ApiClient.progressApi(context)
            )

            return ScheduleDetailsViewModel(
                repo = repo,
                scheduleId = scheduleId,
                progressRepository = progressRepo
            ) as T
        }
    }
}
