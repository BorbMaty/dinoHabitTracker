package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dinoHabitTracker.app.repository.ProfileResponseRepository
import com.dinoHabitTracker.app.repository.HabitRepository
import com.dinoHabitTracker.app.repository.ScheduleRepository
import com.dinoHabitTracker.app.ui.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class ProfileViewModel(
    private val profileRepo: ProfileResponseRepository,
    private val habitRepo: HabitRepository,
    private val scheduleRepo: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true

            // 1) PROFIL BACKENDBŐL
            profileRepo.getProfile()
                .onSuccess { dto ->
                    _uiState.value = ProfileUiState(
                        username = dto.username.orEmpty(),          // ⬅ String? → String
                        email = dto.email.orEmpty(),                // biztos, ami biztos
                        description = dto.description,
                        imageBase64 = dto.profileImageBase64.orEmpty(),   // ⬅ String? → String
                        totalHabits = dto.totalHabits ?: 0,
                        completedToday = dto.completedToday ?: 0,
                        streakDays = dto.streakDays ?: 0
                    )




                    _error.value = null
                }
                .onFailure { e ->
                    e.printStackTrace()
                    _error.value = e.message ?: "Failed to load profile"
                }

            // 2) HABIT + SCHEDULE ALAPÚ STATOK (CLIENT-SIDE)
            try {
                // összes habit száma
                val habits = habitRepo.getAllHabits()
                val totalHabits = habits.size

                // ma
                val todayUtc = LocalDate.now(ZoneOffset.UTC).toString()
                val todaySchedules = scheduleRepo.getByDay(todayUtc)
                val completedToday = todaySchedules.count { it.status == "Completed" }

                // streak: hány egymást követő nap volt legalább 1 Completed
                val streakDays = computeStreak(maxDays = 30)

                val current = _uiState.value
                _uiState.value = current.copy(
                    totalHabits = totalHabits,
                    completedToday = completedToday,
                    streakDays = streakDays
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // ha itt hiba van, a profil ettől még oké, csak a statokat nem frissítjük
            }

            _loading.value = false
        }
    }

    /**
     * Egyszerű streak számolás:
     * visszafelé megyünk naponként max [maxDays]-ig,
     * addig számoljuk, amíg minden napon van legalább 1 Completed schedule.
     */
    private suspend fun computeStreak(maxDays: Int = 30): Int {
        var streak = 0
        var date = LocalDate.now(ZoneOffset.UTC)

        repeat(maxDays) {
            val dayStr = date.toString()
            val daySchedules = scheduleRepo.getByDay(dayStr)
            val completed = daySchedules.count { it.status == "Completed" }

            if (completed == 0) {
                // itt megszakad a streak
                return streak
            } else {
                streak++
                date = date.minusDays(1)
            }
        }
        return streak
    }

    // EditProfile: UI azonnal frissül, PATCH /profile megy háttérben
    fun updateLocalProfile(username: String, description: String?) {
        val current = _uiState.value
        _uiState.value = current.copy(
            username = username,
            description = description
        )

        viewModelScope.launch {
            profileRepo.updateProfile(username, description)
                .onFailure { e ->
                    e.printStackTrace()
                    _error.value = e.message ?: "Failed to update profile"
                }
        }
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val profileRepo = ProfileResponseRepository(context)
            val habitRepo = HabitRepository(context)
            val scheduleRepo = ScheduleRepository(context)
            return ProfileViewModel(profileRepo, habitRepo, scheduleRepo) as T
        }
    }
}
