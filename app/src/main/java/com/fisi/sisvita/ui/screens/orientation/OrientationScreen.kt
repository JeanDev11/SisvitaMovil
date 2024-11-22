package com.fisi.sisvita.ui.screens.orientation

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fisi.sisvita.ui.theme.SisvitaTheme
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import java.util.UUID

@Composable
fun OrientationScreen(
    paddingValues: PaddingValues,
    viewModel: OrientationViewModel = koinViewModel()
) {
    val response = viewModel.response.value
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
                        // Reseteamos las amplitudes
                        amplitudes = List(30) { 0 }
                    }

                    override fun onError(utteranceId: String?) {
                        isPlaying = false
                        animationJob?.cancel()
                        amplitudes = List(30) { 0 }
                    }
                })
            }
        }
        viewModel.obtenerRespuesta("sara", "triste y ansioso")
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

        Button(onClick = {
            tts.value?.let { textToSpeech ->
                response.forEach { message ->
                    val utteranceId = UUID.randomUUID().toString()
                    textToSpeech.speak(
                        message,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        utteranceId
                    )
                }
            }
        }) {
            Text("Reproducir respuesta")
        }

//        response.forEach {
//            Text(text = it)
//        }
    }

    DisposableEffect(Unit) {
        onDispose {
            animationJob?.cancel()
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpMePreview() {
    SisvitaTheme(darkTheme = false) {
        OrientationScreen(paddingValues = PaddingValues(0.dp))
    }
}