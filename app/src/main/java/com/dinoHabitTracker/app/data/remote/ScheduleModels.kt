package com.dinoHabitTracker.app.data.remote


import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ScheduleResponse(
    val id: Long,

    @SerializedName("start_time")
    val startTime: LocalDateTime? = null,

    @SerializedName("end_time")
    val endTime: LocalDateTime? = null,

    val status: String? = null,          // "Planned" | "Completed" | "Skipped"
    val date: String? = null,

    @SerializedName("is_custom")
    val isCustom: Boolean = true,

    val notes: String? = null,
    val habit: HabitResponse? = null
)

data class HabitResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val goal: String? = null,
    val category: HabitCategory? = null
)

data class HabitCategory(
    val id: Long,
    val name: String,
    val iconUrl: String? = null
)