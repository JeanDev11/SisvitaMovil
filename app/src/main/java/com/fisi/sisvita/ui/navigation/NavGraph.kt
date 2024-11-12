package com.fisi.sisvita.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fisi.sisvita.ui.screens.camera.CameraScreen
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.HomeScreen
import com.fisi.sisvita.ui.screens.ResultsScreen

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues,) {
    NavHost(navController = navController, startDestination = "Inicio") {
        composable("Inicio") {
            HomeScreen(paddingValues, navController)
        }
        composable("Test") {
            ResultsScreen()
        }
        composable("Necesito ayuda") {
            HelpMeScreen(navController = navController)
        }
        composable("Historial") {
            HomeScreen(paddingValues, navController)
        }
        composable("Camara") {
            CameraScreen()
        }
        composable("Cuenta") {
            HomeScreen(paddingValues, navController)
        }
    }
}