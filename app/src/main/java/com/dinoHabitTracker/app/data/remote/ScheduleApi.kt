package com.dinoHabitTracker.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST

import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto
import retrofit2.Response
import com.dinoHabitTracker.app.data.dto.CreateCustomScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateRecurringScheduleDto
import com.dinoHabitTracker.app.data.dto.CreateWeekdayRecurringDto

import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.PATCH


interface ScheduleApi {

    // már meglévő:
    @GET("schedule/day")
    suspend fun getByDay(@Query("date") date: String): List<ScheduleResponse>

    // 🔽 ÚJ: DTO + endpoint a custom schedule létrehozásához
    data class CreateCustomScheduleRequest(
        val habitId: Long,
        val date: String,                 // "YYYY-MM-DD" (UTC nap)
        val start_time: String? = null,   // pl. "2025-11-05T18:00:00Z"
        val end_time: String? = null,     // vagy duration_minutes
        val duration_minutes: Int? = null,
        val is_custom: Boolean = true,
        val notes: String? = null
    )

    @GET("schedule/{id}")
    suspend fun getScheduleById(
        @Path("id") id: Long
    ): ScheduleResponseDto

    @POST("schedule/custom")
    suspend fun createCustom(@Body body: CreateCustomScheduleRequest): ScheduleResponse

    data class UpdateScheduleRequest(
        val start_time: String? = null,
        val end_time: String? = null,
        val duration_minutes: Int? = null,
        val status: String? = null,
        val date: String? = null,
        val is_custom: Boolean? = null,
        val participantIds: List<Long>? = null,
        val notes: String? = null
    )

    @PATCH("schedule/{id}")
    suspend fun updateSchedule(
        @Path("id") id: Long,
        @Body body: UpdateScheduleRequest
    ): ScheduleResponseDto

    @DELETE("schedule/{id}")
    suspend fun deleteSchedule(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("schedule/custom")
    suspend fun createCustomSchedule(
        @Body body: CreateCustomScheduleDto
    ): ScheduleResponseDto

    @POST("schedule/recurring")
    suspend fun createRecurringSchedule(
        @Body body: CreateRecurringScheduleDto
    ): List<ScheduleResponseDto>

    @POST("schedule/recurring/weekdays")
    suspend fun createWeekdayRecurringSchedule(
        @Body body: CreateWeekdayRecurringDto
    ): List<ScheduleResponseDto>

}
