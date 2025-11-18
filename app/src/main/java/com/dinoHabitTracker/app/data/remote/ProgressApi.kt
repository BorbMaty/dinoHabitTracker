package com.dinoHabitTracker.app.data.remote

import com.dinoHabitTracker.app.data.dto.CreateProgressRequestDto
import com.dinoHabitTracker.app.data.dto.ProgressResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ProgressApi {

    @POST("progress")
    suspend fun createProgress(
        @Body body: CreateProgressRequestDto
    ): ProgressResponseDto
}