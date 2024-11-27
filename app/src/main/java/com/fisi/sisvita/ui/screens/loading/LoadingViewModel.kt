package com.fisi.sisvita.ui.screens.loading

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotionalAnalysisResponse
import com.fisi.sisvita.data.repository.emotionalAnalysis.EmotonialAnalysisRepository
import com.fisi.sisvita.data.repository.emotionalOrientation.EmotionalOrientationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.math.RoundingMode

class LoadingViewModel(
    private val repository: EmotonialAnalysisRepository,
    private val responseRepository: EmotionalOrientationRepository,
) : ViewModel() {
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> get() = _uploadState

    private val _responseData = MutableStateFlow<List<String>?>(null)
    val responseData: StateFlow<List<String>?> get() = _responseData

    private val _hasHandledError = MutableStateFlow(false)
    val hasHandledError: Boolean get() = _hasHandledError.value

    fun uploadVideoAndProcess(context: Context, userName: String) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            _hasHandledError.value = false
            Log.d("Upload", "Iniciando carga de video...")

            val videoFile = File(context.cacheDir, "temp_video.mp4")
            if (!videoFile.exists()) {
                Log.e("Upload", "El archivo de video no existe.")
                _uploadState.value = UploadState.Error("Archivo de video no encontrado. Por favor, intenta de nuevo.")
                return@launch
            }

            try {
                // Convertir File a MultipartBody.Part
                val videoPart = MultipartBody.Part.createFormData(
                    "video",
                    videoFile.name,
                    videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )

                val response = repository.analyzeVideo(videoPart)
                if (response.isSuccessful) {
                    val emotions = response.body()!!
                    Log.d("Upload", "Emociones recibidas: $emotions")

                    val emotionPercentages = emotions.let {
                        mapOf(
                            "Disgustado" to (it.disgusted ?: 0.0) * 100,
                            "Enojado" to (it.angry ?: 0.0) * 100,
                            "Feliz" to (it.happy ?: 0.0) * 100,
                            "Miedo" to (it.scared ?: 0.0) * 100,
                            "Neutral" to (it.neutral ?: 0.0) * 100,
                            "Sorpresa" to (it.surprised ?: 0.0) * 100,
                            "Triste" to (it.sad ?: 0.0) * 100
                        ).mapValues { entry ->
                            entry.value.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toFloat()
                        }
                    }
                    // Enviar porcentajes al segundo servidor
                    val orientationResponse = responseRepository.getRespuesta(userName, emotionPercentages)
                    _responseData.value = orientationResponse.response

                    Log.d("Upload", "Emociones porcentajes: $emotionPercentages")
                    Log.d("Upload", "Respuesta del segundo servidor: ${_responseData.value}")
                    // Cambiar estado a éxito
                    _uploadState.value = UploadState.Success(emotionPercentages)

                } else {
                    Log.e("Upload", "Error del servidor: ${response.code()} - ${response.message()}")
                    _uploadState.value = UploadState.Error("Hubo un problema al procesar la solicitud. Intenta más tarde.")
                }
            } catch (e: Exception) {
                Log.e("Upload", "Excepción al cargar: ${e.message}")
                _uploadState.value = UploadState.Error("Ocurrió un error inesperado: ${e.message}. Por favor, intenta nuevamente.")
            }
        }
    }

    fun resetErrorFlag() {
        _hasHandledError.value = true // Marcar el error como manejado
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
        _hasHandledError.value = false
    }

    fun clearResponseData() {
        _responseData.value = null
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val emotions: Map<String, Float>) : UploadState()
    data class Error(val message: String) : UploadState()
}

//class LoadingViewModel(private val repository: EmotonialAnalysisRepository) : ViewModel() {
//    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
//    val uploadState: StateFlow<UploadState> get() = _uploadState
//
//    fun uploadVideo(context: Context) {
//        viewModelScope.launch {
//            _uploadState.value = UploadState.Loading
//            Log.d("Upload", "Iniciando carga de video...")
//
//            val videoFile = File(context.cacheDir, "temp_video.mp4")
//            if (!videoFile.exists()) {
//                Log.e("Upload", "El archivo de video no existe.")
//                _uploadState.value = UploadState.Error("Archivo de video no encontrado.")
//                return@launch
//            }
//            Log.d("Upload", "Archivo de video encontrado: ${videoFile.absolutePath}")
//
//            //delay(600)
//
//            try {
//                // Convertir File a MultipartBody.Part
//                val videoPart = MultipartBody.Part.createFormData(
//                    "video",
//                    videoFile.name,
//                    videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
//                )
//
//                val response = repository.analyzeVideo(videoPart)
//                if (response.isSuccessful) {
//                    val emotions = response.body()
//                    Log.d("Upload", "Emociones recibidas: $emotions")
//
//                    if (emotions != null) {
//                        _uploadState.value = UploadState.Success(emotions)
//                        Log.d("Upload", "Estado success: $_uploadState")
//
//                    } else {
//                        _uploadState.value = UploadState.Error("Emociones nulas")
//                        Log.d("Upload", "Estado error: $_uploadState")
//                    }
//                } else {
//                    Log.e("Upload", "Error del servidor: ${response.code()} - ${response.message()}")
//                    _uploadState.value = UploadState.Error("Error: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                Log.e("Upload", "Excepción al cargar: ${e.message}")
//                _uploadState.value = UploadState.Error("Exception: ${e.message}")
//            }
//        }
//    }
//
//    fun resetState() {
//        _uploadState.value = UploadState.Idle
//    }
//}
