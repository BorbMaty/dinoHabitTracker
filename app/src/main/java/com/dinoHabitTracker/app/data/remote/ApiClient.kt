package com.dinoHabitTracker.app.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // ←← EZZEL KEZDJ: Írd be a saját backend címedet. A VÉGÉN LEGYEN / !
    // Példák:
    //  - Ha Emulatoron fut a backend a gépeden: "http://10.0.2.2:3000/"
    //  - Ha fizikai telefon + PC IP: "http://192.168.1.123:3000/"
    //  - Ha távoli szerver: "https://api.teprojekted.hu/"
    private const val BASE_URL = "http://172.21.0.1:8080/"


    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}
