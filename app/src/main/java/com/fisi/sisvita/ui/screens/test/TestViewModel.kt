package com.fisi.sisvita.ui.screens.test

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.model.Pregunta
import com.fisi.sisvita.data.model.Respuesta
import com.fisi.sisvita.data.model.TestSubmission
import com.fisi.sisvita.data.repository.TestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {
    private val repository = TestRepository()

    private val _preguntas = MutableStateFlow<List<Pregunta>>(emptyList())
    val preguntas: StateFlow<List<Pregunta>> get() = _preguntas

    private val _respuestas = MutableStateFlow<List<Respuesta>>(emptyList())
    val respuestas: StateFlow<List<Respuesta>> get() = _respuestas

    fun fetchPreguntas() {
        viewModelScope.launch {
            val fetchedPreguntas = repository.getPreguntas()
            _preguntas.value = fetchedPreguntas
        }
    }

    fun fetchRespuestas() {
        viewModelScope.launch {
            val fetchedRespuestas = repository.getRespuestas()
            _respuestas.value = fetchedRespuestas
        }
    }

    fun submitTest(testSubmission: TestSubmission, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("TestSubmission", testSubmission.toString())
            val result = repository.submitTest(testSubmission)
            onResult(result)
        }
    }
}