package com.dinoHabitTracker.app.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.dinoHabitTracker.app.data.remote.HabitCategoryResponse
import com.dinoHabitTracker.app.repository.HabitRepository
import kotlinx.coroutines.launch

class HabitViewModel(private val repo: HabitRepository) : ViewModel() {

    // --- UI state ---
    private val _categories = MutableLiveData<List<HabitCategoryResponse>>(emptyList())
    val categories: LiveData<List<HabitCategoryResponse>> = _categories

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _createSuccessId = MutableLiveData<Long?>(null)
    val createSuccessId: LiveData<Long?> = _createSuccessId

    // --- Actions ---
    fun loadCategories() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val list = repo.getCategories()
                _categories.value = list
                _error.value = null
            } catch (e: Exception) {
                _categories.value = emptyList()
                _error.value = e.message ?: "Failed to load categories"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createHabit(
        name: String,
        categoryId: Long,
        goal: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val resp = repo.createHabit(name, categoryId, goal, description)
                _createSuccessId.value = resp.id
                _error.value = null
            } catch (e: Exception) {
                _createSuccessId.value = null
                _error.value = e.message ?: "Failed to create habit"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearCreateResult() { _createSuccessId.value = null }
    fun clearError() { _error.value = null }

    // --- Factory ---
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HabitViewModel(HabitRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
