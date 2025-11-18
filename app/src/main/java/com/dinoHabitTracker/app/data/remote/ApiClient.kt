package com.dinoHabitTracker.app.data.remote

import android.content.Context
import com.dinoHabitTracker.app.data.auth.TokenStore
import com.dinoHabitTracker.app.data.remote.ProgressApi
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object ApiClient {

    // Emulátorból a host gép így érhető el:
    private const val BASE_URL = "http://10.203.156.23:8080/"

    /**
     * Creates a Retrofit instance with authentication & gson.
     */
    fun retrofit(context: Context): Retrofit {
        // HTTP log (fejlesztéshez)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Authorization header interceptor
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token: String? = runBlocking { TokenStore(context).accessToken.firstOrNull() }

            val request = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else original

            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Convenience method for any API service creation.
     */
    fun <T> api(context: Context, service: Class<T>): T =
        retrofit(context).create(service)

    /**
     * Progress API provider.
     */
    fun progressApi(context: Context): ProgressApi =
        api(context, ProgressApi::class.java)
}
