package com.dinoHabitTracker.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/local/signin")
    suspend fun signIn(@Body body: SignInDto): AuthResponseDto
}