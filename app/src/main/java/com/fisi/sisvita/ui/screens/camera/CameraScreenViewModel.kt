package com.fisi.sisvita.ui.screens.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.camera.video.VideoCapture
import androidx.camera.video.Recorder
import androidx.camera.video.FileOutputOptions
import java.io.File
import android.util.Log
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri


class CameraScreenViewModel : ViewModel() {
    private var camera: Camera? = null
    private var recording: Recording? = null

    // Estado para el flash (on/off)
    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn

    // Estado para la grabación (grabando/no grabando)
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean>  = _isRecording

    // Estado para la cámara (trasera/frontal)
    private val _isUsingFrontCamera = MutableStateFlow(false)
    val isUsingFrontCamera: StateFlow<Boolean> = _isUsingFrontCamera

    // Métodos para actualizar los estados
    fun toggleFlash() {

        camera?.let {
            val newFlashState = !_isFlashOn.value
            it.cameraControl.enableTorch(newFlashState)
            _isFlashOn.value = newFlashState

        }
    }

    fun setCamera(camera: Camera) {
        // this.camera = camera
        // _isFlashOn.value = camera.cameraInfo.hasFlashUnit() && camera.cameraControl.torchEnabled
        this.camera = camera
    }

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
    }

    fun toggleCamera() {
        _isUsingFrontCamera.value = !_isUsingFrontCamera.value
    }

    // Inicia la grabación
    fun startRecording(context: Context, videoCapture: VideoCapture<Recorder>): Uri? {
        // Validación inicial del estado de la grabación
        if (_isRecording.value) {
            Log.e("Recording", "La grabación ya está en curso.")
            return null
        }

        // Validación del directorio de caché
        val cacheDir = context.cacheDir
        if (!cacheDir.exists() || !cacheDir.canWrite()) {
            Log.e("Recording", "El directorio de caché no está disponible o no se puede escribir.")
            return null
        }

        // Creación del archivo temporal
        val file = File.createTempFile("temp_video", ".mp4", cacheDir)
        if (!file.canWrite()) {
            Log.e("Recording", "No se puede escribir en el archivo de destino.")
            return null
        }

        Log.e("Recording", "Archivo creado en: ${file.absolutePath}")

        try {
            // Configuración y preparación de la grabación
            val outputOptions = FileOutputOptions.Builder(file).build()
            recording = videoCapture.output
                .prepareRecording(context, outputOptions)
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            Log.e("Recording", "Grabación iniciada.")
                            _isRecording.value = true // Actualizamos el estado en el ViewModel
                        }
                        is VideoRecordEvent.Finalize -> {
                            _isRecording.value = false // Resetear el estado al finalizar
                            if (recordEvent.hasError()) {
                                Log.e(
                                    "Recording",
                                    "Error al finalizar la grabación: ${recordEvent.error}, "
                                )
                            } else {
                                Log.e("Recording", "Video guardado en: ${file.absolutePath}")
                            }
                        }
                        else -> {
                            Log.e("Recording", "Evento de grabación: $recordEvent")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("Recording", "Error al iniciar la grabación: ${e.message}")
            return null
        }

        return file.toUri()
    }

    // Detiene la grabación
    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    // Implementar el vaciado de la caché (Opcional)
}


