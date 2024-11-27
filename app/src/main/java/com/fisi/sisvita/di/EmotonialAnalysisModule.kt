package com.fisi.sisvita.di

import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotonialAnalysisRepository
import com.fisi.sisvita.data.repository.emotionalOrientation.EmotionalOrientationRepository
import com.fisi.sisvita.ui.screens.loading.LoadingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val emotionalAnalysisModule = module {
    // Repositorio
    single { EmotonialAnalysisRepository() }
    single { EmotionalOrientationRepository() }

    // ViewModel
    viewModel { LoadingViewModel(get(), get()) }
}