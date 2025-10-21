package com.dinoHabitTracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dinoHabitTracker.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(app)

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onEmailChange(v: String) {
        _state.value = _state.value.copy(email = v, error = null)
    }

    fun onPasswordChange(v: String) {
        _state.value = _state.value.copy(password = v, error = null)
    }

    fun signIn(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Email és jelszó szükséges.")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val result = repo.signIn(s.email, s.password)
            _state.value = _state.value.copy(loading = false)
            result
                .onSuccess { onSuccess() }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Sikertelen bejelentkezés"
                    )
                }
        }
    }
}