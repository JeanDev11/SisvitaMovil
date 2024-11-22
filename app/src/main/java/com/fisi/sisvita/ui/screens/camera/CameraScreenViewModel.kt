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
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotionalAnalysisResponse
import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotonialAnalysisRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class CameraScreenViewModel(private val repository: EmotonialAnalysisRepository) : ViewModel() {
    private var camera: Camera? = null
    private var recording: Recording? = null

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> get() = _uploadState

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
        val file = File(cacheDir, "temp_video.mp4")
        // Si el archivo existe, eliminarlo
        if (file.exists()) {
            file.delete()
            Log.d("Recording", "Archivo existente eliminado: ${file.absolutePath}")
        }

        Log.d("Recording", "Archivo creado en: ${file.absolutePath}")

        try {
            // Configuración y preparación de la grabación
            val outputOptions = FileOutputOptions.Builder(file).build()
            recording = videoCapture.output
                .prepareRecording(context, outputOptions)
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            Log.i("Recording", "Grabación iniciada.")
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
                                Log.i("Recording", "Video guardado en: ${file.absolutePath}")
                            }
                        }
                        else -> {
                            Log.d("Recording", "Evento de grabación: $recordEvent")
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

    fun uploadVideo(context: Context) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            Log.d("Upload", "Iniciando carga de video...")

            val videoFile = File(context.cacheDir, "temp_video.mp4")
            if (!videoFile.exists()) {
                Log.e("Upload", "El archivo de video no existe.")
                _uploadState.value = UploadState.Error("Archivo de video no encontrado.")
                return@launch
            }
            Log.d("Upload", "Archivo de video encontrado: ${videoFile.absolutePath}")

            delay(600)

            try {
                // Convertir File a MultipartBody.Part
                val videoPart = MultipartBody.Part.createFormData(
                    "video",
                    videoFile.name,
                    videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )

                val response = repository.analyzeVideo(videoPart)
                if (response.isSuccessful) {
                    val emotions = response.body()
                    Log.d("Upload", "Emociones recibidas: $emotions")

                    if (emotions != null) {
                        _uploadState.value = UploadState.Success(emotions)
                        Log.d("Upload", "Estado success: $_uploadState")
                        Log.d("Upload", "ViewModel hashCode: ${this.hashCode()}")

                    } else {
                        _uploadState.value = UploadState.Error("Emociones nulas")
                        Log.d("Upload", "Estado error: $_uploadState")
                    }
                } else {
                    Log.e("Upload", "Error del servidor: ${response.code()} - ${response.message()}")
                    _uploadState.value = UploadState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Upload", "Excepción al cargar: ${e.message}")
                _uploadState.value = UploadState.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val emotions: EmotionalAnalysisResponse) : UploadState()
    data class Error(val message: String) : UploadState()
}