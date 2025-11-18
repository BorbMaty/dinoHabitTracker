package com.dinoHabitTracker.app.data.dto

data class ScheduleResponseDto(
    val id: Long,
    val habit: HabitResponseDto,
    val status: String,
    val date: String?,
    val start_time: String?,
    val end_time: String?,
    val duration_minutes: Int?,
    val notes: String?,
    val isCustom: Boolean?,
    val progress: List<ProgressResponseDto>? = emptyList()   // ⬅ NEW
)
