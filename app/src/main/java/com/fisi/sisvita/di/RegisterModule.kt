package com.fisi.sisvita.di

import RegisterViewModel
import com.fisi.sisvita.data.repository.RegisterRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registerModule = module {
    // Repositorio
    single { RegisterRepository() }

    // ViewModel
    viewModel { RegisterViewModel(get()) }
}