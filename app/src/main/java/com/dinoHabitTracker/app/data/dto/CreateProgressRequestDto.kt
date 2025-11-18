package com.dinoHabitTracker.app.data.dto

data class CreateProgressRequestDto(
    val scheduleId: Long,
    val date: String,               // pl. "2025-11-18"
    val logged_time: Int? = null,
    val notes: String? = null,
    val is_completed: Boolean? = null
)
