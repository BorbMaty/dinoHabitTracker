package com.dinoHabitTracker.app.data.dto

import com.google.gson.annotations.SerializedName

data class ProgressRequestDto(
    @SerializedName("scheduleId")
    val scheduleId: Long,

    @SerializedName("date")
    val date: String,

    @SerializedName("logged_time")
    val loggedTime: Int? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("is_completed")
    val isCompleted: Boolean? = null
)

data class ProgressResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("scheduleId")
    val scheduleId: Long,

    @SerializedName("date")
    val date: String,

    @SerializedName("logged_time")
    val loggedTime: Int?,

    @SerializedName("notes")
    val notes: String?,

    @SerializedName("is_completed")
    val isCompleted: Boolean?
)
