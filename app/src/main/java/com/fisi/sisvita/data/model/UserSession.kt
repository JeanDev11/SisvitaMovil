package com.fisi.sisvita.data.model

import androidx.compose.runtime.mutableStateOf

object UserSession {
    var personId = mutableStateOf<String?>(null)
    var userName = mutableStateOf<String?>(null)
}