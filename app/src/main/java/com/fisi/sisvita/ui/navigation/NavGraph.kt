package com.fisi.sisvita.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.data.repository.TestRepository
import com.fisi.sisvita.ui.home.HomeScreen
import com.fisi.sisvita.ui.home.HomeViewModel
import com.fisi.sisvita.ui.screens.camera.CameraScreen
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.ResultsScreen
import com.fisi.sisvita.ui.screens.loading.LoadingScreen
import com.fisi.sisvita.ui.screens.orientation.OrientationScreen
import com.fisi.sisvita.ui.screens.test.TestsScreen

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues,) {
    NavHost(navController = navController, startDestination = "Inicio") {
        composable("Inicio") {
            HomeScreen(paddingValues, navController, HomeViewModel(TestRepository())) // Inyecta el ViewModel
        }
        composable("Test") {
            TestNavHost(paddingValues)
        }
        composable("Necesito ayuda") {
            HelpMeNavHost(paddingValues)
        }
        composable("Historial") {
            ResultsScreen(paddingValues)
        }
        composable("Cuenta") {
            OrientationScreen(paddingValues)
        }
        composable("DoTest") {
//            TestsScreen(paddingValues, navController)
            TestNavHost(paddingValues)
        }
    }
}

@Composable
fun HelpMeNavHost(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Steps") {
        composable("Steps") {
            HelpMeScreen(navController)
        }
        composable("Rec") {
            CameraScreen()
        }
        composable("Loading") {
            LoadingScreen()
        }
        composable("Result") {
            ResultsScreen(paddingValues)
        }
        composable("Recommendations") {
            OrientationScreen(paddingValues)
        }
    }
}

@Composable
fun TestNavHost(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "DoTest") {
        composable("DoTest") {
            TestsScreen(paddingValues, navController)
        }
        composable("ResultTest") {
            //ResultsTestScreen(paddingValues)
        }
    }
}