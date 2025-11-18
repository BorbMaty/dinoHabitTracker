package com.dinoHabitTracker.app.data.dto

// POST /schedule/custom
data class CreateCustomScheduleDto(
    val habitId: Long,
    val date: String,               // pl. "2025-09-07"
    val start_time: String,         // pl. "2025-09-07T08:00:00Z"
    val end_time: String? = null,   // optional
    val duration_minutes: Int? = null,
    val is_custom: Boolean = true,
    val participantIds: List<Long>? = null,
    val notes: String? = null
)

// POST /schedule/recurring
data class CreateRecurringScheduleDto(
    val habitId: Long,
    val start_time: String,
    val end_time: String? = null,
    val duration_minutes: Int? = null,
    val repeatPattern: String,      // "none" | "daily" | "weekdays" | "weekends"
    val repeatDays: Int? = 30,
    val is_custom: Boolean = true,
    val participantIds: List<Long>? = null,
    val notes: String? = null
)

// POST /schedule/recurring/weekdays
data class CreateWeekdayRecurringDto(
    val habitId: Long,
    val start_time: String,
    val duration_minutes: Int? = null,
    val end_time: String? = null,
    val daysOfWeek: List<Int>,      // 1 = Monday ... 7 = Sunday
    val numberOfWeeks: Int = 4,
    val participantIds: List<Long>? = null,
    val notes: String? = null
)
