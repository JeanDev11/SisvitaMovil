package com.fisi.sisvita.data.repository.emotionalOrientation

import retrofit2.http.Body
import retrofit2.http.POST

interface EmotionalOrientationService {
    @POST("/generar_respuesta")
    suspend fun generarRespuesta(@Body requestBody: EmotionalOrientationRequest): EmotionalOrientationResponse
}