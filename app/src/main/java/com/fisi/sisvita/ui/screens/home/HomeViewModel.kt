package com.fisi.sisvita.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.model.Test
import com.fisi.sisvita.data.repository.TestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val testRepository: TestRepository) : ViewModel() {
    private val _tests = MutableStateFlow<List<Test>>(emptyList())
    val tests: StateFlow<List<Test>> = _tests.asStateFlow()

    init {
        getTests()
    }

    private fun getTests() {
        viewModelScope.launch {
            val fetchedTests = testRepository.getTests()
            _tests.value = fetchedTests
        }
    }
}
