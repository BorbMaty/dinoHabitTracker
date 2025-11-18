package com.dinoHabitTracker.app.repository

import android.content.Context
import com.dinoHabitTracker.app.data.remote.ApiClient
import com.dinoHabitTracker.app.data.remote.HabitApi
import com.dinoHabitTracker.app.data.remote.CreateHabitRequest
import com.dinoHabitTracker.app.data.remote.HabitCategoryResponse
import com.dinoHabitTracker.app.data.remote.HabitCreatedResponse
import com.dinoHabitTracker.app.data.remote.HabitShortDto

class HabitRepository(context: Context) {

    private val api: HabitApi =
        ApiClient.retrofit(context).create(HabitApi::class.java)

    suspend fun getCategories(): List<HabitCategoryResponse> {
        return api.getCategories()
    }

    suspend fun createHabit(
        name: String,
        categoryId: Long,
        goal: String,
        description: String? = null
    ): HabitCreatedResponse {
        val body = CreateHabitRequest(
            name = name,
            categoryId = categoryId,
            goal = goal,
            description = description
        )
        return api.createHabit(body)
    }


    suspend fun getAllHabits(): List<HabitShortDto> = api.getAllHabits()
}
