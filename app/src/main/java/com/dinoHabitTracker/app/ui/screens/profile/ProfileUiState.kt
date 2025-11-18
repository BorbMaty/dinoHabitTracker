package com.dinoHabitTracker.app.ui.screens.profile

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val description: String? = null,
    val imageBase64: String = "",
    val totalHabits: Int = 0,
    val completedToday: Int = 0,
    val streakDays: Int = 0
)

