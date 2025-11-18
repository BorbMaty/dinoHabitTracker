package com.dinoHabitTracker.app.ui.screens.schedule

import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto

data class ScheduleDetailsUiState(
    val loading: Boolean = false,
    val schedule: ScheduleResponseDto? = null,
    val error: String? = null
)
