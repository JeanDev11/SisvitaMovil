package com.fisi.sisvita.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel (private val loginRepository: LoginRepository) : ViewModel() {
    private val _loginState = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    var showErrorDialog by mutableStateOf(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val success = loginRepository.login(username, password)
            if (success) {
                _loginState.value = true
            } else {
                showErrorDialog = true
            }
        }
    }

    fun logout() {
        _loginState.value = false
    }
}
