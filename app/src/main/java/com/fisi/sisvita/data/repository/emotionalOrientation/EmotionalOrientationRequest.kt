package com.fisi.sisvita.data.repository.emotionalOrientation

data class EmotionalOrientationRequest(
    val nombre: String,
    val emociones: Map<String, Float>,
)
