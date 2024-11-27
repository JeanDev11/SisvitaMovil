package com.fisi.sisvita.data.repository.emotionalAnalysis

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface EmotionalAnalysisService {
    @Multipart
    @POST("/upload_video")
    suspend fun analyzeVideo(@Part video: MultipartBody.Part): Response<EmotionalAnalysisResponse>
}