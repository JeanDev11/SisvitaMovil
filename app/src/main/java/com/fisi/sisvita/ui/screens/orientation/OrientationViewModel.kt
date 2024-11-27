package com.fisi.sisvita.ui.screens.orientation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.emotionalOrientation.EmotionalOrientationRepository
import kotlinx.coroutines.launch

class OrientationViewModel (private val repository: EmotionalOrientationRepository) : ViewModel() {
//    private val _response = mutableStateOf<List<String>>(emptyList())
//    val response: State<List<String>> get() = _response
//
//    fun obtenerRespuesta(nombre: String, emocion: String) {
//        viewModelScope.launch {
//            try {
//                val respuesta = repository.getRespuesta(nombre, emocion)
//                _response.value = respuesta.response
//            } catch (e: Exception) {
//                _response.value = listOf("Error al obtener la respuesta")
//            }
//        }
//    }
}