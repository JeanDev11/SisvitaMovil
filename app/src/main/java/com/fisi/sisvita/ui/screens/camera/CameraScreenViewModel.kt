package com.fisi.sisvita.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.camera.core.CameraSelector
import androidx.camera.video.VideoCapture
import androidx.camera.video.Recorder
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.PendingRecording
import java.io.File
import android.util.Log
import androidx.camera.core.Preview
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
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
        val cacheDir = context.cacheDir
        if (!cacheDir.exists() || !cacheDir.canWrite()) {
            Log.e("Recording", "El directorio de caché no está disponible o no se puede escribir.")
        }
        val fileName = "video_${System.currentTimeMillis()}.mp4"
        //val file = File(cacheDir, fileName)
        val file = File.createTempFile("temp_video", ".mp4", cacheDir)
        if (!file.canWrite()) {
            Log.e("Recording", "No se puede escribir en el archivo de destino.")
        }

        val outputOptions = FileOutputOptions.Builder(file).build()
        recording = videoCapture.output.prepareRecording(context, outputOptions)
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                if (recordEvent is VideoRecordEvent.Finalize) {
                    if (recordEvent.hasError()) {
                    Log.e("Recording", "Error al finalizar la grabación: ${recordEvent.error}, "+ " Descripción: ${recordEvent.error.toString()}")

                    } else {
                        Log.i("Recording", "Video guardado en: ${file.absolutePath}")
                    }
                }
            }
        return file.toUri()
    }

    // Detiene la grabación
    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    }


