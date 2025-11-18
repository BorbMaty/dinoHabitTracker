package com.dinoHabitTracker.app.data.remote

import com.dinoHabitTracker.app.data.dto.UpdateProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileApi {

    // 🔹 Saját profil lekérése – GET /profile
    @GET("profile")
    suspend fun getMyProfile(): ProfileResponseDto

    // 🔹 Profil frissítése – PATCH /profile
    @PATCH("profile")
    suspend fun updateProfile(
        @Body body: UpdateProfileDto
    ): ProfileResponseDto
}
