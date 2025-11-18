package com.dinoHabitTracker.app.repository

import com.dinoHabitTracker.app.data.remote.ProgressApi
import com.dinoHabitTracker.app.data.dto.CreateProgressRequestDto
import com.dinoHabitTracker.app.data.dto.ProgressResponseDto

class ProgressRepository(
    private val api: ProgressApi
) {

    /**
     * Nyers API hívás (DTO-val)
     */
    suspend fun createProgress(body: CreateProgressRequestDto): Result<ProgressResponseDto> {
        return try {
            val res = api.createProgress(body)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ez a ViewModel által használt convenience wrapper.
     *
     * scheduleId: Long
     * date: "YYYY-MM-DD"
     * loggedMinutes: Int? (nullable)
     * notes: String? (nullable)
     * isCompleted: Boolean
     */
    suspend fun addProgress(
        scheduleId: Long,
        date: String,
        loggedMinutes: Int?,
        notes: String?,
        isCompleted: Boolean
    ): Result<ProgressResponseDto> {

        val body = CreateProgressRequestDto(
            scheduleId = scheduleId,
            date = date,
            logged_time = loggedMinutes,
            notes = notes,
            is_completed = isCompleted
        )

        return createProgress(body)
    }
}
