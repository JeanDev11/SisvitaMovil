package com.fisi.sisvita.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fisi.sisvita.data.repository.LoginRepository
import com.fisi.sisvita.ui.screens.login.LoginViewModel

class LoginViewModelFactory(private val repository: LoginRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}