package com.fisi.sisvita.ui.screens.loading

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.components.LottieAnimationComponent
import com.fisi.sisvita.ui.theme.SisvitaTheme
import com.fisi.sisvita.util.toJson
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.math.RoundingMode

@Composable
fun LoadingScreen(
    navController: NavController,
    viewModel: LoadingViewModel
) {
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uploadVideoAndProcess(context, "Sara")
    }

    when (val state = uploadState) {
        is UploadState.Loading -> {
            LoadingView()
        }
        is UploadState.Success -> {
            // Calcular nivel de ansiedad
            val anxietyLevel = calculateAnxietyLevel(state.emotions)

            // Navegar a ResultsScreen
            navController.navigate("results?anxietyLevel=$anxietyLevel&emotions=${state.emotions.toJson()}"){
                popUpTo("Loading") { inclusive = true }
            }

            // Resetear el estado después de navegar
            viewModel.resetState()
        }
        is UploadState.Error -> {
            if (!viewModel.hasHandledError) { // Manejar el error solo una vez
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()

                // Navegar de vuelta a "Steps"
                navController.navigate("Steps") {
                    popUpTo("Loading") { inclusive = true }
                }

                // Marcar el error como manejado
                viewModel.resetErrorFlag()
            }
        }
        else -> {}
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LottieAnimationComponent(
                    resId = R.raw.loading,
                    modifier = Modifier.height(60.dp)
                )
                Text(
                    text = "Se esta analizando los resultados.\n Por favor, espere...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LottieAnimationComponent(
                    resId = R.raw.mywave,
                    scaleY = -1f
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.2f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}

fun calculateAnxietyLevel(emotions: Map<String, Float>): Float {
    // Ponderaciones para cada emoción
    val weights = mapOf(
        "Disgustado" to 0.15f,   // Baja contribución
        "Enojado" to 0.3f,      // Moderada contribución
        "Feliz" to -0.4f,       // Reduce la ansiedad
        "Miedo" to 0.4f,        // Alta contribución
        "Neutral" to -0.1f,     // Reduce ligeramente la ansiedad
        "Sorpresa" to 0.1f,     // Baja contribución
        "Triste" to 0.3f        // Moderada contribución
    )

    // Calcular el nivel de ansiedad ponderado
    var weightedSum = 0f
    for ((emotion, value) in emotions) {
        weightedSum += (weights[emotion] ?: 0f) * value
    }

    // Escalar el resultado al rango 0-100
    val anxietyLevel = (weightedSum * 100).coerceIn(0f, 100f)

    return anxietyLevel.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toFloat()
//    val negativeEmotions = listOf("Disgustado", "Enojado", "Triste", "Miedo")
//    val anxietySum = negativeEmotions.map { emotions[it] ?: 0f }.sum()
//    return (anxietySum / negativeEmotions.size * 100)
//        .toBigDecimal()
//        .setScale(3, RoundingMode.HALF_UP)
//        .toFloat()
}

@Preview(showBackground = true)
@Composable
fun AnalyzingViewPreview() {
    SisvitaTheme(darkTheme = false) {
        //LoadingScreen()
    }
}