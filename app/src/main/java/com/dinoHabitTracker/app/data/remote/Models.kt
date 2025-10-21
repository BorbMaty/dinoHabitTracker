package com.dinoHabitTracker.app.data.remote


data class SignInDto(
    val email: String,
    val password: String
)

data class TokensDto(
    val accessToken: String,
    val refreshToken: String
)

data class UserDto(
    val id: Long,
    val email: String,
    val username: String?
)

data class AuthResponseDto(
    val message: String?,
    val user: UserDto,
    val tokens: TokensDto
)
