package com.dinoHabitTracker.app.data.dto

data class HabitResponseDto(
    val id: Long,
    val name: String,
    val description: String?,
    val category: HabitCategoryDto,
    val goal: String,
    val created_at: String,
    val updated_at: String
)

data class HabitCategoryDto(
    val id: Long,
    val name: String,
    val iconUrl: String
)
