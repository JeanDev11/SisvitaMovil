package com.fisi.sisvita.ui.screens.test

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.data.model.Test
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.data.repository.TestRepository
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun TestScreen(paddingValues: PaddingValues, navController: NavController, viewModel: TestViewModel) {
    val tests by viewModel.tests.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TestAdd(tests, navController, viewModel)
    }
}

@Composable
fun TestAdd(tests: List<Test>, navController: NavController, viewModel: TestViewModel) {
    if (tests.isEmpty()) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
    } else {
        Text(
            text = "Explora nuestros tests y encuentra el más adecuado para ti.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TestCarousel(tests, navController, viewModel)
    }
}

@Composable
fun TestCarousel(tests: List<Test>, navController: NavController, viewModel: TestViewModel) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tests) { test ->
            TestCard(test, navController = navController, viewModel)
        }
    }
}

@Composable
fun TestCard(test: Test, navController: NavController, viewModel: TestViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = test.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = test.descripcion,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    try {
                        // Selecciona el test en el ViewModel
                        viewModel.selectTest(test.testid)
                        Log.e("NavigationError", test.testid.toString())
                        // Navega a TestFormScreen
                        navController.navigate("DoTest")
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to Test: ${e.message}")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Comenzar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultDialogPreview() {
//    ResultDialog(diagnostico = "Depresión grave", puntaje = 64, onDismiss = {})
}

@Preview(showBackground = false)
@Composable
fun TestCardPreview() {
    // Datos de ejemplo para el test
    val tests = listOf(
        Test(testid = 1, nombre = "Test de Ansiedad", descripcion = "Evalúa el nivel de ansiedad general."),
        Test(testid = 2, nombre = "Test de Depresión", descripcion = "Evalúa los síntomas de depresión en diferentes situaciones."),
        Test(testid = 3, nombre = "Test de Estrés", descripcion = "Mide el nivel de estrés a partir de situaciones cotidianas.")
    )
    val navController = rememberNavController()
    SisvitaTheme(darkTheme = false) {
        TestCarousel(tests = tests, navController = navController, viewModel = TestViewModel(
            TestRepository()
        ))
    }

}