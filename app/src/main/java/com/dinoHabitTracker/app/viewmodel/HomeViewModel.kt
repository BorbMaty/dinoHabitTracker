package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.dinoHabitTracker.app.data.remote.ScheduleResponse
import com.dinoHabitTracker.app.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class HomeViewModel(private val repo: ScheduleRepository) : ViewModel() {

    private val _items = MutableLiveData<List<ScheduleResponse>>(emptyList())
    val items: LiveData<List<ScheduleResponse>> = _items

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadByDate(dateUtc: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repo.getByDay(dateUtc)
                _items.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load schedules"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Ma betöltése – továbbra is működik
     */
    fun loadToday() {
        val today = LocalDate.now(ZoneOffset.UTC).toString()
        loadByDate(today)
    }

    fun clearError() {
        _error.value = null
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(ScheduleRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
