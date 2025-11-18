package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.dinoHabitTracker.app.data.dto.CreateCustomScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateRecurringScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateWeekdayRecurringDto
import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto
import com.dinoHabitTracker.app.data.remote.HabitShortDto
import com.dinoHabitTracker.app.repository.HabitRepository
import com.dinoHabitTracker.app.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class AddScheduleViewModel(
    private val repo: ScheduleRepository
) : ViewModel() {

    // Habits a dropdownhoz
    private val _habits = MutableLiveData<List<HabitShortDto>>(emptyList())
    val habits: LiveData<List<HabitShortDto>> = _habits

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // 🔹 Mostantól DTO-t tartunk itt, de a UI szempontjából elég, hogy nem-null
    private val _created = MutableLiveData<ScheduleResponseDto?>(null)
    val created: LiveData<ScheduleResponseDto?> = _created

    /**
     * Alap use-case: ha csak az első 6 paramétert adod meg (mint eddig),
     * akkor /schedule/custom hívódik.
     *
     * Logika:
     *  - repeatPattern == "none" && daysOfWeek üres → /schedule/custom
     *  - daysOfWeek nem üres → /schedule/recurring/weekdays
     *  - különben → /schedule/recurring
     */
    fun createSchedule(
        habitId: Long,
        dateUtc: String,              // "YYYY-MM-DD"
        startIso: String? = null,     // "2025-11-05T18:00:00Z"
        endIso: String? = null,
        durationMinutes: Int? = null,
        notes: String? = null,
        repeatPattern: String = "none",   // "none" | "daily" | "weekdays" | "weekends"
        daysOfWeek: List<Int> = emptyList(), // 1 = hétfő ... 7 = vasárnap
        repeatDays: Int = 30,
        numberOfWeeks: Int = 4
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val start = startIso ?: throw IllegalArgumentException("Start time is required")

                val createdDto: ScheduleResponseDto? = when {
                    // 1) NINCS ismétlés és nincsenek konkrét napok → POST /schedule/custom
                    repeatPattern == "none" && daysOfWeek.isEmpty() -> {
                        val body = CreateCustomScheduleDto(
                            habitId = habitId,
                            date = dateUtc,
                            start_time = start,
                            end_time = endIso,
                            duration_minutes = durationMinutes,
                            is_custom = true,
                            participantIds = null,
                            notes = notes
                        )

                        val result = repo.createCustomSchedule(body)
                        result.getOrThrow()          // Result<ScheduleResponseDto> → ScheduleResponseDto
                    }

                    // 2) Konkrét napok vannak → POST /schedule/recurring/weekdays
                    daysOfWeek.isNotEmpty() -> {
                        val body = CreateWeekdayRecurringDto(
                            habitId = habitId,
                            start_time = start,
                            duration_minutes = durationMinutes,
                            end_time = endIso,
                            daysOfWeek = daysOfWeek,
                            numberOfWeeks = numberOfWeeks,
                            participantIds = null,
                            notes = notes
                        )

                        val result = repo.createWeekdayRecurringSchedule(body)
                        result.getOrThrow().firstOrNull()   // több schedule jön vissza → az elsőt eltároljuk
                    }

                    // 3) Általános ismétlés → POST /schedule/recurring
                    else -> {
                        val body = CreateRecurringScheduleDto(
                            habitId = habitId,
                            start_time = start,
                            end_time = endIso,
                            duration_minutes = durationMinutes,
                            repeatPattern = repeatPattern,
                            repeatDays = repeatDays,
                            is_custom = true,
                            participantIds = null,
                            notes = notes
                        )

                        val result = repo.createRecurringSchedule(body)
                        result.getOrThrow().firstOrNull()
                    }
                }

                _created.value = createdDto
                _error.value = null
            } catch (e: Exception) {
                _created.value = null
                _error.value = e.message ?: "Failed to create schedule"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearResult() { _created.value = null }
    fun clearError() { _error.value = null }

    // Factory ugyanaz marad
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddScheduleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddScheduleViewModel(ScheduleRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }

    fun loadHabits(context: Context) {
        viewModelScope.launch {
            try {
                val list = HabitRepository(context).getAllHabits()
                _habits.value = list
            } catch (_: Exception) {
                // ha hiba van, most lenyeljük, a form ettől még használható
            }
        }
    }

    // ha a képernyőn LocalDate/LocalTime-ból szeretnél stringet készíteni
    private fun toIsoDateTime(date: LocalDate, time: LocalTime): String {
        return date.atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toString()
    }

    private fun toIsoDate(date: LocalDate): String {
        return date.toString()
    }
}
