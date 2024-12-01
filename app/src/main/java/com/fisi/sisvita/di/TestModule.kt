package com.fisi.sisvita.di

import com.fisi.sisvita.data.repository.TestRepository
import com.fisi.sisvita.ui.screens.test.TestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {
    // Repositorio
    single { TestRepository() }

    // ViewModel
    viewModel { TestViewModel(get()) }
}