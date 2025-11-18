package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto
import com.dinoHabitTracker.app.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EditScheduleViewModel(
    private val repo: ScheduleRepository,
    private val scheduleId: Long
) : ViewModel() {

    private val _schedule = MutableLiveData<ScheduleResponseDto?>(null)
    val schedule: LiveData<ScheduleResponseDto?> = _schedule

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _saving = MutableLiveData(false)
    val saving: LiveData<Boolean> = _saving

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _saved = MutableLiveData(false)
    val saved: LiveData<Boolean> = _saved

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repo.getScheduleDetails(scheduleId)
                result.onSuccess {
                    _schedule.value = it
                }.onFailure { e ->
                    _error.value = e.message ?: "Failed to load schedule"
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateSchedule(
        startHHmm: String?,
        endHHmm: String?,
        durationMinutes: Int?,
        status: String,
        isCustom: Boolean,
        notes: String?
    ) {
        val current = _schedule.value ?: return

        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                val baseDate = current.date?.let { parseDate(it) }
                    ?: LocalDate.now(ZoneId.systemDefault())

                val startIso = hhmmToIso(startHHmm, baseDate)
                val endIso = hhmmToIso(endHHmm, baseDate)

                val result = repo.updateSchedule(
                    id = current.id,
                    date = current.date,              // dátumot most nem piszkáljuk
                    startTime = startIso,
                    endTime = endIso,
                    durationMinutes = durationMinutes,
                    status = status,
                    isCustom = isCustom,
                    notes = notes
                )

                result.onSuccess { updated ->
                    _schedule.value = updated
                    _saved.value = true
                }.onFailure { e ->
                    _error.value = e.message ?: "Failed to update schedule"
                }
            } finally {
                _saving.value = false
            }
        }
    }

    private fun parseDate(raw: String): LocalDate {
        return try {
            Instant.parse(raw)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (_: Exception) {
            try {
                LocalDate.parse(raw.take(10))
            } catch (_: Exception) {
                LocalDate.now()
            }
        }
    }

    private fun hhmmToIso(hhmm: String?, baseDate: LocalDate): String? {
        if (hhmm.isNullOrBlank()) return null
        return try {
            val t = LocalTime.parse(hhmm, DateTimeFormatter.ofPattern("HH:mm"))
            baseDate.atTime(t)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toString()
        } catch (_: Exception) {
            null
        }
    }

    class Factory(
        private val context: Context,
        private val scheduleId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditScheduleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditScheduleViewModel(
                    ScheduleRepository(context),
                    scheduleId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
