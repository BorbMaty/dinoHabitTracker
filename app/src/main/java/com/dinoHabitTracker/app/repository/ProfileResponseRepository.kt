package com.dinoHabitTracker.app.repository

import android.content.Context
import com.dinoHabitTracker.app.data.remote.ApiClient
import com.dinoHabitTracker.app.data.remote.ProfileApi
import com.dinoHabitTracker.app.data.remote.ProfileResponseDto
import com.dinoHabitTracker.app.data.dto.UpdateProfileDto

class ProfileResponseRepository(
    context: Context
) {

    private val api: ProfileApi = ApiClient.api(context, ProfileApi::class.java)

    suspend fun getProfile(): Result<ProfileResponseDto> = try {
        val dto = api.getMyProfile()

        val imageModel = when {
            !dto.profileImageUrl.isNullOrBlank() ->
                dto.profileImageUrl
            !dto.profileImageBase64.isNullOrBlank() ->
                "data:image/png;base64,${dto.profileImageBase64}"
            else -> null
        }

        Result.success(dto.copy(profileImageUrl = imageModel))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun updateProfile(
        username: String?,
        description: String?
    ): Result<ProfileResponseDto> = try {
        val body = UpdateProfileDto(
            username = username,
            description = description
        )

        val dto = api.updateProfile(body)

        val imageModel = when {
            !dto.profileImageUrl.isNullOrBlank() ->
                dto.profileImageUrl
            !dto.profileImageBase64.isNullOrBlank() ->
                "data:image/png;base64,${dto.profileImageBase64}"
            else -> null
        }

        Result.success(dto.copy(profileImageUrl = imageModel))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
