package com.fisi.sisvita.ui.screens.test

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.model.Pregunta
import com.fisi.sisvita.data.model.Respuesta
import com.fisi.sisvita.data.model.Test
import com.fisi.sisvita.data.model.TestSubmission
import com.fisi.sisvita.data.repository.TestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestViewModel(private val testRepository: TestRepository) : ViewModel() {
    // Tests disponibles
    private val _tests = MutableStateFlow<List<Test>>(emptyList())
    val tests: StateFlow<List<Test>> = _tests.asStateFlow()

    // Test seleccionado
    private val _selectedTestId = MutableStateFlow<Int?>(null)
    val selectedTestId: StateFlow<Int?> = _selectedTestId.asStateFlow()

    // Preguntas y respuestas del test seleccionado
    private val _preguntas = MutableStateFlow<List<Pregunta>>(emptyList())
    val preguntas: StateFlow<List<Pregunta>> = _preguntas.asStateFlow()

    private val _respuestas = MutableStateFlow<List<Respuesta>>(emptyList())
    val respuestas: StateFlow<List<Respuesta>> = _respuestas.asStateFlow()

    init {
        getTests()
    }

    private fun getTests() {
        viewModelScope.launch {
            val fetchedTests = testRepository.getTests()
            _tests.value = fetchedTests
        }
    }

    fun selectTest(testId: Int) {
        _selectedTestId.value = testId
        fetchPreguntas(testId)
        fetchRespuestas(testId)
    }

    private fun fetchPreguntas(testId: Int) {
        viewModelScope.launch {
            val fetchedPreguntas = testRepository.getPreguntas(testId)
            _preguntas.value = fetchedPreguntas
        }
    }

    private fun fetchRespuestas(testId: Int) {
        viewModelScope.launch {
            val fetchedRespuestas = testRepository.getRespuestas(testId)
            _respuestas.value = fetchedRespuestas
        }
    }

    fun submitTest(testSubmission: TestSubmission, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("TestSubmission", testSubmission.toString())
            val result = testRepository.submitTest(testSubmission)
            onResult(result)
        }
    }
}