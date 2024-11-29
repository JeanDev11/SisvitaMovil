package com.fisi.sisvita.data.model

import androidx.compose.runtime.mutableStateOf

object UserSession {
    var personId = mutableStateOf<String?>(null)
    var userName = mutableStateOf<String?>(null)
    var testId = mutableStateOf<String?>(null)
    var diagnostico = mutableStateOf<String?>(null)
    var puntaje = mutableStateOf<Int?>(null)
}