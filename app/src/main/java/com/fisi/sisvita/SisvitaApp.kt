package com.fisi.sisvita

import android.app.Application
import com.fisi.sisvita.di.emotionOrientationModule
import com.fisi.sisvita.di.loginModule
import com.fisi.sisvita.di.registerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SisvitaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SisvitaApp)
            // Aquí se incluye todos los módulos Koin creados
            modules(loginModule, emotionOrientationModule, registerModule)
        }
    }
}