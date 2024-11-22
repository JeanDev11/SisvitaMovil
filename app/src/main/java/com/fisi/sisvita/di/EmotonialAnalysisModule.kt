package com.fisi.sisvita.di

import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotonialAnalysisRepository
import com.fisi.sisvita.ui.screens.camera.CameraScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val emotionalAnalysisModule = module {
    // Repositorio
    single { EmotonialAnalysisRepository() }

    // ViewModel
    viewModel { CameraScreenViewModel(get()) }
}