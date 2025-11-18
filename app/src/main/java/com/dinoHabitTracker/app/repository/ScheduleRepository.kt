package com.dinoHabitTracker.app.repository

import android.content.Context
import com.dinoHabitTracker.app.data.remote.ApiClient
import com.dinoHabitTracker.app.data.remote.ScheduleApi
import com.dinoHabitTracker.app.data.remote.ScheduleResponse
import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto
import com.dinoHabitTracker.app.data.remote.ScheduleApi.UpdateScheduleRequest
import com.dinoHabitTracker.app.data.dto.CreateCustomScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateRecurringScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateWeekdayRecurringDto

class ScheduleRepository(context: Context) {

    private val api: ScheduleApi =
        ApiClient.retrofit(context).create(ScheduleApi::class.java)

    // Napi lista (HomeScreen)
    suspend fun getByDay(dateUtc: String): List<ScheduleResponse> =
        api.getByDay(dateUtc)

    // Schedule details (Details screen)
    suspend fun getScheduleDetails(id: Long): Result<ScheduleResponseDto> {
        return try {
            val dto = api.getScheduleById(id)
            Result.success(dto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update (Edit Schedule)
    suspend fun updateSchedule(
        id: Long,
        date: String?,
        startTime: String?,
        endTime: String?,
        durationMinutes: Int?,
        status: String?,
        isCustom: Boolean?,
        notes: String?
    ): Result<ScheduleResponseDto> {
        return try {
            val body = UpdateScheduleRequest(
                start_time = startTime,
                end_time = endTime,
                duration_minutes = durationMinutes,
                status = status,
                date = date,
                is_custom = isCustom,
                participantIds = null,   // ezt most nem kezeljük UI-ban
                notes = notes
            )
            val dto = api.updateSchedule(id, body)
            Result.success(dto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete
    suspend fun deleteSchedule(id: Long): Result<Unit> {
        return try {
            api.deleteSchedule(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 ÚJ, egységes DTO-s create-ek – EZEKET használja az AddScheduleViewModel

    suspend fun createCustomSchedule(
        body: CreateCustomScheduleDto
    ): Result<ScheduleResponseDto> = runCatching {
        api.createCustomSchedule(body)
    }

    suspend fun createRecurringSchedule(
        body: CreateRecurringScheduleDto
    ): Result<List<ScheduleResponseDto>> = runCatching {
        api.createRecurringSchedule(body)
    }

    suspend fun createWeekdayRecurringSchedule(
        body: CreateWeekdayRecurringDto
    ): Result<List<ScheduleResponseDto>> = runCatching {
        api.createWeekdayRecurringSchedule(body)
    }
}
