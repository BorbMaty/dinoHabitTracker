package com.dinoHabitTracker.app.data.remote

import com.google.gson.annotations.SerializedName

data class ProfileResponseDto(
    val id: Int? = null,
    val email: String,
    val username: String? = null,
    val description: String? = null,
    val profileImageUrl: String? = null,
    val profileImageBase64: String? = null,
    val coverImageUrl: String? = null,
    val fcmToken: String? = null,
    val preferences: Any? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,

    // Ezeket a backend még nem küldi, de a UI-nak jól jönnek
    val totalHabits: Int? = null,
    val completedToday: Int? = null,
    val streakDays: Int? = null
)
