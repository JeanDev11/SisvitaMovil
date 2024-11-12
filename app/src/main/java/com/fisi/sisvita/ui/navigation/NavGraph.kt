package com.fisi.sisvita.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.ui.screens.camera.CameraScreen
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.HomeScreen
import com.fisi.sisvita.ui.screens.ResultsScreen
import com.fisi.sisvita.ui.screens.loading.LoadingScreen

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues,) {
    NavHost(navController = navController, startDestination = "Inicio") {
        composable("Inicio") {
            HomeScreen(paddingValues, navController)
        }
        composable("Test") {
            ResultsScreen(paddingValues)
        }
        composable("Necesito ayuda") {
            HelpMeNavHost()
        }
        composable("Historial") {
            HomeScreen(paddingValues, navController)
        }
        composable("Cuenta") {
            HomeScreen(paddingValues, navController)
        }
    }
}

@Composable
fun HelpMeNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Steps") {
        composable("Steps") {
            HelpMeScreen(navController = navController)
        }
        composable("Rec") {
            CameraScreen()
        }
        composable("Loading") {
            LoadingScreen()
        }
        composable("Result") {
//            ResultScreen(navController)
        }
        composable("Recommendations") {
//            RecomendacionesScreen(navController)
        }
    }
}