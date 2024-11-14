package com.fisi.sisvita.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fisi.sisvita.data.repository.RegisterRepository
import com.fisi.sisvita.ui.screens.register.RegisterViewModel

class RegisterViewModelFactory (private val repository: RegisterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}