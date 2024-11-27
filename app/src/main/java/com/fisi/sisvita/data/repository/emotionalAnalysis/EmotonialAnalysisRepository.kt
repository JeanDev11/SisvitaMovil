package com.fisi.sisvita.data.repository.emotionalAnalysis

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class EmotonialAnalysisRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para conectar con el servidor
        .writeTimeout(120, TimeUnit.SECONDS) // Tiempo para enviar datos al servidor
        .readTimeout(120, TimeUnit.SECONDS)  // Tiempo para leer la respuesta
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://741d-38-56-110-24.ngrok-free.app")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(EmotionalAnalysisService::class.java)

    suspend fun analyzeVideo(video: MultipartBody.Part): Response<EmotionalAnalysisResponse> {
        return service.analyzeVideo(video)
    }
}