package com.fisi.sisvita.data.model

data class Test(
    val testid: Int,
    val nombre: String,
    val descripcion: String,
)

data class TestSubmission(
    val personaid: Int,
    val testid: Int,
    val respuestas: List<RespuestaSubmission>
)

data class RespuestaSubmission(
    val preguntaid: Int,
    val respuestaid: Int
)