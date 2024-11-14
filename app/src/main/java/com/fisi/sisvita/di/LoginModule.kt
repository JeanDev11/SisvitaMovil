package com.fisi.sisvita.di

import com.fisi.sisvita.data.repository.LoginRepository
import com.fisi.sisvita.ui.screens.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    // Repositorio
    single { LoginRepository() }

    // ViewModel
    viewModel { LoginViewModel(get()) }
}