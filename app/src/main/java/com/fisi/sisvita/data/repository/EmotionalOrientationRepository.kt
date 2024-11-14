package com.fisi.sisvita.data.repository

import com.fisi.sisvita.data.model.RequestBody
import com.fisi.sisvita.data.model.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface EmotionService {
    @POST("/generar_respuesta")
    suspend fun generarRespuesta(@Body requestBody: RequestBody): Response
}

class EmotionalOrientationRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://servidor-respuestaemocional.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val emotionService = retrofit.create(EmotionService::class.java)

    suspend fun getRespuesta(nombre: String, emocion: String): Response {
        val requestBody = RequestBody(nombre, emocion)
        return emotionService.generarRespuesta(requestBody)
    }
}