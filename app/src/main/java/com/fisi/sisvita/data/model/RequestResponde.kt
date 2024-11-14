package com.fisi.sisvita.data.model

data class RequestBody(
    val nombre: String,
    val emocion: String,
)

data class Response(
    val success: Boolean,
    val message: String,
    val response: List<String>
)
