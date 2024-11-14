package com.fisi.sisvita.di

import com.fisi.sisvita.data.repository.EmotionalOrientationRepository
import com.fisi.sisvita.ui.screens.orientation.OrientationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val emotionOrientationModule = module {
    // Repositorio
    single { EmotionalOrientationRepository() }

    // ViewModel
    viewModel { OrientationViewModel(get()) }
}