package com.fisi.sisvita.data.repository.emotionalAnalysis

import com.google.gson.annotations.SerializedName

data class EmotionalAnalysisResponse(
    @SerializedName("Disgustado") val disgusted: Double?,
    @SerializedName("Enojado") val angry: Double?,
    @SerializedName("Feliz") val happy: Double?,
    @SerializedName("Miedo") val scared: Double?,
    @SerializedName("Neutral") val neutral: Double?,
    @SerializedName("Sorpresa") val surprised: Double?,
    @SerializedName("Triste") val sad: Double?
)
