package com.dinoHabitTracker.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

// --- DTO-k az endpointokhoz ---

// Rövid DTO a listázáshoz
data class HabitShortDto(
    val id: Long,
    val name: String
)


data class CreateHabitRequest(
    val name: String,
    val categoryId: Long,
    val goal: String,
    val description: String? = null
)

data class HabitCategoryResponse(
    val id: Long,
    val name: String,
    val iconUrl: String? = null
)

data class HabitCreatedResponse( // a backend HabitResponseDto-jának minimál változata
    val id: Long,
    val name: String,
    val description: String?,
    val goal: String,
    val category: HabitCategoryResponse
)

// --- API interface ---

interface HabitApi {

    // Összes habit lekérése (az aktuális userhez)
    @GET("habit")
    suspend fun getAllHabits(): List<HabitShortDto>


    // Spec: GET /habit/categories – habit kategóriák listázása
    @GET("habit/categories")
    suspend fun getCategories(): List<HabitCategoryResponse>

    // Spec: POST /habit – új habit létrehozása
    @POST("habit")
    suspend fun createHabit(@Body body: CreateHabitRequest): HabitCreatedResponse
}
