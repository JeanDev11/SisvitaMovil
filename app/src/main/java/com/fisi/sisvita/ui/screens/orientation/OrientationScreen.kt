package com.fisi.sisvita.ui.screens.orientation

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.fisi.sisvita.ui.screens.loading.LoadingViewModel
import com.fisi.sisvita.ui.theme.SisvitaTheme
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.Job
import com.fisi.sisvita.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@Composable
fun OrientationScreen(
    viewModel: LoadingViewModel,
    paddingValues: PaddingValues,
    rootNavController: NavController
) {
    val responseData by viewModel.responseData.collectAsState()
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    // Mantenemos un buffer de amplitudes para la visualización
    var amplitudes by remember { mutableStateOf(List(30) { 0.2f.toInt() }) }

    // Creamos un timer para actualizar las amplitudes mientras se reproduce
    val scope = rememberCoroutineScope()
    var animationJob by remember { mutableStateOf<Job?>(null) }

    val tts = remember {
        mutableStateOf<TextToSpeech?>(null)
    }

    // Animación
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset("roboto.lottie"),
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5f,
        isPlaying = true
    )

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.getDefault()
                tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isPlaying = true
                        // Iniciamos la animación de las ondas
                        animationJob = scope.launch {
                            while(isActive) {
                                amplitudes = List(30) {
                                    (Math.random() * 100).toInt()
                                }
                                delay(100) // Actualizamos cada 100ms
                            }
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        isPlaying = false
                        animationJob?.cancel()
                        amplitudes = List(30) { 0 } // Reseteamos las amplitudes
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        isPlaying = false
                        animationJob?.cancel()
                        amplitudes = List(30) { 0 }
                    }
                })
            }
        }
    }

    OrientationContent(
        navController = rootNavController,
        paddingValues = paddingValues,
        amplitudes = amplitudes,
        lottieComposition = lottieComposition,
        progress = progress,
        isPlaying = isPlaying,
        onPlayPauseClick = {
            if (isPlaying) {
                // Pausar
                tts.value?.stop()
                isPlaying = false
                animationJob?.cancel()
                amplitudes = List(30) { 0 }
            } else {
                // Reproducir
                tts.value?.let { textToSpeech ->
                    responseData?.forEach { message ->
                        val utteranceId = UUID.randomUUID().toString()
                        textToSpeech.speak(
                            message,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            utteranceId
                        )
                    }
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            animationJob?.cancel()
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }
}

@Composable
fun OrientationContent(
    navController: NavController,
    paddingValues: PaddingValues,
    amplitudes: List<Int>,
    lottieComposition: LottieComposition?,
    progress: Float,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = lottieComposition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            AudioWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                amplitudes = amplitudes,
                spikePadding = 6.dp,
                progressBrush = SolidColor(MaterialTheme.colorScheme.primary),
                waveformBrush = SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                onProgressChange = { /* lógica para manejar el cambio de progreso */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = onPlayPauseClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.ic_stop_ia else R.drawable.ic_play_ia),
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Button(
            onClick = { navController.navigate("Inicio"){
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            } },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Ir al inicio")
        }
//        responseData?.forEach {
//            Text(text = it)
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpMePreview() {
    SisvitaTheme(darkTheme = false) {
        val navController = rememberNavController() // Para el NavController
        val paddingValues = PaddingValues(16.dp)  // Valores de padding
        val amplitudes = listOf(1, 2, 3, 4, 5, 6) // Lista de amplitudes de ejemplo
        val lottieComposition by rememberLottieComposition(
            LottieCompositionSpec.Asset("roboto.lottie")
        )
        val progress = 0.5f

        OrientationContent(
            navController = navController,
            paddingValues = paddingValues,
            amplitudes = amplitudes,
            lottieComposition = lottieComposition,
            progress = progress,
            isPlaying = false,
            onPlayPauseClick = {
                println("Play clicked")
            }
        )
    }
}