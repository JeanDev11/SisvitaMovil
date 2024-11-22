package com.fisi.sisvita.data.repository.emotionalOrientation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmotionalOrientationRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://servidor-respuestaemocional.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val emotionService = retrofit.create(EmotionalOrientationService::class.java)

    suspend fun getRespuesta(nombre: String, emocion: String): EmotionalOrientationResponse {
        val requestBody = EmotionalOrientationRequest(nombre, emocion)
        return emotionService.generarRespuesta(requestBody)
    }
}