package com.fisi.sisvita.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fisi.sisvita.ui.screens.camera.CameraScreen
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.HomeScreen
import com.fisi.sisvita.ui.screens.ResultsScreen
import com.fisi.sisvita.ui.screens.loading.LoadingScreen
import com.fisi.sisvita.ui.screens.loading.LoadingViewModel
import com.fisi.sisvita.ui.screens.orientation.OrientationScreen
import com.fisi.sisvita.util.fromJson
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues,) {
    NavHost(navController = navController, startDestination = "Inicio") {
        composable("Inicio") {
            HomeScreen(paddingValues, navController)
        }
        composable("Test") {
            //ResultsScreen(paddingValues)
        }
        composable("Necesito ayuda") {
            HelpMeNavHost(paddingValues)
        }
        composable("Historial") {

        }
        composable("Cuenta") {

        }
    }
}

@Composable
fun HelpMeNavHost(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    val viewModel: LoadingViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = "Steps") {
        composable("Steps") {
            HelpMeScreen(navController)
        }
        composable("Rec") {
            CameraScreen(navController)
        }
        composable("Loading") {
            LoadingScreen(navController, viewModel)
        }
        composable(
            "results?anxietyLevel={anxietyLevel}&emotions={emotions}",
            arguments = listOf(
                navArgument("anxietyLevel") { type = NavType.FloatType },
                navArgument("emotions") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val anxietyLevel = backStackEntry.arguments?.getFloat("anxietyLevel") ?: 0f
            val emotionsJson = backStackEntry.arguments?.getString("emotions") ?: "{}"
            val emotionPercentages = emotionsJson.fromJson<Map<String, Float>>()
            ResultsScreen(paddingValues, anxietyLevel, emotionPercentages, navController)
        }
        composable("Orientations") {
            OrientationScreen(paddingValues, viewModel)
        }
    }
}