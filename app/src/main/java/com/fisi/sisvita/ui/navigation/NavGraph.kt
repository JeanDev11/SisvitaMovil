package com.fisi.sisvita.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fisi.sisvita.ui.screens.home.HomeScreen
import com.fisi.sisvita.ui.screens.camera.CameraScreen
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.ResultsScreen
import com.fisi.sisvita.ui.screens.loading.LoadingScreen
import com.fisi.sisvita.ui.screens.loading.LoadingViewModel
import com.fisi.sisvita.ui.screens.orientation.OrientationScreen
import com.fisi.sisvita.ui.screens.test.TestScreen
import com.fisi.sisvita.ui.screens.test.TestViewModel
import com.fisi.sisvita.ui.screens.testForm.TestFormScreen
import com.fisi.sisvita.util.fromJson
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel: TestViewModel = koinViewModel()
    NavHost(navController = navController, startDestination = "Inicio") {
        composable("Inicio") {
            HomeScreen(paddingValues, navController, viewModel)
        }
        composable("Test") {
            TestScreen(paddingValues, navController, viewModel)
        }
        composable("Necesito ayuda") {
            HelpMeNavHost(navController, paddingValues)
        }
        composable("Historial") {

        }
        composable("Cuenta") {
//            OrientationScreen(paddingValues)
        }
        composable("DoTest") {
            TestFormScreen(paddingValues, navController, viewModel)
        }
    }
}

@Composable
fun HelpMeNavHost(rootNavController: NavHostController, paddingValues: PaddingValues) {
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
            OrientationScreen(viewModel, paddingValues, rootNavController = rootNavController )
        }
    }
}