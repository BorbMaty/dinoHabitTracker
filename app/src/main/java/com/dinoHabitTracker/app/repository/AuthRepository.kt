package com.dinoHabitTracker.app.repository

import android.content.Context
import com.dinoHabitTracker.app.data.auth.TokenStore
import com.dinoHabitTracker.app.data.remote.ApiClient
import com.dinoHabitTracker.app.data.remote.AuthApi
import com.dinoHabitTracker.app.data.remote.SignInDto
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(context: Context) {

    private val api = ApiClient.retrofit.create(AuthApi::class.java)
    private val tokenStore = TokenStore(context)

    /**
     * Meghívja a POST /auth/local/signin végpontot.
     * Siker esetén elmenti a tokeneket, és Result.success(Unit)-ot ad vissza.
     * Hibánál Result.failure(Exception)-t ad (üzenettel).
     */
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val resp = api.signIn(SignInDto(email = email, password = password))

            // tokenek mentése DataStore-ba
            tokenStore.save(
                access = resp.tokens.accessToken,
                refresh = resp.tokens.refreshToken
            )

            Result.success(Unit)
        } catch (e: HttpException) {
            // szerver válaszolt hibával (4xx/5xx)
            Result.failure(Exception("Sikertelen bejelentkezés (${e.code()}): ${e.message()}"))
        } catch (e: IOException) {
            // hálózati hiba (nincs internet, time-out, stb.)
            Result.failure(Exception("Hálózati hiba: ellenőrizd a kapcsolatot (${e.message})"))
        } catch (e: Exception) {
            // egyéb
            Result.failure(Exception(e.message ?: "Ismeretlen hiba a bejelentkezés során"))
        }
    }
}
